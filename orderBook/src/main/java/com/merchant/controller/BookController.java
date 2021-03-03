package com.merchant.controller;

import com.merchant.model.Book;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.time.LocalTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@RestController
public class BookController {

    @Value("${camel-router.port}")
    private String serverPort;


    private final RestTemplate restTemplate;

    public BookController(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }

    /**
     * Do a POST request to http://localhost:8080/camel/api/bean
     * with header parameters: Content-Type: application/json,
     * and a payload {“id”: 1,”name”: “World”}
     *
     * @param name for the MyBean
     * @return MyBean
     */
    @GetMapping("/order-book")
    @ResponseBody
    public Book OrderBook(@RequestParam(name = "name")  String name) {
        final String url = "http://localhost:" + serverPort + "/camel/api/order-router";

        // create headers
        HttpHeaders headers = new HttpHeaders();

        // set `content-type` header
        headers.setContentType(MediaType.APPLICATION_JSON);

        // set `accept` header
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        // create a map for post parameters
        Map<String, Object> map = new HashMap<>();
        map.put("id", LocalTime.now().toSecondOfDay());
        map.put("name", name);

        // build the request
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(map, headers);

        // send POST request
        ResponseEntity<Book> response = this.restTemplate.postForEntity(url, entity, Book.class);

        // check response status code
        if (response.getStatusCode() == HttpStatus.OK) {
            return response.getBody();
        } else {
            return null;
        }
    }

    @PostMapping("/confirm")
    @ResponseBody
    public Book ConfirmBook(@RequestBody Book book) {
        System.out.println("Book Confirmed : " + book);
        return book;
    }

}