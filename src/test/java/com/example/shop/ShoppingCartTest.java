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

}