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

    /**
     * Executa o caso de uso de adicionar um produto ao carrinho do usuário.
     * Valida parâmetros, carrega o produto, obtém/cria o carrinho, adiciona o item
     * e persiste o carrinho.
     *
     * @param userId id do usuário dono do carrinho
     * @param productId id do produto a ser adicionado
     * @param quantity quantidade desejada (deve ser > 0)
     * @return o `Cart` atualizado
     * @throws IllegalArgumentException quando parâmetros inválidos ou produto inexistente
     */
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
