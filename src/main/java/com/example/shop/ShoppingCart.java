package com.example.shop;

import java.util.HashMap;
import java.util.Map;

public class ShoppingCart {

    private final Map<Product, Integer> items = new HashMap<>();
    private double discount = 0.0;

    public void addItem(Product product) {
        if (product == null) {
            throw new IllegalArgumentException("Product cannot be null");
        }
        items.put(product, items.getOrDefault(product, 0) + 1);
    }

    public double getTotalPrice() {
        double sum = items.entrySet().stream()
                .mapToDouble(entry -> entry.getKey().getPrice() * entry.getValue())
                .sum();
        return sum * (1 - discount);
    }

    public int getItemCount() {
        return items.size();
    }

    public int getQuantity(Product product) {
        return items.getOrDefault(product, 0);
    }

    public void updateQuantity(Product product, int quantity) {
        if (product == null) {
            throw new IllegalArgumentException("Product cannot be null");
        }
        if (quantity == 0) {
            items.remove(product);
        } else {
            items.put(product, quantity);
        }
    }

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
