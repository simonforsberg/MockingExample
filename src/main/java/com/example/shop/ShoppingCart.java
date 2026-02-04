package com.example.shop;

public class ShoppingCart {

    private int totalPrice = 0;
    private int itemCount = 0;

    public void addItem(String name, int price) {
        totalPrice += price;
        itemCount++;
    }

    public int getTotalPrice() {
        return totalPrice;
    }

    public int getItemCount() {
        return itemCount;
    }
}
