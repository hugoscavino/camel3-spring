package com.merchant.repository;

import com.merchant.entity.OrderConfirmationEntity;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDate;
import java.util.List;

/**
 * Simple JPA CrudRepository for the OrderConfirmationEntity
 */
public interface OrderRepository extends CrudRepository<OrderConfirmationEntity, Long> {

    List<OrderConfirmationEntity> findByOrderDate(LocalDate localDate);

    OrderConfirmationEntity findById(long id);
}