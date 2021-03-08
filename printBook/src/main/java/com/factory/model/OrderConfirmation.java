package com.factory.model;

import java.time.LocalDate;
import java.util.StringJoiner;
/**
 * com.factory version of the OrderConfirmation PoJo
 */
public class OrderConfirmation {

    private String orderId;
    private LocalDate orderDate;
    private Book book;

    public String getOrderId() {
        return orderId;
    }
    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }
    public LocalDate getOrderDate() {
        return orderDate;
    }
    public void setOrderDate(LocalDate orderDate) {
        this.orderDate = orderDate;
    }
    public Book getBook() {
        return book;
    }
    public void setBook(Book book) {
        this.book = book;
    }


    @Override
    public String toString() {
        return new StringJoiner(", ", OrderConfirmation.class.getSimpleName() + "[", "]")
                .add("orderId='" + orderId + "'")
                .add("orderDate=" + orderDate)
                .add("book=" + book)
                .toString();
    }
}
