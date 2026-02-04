package com.example.shop;

import java.util.ArrayList;
import java.util.List;

public class ShoppingCart {

    private final List<Product> items = new ArrayList<>();

    public void addItem(Product product) {
        items.add(product);
    }

    public double getTotalPrice() {
        return items.stream()
                .mapToDouble(Product::getPrice)
                .sum();
    }

    public int getItemCount() {
        return items.size();
    }

    public void removeItem(Product product) {
        items.remove(product);
    }
}
