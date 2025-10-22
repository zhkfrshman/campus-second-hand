package com.softwareengineering.team.campussecondhand.repository;

import com.softwareengineering.team.campussecondhand.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUidOrderByCreatedAtDesc(Long uid);
}