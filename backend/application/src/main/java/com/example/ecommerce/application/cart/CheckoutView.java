package com.example.ecommerce.application.cart;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.util.UUID;

/**
 * View retornada pelo processo de checkout.
 */
@Schema(description = "Resumo do pedido após checkout")
public class CheckoutView {
    @Schema(description = "Identificador do pedido gerado")
    private final UUID orderId;
    @Schema(description = "Valor total do pedido")
    private final BigDecimal total;

    public CheckoutView(UUID orderId, BigDecimal total) {
        this.orderId = orderId;
        this.total = total;
    }

    public UUID getOrderId() { return orderId; }
    public BigDecimal getTotal() { return total; }
}
