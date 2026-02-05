package com.example.shop;

/**
 * Immutable value object representing a product.
 * <p>
 * A product is defined by its name and price and is immutable by design.
 * Equality and hash code are derived from these components, which makes
 * {@code Product} safe to use as a key in hash-based collections
 * such as {@link java.util.Map}.
 * <p>
 * Validation is performed at construction time to ensure a non-blank name
 * and a non-negative price.
 */
public record Product(String name, double price) {
    public Product {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Product name cannot be null or blank");
        }
        if (price < 0) {
            throw new IllegalArgumentException("Price cannot be negative");
        }
    }
}
