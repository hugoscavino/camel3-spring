package com.merchant.entity;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.StringJoiner;

@Entity
public class OrderConfirmationEntity {

    @Id
    @Column(name = "id")
    private String orderId;
    private LocalDate orderDate;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinTable(name = "order_book",
            joinColumns =
                    { @JoinColumn(name = "order_id", referencedColumnName = "id") },
            inverseJoinColumns =
                    { @JoinColumn(name = "book_id", referencedColumnName = "id") })
    private BookEntity book;

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

    public BookEntity getBook() {
        return book;
    }

    public void setBook(BookEntity book) {
        this.book = book;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", OrderConfirmationEntity.class.getSimpleName() + "[", "]")
                .add("orderId='" + orderId + "'")
                .add("orderDate=" + orderDate)
                .add("book=" + book)
                .toString();
    }
}
