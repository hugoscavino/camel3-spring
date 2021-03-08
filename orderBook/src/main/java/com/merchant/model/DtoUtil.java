package com.merchant.model;

import com.merchant.entity.BookEntity;
import com.merchant.entity.OrderConfirmationEntity;

public final class DtoUtil {

    private DtoUtil(){}

    public static OrderConfirmationEntity toOrderConfirmationEntity(OrderConfirmation orderConfirmation){

        OrderConfirmationEntity orderConfirmationEntity = new OrderConfirmationEntity();

        orderConfirmationEntity.setOrderId(orderConfirmation.getOrderId());
        orderConfirmationEntity.setOrderDate(orderConfirmation.getOrderDate());
        orderConfirmationEntity.setBook(toBookEntity(orderConfirmation.getBook()));
        return orderConfirmationEntity;

    }

    public static BookEntity toBookEntity(Book book){
        BookEntity bookEntity = new BookEntity();
        bookEntity.setId(book.getId());
        bookEntity.setIsbn(book.getIsbn());
        bookEntity.setTitle(book.getTitle());
        bookEntity.setTranslatedTitle(book.getTranslatedTitle());
        bookEntity.setPrice(book.getPrice());
        return bookEntity;

    }
}
