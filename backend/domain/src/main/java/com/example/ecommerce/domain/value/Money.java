package com.example.ecommerce.domain.value;

import java.math.BigDecimal;
import java.util.Objects;
import java.math.RoundingMode;

/**
 * Value Object imutável representando valor monetário em BRL (simples, sem moeda).
 * Bom exemplo: imutabilidade e validação centralizada.
 */
public final class Money implements Comparable<Money> {
    private final BigDecimal amount;

    public Money(BigDecimal amount) {
        if (amount == null) throw new IllegalArgumentException("amount required");
        this.amount = amount.setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal asBigDecimal() { return amount; }

    public Money plus(Money other) { return new Money(this.amount.add(other.amount)); }

    public Money times(int multiplier) { return new Money(this.amount.multiply(BigDecimal.valueOf(multiplier))); }

    @Override public int compareTo(Money o) { return this.amount.compareTo(o.amount); }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Money money = (Money) o;
        return Objects.equals(amount, money.amount);
    }

    @Override public int hashCode() { return Objects.hash(amount); }

    @Override public String toString() { return amount.toString(); }
}
