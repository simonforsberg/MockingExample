package com.example.shop;

public class ShoppingCart {

    private int totalPrice = 0;

    public void addItem(String name, int price) {
        totalPrice += price;
    }

    public int getTotalPrice() {
        return totalPrice;
    }

    public int getItemCount() {
        return 0;
    }
}
