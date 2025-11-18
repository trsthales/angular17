package com.example.ecommerce.application.ports.cart;

import com.example.ecommerce.domain.cart.Cart;

import java.util.UUID;

/** Porta para salvar/persistir o carrinho de um usuário. */
public interface SaveCartPort {
    void save(UUID userId, Cart cart);
}
