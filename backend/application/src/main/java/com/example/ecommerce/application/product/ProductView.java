package com.example.ecommerce.application.product;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.util.UUID;

/** DTO de saída (application) para expor produtos sem vazar domínio. */
public record ProductView(
	@Schema(description = "Identificador único do produto", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6") UUID id,
	@Schema(description = "Nome do produto", example = "Camiseta básica") String name,
	@Schema(description = "Preço do produto em BRL", example = "49.90") BigDecimal price) {}
