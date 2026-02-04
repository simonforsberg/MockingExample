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
    void addItem_shouldUpdateTotalPrice() {
        // Act
        cart.addItem("Keps", 200.0);
        // Assert
        assertThat(cart.getTotalPrice()).isEqualTo(200.0);
    }

    @Test
    void addItem_shouldIncreaseItemCount() {
        // Act
        cart.addItem("Keps", 200.0);
        // Assert
        assertThat(cart.getItemCount()).isEqualTo(1);
    }

}