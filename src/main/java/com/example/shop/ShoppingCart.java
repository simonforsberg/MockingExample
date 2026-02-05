package com.example.shop;

import java.util.ArrayList;
import java.util.List;

public class ShoppingCart {

    private final List<Product> items = new ArrayList<>();
    private double discount = 0.0;

    public void addItem(Product product) {
        if (product == null) {
            throw new IllegalArgumentException("Product cannot be null");
        }
        items.add(product);
    }

    public double getTotalPrice() {
        double sum = items.stream()
                .mapToDouble(Product::getPrice)
                .sum();
        return sum * (1 - discount);
    }

    public int getItemCount() {
        return items.size();
    }

    public boolean removeItem(Product product) {
        if (product == null) {
            throw new IllegalArgumentException("Product cannot be null");
        }
        return items.remove(product);
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
