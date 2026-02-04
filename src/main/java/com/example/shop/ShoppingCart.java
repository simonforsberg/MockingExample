package com.example.shop;

public class ShoppingCart {

    private double totalPrice = 0.0;
    private int itemCount = 0;

    public void addItem(String name, double price) {
        totalPrice += price;
        itemCount++;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public int getItemCount() {
        return itemCount;
    }
}
