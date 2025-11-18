package com.example.ecommerce.infrastructure.cart;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface SpringDataCartRepository extends JpaRepository<CartEntity, UUID> {
    @EntityGraph(attributePaths = "items")
    Optional<CartEntity> findByUserId(UUID userId);
}
