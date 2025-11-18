package com.example.ecommerce.application.product;

import java.math.BigDecimal;
import java.util.UUID;

/** DTO de saída (application) para expor produtos sem vazar domínio. */
public record ProductView(UUID id, String name, BigDecimal price) {}
