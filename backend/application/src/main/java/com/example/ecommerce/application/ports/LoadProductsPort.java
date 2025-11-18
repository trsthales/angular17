package com.example.ecommerce.application.ports;

import com.example.ecommerce.domain.product.Product;

import java.util.List;

/** Porta de saída (Application -> Infra): carregar produtos do armazenamento. */
public interface LoadProductsPort {
    List<Product> loadAll();
}
