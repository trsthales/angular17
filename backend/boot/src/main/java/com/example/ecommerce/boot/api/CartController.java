package com.example.ecommerce.boot.api;

import com.example.ecommerce.application.cart.AddToCartUseCase;
import com.example.ecommerce.application.cart.CartItemView;
import com.example.ecommerce.application.cart.CartView;
import com.example.ecommerce.application.cart.GetCartUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
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
@Tag(name = "Cart", description = "Operações de carrinho")
@SecurityRequirement(name = "bearerAuth")
public class CartController {

    private final AddToCartUseCase addToCart;
    private final GetCartUseCase getCart;
    private final com.example.ecommerce.application.cart.UpdateCartItemUseCase updateCartItem;
    private final com.example.ecommerce.application.cart.CheckoutUseCase checkoutUseCase;

    public CartController(AddToCartUseCase addToCart, GetCartUseCase getCart, com.example.ecommerce.application.cart.UpdateCartItemUseCase updateCartItem,
                          com.example.ecommerce.application.cart.CheckoutUseCase checkoutUseCase) {
        this.addToCart = addToCart;
        this.getCart = getCart;
        this.updateCartItem = updateCartItem;
        this.checkoutUseCase = checkoutUseCase;
    }

    /**
     * DTO de requisição para adicionar um item.
     * - `productId`: id do produto a ser adicionado
     * - `quantity`: quantidade (>= 1)
     */
    /**
     * DTO de requisição para adicionar um item.
     * - `productId`: id do produto a ser adicionado
     * - `quantity`: quantidade (>= 1)
     */
    public record AddItemRequest(@NotNull UUID productId, @Min(1) int quantity) {}

    /**
     * DTO para atualização de item (permite 0 para remoção).
     */
    public record UpdateItemRequest(@NotNull UUID productId, @Min(0) int quantity) {}

    /**
     * Endpoint para adicionar um item ao carrinho do usuário.
     * Espera o header `X-User-Id` com o UUID do usuário e o body com produto+quantidade.
     */
    @PostMapping("/items")
        @Operation(summary = "Adicionar item ao carrinho", description = "Adiciona um item ao carrinho do usuário. Requer header X-User-Id com UUID do usuário.")
        @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Carrinho atualizado",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = CartView.class))),
            @ApiResponse(responseCode = "400", description = "Requisição inválida")
        })
        public ResponseEntity<CartView> addItem(
            @Parameter(name = "X-User-Id", in = ParameterIn.HEADER, required = true, description = "UUID do usuário")
            @RequestHeader(value = "X-User-Id", required = false) String userIdHeader,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Produto e quantidade")
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
     * Atualiza a quantidade de um item no carrinho. Se `quantity` for 0, o item é removido.
     */
    @PutMapping("/items")
        @Operation(summary = "Atualizar quantidade do item", description = "Atualiza a quantidade de um item no carrinho (0 remove)")
        @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Carrinho atualizado",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = CartView.class)))
        })
        public ResponseEntity<CartView> updateItem(
            @Parameter(name = "X-User-Id", in = ParameterIn.HEADER, required = true, description = "UUID do usuário")
            @RequestHeader(value = "X-User-Id", required = false) String userIdHeader,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Produto e nova quantidade")
            @Valid @RequestBody UpdateItemRequest req) {
        if (userIdHeader == null || userIdHeader.isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        UUID userId;
        try { userId = UUID.fromString(userIdHeader); } catch (IllegalArgumentException e) { return ResponseEntity.badRequest().build(); }

        Cart cart = updateCartItem.handle(userId, req.productId(), req.quantity());
        return ResponseEntity.ok(toView(cart));
    }

    /**
     * Remove um item do carrinho.
     */
    @DeleteMapping("/items/{productId}")
        @Operation(summary = "Remover item do carrinho", description = "Remove um item do carrinho do usuário")
        public ResponseEntity<CartView> removeItem(
            @Parameter(name = "X-User-Id", in = ParameterIn.HEADER, required = true, description = "UUID do usuário")
            @RequestHeader(value = "X-User-Id", required = false) String userIdHeader,
            @Parameter(description = "ID do produto") @PathVariable UUID productId) {
        if (userIdHeader == null || userIdHeader.isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        UUID userId;
        try { userId = UUID.fromString(userIdHeader); } catch (IllegalArgumentException e) { return ResponseEntity.badRequest().build(); }

        Cart cart = updateCartItem.handle(userId, productId, 0);
        return ResponseEntity.ok(toView(cart));
    }

    /**
     * Endpoint de checkout: processa o pedido e limpa o carrinho.
     */
    @PostMapping("/checkout")
        @Operation(summary = "Finalizar compra", description = "Processa o pedido e limpa o carrinho do usuário")
        public ResponseEntity<com.example.ecommerce.application.cart.CheckoutView> checkout(
            @Parameter(name = "X-User-Id", in = ParameterIn.HEADER, required = true, description = "UUID do usuário")
            @RequestHeader(value = "X-User-Id", required = false) String userIdHeader) {
        if (userIdHeader == null || userIdHeader.isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        UUID userId;
        try { userId = UUID.fromString(userIdHeader); } catch (IllegalArgumentException e) { return ResponseEntity.badRequest().build(); }

        var res = checkoutUseCase.handle(userId);
        return ResponseEntity.ok(res);
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
