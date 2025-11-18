package com.example.ecommerce.application.cart;

import com.example.ecommerce.application.ports.cart.LoadCartPort;
import com.example.ecommerce.application.ports.cart.SaveCartPort;
import com.example.ecommerce.application.ports.product.LoadProductByIdPort;
import com.example.ecommerce.domain.cart.Cart;
import com.example.ecommerce.domain.product.Product;

import java.util.UUID;

/** Caso de uso: adicionar item ao carrinho de um usuário. */
public class AddToCartUseCase {
    private final LoadCartPort loadCart;
    private final SaveCartPort saveCart;
    private final LoadProductByIdPort loadProductById;

    public AddToCartUseCase(LoadCartPort loadCart, SaveCartPort saveCart, LoadProductByIdPort loadProductById) {
        this.loadCart = loadCart;
        this.saveCart = saveCart;
        this.loadProductById = loadProductById;
    }

    public Cart handle(UUID userId, UUID productId, int quantity) {
        if (userId == null || productId == null) throw new IllegalArgumentException("ids required");
        if (quantity <= 0) throw new IllegalArgumentException("quantity must be > 0");
        Product product = loadProductById.loadById(productId).orElseThrow(() -> new IllegalArgumentException("product not found"));
        Cart cart = loadCart.loadByUserId(userId).orElseGet(Cart::new);
        cart.addItem(product, quantity);
        saveCart.save(userId, cart);
        return cart;
    }
}
