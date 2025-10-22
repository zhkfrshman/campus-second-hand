package com.softwareengineering.team.campussecondhand.repository;

import com.softwareengineering.team.campussecondhand.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    List<OrderItem> findByOrderId(Long orderId);
}