package com.softwareengineering.team.campussecondhand.repository;

import com.softwareengineering.team.campussecondhand.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    Page<Product> findByDisplay(Integer display, Pageable pageable);
    Page<Product> findByNameContainingAndDisplay(String name, Integer display, Pageable pageable);
    List<Product> findByUid(Long uid);

    Page<Product> findByDisplayAndCountGreaterThan(Integer display, Integer count, Pageable pageable);
    // 默认按 id 倒序（避免实体无 createdAt 导致排序属性不存在）
    default List<Product> findByDisplayOrderByCreatedAtDesc(boolean display) {
        return findByDisplay(display ? 1 : 0,
                org.springframework.data.domain.PageRequest.of(
                        0, 1000,
                        org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.DESC, "id")
                )
        ).getContent();
    }
    @Query("""
           select p from Product p
           where p.display = 1
             and (p.count is null or p.count > 0)
             and (:q is null or :q = '' 
                  or lower(p.name) like lower(concat('%', :q, '%'))
                  or lower(p.remark) like lower(concat('%', :q, '%')))
           """)
    Page<Product> searchAvailable(@Param("q") String q, Pageable pageable);
}