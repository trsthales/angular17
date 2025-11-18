package com.example.ecommerce.boot.api;

import com.example.ecommerce.application.product.ListProductsUseCase;
import com.example.ecommerce.application.product.ProductView;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/** Controller fino (apenas orquestra casos de uso). */
@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ListProductsUseCase listProducts;

    public ProductController(ListProductsUseCase listProducts) {
        this.listProducts = listProducts;
    }

    @GetMapping
    public List<ProductView> list() { return listProducts.handle(); }
}
