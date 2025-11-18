package com.example.ecommerce.boot.api;

import com.example.ecommerce.application.product.ListProductsUseCase;
import com.example.ecommerce.application.product.ProductView;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Controller REST para produtos. Componente fino que apenas orquestra o
 * caso de uso `ListProductsUseCase` e realiza o mapeamento para `ProductView`.
 *
 * Não contém regra de negócio — essa responsabilidade está na camada de aplicação.
 */
@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ListProductsUseCase listProducts;

    public ProductController(ListProductsUseCase listProducts) {
        this.listProducts = listProducts;
    }

    /**
     * Lista produtos disponíveis.
     *
     * @return lista de `ProductView` montada pelo caso de uso
     */
    @GetMapping
    public List<ProductView> list() { return listProducts.handle(); }
}
