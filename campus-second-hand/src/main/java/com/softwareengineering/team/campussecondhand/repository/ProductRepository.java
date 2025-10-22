package com.softwareengineering.team.campussecondhand.repository;

import com.softwareengineering.team.campussecondhand.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    Page<Product> findByDisplay(Integer display, Pageable pageable);
    Page<Product> findByNameContainingAndDisplay(String name, Integer display, Pageable pageable);
    List<Product> findByUid(Long uid);
    default List<Product> findByDisplayOrderByCreatedAtDesc(boolean display) {
        return findByDisplay(display ? 1 : 0, org.springframework.data.domain.PageRequest.of(0, 1000, 
               org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.DESC, "createdAt")))
               .getContent();
    }
}