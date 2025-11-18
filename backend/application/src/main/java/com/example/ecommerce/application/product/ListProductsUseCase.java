package com.example.ecommerce.application.product;

import com.example.ecommerce.application.ports.LoadProductsPort;

import java.util.List;

/** Caso de uso: listar produtos (apenas leitura). */
public class ListProductsUseCase {
    private final LoadProductsPort loadProducts;

    public ListProductsUseCase(LoadProductsPort loadProducts) {
        this.loadProducts = loadProducts;
    }

    public List<ProductView> handle() {
        return loadProducts.loadAll().stream()
                .map(p -> new ProductView(p.getId(), p.getName(), p.getPrice().asBigDecimal()))
                .toList();
    }
}
