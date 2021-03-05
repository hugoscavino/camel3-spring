package com.factory.model;

import java.time.LocalDate;
import java.util.StringJoiner;

public class OrderConfirmation {

    private String orderId;
    private Long productId;
    private LocalDate orderDate;
    private Book book;

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public LocalDate getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDate orderDate) {
        this.orderDate = orderDate;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", OrderConfirmation.class.getSimpleName() + "[", "]")
                .add("orderId=" + orderId)
                .add("productId=" + productId)
                .add("orderDate=" + orderDate)
                .toString();
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }
}
