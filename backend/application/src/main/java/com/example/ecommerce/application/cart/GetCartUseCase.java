package com.example.ecommerce.application.cart;

import com.example.ecommerce.application.ports.cart.LoadCartPort;
import com.example.ecommerce.domain.cart.Cart;

import java.util.UUID;

/** Caso de uso de leitura: obter o carrinho do usuário (ou vazio). */
public class GetCartUseCase {
    private final LoadCartPort loadCart;

    public GetCartUseCase(LoadCartPort loadCart) { this.loadCart = loadCart; }

    public Cart handle(UUID userId) {
        if (userId == null) throw new IllegalArgumentException("userId required");
        return loadCart.loadByUserId(userId).orElseGet(Cart::new);
    }
}
