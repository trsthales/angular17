package com.example.ecommerce.application.ports.cart;

import com.example.ecommerce.domain.cart.Cart;

import java.util.Optional;
import java.util.UUID;

/** Porta para carregar o carrinho de um usuário. */
public interface LoadCartPort {
    Optional<Cart> loadByUserId(UUID userId);
}
