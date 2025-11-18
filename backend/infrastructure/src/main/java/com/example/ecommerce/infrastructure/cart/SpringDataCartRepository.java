package com.example.ecommerce.infrastructure.cart;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

/**
 * Repositório Spring Data para a entidade `CartEntity`.
 *
 * O método `findByUserId` carrega o carrinho do usuário com os itens (fetch graph),
 * evitando N+1 quando os itens são acessados.
 */
public interface SpringDataCartRepository extends JpaRepository<CartEntity, UUID> {
    /**
     * Localiza o carrinho do usuário incluindo a coleção `items`.
     * @param userId id do usuário
     * @return Optional com a entidade do carrinho
     */
    @EntityGraph(attributePaths = "items")
    Optional<CartEntity> findByUserId(UUID userId);
}
