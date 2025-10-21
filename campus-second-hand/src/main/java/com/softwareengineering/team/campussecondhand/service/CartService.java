package com.softwareengineering.team.campussecondhand.service;

import com.softwareengineering.team.campussecondhand.entity.CartItem;

import java.util.List;

public interface CartService {
    void addToCart(Long uid, Long sid, int qty);
    List<CartItem> getCartItems(Long uid);
    void removeItem(Long id);
    void clearCart(Long uid);
}
