package com.example.ecommerce.boot.api;

import com.example.ecommerce.application.cart.AddToCartUseCase;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.example.ecommerce.domain.cart.Cart;
import com.example.ecommerce.domain.product.Product;
import com.example.ecommerce.domain.value.Money;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CartController.class)
@SuppressWarnings("null")
class CartControllerTest {

    @Autowired MockMvc mvc;
    @Autowired ObjectMapper om;

    @MockBean AddToCartUseCase addToCartUseCase;
    @MockBean com.example.ecommerce.application.cart.GetCartUseCase getCartUseCase;

    @Test
    void addItem_shouldReturnCartView() throws Exception {
        var userId = UUID.randomUUID();
        var productId = UUID.randomUUID();
        var body = new CartController.AddItemRequest(productId, 2);

        var cart = new Cart();
        var product = new Product(productId, "Livro", new Money(new BigDecimal("50.00")));
        cart.addItem(product, 2);
        when(addToCartUseCase.handle(userId, productId, 2)).thenReturn(cart);

        mvc.perform(post("/api/cart/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(body))
                        .header("X-User-Id", userId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items", hasSize(1)))
                .andExpect(jsonPath("$.items[0].productId", is(productId.toString())))
                .andExpect(jsonPath("$.items[0].quantity", is(2)))
                .andExpect(jsonPath("$.total", is(100.00)));
    }

    @Test
    void addItem_shouldRejectInvalidQuantity() throws Exception {
        var body = new CartController.AddItemRequest(UUID.randomUUID(), 0);
        mvc.perform(post("/api/cart/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(body))
                        .header("X-User-Id", UUID.randomUUID().toString()))
                .andExpect(status().isBadRequest());
    }

    @Test
    void addItem_shouldRejectMissingUserHeader() throws Exception {
        var body = new CartController.AddItemRequest(UUID.randomUUID(), 1);
        mvc.perform(post("/api/cart/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(body)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getCart_shouldReturnEmptyCartWhenAbsent() throws Exception {
        var userId = UUID.randomUUID();
        when(getCartUseCase.handle(userId)).thenReturn(new Cart());
        mvc.perform(get("/api/cart")
                        .header("X-User-Id", userId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items", hasSize(0)))
                .andExpect(jsonPath("$.total", is(0.00)));
    }

    @Test
    void getCart_shouldRejectMissingUserHeader() throws Exception {
        mvc.perform(get("/api/cart"))
                .andExpect(status().isBadRequest());
    }
}
