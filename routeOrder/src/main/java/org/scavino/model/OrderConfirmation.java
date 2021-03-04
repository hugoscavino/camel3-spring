package org.scavino.model;

import java.time.LocalDate;
import java.util.StringJoiner;

public class OrderConfirmation {

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

    @Override
    public String toString() {
        return new StringJoiner(", ", OrderConfirmation.class.getSimpleName() + "[", "]")
                .add("orderId=" + orderId)
                .add("productId=" + productId)
                .add("orderDate=" + orderDate)
                .toString();
    }
}
