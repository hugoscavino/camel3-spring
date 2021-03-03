package org.scavino.service;

import org.scavino.model.Book;

public class ValidateService {

    public static void validateBook(Book book) {
        System.out.println("ValidateService BEGIN processing : " + book);

        book.setName( "Title: " + book.getName() );
        book.setId(book.getId() + 10);

        System.out.println("ValidateService END processing : " + book);
    }
}
