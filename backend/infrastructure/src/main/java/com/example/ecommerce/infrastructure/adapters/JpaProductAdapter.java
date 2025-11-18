package com.example.ecommerce.infrastructure.adapters;

import com.example.ecommerce.application.ports.LoadProductsPort;
import com.example.ecommerce.application.ports.product.LoadProductByIdPort;
import com.example.ecommerce.domain.product.Product;
import com.example.ecommerce.domain.value.Money;
import com.example.ecommerce.infrastructure.product.ProductEntity;
import com.example.ecommerce.infrastructure.product.SpringDataProductRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/** Adapter Infra -> Application, implementa portas de leitura de produtos. */
@Component
@SuppressWarnings("null")
public class JpaProductAdapter implements LoadProductsPort, LoadProductByIdPort {

    private final SpringDataProductRepository repo;

    public JpaProductAdapter(SpringDataProductRepository repo) {
        this.repo = repo;
    }

    @Override
    public List<Product> loadAll() {
        return repo.findAll().stream().map(this::toDomain).toList();
    }

    @Override
    public Optional<Product> loadById(UUID id) {
        // suppressão local para toolings que marcam UUID como @NonNull
        return repo.findById(id).map(this::toDomain);
    }

    private Product toDomain(ProductEntity e) {
        return new Product(e.getId(), e.getName(), new Money(e.getPrice()));
    }
}
