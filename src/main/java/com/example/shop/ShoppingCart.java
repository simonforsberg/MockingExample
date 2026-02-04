package com.example.shop;

import java.util.ArrayList;
import java.util.List;

public class ShoppingCart {

    private final List<Product> items = new ArrayList<>();
    private double discount = 0.0;

    public void addItem(Product product) {
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

    public void removeItem(Product product) {
        items.remove(product);
    }

    public void applyDiscount(double discount) {
        this.discount = discount;
    }
}
