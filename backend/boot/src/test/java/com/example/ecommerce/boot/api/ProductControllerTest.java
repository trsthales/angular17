package com.example.ecommerce.boot.api;

import com.example.ecommerce.application.product.ListProductsUseCase;
import com.example.ecommerce.application.product.ProductView;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductController.class)
class ProductControllerTest {

    @Autowired
    MockMvc mvc;

    @MockBean
    ListProductsUseCase listProductsUseCase;

    @Test
    void shouldReturnProductsAsJson() throws Exception {
        var list = List.of(new ProductView(UUID.randomUUID(), "Tênis", new BigDecimal("199.90")));
        when(listProductsUseCase.handle()).thenReturn(list);
        mvc.perform(get("/api/products")).andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }
}
