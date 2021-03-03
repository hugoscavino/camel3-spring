package org.scavino.route;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.jackson.JacksonDataFormat;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.apache.camel.model.rest.RestBindingMode;
import org.scavino.model.Book;
import org.scavino.service.ValidateService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import processor.MyProcessor;

import javax.ws.rs.core.MediaType;

@Component
class RestApi extends RouteBuilder {

    @Value("${server.port}")
    String serverPort;

    @Value("${book-service.port}")
    String bookServicePort;


    @Override
    public void configure() {

        CamelContext context = new DefaultCamelContext();

        // http://localhost:8080/camel/api-doc
        restConfiguration()
                .port(serverPort)
                .enableCORS(true)
                .apiContextPath("/api-doc")
                .apiProperty("api.title", "Sample REST API")
                .apiProperty("api.version", "v1")
                .apiProperty("cors", "true") // cross-site
                .apiContextRouteId("doc-api")
                .component("servlet")
                .bindingMode(RestBindingMode.json)
                .dataFormatProperty("prettyPrint", "true");

        /*
         The Rest DSL supports automatic binding json/xml contents to/from
         POJOs using Camels Data Format.

         By default the binding mode is off, meaning there is no automatic
         binding happening for incoming and outgoing messages.

         You may want to use binding if you develop POJOs that maps to
         your REST services request and response types.
         */

        rest("/api/")
                .description("Sample REST Service")
                .id("api-route")
                .post("/order-router")
                .produces(MediaType.APPLICATION_JSON)
                .consumes(MediaType.APPLICATION_JSON)
                .bindingMode(RestBindingMode.json)
                .type(Book.class)
                .enableCORS(true)
                .to("direct:remoteService");



        from("direct:remoteService")
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
                }).to("direct:confirm");

        JacksonDataFormat jsonDataFormat = new JacksonDataFormat(Book.class);
        final String confirmationUrl = "http://localhost:" + bookServicePort + "/confirm?bridgeEndpoint=true&amp;throwExceptionOnFailure=false";

        from("direct:confirm")
                .description("Calling Confirmation HTTP Service")
                .routeId("confirm-route")
                .tracing()
                .log(">>>id  >>> ${body.id}")
                .log(">>>name>>> ${body.name}")
                .process(new Processor() {
                    @Override
                    public void process(Exchange exchange) throws Exception {
                        Book confirmBook = (Book) exchange.getIn().getBody();
                        ValidateService.confirmBook(confirmBook);
                        exchange.getIn().setBody(confirmBook);
                    }
                });
    }
}
