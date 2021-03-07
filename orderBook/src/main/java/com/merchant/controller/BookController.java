package com.merchant.controller;

import com.merchant.model.Book;
import com.merchant.model.OrderConfirmation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
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
     * @param title for Book
     * @return OrderConfirmation
     */
    @GetMapping("/order-book")
    @ResponseBody
    public OrderConfirmation OrderBook(@RequestParam(name = "title")  String title) {
        final String url = "http://localhost:" + serverPort + "/api/router/book-router";

        // create headers
        HttpHeaders headers = new HttpHeaders();

        // set `content-type` header
        headers.setContentType(MediaType.APPLICATION_JSON);

        // set `accept` header
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        // create a map for post parameters
        Map<String, Object> map = new HashMap<>();
        map.put("id", LocalTime.now().toSecondOfDay());
        map.put("title", title);

        // build the request
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(map, headers);

        // send POST request
        ResponseEntity<OrderConfirmation> response = this.restTemplate.postForEntity(url, entity, OrderConfirmation.class);
        //ResponseEntity<String> response = this.restTemplate.postForEntity(url, entity, String.class);

        // check response status code
        if (response.getStatusCode() == HttpStatus.OK) {
            return response.getBody();
            //return null;
        } else {
            return null;
        }
    }

    @PostMapping("/confirm")
    @ResponseBody
    public OrderConfirmation ConfirmBook(@RequestBody Book book) {
        OrderConfirmation orderConfirmation = new OrderConfirmation();
        orderConfirmation.setOrderId("ORDER-" + book.getId());
        orderConfirmation.setProductId(book.getId());
        orderConfirmation.setOrderDate(LocalDate.now());
        return orderConfirmation;
    }

}
