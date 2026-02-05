package com.example.shop;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents a shopping cart containing products with quantities.
 * <p>
 * Supports adding and removing products, updating quantities,
 * applying discounts, and calculating totals.
 * <p>
 * The cart aggregates quantities per product and applies
 * a single percentage-based discount to the total price.
 */
public class ShoppingCart {

    private final Map<Product, Integer> items = new HashMap<>();
    private double discount = 0.0;

    /**
     * Adds one unit of the given product to the cart.
     *
     * @param product the product to add
     * @throws IllegalArgumentException if the product is null
     */
    public void addItem(Product product) {
        if (product == null) {
            throw new IllegalArgumentException("Product cannot be null");
        }
        items.put(product, items.getOrDefault(product, 0) + 1);
    }

    /**
     * Removes one unit of the given product from the cart.
     *
     * @param product the product to remove
     * @return true if the product was present and removed, false otherwise
     * @throws IllegalArgumentException if the product is null
     */
    public boolean removeItem(Product product) {
        if (product == null) {
            throw new IllegalArgumentException("Product cannot be null");
        }
        if (!items.containsKey(product)) {
            return false;
        }
        int currentQuantity = items.get(product);
        if (currentQuantity > 1) {
            items.put(product, currentQuantity - 1);
        } else {
            items.remove(product);
        }
        return true;
    }

    /**
     * Sets a new quantity for a product.
     * <p>
     * A quantity of zero removes the product from the cart.
     *
     * @param product  the product to update
     * @param quantity the new quantity (must be >= 0)
     * @throws IllegalArgumentException if the product is null or quantity is negative
     */
    public void updateQuantity(Product product, int quantity) {
        if (product == null) {
            throw new IllegalArgumentException("Product cannot be null");
        }
        if (quantity < 0) {
            throw new IllegalArgumentException("Quantity cannot be negative");
        }
        if (quantity == 0) {
            items.remove(product);
        } else {
            items.put(product, quantity);
        }
    }

    public void clear() {
        items.clear();
        discount = 0.0;
    }

    public int getItemCount() {
        return items.size();
    }

    public int getTotalItems() {
        return items.values().stream()
                .mapToInt(Integer::intValue)
                .sum();
    }

    public int getQuantity(Product product) {
        return items.getOrDefault(product, 0);
    }

    /**
     * Returns the total price of all products in the cart,
     * including any applied discount.
     *
     * @return potentially discounted total price
     */
    public double getTotalPrice() {
        double sum = items.entrySet().stream()
                .mapToDouble(entry -> entry.getKey().getPrice() * entry.getValue())
                .sum();
        return sum * (1 - discount);
    }

    public void applyDiscount(double discount) {
        if (discount > 1) {
            throw new IllegalArgumentException("Discount cannot be greater than 100%");
        }
        if (discount < 0) {
            throw new IllegalArgumentException("Discount cannot be negative");
        }
        this.discount = discount;
    }
}
