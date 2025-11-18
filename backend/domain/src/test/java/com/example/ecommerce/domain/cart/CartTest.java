package com.example.ecommerce.domain.cart;

import com.example.ecommerce.domain.product.Product;
import com.example.ecommerce.domain.value.Money;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Testes dirigidos por comportamento para o agregado de Carrinho.
 * Objetivo: forçar a modelagem do domínio por TDD.
 */
class CartTest {

    @Test
    void addItem_shouldIncreaseQuantity_whenSameProductAdded() {
        var p1 = new Product("P1", new Money(new BigDecimal("10.50")));
        var cart = new Cart();

        cart.addItem(p1, 1);
        cart.addItem(p1, 2);

        assertThat(cart.getItems()).hasSize(1);
        assertThat(cart.getItems().get(0).quantity()).isEqualTo(3);
        assertThat(cart.getTotal()).isEqualTo(new Money(new BigDecimal("31.50")));
    }

    @Test
    void addItem_shouldRejectZeroOrNegativeQuantity() {
        var p = new Product("P", new Money(new BigDecimal("5.00")));
        var cart = new Cart();

        assertThatThrownBy(() -> cart.addItem(p, 0)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> cart.addItem(p, -1))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void removeItem_shouldRemoveByProductId() {
        var p1 = new Product("P1", new Money(new BigDecimal("10.00")));
        var p2 = new Product("P2", new Money(new BigDecimal("3.00")));
        var cart = new Cart();
        cart.addItem(p1, 1);
        cart.addItem(p2, 2);

        cart.removeItem(p2.getId());

        assertThat(cart.getItems()).extracting(CartItem::productId)
                .containsExactly(p1.getId());
        assertThat(cart.getTotal()).isEqualTo(new Money(new BigDecimal("10.00")));
    }

    @Test
    void getTotal_shouldReturnZeroForEmptyCart() {
        var cart = new Cart();
        assertThat(cart.getTotal()).isEqualTo(new Money(BigDecimal.ZERO));
    }

}
