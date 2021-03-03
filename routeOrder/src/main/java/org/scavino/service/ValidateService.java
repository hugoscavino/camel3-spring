package org.scavino.service;

import org.scavino.model.Book;

public class ValidateService {

    public static void validateBook(Book book) {
        System.out.println("validateBook BEGIN processing : " + book);

        book.setName( "Title: " + book.getName() );
        book.setId(book.getId() + 10);

        System.out.println("validateBook END processing : " + book);
    }

    public static void confirmBook(Book book) {
        System.out.println("confirmBook BEGIN processing : " + book);

        book.setName( "Confirmed Title: " + book.getName() );
        book.setId(book.getId() + 20);

        System.out.println("confirmBook END processing : " + book);
    }
}
