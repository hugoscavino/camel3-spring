package com.merchant.controller;

import com.merchant.entity.OrderConfirmationEntity;
import com.merchant.model.Book;
import com.merchant.repository.OrderRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class IndexController {

    private final OrderRepository orderRepository;

    public IndexController(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }


    @GetMapping("/")
    public String index(Model model) {

        final Iterable<OrderConfirmationEntity> orderConfirmations = orderRepository.findAll();

        model.addAttribute("book", new Book());
        model.addAttribute("orderConfirmations", orderConfirmations);

        // return view name
        return "index";
    }
}
