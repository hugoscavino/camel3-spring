package com.factory.controller;

import com.factory.model.Book;
import com.factory.model.OrderConfirmation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
public class PrintBookController {

    @PostMapping("/print")
    @ResponseBody
    public OrderConfirmation printBook(@RequestBody Book book) {
        OrderConfirmation orderConfirmation = new OrderConfirmation();
        orderConfirmation.setBook(book);
        orderConfirmation.setOrderId("PRINT-ORDER-" + book.getId());
        orderConfirmation.setOrderDate(LocalDate.now());
        // TODO Do something interesting here
        return orderConfirmation;
    }

}
