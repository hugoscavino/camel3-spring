package com.factory.entity;

import com.factory.model.Book;

public final class EntityUtils {

    private EntityUtils(){}

    public static BookEntity toEntityDto(Book book){
        BookEntity bookEntity = new BookEntity(book.getId().longValue(),
                                                book.getTitle(),
                                                book.getTranslatedTitle(),
                                                book.getIsbn(),
                                                book.getPrice());
        return bookEntity;
    }

    public static Book toEntityDto(BookEntity book){
        Book bookDto = new Book(
                book.getId(),
                book.getTitle(),
                book.getTranslatedTitle(),
                book.getIsbn(),
                book.getPrice());
        return bookDto;
    }
}
