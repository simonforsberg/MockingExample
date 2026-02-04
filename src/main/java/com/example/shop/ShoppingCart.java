package com.example.shop;

import java.util.ArrayList;
import java.util.List;

public class ShoppingCart {

    private double totalPrice = 0.0;
    private int itemCount = 0;
    private final List<Product> items = new ArrayList<>();

    public void addItem(Product product) {
        items.add(product);
        totalPrice += product.getPrice();
        itemCount++;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public int getItemCount() {
        return itemCount;
    }
}
