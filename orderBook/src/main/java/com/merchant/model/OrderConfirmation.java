package com.merchant.model;

import java.time.LocalDate;
import java.util.StringJoiner;

public class OrderConfirmation {
    private Book book;
    private String orderId;
    private Integer productId;
    private LocalDate orderDate;

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
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
                .add("book=" + book)
                .add("orderId='" + orderId + "'")
                .add("productId=" + productId)
                .add("orderDate=" + orderDate)
                .toString();
    }
}
