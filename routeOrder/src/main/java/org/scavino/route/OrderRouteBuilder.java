package org.scavino.route;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.model.rest.RestBindingMode;
import org.scavino.config.BookServiceConfig;
import org.scavino.config.FulfillmentServiceConfig;
import org.scavino.config.TranslateServiceConfig;
import org.scavino.model.Book;
import org.scavino.model.OrderConfirmation;
import org.scavino.processor.OrderConfirmationProcessor;
import org.scavino.processor.PrintBodyAsStringProcessor;
import org.scavino.processor.TranslateProcessor;
import org.scavino.processor.ValidateProcessor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.ws.rs.core.MediaType;

@Component


class OrderRouteBuilder extends RouteBuilder {

    @Value("${server.port}")
    private int serverPort;

    final public String bridgeParams = "?bridgeEndpoint=true&amp;throwExceptionOnFailure=false";

    private final BookServiceConfig bookServiceConfig;
    private final TranslateServiceConfig translateServiceConfig;
    private final FulfillmentServiceConfig fulfillmentServiceConfig;

    private final PrintBodyAsStringProcessor printBodyAsStringProcessor;
    private final OrderConfirmationProcessor orderConfirmationProcessor;
    private final TranslateProcessor translateProcessor;
    private final ValidateProcessor validateProcessor;

    public OrderRouteBuilder(BookServiceConfig bookServiceConfig,
                             TranslateServiceConfig translateServiceConfig,
                             FulfillmentServiceConfig fulfillmentServiceConfig,
                             PrintBodyAsStringProcessor printBodyAsStringProcessor,
                             OrderConfirmationProcessor orderConfirmationProcessor,
                             TranslateProcessor translateProcessor,
                             ValidateProcessor validateProcessor){
        this.bookServiceConfig = bookServiceConfig;
        this.translateServiceConfig = translateServiceConfig;
        this.fulfillmentServiceConfig = fulfillmentServiceConfig;
        this.printBodyAsStringProcessor = printBodyAsStringProcessor;
        this.orderConfirmationProcessor = orderConfirmationProcessor;
        this.translateProcessor = translateProcessor;
        this.validateProcessor = validateProcessor;
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

        // REST INGESTER

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
                .to("direct:translateService");

        final String translateHttp = "http://" +
                translateServiceConfig.getServer() + ":" +
                translateServiceConfig.getPort() +
                translateServiceConfig.getContextRoot() +
                bridgeParams;

        // TRANSLATE SERVICE
        // https://stackoverflow.com/questions/9194720/apache-camel-how-store-variable-for-later-use
        from("direct:translateService")
                .description("Calling ValidateService")
                .routeId("translation-route")
                .tracing()
                .log(">>>id  >>> ${body.id}")
                .log(">>>title>>> ${body.title}")
                .setProperty("BOOK_ID", simple("${body}"))
                .process(printBodyAsStringProcessor)
                .setHeader(Exchange.HTTP_METHOD, constant("GET"))
                .setHeader(Exchange.CONTENT_TYPE, constant("application/json"))
                .setHeader(Exchange.HTTP_QUERY, simple("title=${body.title}&language=es"))
                .to(translateHttp)
                .process(printBodyAsStringProcessor)
                .process(translateProcessor)
                .process(printBodyAsStringProcessor)
                .to("direct:validateService")
                .removeHeader(Exchange.HTTP_QUERY) // Don't forget to remove this header
                ;

        // VALIDATION SERVICE
        from("direct:validateService")
                .description("Calling ValidateService")
                .routeId("validation-route")
                .tracing()
                .log(">>>validateService id  >>> ${body.id}")
                .log(">>>validateService title>>> ${body.title}")
                .process(printBodyAsStringProcessor)
                .process(validateProcessor)
                .process(printBodyAsStringProcessor)
                .to("direct:fulfill");


        // Fulfillment SERVICE
        final String fulfillmentUrl = "http://" +
                fulfillmentServiceConfig.getServer() + ":" +
                fulfillmentServiceConfig.getPort() +
                fulfillmentServiceConfig.getContextRoot() +
                bridgeParams;

        from("direct:fulfill")
                .description("Calling Fulfillment Service")
                .routeId("fulfill-route")
                .tracing()
                .log(">>>Confirm Book  >>> ${body}")
                .process(printBodyAsStringProcessor)
                .setHeader(Exchange.HTTP_METHOD, constant("POST"))
                .setHeader(Exchange.CONTENT_TYPE, constant("application/json"))
                .to(fulfillmentUrl)
                .process(orderConfirmationProcessor)
                .process(printBodyAsStringProcessor)
                .log(">>>OrderConfirmation  >>> ${body}")
                ;
    }
}
