package com.example.ecommerce.domain.cart;

import com.example.ecommerce.domain.product.Product;
import com.example.ecommerce.domain.value.Money;

import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

/**
 * Agregado Carrinho com regras de negócio básicas.
 * Obs: Implementação propositalmente mínima para guiar pelo TDD.
 */
public class Cart {
    private final UUID id = UUID.randomUUID();
    private final LinkedList<CartItem> items = new LinkedList<>();

    public UUID getId() { return id; }

    public List<CartItem> getItems() { return List.copyOf(items); }

    public void addItem(Product product, int quantity) {
        if (quantity <= 0) throw new IllegalArgumentException("quantity must be > 0");
        var existingIndex = findIndexByProduct(product.getId());
        if (existingIndex >= 0) {
            var existing = items.get(existingIndex);
            var merged = new CartItem(existing.productId(), existing.productName(), existing.unitPrice(), existing.quantity() + quantity);
            items.set(existingIndex, merged);
        } else {
            items.add(new CartItem(product.getId(), product.getName(), product.getPrice(), quantity));
        }
    }

    public void removeItem(UUID productId) {
        int idx = findIndexByProduct(productId);
        if (idx >= 0) items.remove(idx);
    }

    private int findIndexByProduct(UUID productId) {
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).productId().equals(productId)) return i;
        }
        return -1;
    }

    public Money getTotal() {
        BigDecimal total = BigDecimal.ZERO;
        for (var item : items) {
            total = total.add(item.lineTotal().asBigDecimal());
        }
        return new Money(total);
    }
}
