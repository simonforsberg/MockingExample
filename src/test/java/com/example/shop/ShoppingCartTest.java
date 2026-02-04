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

}