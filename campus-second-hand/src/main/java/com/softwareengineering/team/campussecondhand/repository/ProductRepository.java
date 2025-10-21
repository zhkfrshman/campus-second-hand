package com.softwareengineering.team.campussecondhand.repository;

import com.softwareengineering.team.campussecondhand.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
    Page<Product> findByDisplay(Integer display, Pageable pageable);
    Page<Product> findByNameContainingAndDisplay(String name, Integer display, Pageable pageable);
}