package com.example.ecommerce.domain.product;

import com.example.ecommerce.domain.value.Money;

import java.util.Objects;
import java.util.UUID;

/**
 * Entidade de domínio: Produto.
 * Comentada para fins educativos (o porquê de cada decisão).
 */
public class Product {
    private final UUID id;
    private String name;
    private Money price;

    public Product(String name, Money price) {
        this(UUID.randomUUID(), name, price);
    }

    public Product(UUID id, String name, Money price) {
        if (id == null) throw new IllegalArgumentException("id required");
        if (name == null || name.isBlank()) throw new IllegalArgumentException("name required");
        if (price == null) throw new IllegalArgumentException("price required");
        this.id = id;
        this.name = name;
        this.price = price;
    }

    public UUID getId() { return id; }
    public String getName() { return name; }
    public Money getPrice() { return price; }

    public void rename(String newName) {
        if (newName == null || newName.isBlank()) throw new IllegalArgumentException("newName required");
        this.name = newName;
    }

    public void reprice(Money newPrice) {
        if (newPrice == null) throw new IllegalArgumentException("newPrice required");
        this.price = newPrice;
    }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Product)) return false;
        Product product = (Product) o;
        return Objects.equals(id, product.id);
    }

    @Override public int hashCode() { return Objects.hash(id); }
}
