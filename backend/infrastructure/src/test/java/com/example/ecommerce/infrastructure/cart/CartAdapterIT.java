package com.example.ecommerce.infrastructure.cart;

import com.example.ecommerce.application.ports.cart.LoadCartPort;
import com.example.ecommerce.application.ports.cart.SaveCartPort;
import com.example.ecommerce.domain.cart.Cart;
import com.example.ecommerce.domain.product.Product;
import com.example.ecommerce.domain.value.Money;
import com.example.ecommerce.infrastructure.adapters.JpaCartAdapter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@DataJpaTest
@Import(JpaCartAdapter.class)
@SuppressWarnings({"resource", "null"})
class CartAdapterIT {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("ecommerce")
            .withUsername("postgres")
            .withPassword("postgres");

    @DynamicPropertySource
    static void props(DynamicPropertyRegistry r) {
        r.add("spring.datasource.url", postgres::getJdbcUrl);
        r.add("spring.datasource.username", postgres::getUsername);
        r.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    SpringDataCartRepository repo;

    @Autowired
    LoadCartPort loadCartPort;
    @Autowired
    SaveCartPort saveCartPort;

    @Test
    void saveAndLoadCartByUser() {
        UUID userId = UUID.randomUUID();
        var product = new Product(UUID.randomUUID(), "Mouse", new Money(new BigDecimal("120.00")));
        var cart = new Cart();
        cart.addItem(product, 1);

        saveCartPort.save(userId, cart);

        var loaded = loadCartPort.loadByUserId(userId);
        assertThat(loaded).isPresent();
        assertThat(loaded.get().getItems()).hasSize(1);
        assertThat(loaded.get().getTotal().asBigDecimal()).isEqualByComparingTo("120.00");
    }
}
