package com.example.ecommerce.domain.cart;

import com.example.ecommerce.domain.value.Money;

import java.util.UUID;

/**
 * Item do carrinho (parte do agregado Cart) - imutável.
 * Bom exemplo: tipo record para reduzir boilerplate.
 */
public record CartItem(UUID productId, String productName, Money unitPrice, int quantity) {
    public CartItem {
        if (productId == null) throw new IllegalArgumentException("productId required");
        if (productName == null || productName.isBlank()) throw new IllegalArgumentException("productName required");
        if (unitPrice == null) throw new IllegalArgumentException("unitPrice required");
        if (quantity <= 0) throw new IllegalArgumentException("quantity must be > 0");
    }

    public Money lineTotal() { return unitPrice.times(quantity); }
}
