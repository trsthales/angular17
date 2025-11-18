package com.example.ecommerce.application.cart;

import java.math.BigDecimal;
import java.util.List;

/** DTO do carrinho para a API. */
public record CartView(List<CartItemView> items, BigDecimal total) {}
