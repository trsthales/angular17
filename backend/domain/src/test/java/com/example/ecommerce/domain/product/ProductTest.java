package com.example.ecommerce.domain.product;

import com.example.ecommerce.domain.value.Money;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/*
    * Testes dirigidos por comportamento para a entidade Produto.
    * Objetivo: forçar a modelagem do domínio por TDD.
    * @author Thales Ramalho
    * @Date 18/11/2025
    *
 */
public class ProductTest {

    @Test
    void changeName_NullName_ShouldThrowException() {
        Product p1 = new Product("Product 1", new Money(new BigDecimal(10)));

        assertThatThrownBy(() -> p1.rename(null))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("newName required");
    }

    @Test
    void changeName_EmptyName_ShouldThrowException() {
        Product p1 = new Product("Product 1", new Money(new BigDecimal(10)));

        assertThatThrownBy(() -> p1.rename("")).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void changeName_ValidName_ShouldSucceed() {
        Product p1 = new Product("Product 1", new Money(new BigDecimal(10)));

        p1.rename("New Product Name");

        assertThat(p1.getName()).isNotNull();
        assertThat(p1.getName()).isNotEmpty();
    }

    @Test
    void changePrice_NullPrice_ShouldThrowException() {
        Product p1 = new Product("Product 1", new Money(new BigDecimal(10)));
        assertThatThrownBy(() -> p1.reprice(null))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("newPrice required");
    }

    @Test
    void changePrice_ValidPrice_ShouldSucceed() {
        Product p1 = new Product("Product 1", new Money(new BigDecimal(10)));

        p1.reprice(new Money(new BigDecimal(25)));

        assertThat(p1.getPrice()).isNotNull();
        assertThat(p1.getPrice()).isGreaterThan(new Money(new BigDecimal(-1)));
    }
}
