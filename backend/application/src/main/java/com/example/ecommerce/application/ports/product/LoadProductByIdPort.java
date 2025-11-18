package com.example.ecommerce.application.ports.product;

import com.example.ecommerce.domain.product.Product;

import java.util.Optional;
import java.util.UUID;

/** Porta para carregar um produto por id. */
public interface LoadProductByIdPort {
    Optional<Product> loadById(UUID id);
}
