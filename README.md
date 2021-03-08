# camel3-spring example project
## About / Synopsis

* This was my way to learn about Spring Boot 2.x and Camel 3.x. I created an ultra simple
  front-end so a fake user can enter a book title and then in have camel routes process the
  order calling separate HTTP services along the way
* Project status: working/sample
* No Support

I reviewed these guides to help along the way, the problem with these were 
still on version 2 or not using Spring Boot 2.x. I wanted to find a tutorial
using version 3.8 (the latest as of March 7) and Spring Boot 2.3.4 (again the
latest on March 7, 2021). Ultimately, I combined many of the below concepts with 
the camel example code which had a working pom.xml and spring boot integration:

* <https://github.com/apache/camel-spring-boot-examples/tree/master/spring-boot/src/main/java/sample/camel>
* <https://www.baeldung.com/spring-apache-camel-tutorial>
* <https://camel.apache.org/manual/latest/walk-through-an-example.html>
* <https://www.javainuse.com/spring/bootcamel>

## Book Odering Service and UI
This is the fake UI that starts the Camel router. It posts a Book.class with just a
title. The rest of the process will create the Order Confirmation and update the remaining
attributes.

![Demo UI Screenshot](./demo-ui.png?raw=true "Demo UI")

    public class Book {
      private Long id;
      private String title;
      private String translatedTitle; // Updated by external Translation Service
      private String isbn;
      private BigDecimal price;

Posts to defined camel API context. I changed the default context from /camel to /api

    route-service:
      server: localhost
        port: 8080
          context-root: /api/router/book-router

    # In the routeOrder module in the application.properties file
    # to reconfigure the camel servlet context-path mapping to use /api/* instead of /camel/*
    camel.component.servlet.mapping.context-path=/api/*

This service also takes the final OrderConfirmation.class and saves it to an H2 database
to persist the results. In the next version of this example, I will have camel save the
order confirmation rather than have the UI perform that duty.

The UI is based on the most minimal ThymeLeaf and bootstrap pages. There is nothing fancy
here.


## Mock Services

### External Translate Service
#### port 8082
This service pretends to translate the book's title to another language like Spanish 
in this case. I considered using an actual translation service from Google, AWS or Microsoft
but each of these had some cost associated with them and in the end did not feel it was worth
the effort. Feel free to extend this service.

    @GetMapping("/translate")
    
    See the @Service class BabelFish for the fake translation implementation


### External Print Service
#### port 8083
This fake service takes the OrderConfirmation class and "Prints" it for the user.
The import part here is "Camel" calls POST on this REST API with a Book.class and
retrieves an OrderConfirmation.class. This was challenging as I needed to create and
register a TypeConverter in a way camel 3.0 would recognize in a Spring Boot context.

    public class OrderConfirmation {
      private String orderId;
      private LocalDate orderDate;
      private Book book;

## Camel Router
### default port 8080
This module is the actual code example. There were lot os issues resolved along the way 
in order to use the 3.8 version and SpringBoot 2.x.x

### TypeConverters:
I am still not sure why I had to implement and configure my own TypeConverter to convert
the ${body} from InputStream into a viable object. Need to perhaps use the marashl() or
unmarshall() jacskson methods.

#### TypeConverters
    
    org.scavino.converters.BookTypeConverter

In the early 3.x version of Camel I was able to install these using the META-INF solution. 
This stopped working in version 3.7/3.8 and I instead had to get an instance of the Camel
Context and add the BookTypeConverter.class to the registry

Worked before 3.7 and had to switch to using the CamelContext

    routeOrder/src/main/resources/META-INF/services/org/apache/camel

New TypeConverter registration process in Camel 3.8+
    
    @Autowired
    private CamelContext camelContext;

    @Autowired
    private BookTypeConverter bookTypeConverter;

    ...
    camelContext.getTypeConverterRegistry().addTypeConverters(bookTypeConverter);


From the console out in start up. Once I saw this in the console I new my TypeConverter 
was going to work. 

    WARN  o.a.c.i.c.CoreTypeConverterRegistry - Overriding type converter from: InstanceMethodTypeConverter: public java.io.InputStream org.scavino.converters.BookTypeConverter.orderConfirmationToInputStream(org.scavino.model.OrderConfirmation) to: InstanceMethodTypeConverter: public java.io.InputStream org.scavino.converters.BookTypeConverter.orderConfirmationToInputStream(org.scavino.model.OrderConfirmation)
    WARN  o.a.c.i.c.CoreTypeConverterRegistry - Overriding type converter from: InstanceMethodTypeConverter: public org.scavino.model.OrderConfirmation org.scavino.converters.BookTypeConverter.inputStreamToOrderConfirmation(java.io.InputStream) to: InstanceMethodTypeConverter: public org.scavino.model.OrderConfirmation org.scavino.converters.BookTypeConverter.inputStreamToOrderConfirmation(java.io.InputStream)
    WARN  o.a.c.i.c.CoreTypeConverterRegistry - Overriding type converter from: InstanceMethodTypeConverter: public org.scavino.model.Book org.scavino.converters.BookTypeConverter.inputStreamToBook(java.io.InputStream) to: InstanceMethodTypeConverter: public org.scavino.model.Book org.scavino.converters.BookTypeConverter.inputStreamToBook(java.io.InputStream)
    WARN  o.a.c.i.c.CoreTypeConverterRegistry - Overriding type converter from: InstanceMethodTypeConverter: public java.io.InputStream org.scavino.converters.BookTypeConverter.bookToInputStream(org.scavino.model.Book) to: InstanceMethodTypeConverter: public java.io.InputStream org.scavino.converters.BookTypeConverter.bookToInputStream(org.scavino.model.Book)
22:0

### Processors
#### PrintBodyAsStringProcessor
This was created for debugging. Converts the payload to a String and then prints it out
to System.out.println

        Message message = exchange.getIn();
        String json = MessageHelper.extractBodyAsString(message);
        System.out.println(json);

#### PrintBookProcessor
This processor takes the saved ${body} which as Book.class coming into the router
and attaches it to OrderConfirmation.class coming from the Print Service

        OrderConfirmation orderConfirmation = exchange.getIn().getBody(OrderConfirmation.class);
        Book book = (Book)exchange.getProperty("BOOK_ID");
        orderConfirmation.setBook(book);
        message.setBody(orderConfirmation);

#### TranslateProcessor
This processor takes the JSON result from the call to the external translation 
service and then unmarshalls the result to a Translation.class. this class the 
Spanish version of the Title.

        Message message = exchange.getIn();
        String json = MessageHelper.extractBodyAsString(message);
        Translation translation = objectMapper.readValue(json, Translation.class );

        Book book = (Book)exchange.getProperty("BOOK_ID");
        book.setTranslatedTitle(translation.getTitle());
        message.setBody(book);

#### ValidateProcessor
This validation processor uses the internal validation service and attaches the
isbn, and price to the Book.class. This really should not live in the router module
and should be moved out of this camel router and made its own separate serivce. I just 
got tired of creating Spring Boot applications and running instances.

    @Component
    public class ValidateProcessor implements Processor  {

      private final ObjectMapper objectMapper;
      private final ValidateService validateService;
      ...
      public void process(Exchange exchange) throws Exception {
          Message message = exchange.getIn();
          Book book = message.getBody(Book.class);
          Book validatedBook = validateService.validateBook(book);
          message.setBody(validatedBook);
      }
    
#### undertow instead of tomcat
The sample program I copied used undertow instead of tomcat in the Spring Boot pom.xml. I 
did not want to fight the example from Camel so I just kept it. Which meant my 
SpringApplication class did not need the extra tomcat Servlet configuration.
    
From console

    INFO  o.s.b.w.e.undertow.UndertowWebServer - Undertow started on port(s) 8080 (http)

From pom.xml

    <dependencies>
        <!-- Spring Boot -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
            <exclusions>
                <exclusion>
                    <groupId>org.springframework.boot</groupId>
                    <artifactId>spring-boot-starter-tomcat</artifactId>
                </exclusion>
            </exclusions>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-undertow</artifactId>
        </dependency>

#### ObjectMapper
See the pom.xml to see how to get all the Jackson JARs setup so that the rest and http
components would work as expected. I am still not sure I got everything correct so will
be experimenting here. This the output you want in the console otherwise you are
stuck using the default ObjectMapper. For me that was not going to work as I need to
convert LocalDate to String and not a TimeStamp and had to load my own ObjectMapper which
is the same one @Autowired throughout the appication. 

##### application.properties
    camel.dataformat.json-jackson.auto-discover-object-mapper=true

##### console output
    o.a.c.c.jackson.JacksonDataFormat - The objectMapper was already found in the registry, no customizations will be applied
    o.a.c.c.jackson.JacksonDataFormat - Found single ObjectMapper in Registry to use: com.fasterxml.jackson.databind.ObjectMapper@d76099a
    o.a.c.c.jackson.JacksonDataFormat - The objectMapper was already found in the registry, no customizations will be applied
    o.a.c.c.jackson.JacksonDataFormat - Found single ObjectMapper in Registry to use: com.fasterxml.jackson.databind.ObjectMapper@d76099a
    o.a.c.c.jackson.JacksonDataFormat - The objectMapper was already found in the registry, no customizations will be applied

#### Routes
From the console startup

    AbstractCamelContext - Routes startup summary (total:5 started:5)
    AbstractCamelContext - 	Started translation-route (direct://translateService)
    AbstractCamelContext - 	Started validation-route (direct://validateService)
    AbstractCamelContext - 	Started print-book-route (direct://printBook)
    AbstractCamelContext - 	Started doc-api (rest-api:///api-doc)
    AbstractCamelContext - 	Started route1 (rest://post:/router/:/book-router)

### Swagger API Route
There are critical JARs to add to your pom.xml to make the below work. You can see the
non-child module SwaggerCamelRoute which I cloned from the Camel examples repository.

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

This is the JSON output when invoking the URL

* <http://localhost:8080/api/api-doc>


    {
      "swagger" : "2.0",
      "info" : {
        "version" : "v1.0",
        "title" : "Swagger Router Rest API"
      },
      "host" : "localhost:8080",
      "tags" : [ {
        "name" : "router/",
        "description" : "Book Ordering REST Service"
      } ],
      "schemes" : [ "http" ],
      "paths" : {
        "/router/book-router" : {
          "post" : {
            "tags" : [ "router/" ],
            "operationId" : "verb1",
            "consumes" : [ "application/json" ],
            "produces" : [ "application/json" ],
            "parameters" : [ {
              "in" : "body",
              "name" : "body",
              "required" : true,
              "schema" : {
                "$ref" : "#/definitions/Book"
              }
            } ],
            "responses" : {
              "200" : {
                "description" : "Output type",
                "schema" : {
                  "$ref" : "#/definitions/OrderConfirmation"
                }
              }
            }
          }
        }
      },

#### Rest Component
Rather than reading file or MQ we are using the REST component from camel so clients can
POST REST messages to us. This API is expecting a Book.class and sending back and
OrderConfirmation.class from the last step in the route. Note the binding mode is
RestBindingMode.json

        // REST Component
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

#### Translation Route
This service is the first one in the route. It calls the translation service to translate 
the title to Spanish. Note this is using the http component to call the external REST API.
The service returns an Translation.class from the call.

* <https://stackoverflow.com/questions/9194720/apache-camel-how-store-variable-for-later-use>

Note how we pass a Query param to the GET method.

    .setHeader(Exchange.HTTP_QUERY, simple("title=${body.title}&language=es"))

We have to remove it at the end otherwise it sticks around

    .removeHeader(Exchange.HTTP_QUERY) // Don't forget to remove this header

The translation route also saves off the original Book.class into a property!

        from("direct:translateService")
                .description("Calling ValidateService")
                .routeId("translation-route")
                .setProperty("BOOK_ID", simple("${body}"))
                .setHeader(Exchange.HTTP_METHOD, constant("GET"))
                .setHeader(Exchange.CONTENT_TYPE, constant("application/json"))
                .setHeader(Exchange.HTTP_QUERY, simple("title=${body.title}&language=es"))
                .to(translateHttp)
                .process(translateProcessor)
                .to("direct:validateService")
                .removeHeader(Exchange.HTTP_QUERY) // Don't forget to remove this header
                ;

#### Validation Route and Service
I got tired of creating SpringBoot applications. I just embedded the validation service
in the Camel module itself. This really should be an external service.

        from("direct:validateService")
                .description("Calling ValidateService")
                .routeId("validation-route")
                 ...
                .noStreamCaching()
                .process(validateProcessor)
                .to("direct:printBook");

#### Print Service - Last Step
This router calls another external http service (POST) to print the book. This service
is the one that directly returns the OrderConfirmation.class back to the UI. This
may be too coupled in real life but for this example works for me.

        from("direct:printBook")
                .description("Calling Print Book Service")
                .routeId("print-book-route")
                .setHeader(Exchange.HTTP_METHOD, constant("POST"))
                .setHeader(Exchange.CONTENT_TYPE, constant("application/json"))
                .to(printBookServiceConfigUrl)
                .unmarshal().json(JsonLibrary.Jackson, OrderConfirmation.class)
                .process(printBookProcessor)

### Build

    mvn clean install

## License

[Apache License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0.html)

## About Hugo Scavino
I am a full stack developer and I like to teach myself these technologies in order
to jump start my teams.
