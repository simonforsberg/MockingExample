package com.example.shop;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ShoppingCartTest {

    @Test
    void newCart_shouldHaveZeroItems() {
        // Arrange
        ShoppingCart cart = new ShoppingCart();
        // Act
        int itemCount = cart.getItemCount();
        // Assert
        assertThat(itemCount).isZero();
    }

    @Test
    void addItem_shouldUpdateTotalPrice() {
        // Arrange
        ShoppingCart cart = new ShoppingCart();
        // Act
        cart.addItem("Keps", 200);
        // Assert
        assertThat(cart.getTotalPrice()).isEqualTo(200);
    }

}