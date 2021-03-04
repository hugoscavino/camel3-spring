package org.scavino.route;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.model.rest.RestBindingMode;
import org.scavino.model.Book;
import org.scavino.model.OrderConfirmation;
import org.scavino.processor.OrderConfirmationProcessor;
import org.scavino.service.ValidateService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.ws.rs.core.MediaType;

@Component
class OrderRouteBuilder extends RouteBuilder {


    private final String serverPort;
    private final String bookServicePort;
    private final OrderConfirmationProcessor orderConfirmationProcessor;

    public OrderRouteBuilder(@Value("${server.port}") String serverPort,
                             @Value("${book-service.port}") String bookServicePort,
                             OrderConfirmationProcessor orderConfirmationProcessor){
        this.serverPort = serverPort;
        this.bookServicePort = bookServicePort;
        this.orderConfirmationProcessor = orderConfirmationProcessor;
    }

    @Override
    public void configure() {

        CamelContext context = new DefaultCamelContext();
        context.setStreamCaching(false);

        // http://localhost:8080/camel/api-doc
        restConfiguration()
                .port(serverPort)
                .enableCORS(true)
                .apiContextPath("/api-doc")
                .apiProperty("api.title", "BOOK ROUTER REST API")
                .apiProperty("api.version", "v1.0")
                .apiProperty("cors", "true") // cross-site
                .apiContextRouteId("doc-api")
                .component("servlet")
                .bindingMode(RestBindingMode.json)
                .dataFormatProperty("prettyPrint", "true");

        // REST INJESTER
        rest("/api/")
                .description("Book Ordering REST Service")
                .id("api-route")
                .enableCORS(true)
                .bindingMode(RestBindingMode.json)
                .post("/order-router")
                .produces(MediaType.APPLICATION_JSON)
                .consumes(MediaType.APPLICATION_JSON)
                .type(Book.class)
                .outType(OrderConfirmation.class)
                .to("direct:validateService");

        // VALIDATION SERVICE
        from("direct:validateService")
                .description("Calling ValidateService")
                .routeId("validation-route")
                .tracing()
                .log(">>>id  >>> ${body.id}")
                .log(">>>name>>> ${body.name}")
                .process(new Processor() {
                    @Override
                    public void process(Exchange exchange) throws Exception {
                        Book bodyIn = (Book) exchange.getIn().getBody();
                        ValidateService.validateBook(bodyIn);
                        exchange.getIn().setBody(bodyIn);
                    }
                })
                .to("direct:confirm");

        final String externalHttp = "http://localhost:" + bookServicePort + "/confirm?bridgeEndpoint=true&amp;throwExceptionOnFailure=false";

        // CONFIRMATION HTTP SERVICE
        from("direct:confirm")
                .description("Calling Confirmation HTTP Service")
                .routeId("confirm-route")
                .tracing()
                .log(">>>Confirm Book  >>> ${body}")
                .setHeader(Exchange.HTTP_METHOD, constant("POST"))
                .setHeader(Exchange.CONTENT_TYPE, constant("application/json"))
                .to(externalHttp)
                .process(orderConfirmationProcessor)
                .log(">>>Confirm OrderConfirmation  >>> ${body}")
                ;
    }
}
