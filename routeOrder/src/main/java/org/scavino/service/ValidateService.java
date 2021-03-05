package org.scavino.service;

import org.scavino.model.Book;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class ValidateService {

    public Book validateBook(Book book) {
        System.out.println("ValidateService BEGIN : " + book);
        book.setIsbn("ISBN-0001");
        book.setPrice(BigDecimal.valueOf(100.00));
        System.out.println("ValidateService END : " + book);

        return book;
    }

}
