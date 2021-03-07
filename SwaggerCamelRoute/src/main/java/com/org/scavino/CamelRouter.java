package com.org.scavino;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;
import org.springframework.stereotype.Component;

import static org.apache.camel.model.rest.RestParamType.body;
import static org.apache.camel.model.rest.RestParamType.path;
/**
 * A simple Camel REST DSL route with Swagger API documentation.
 */
@Component
public class CamelRouter extends RouteBuilder {

    @Override
    public void configure() throws Exception {

        // http://localhost:8080/camel/api-doc
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

        // http://localhost:8080/api/users
        rest("/users").description("User REST service")
                .consumes("application/json")
                .produces("application/json")
                .get().description("Find all users").outType(User[].class)
                .responseMessage().code(200).message("All users successfully returned").endResponseMessage()
                .to("bean:userService?method=findUsers")
                .get("/{id}").description("Find user by ID")
                .outType(User.class)
                .param().name("id").type(path).description("The ID of the user").dataType("integer").endParam()
                .responseMessage().code(200).message("User successfully returned").endResponseMessage()
                .to("bean:userService?method=findUser(${header.id})")

                .put("/{id}").description("Update a user").type(User.class)
                .param().name("id").type(path).description("The ID of the user to update").dataType("integer").endParam()
                .param().name("body").type(body).description("The user to update").endParam()
                .responseMessage().code(204).message("User successfully updated").endResponseMessage()
                .to("direct:update-user");

        from("direct:update-user")
                .to("bean:userService?method=updateUser")
                .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(204))
                .setBody(constant(""));

    }


}