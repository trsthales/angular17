package com.example.ecommerce.boot.api;

import com.example.ecommerce.application.cart.AddToCartUseCase;
import com.example.ecommerce.application.cart.CartItemView;
import com.example.ecommerce.application.cart.CartView;
import com.example.ecommerce.application.cart.GetCartUseCase;
import com.example.ecommerce.domain.cart.Cart;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.Valid;
import java.util.UUID;

/**
 * Controller REST para operações de carrinho.
 *
 * Este controller é fino: delega toda a lógica para os casos de uso (`AddToCartUseCase`, `GetCartUseCase`).
 * Validações HTTP/entrada e mapeamentos de DTOs/Views ficam aqui.
 */
@RestController
@RequestMapping("/api/cart")
@Validated
public class CartController {

    private final AddToCartUseCase addToCart;
    private final GetCartUseCase getCart;

    public CartController(AddToCartUseCase addToCart, GetCartUseCase getCart) {
        this.addToCart = addToCart;
        this.getCart = getCart;
    }

    /**
     * DTO de requisição para adicionar um item.
     * - `productId`: id do produto a ser adicionado
     * - `quantity`: quantidade (>= 1)
     */
    public record AddItemRequest(@NotNull UUID productId, @Min(1) int quantity) {}

    /**
     * Endpoint para adicionar um item ao carrinho do usuário.
     * Espera o header `X-User-Id` com o UUID do usuário e o body com produto+quantidade.
     */
    @PostMapping("/items")
    public ResponseEntity<CartView> addItem(@RequestHeader(value = "X-User-Id", required = false) String userIdHeader,
                                            @Valid @RequestBody AddItemRequest req) {
        if (userIdHeader == null || userIdHeader.isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        UUID userId;
        try { userId = UUID.fromString(userIdHeader); } catch (IllegalArgumentException e) { return ResponseEntity.badRequest().build(); }

        Cart cart = addToCart.handle(userId, req.productId(), req.quantity());
        CartView view = toView(cart);
        return ResponseEntity.ok(view);
    }

    /**
     * Converte o modelo de domínio `Cart` em `CartView` usado pela API.
     */
    private CartView toView(Cart cart) {
        var items = cart.getItems().stream()
                .map(i -> new CartItemView(i.productId(), i.productName(), i.unitPrice().asBigDecimal(), i.quantity(), i.lineTotal().asBigDecimal()))
                .toList();
        return new CartView(items, cart.getTotal().asBigDecimal());
    }

    /**
     * Endpoint para obter o carrinho do usuário. Requer header `X-User-Id`.
     */
    @GetMapping
    public ResponseEntity<CartView> getCart(@RequestHeader(value = "X-User-Id", required = false) String userIdHeader) {
        if (userIdHeader == null || userIdHeader.isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        UUID userId;
        try { userId = UUID.fromString(userIdHeader); } catch (IllegalArgumentException e) { return ResponseEntity.badRequest().build(); }
        Cart cart = getCart.handle(userId);
        return ResponseEntity.ok(toView(cart));
    }
}
