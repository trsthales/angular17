package com.example.ecommerce.application.cart;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.util.UUID;

/** DTO de item de carrinho para a API. */
@Schema(description = "Item do carrinho")
public record CartItemView(
	@Schema(description = "ID do produto") UUID productId,
	@Schema(description = "Nome do produto") String productName,
	@Schema(description = "Preço unitário", example = "49.90") BigDecimal unitPrice,
	@Schema(description = "Quantidade") int quantity,
	@Schema(description = "Total da linha (unitPrice * quantity)") BigDecimal lineTotal) {}
