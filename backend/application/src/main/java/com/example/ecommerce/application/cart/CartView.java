package com.example.ecommerce.application.cart;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.util.List;

/** DTO do carrinho para a API. */
@Schema(description = "Visão do carrinho retornada pela API")
public record CartView(
	@Schema(description = "Lista de itens do carrinho") List<CartItemView> items,
	@Schema(description = "Valor total do carrinho", example = "149.90") BigDecimal total) {}
