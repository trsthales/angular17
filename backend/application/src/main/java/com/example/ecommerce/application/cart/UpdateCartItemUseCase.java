package com.example.ecommerce.application.cart;

import com.example.ecommerce.application.ports.cart.LoadCartPort;
import com.example.ecommerce.application.ports.cart.SaveCartPort;
import com.example.ecommerce.application.ports.product.LoadProductByIdPort;
import com.example.ecommerce.domain.cart.Cart;
import com.example.ecommerce.domain.product.Product;

import java.util.UUID;

/**
 * Caso de uso: atualiza a quantidade de um item no carrinho do usuário.
 * - Se `quantity` == 0, o item é removido.
 * - Se `quantity` > 0, o item é ajustado para a nova quantidade (criado se necessário).
 */
public class UpdateCartItemUseCase {
    private final LoadCartPort loadCart;
    private final SaveCartPort saveCart;
    private final LoadProductByIdPort loadProductById;

    public UpdateCartItemUseCase(LoadCartPort loadCart, SaveCartPort saveCart, LoadProductByIdPort loadProductById) {
        this.loadCart = loadCart;
        this.saveCart = saveCart;
        this.loadProductById = loadProductById;
    }

    /**
     * Atualiza a quantidade do item no carrinho.
     * @param userId id do usuário
     * @param productId id do produto
     * @param quantity nova quantidade (>= 0)
     * @return carrinho atualizado
     */
    public Cart handle(UUID userId, UUID productId, int quantity) {
        if (userId == null || productId == null) throw new IllegalArgumentException("ids required");
        if (quantity < 0) throw new IllegalArgumentException("quantity must be >= 0");

        Cart cart = loadCart.loadByUserId(userId).orElseGet(Cart::new);

        // Remove item se existir (em seguida re-adiciona se quantity > 0)
        cart.removeItem(productId);

        if (quantity > 0) {
            Product product = loadProductById.loadById(productId)
                    .orElseThrow(() -> new IllegalArgumentException("product not found"));
            cart.addItem(product, quantity);
        }

        saveCart.save(userId, cart);
        return cart;
    }
}
