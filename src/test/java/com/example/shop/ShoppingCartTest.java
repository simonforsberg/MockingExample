package com.example.shop;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ShoppingCartTest {

    private ShoppingCart cart;

    @BeforeEach
    void setUp() {
        cart = new ShoppingCart();
    }

    @Test
    void newCart_shouldHaveZeroItems() {
        // Act
        int itemCount = cart.getItemCount();
        // Assert
        assertThat(itemCount).isZero();
    }

    @Test
    void newCart_shouldHaveZeroTotal() {
        // Act
        double total = cart.getTotalPrice();
        // Assert
        assertThat(total).isZero();
    }

    @Test
    void addItem_shouldUpdateTotalPrice() {
        // Arrange
        Product keps = new Product("Keps", 200.0);
        // Act
        cart.addItem(keps);
        // Assert
        assertThat(cart.getTotalPrice()).isEqualTo(200.0);
    }

    @Test
    void addItem_shouldIncreaseItemCount() {
        // Arrange
        Product keps = new Product("Keps", 200.0);
        // Act
        cart.addItem(keps);
        // Assert
        assertThat(cart.getItemCount()).isEqualTo(1);
    }

    @Test
    void addMultipleItems_shouldUpdateTotalPriceAndItemCount() {
        // Arrange
        Product hat = new Product("Hat", 200.0);
        Product pants = new Product("Pants", 700.0);
        Product jacket = new Product("Jacket", 900.0);
        // Act
        cart.addItem(hat);
        cart.addItem(pants);
        cart.addItem(jacket);
        // Assert
        assertThat(cart.getTotalPrice()).isEqualTo(1800.0);
        assertThat(cart.getItemCount()).isEqualTo(3);
    }

}