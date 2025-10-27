package com.softwareengineering.team.campussecondhand.repository;

import com.softwareengineering.team.campussecondhand.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    List<CartItem> findByUid(Long uid);
    Optional<CartItem> findByUidAndSid(Long uid, Long sid);
    void deleteByUid(Long uid); // 新增：按用户清空购物车
}
