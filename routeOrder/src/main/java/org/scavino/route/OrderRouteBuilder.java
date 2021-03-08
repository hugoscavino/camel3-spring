package org.scavino.route;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;
import org.scavino.config.BookServiceConfig;
import org.scavino.config.PrintBookServiceConfig;
import org.scavino.config.TranslateServiceConfig;
import org.scavino.converters.BookTypeConverter;
import org.scavino.model.Book;
import org.scavino.model.OrderConfirmation;
import org.scavino.processor.PrintBodyAsStringProcessor;
import org.scavino.processor.PrintBookProcessor;
import org.scavino.processor.TranslateProcessor;
import org.scavino.processor.ValidateProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component


class OrderRouteBuilder extends RouteBuilder {

    final public String bridgeParams = "?bridgeEndpoint=true&amp;throwExceptionOnFailure=false";

    @Autowired
    private final CamelContext camelContext;

    private final BookTypeConverter bookTypeConverter;
    private final BookServiceConfig bookServiceConfig;
    private final TranslateServiceConfig translateServiceConfig;
    private final PrintBookServiceConfig printBookServiceConfig;

    private final PrintBodyAsStringProcessor printBodyAsStringProcessor;
    private final PrintBookProcessor printBookProcessor;
    private final TranslateProcessor translateProcessor;
    private final ValidateProcessor validateProcessor;

    public OrderRouteBuilder(CamelContext camelContext,
                             BookTypeConverter bookTypeConverter,
                             BookServiceConfig bookServiceConfig,
                             TranslateServiceConfig translateServiceConfig,
                             PrintBookServiceConfig printBookServiceConfig,
                             PrintBodyAsStringProcessor printBodyAsStringProcessor,
                             PrintBookProcessor printBookProcessor,
                             TranslateProcessor translateProcessor,
                             ValidateProcessor validateProcessor){
        this.camelContext = camelContext;
        this.bookTypeConverter = bookTypeConverter;
        this.bookServiceConfig = bookServiceConfig;
        this.translateServiceConfig = translateServiceConfig;
        this.printBookServiceConfig = printBookServiceConfig;
        this.printBodyAsStringProcessor = printBodyAsStringProcessor;
        this.printBookProcessor = printBookProcessor;
        this.translateProcessor = translateProcessor;
        this.validateProcessor = validateProcessor;
    }

    @Override
    public void configure() {

        camelContext.setStreamCaching(false);
        camelContext.getTypeConverterRegistry().addTypeConverters(bookTypeConverter);

        // http://localhost:8080/api/api-doc
        restConfiguration()
                .enableCORS(true)
                .apiContextPath("/api-doc")
                .apiProperty("api.title", "Swagger Router Rest API")
                .apiProperty("api.version", "v1.0")
                .apiProperty("cors", "true") // cross-site
                .apiContextRouteId("doc-api")
                .component("servlet")
                .bindingMode(RestBindingMode.json)
                .dataFormatProperty("prettyPrint", "true");

        // REST INGESTOR
        rest("/router/")
                .description("Book Ordering REST Service")
                .id("api-route")
                .enableCORS(true)
                .bindingMode(RestBindingMode.json)
                .post("/book-router")
                .produces("application/json")
                .consumes("application/json")
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
                .noStreamCaching()
                .process(printBodyAsStringProcessor)
                .process(validateProcessor)
                .process(printBodyAsStringProcessor)
                .to("direct:printBook");


        // Print Book SERVICE
        final String printBookServiceConfigUrl = "http://" +
                printBookServiceConfig.getServer() + ":" +
                printBookServiceConfig.getPort() +
                printBookServiceConfig.getContextRoot() +
                bridgeParams;

        from("direct:printBook")
                .description("Calling Print Book Service")
                .routeId("print-book-route")
                .tracing()
                .log(">>>Print Book  >>> ${body}")
                .process(printBodyAsStringProcessor)
                .setHeader(Exchange.HTTP_METHOD, constant("POST"))
                .setHeader(Exchange.CONTENT_TYPE, constant("application/json"))
                .to(printBookServiceConfigUrl)
                .process(printBookProcessor)
                .process(printBodyAsStringProcessor)
                .outputType(OrderConfirmation.class)
                .log(">>>OrderConfirmation  >>> ${body}")
                ;
    }
}
