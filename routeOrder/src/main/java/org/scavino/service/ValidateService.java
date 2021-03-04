package org.scavino.service;

import org.scavino.model.Book;

public class ValidateService {

    public static void validateBook(Book book) {
        System.out.println("ValidateService BEGIN : " + book);

        book.setName( "Validated Title: " + book.getName() );
        book.setId(book.getId() + 100000);

        System.out.println("ValidateService END : " + book);
    }

}
