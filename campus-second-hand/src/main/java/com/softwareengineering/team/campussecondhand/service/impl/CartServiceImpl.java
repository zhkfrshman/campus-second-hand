package com.softwareengineering.team.campussecondhand.service.impl;

import com.softwareengineering.team.campussecondhand.entity.CartItem;
import com.softwareengineering.team.campussecondhand.repository.CartItemRepository;
import com.softwareengineering.team.campussecondhand.service.CartService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CartServiceImpl implements CartService {

    private final CartItemRepository cartItemRepository;

    public CartServiceImpl(CartItemRepository cartItemRepository) {
        this.cartItemRepository = cartItemRepository;
    }

    @Override
    @Transactional
    public void addToCart(Long uid, Long sid, int qty) {
        if (uid == null || sid == null) throw new IllegalArgumentException("uid 和 sid 不能为空");
        var existing = cartItemRepository.findByUidAndSid(uid, sid);
        if (existing.isPresent()) {
            CartItem ci = existing.get();
            ci.setQuantity(ci.getQuantity() + qty);
            cartItemRepository.save(ci);
        } else {
            CartItem ci = new CartItem();
            ci.setUid(uid);
            ci.setSid(sid);
            ci.setQuantity(qty);
            cartItemRepository.save(ci);
        }
    }

    @Override
    public List<CartItem> getCartItems(Long uid) {
        return cartItemRepository.findByUid(uid);
    }

    @Override
    public void removeItem(Long id) {
        cartItemRepository.deleteById(id);
    }

    @Override
    public void clearCart(Long uid) {
        var items = cartItemRepository.findByUid(uid);
        cartItemRepository.deleteAll(items);
    }
}
