package com.example.ecommerce.application.cart;

import java.math.BigDecimal;
import java.util.UUID;

/** DTO de item de carrinho para a API. */
public record CartItemView(UUID productId, String productName, BigDecimal unitPrice, int quantity, BigDecimal lineTotal) {}
