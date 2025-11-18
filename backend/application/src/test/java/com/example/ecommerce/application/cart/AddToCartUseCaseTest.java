package com.example.ecommerce.application.cart;

import com.example.ecommerce.application.ports.cart.LoadCartPort;
import com.example.ecommerce.application.ports.cart.SaveCartPort;
import com.example.ecommerce.application.ports.product.LoadProductByIdPort;
import com.example.ecommerce.domain.cart.Cart;
import com.example.ecommerce.domain.product.Product;
import com.example.ecommerce.domain.value.Money;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class AddToCartUseCaseTest {

    @Test
    void shouldCreateCartAndAddItemWhenAbsent() {
        UUID userId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        var product = new Product(productId, "Livro", new Money(new BigDecimal("50.00")));

        // fakes in-memory
        LoadProductByIdPort loadProduct = id -> Optional.of(product);
        var holder = new CartHolder();
        LoadCartPort loadCart = uid -> Optional.empty();
        SaveCartPort saveCart = (uid, cart) -> holder.cart = cart;

        var usecase = new AddToCartUseCase(loadCart, saveCart, loadProduct);
        var result = usecase.handle(userId, productId, 2);

        assertThat(holder.cart).isNotNull();
        assertThat(result).isNotNull();
        assertThat(holder.cart.getItems()).hasSize(1);
        assertThat(holder.cart.getTotal().asBigDecimal()).isEqualByComparingTo("100.00");
    }

    static class CartHolder { Cart cart; }
}
