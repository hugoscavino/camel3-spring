package com.factory.controller;


import com.factory.entity.BookEntity;
import com.factory.entity.EntityUtils;
import com.factory.model.Book;
import com.factory.model.OrderConfirmation;
import com.factory.repository.BookRepository;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
public class PrintBookController {

    private final BookRepository repository;

    public PrintBookController(BookRepository repository) {
        this.repository = repository;
    }

    @PostMapping("/print")
    @ResponseBody
    public OrderConfirmation printBook(@RequestBody Book book) {
        OrderConfirmation orderConfirmation = new OrderConfirmation();
        orderConfirmation.setBook(book);
        orderConfirmation.setOrderId("ORDER-" + book.getId());
        orderConfirmation.setOrderDate(LocalDate.now());

        final BookEntity bookEntity = EntityUtils.toEntityDto(orderConfirmation.getBook());
        repository.save(bookEntity);
        return orderConfirmation;
    }

}
