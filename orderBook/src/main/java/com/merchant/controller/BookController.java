package com.merchant.controller;

import com.merchant.config.RouteServiceConfig;
import com.merchant.entity.OrderConfirmationEntity;
import com.merchant.model.DtoUtil;
import com.merchant.model.OrderConfirmation;
import com.merchant.repository.OrderRepository;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

import java.time.LocalTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Controller
public class BookController {


    private final RouteServiceConfig routeServiceConfig;
    private final OrderRepository orderRepository;

    private final RestTemplate restTemplate;

    public BookController(RouteServiceConfig routeServiceConfig,
                          OrderRepository orderRepository,
                          RestTemplateBuilder restTemplateBuilder) {
        this.routeServiceConfig = routeServiceConfig;
        this.orderRepository = orderRepository;
        this.restTemplate = restTemplateBuilder.build();
    }

    /**
     * Do a POST request to http://localhost:8080/api/routers/book-router
     * with header parameters: Content-Type: application/json,
     * and a payload {“id”: 1,”title”: “World”}
     *
     * @param title for Book
     * @return OrderConfirmation
     */
    @GetMapping("/order-book")
    public String OrderBook(@RequestParam(name = "title")  String title, Model model) {

        final String url = "http://"
                            + routeServiceConfig.getServer() + ":"
                            + routeServiceConfig.getPort()
                            + routeServiceConfig.getContextRoot();

        // create headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        // create a map for post parameters
        Map<String, Object> map = new HashMap<>();
        map.put("id", LocalTime.now().toSecondOfDay());
        map.put("title", title);

        // build the request
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(map, headers);
        // send POST request
        ResponseEntity<OrderConfirmation> response = this.restTemplate.postForEntity(url, entity, OrderConfirmation.class);

        // Add This Oder Confirmation to Model
        final OrderConfirmation orderConfirmation = response.getBody();
        if (orderConfirmation != null){
            orderRepository.save(DtoUtil.toOrderConfirmationEntity(orderConfirmation));
        }

        // Get All from database and add to model
        final Iterable<OrderConfirmationEntity> orderConfirmationEntities = orderRepository.findAll();

        // check response status code
        if (response.getStatusCode() == HttpStatus.OK) {
            model.addAttribute("order", orderConfirmation);
            model.addAttribute("orderConfirmations", orderConfirmationEntities);
            return "confirmation";
        } else {
            return "500";
        }
    }


}
