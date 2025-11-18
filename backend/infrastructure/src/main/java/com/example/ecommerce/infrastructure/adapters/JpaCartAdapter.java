package com.example.ecommerce.infrastructure.adapters;

import com.example.ecommerce.application.ports.cart.LoadCartPort;
import com.example.ecommerce.application.ports.cart.SaveCartPort;
import com.example.ecommerce.domain.cart.Cart;
import com.example.ecommerce.domain.product.Product;
import com.example.ecommerce.domain.value.Money;
import com.example.ecommerce.infrastructure.cart.CartEntity;
import com.example.ecommerce.infrastructure.cart.CartItemEntity;
import com.example.ecommerce.infrastructure.cart.SpringDataCartRepository;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

@Component
public class JpaCartAdapter implements LoadCartPort, SaveCartPort {

    private final SpringDataCartRepository repo;

    public JpaCartAdapter(SpringDataCartRepository repo) {
        this.repo = repo;
    }

    @Override
    public Optional<Cart> loadByUserId(UUID userId) {
        return repo.findByUserId(userId).map(this::toDomain);
    }

    @Override
    public void save(UUID userId, Cart cart) {
        CartEntity entity = repo.findByUserId(userId).orElseGet(CartEntity::new);
        entity.setUserId(userId);
        // recria itens (orphanRemoval cuida dos antigos)
        var newItems = new ArrayList<CartItemEntity>();
        for (var i : cart.getItems()) {
            var e = new CartItemEntity();
            e.setCart(entity);
            e.setProductId(i.productId());
            e.setProductName(i.productName());
            e.setUnitPrice(i.lineTotal().asBigDecimal().divide(new java.math.BigDecimal(i.quantity())));
            e.setQuantity(i.quantity());
            newItems.add(e);
        }
        entity.getItems().clear();
        entity.getItems().addAll(newItems);
        repo.save(entity);
    }

    private Cart toDomain(CartEntity entity) {
        Cart cart = new Cart();
        entity.getItems().forEach(i -> {
            var p = new Product(i.getProductId(), i.getProductName(), new Money(i.getUnitPrice()));
            cart.addItem(p, i.getQuantity());
        });
        return cart;
    }
}
