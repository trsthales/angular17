package com.example.ecommerce.application.cart;

import com.example.ecommerce.application.ports.cart.LoadCartPort;
import com.example.ecommerce.domain.cart.Cart;

import java.util.UUID;

/** Caso de uso de leitura: obter o carrinho do usuário (ou vazio). */
public class GetCartUseCase {
    private final LoadCartPort loadCart;

    public GetCartUseCase(LoadCartPort loadCart) { this.loadCart = loadCart; }

    /**
     * Executa o caso de uso de leitura do carrinho do usuário.
     * Valida o `userId` e retorna um carrinho (vazio se não existir).
     *
     * @param userId id do usuário
     * @return `Cart` (pode ser vazio)
     */
    public Cart handle(UUID userId) {
        if (userId == null) throw new IllegalArgumentException("userId required");
        return loadCart.loadByUserId(userId).orElseGet(Cart::new);
    }
}
