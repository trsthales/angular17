package com.example.ecommerce.application.cart;

import com.example.ecommerce.application.ports.cart.LoadCartPort;
import com.example.ecommerce.application.ports.cart.SaveCartPort;
import com.example.ecommerce.domain.cart.Cart;

import java.util.Optional;
import java.util.UUID;

/**
 * Caso de uso de checkout: calcula total do carrinho, gera um orderId simples
 * e limpa o carrinho do usuário (persistindo um carrinho vazio).
 */
public class CheckoutUseCase {
    private final LoadCartPort loadCart;
    private final SaveCartPort saveCart;

    public CheckoutUseCase(LoadCartPort loadCart, SaveCartPort saveCart) {
        this.loadCart = loadCart;
        this.saveCart = saveCart;
    }

    /**
     * Executa o checkout para o usuário.
     * @param userId id do usuário
     * @return CheckoutView com orderId e total do pedido
     */
    public CheckoutView handle(UUID userId) {
        if (userId == null) throw new IllegalArgumentException("userId required");

        Optional<Cart> maybe = loadCart.loadByUserId(userId);
        Cart cart = maybe.orElseGet(Cart::new);
        var total = cart.getTotal().asBigDecimal();

        // Gera um id simples para o pedido
        UUID orderId = UUID.randomUUID();

        // Limpa o carrinho persistindo um novo carrinho vazio
        saveCart.save(userId, new Cart());

        return new CheckoutView(orderId, total);
    }
}
