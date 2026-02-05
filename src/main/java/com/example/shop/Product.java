package com.example.shop;

import java.util.Objects;

/**
 * Immutable value object representing a product.
 * <p>
 * Products are uniquely identified by name and price
 * and can be used as keys in collections.
 */
public class Product {
    private final String name;
    private final double price;

    public Product(String name, double price) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Product name cannot be null or blank");
        }
        if (price < 0) {
            throw new IllegalArgumentException("Price cannot be negative");
        }
        this.name = name;
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Product product)) return false;
        return Double.compare(price, product.price) == 0 && Objects.equals(name, product.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, price);
    }
}
