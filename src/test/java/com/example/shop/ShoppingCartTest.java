package com.example.shop;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

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
        Product keps = new Product("Hat", 250.0);
        // Act
        cart.addItem(keps);
        // Assert
        assertThat(cart.getTotalPrice()).isEqualTo(250.0);
    }

    @Test
    void addItem_shouldIncreaseItemCount() {
        // Arrange
        Product keps = new Product("Hat", 250.0);
        // Act
        cart.addItem(keps);
        // Assert
        assertThat(cart.getItemCount()).isEqualTo(1);
    }

    @Test
    void addMultipleItems_shouldUpdateTotalPrice() {
        // Arrange
        Product hat = new Product("Hat", 250.0);
        Product pants = new Product("Pants", 700.0);
        Product jacket = new Product("Jacket", 900.0);
        // Act
        cart.addItem(hat);
        cart.addItem(pants);
        cart.addItem(jacket);
        // Assert
        assertThat(cart.getTotalPrice()).isEqualTo(1850.0);
    }

    @Test
    void addItem_shouldThrowException_whenProductIsNull() {
        // Act + Assert
        assertThatThrownBy(() -> cart.addItem(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Product cannot be null");
    }

    @Test
    void removeItem_shouldDecreaseItemCount() {
        // Arrange
        Product hat = new Product("Hat", 250.0);
        cart.addItem(hat);
        cart.addItem(hat);
        // Act
        cart.removeItem(hat);
        // Assert
        assertThat(cart.getItemCount()).isEqualTo(1);
    }

    @Test
    void removeItem_shouldUpdateTotalPrice() {
        // Arrange
        Product hat = new Product("Hat", 250.0);
        Product pants = new Product("Pants", 700.0);
        cart.addItem(hat);
        cart.addItem(pants);
        // Act
        cart.removeItem(hat);
        // Assert
        assertThat(cart.getTotalPrice()).isEqualTo(700.0);
    }

    @Test
    void removeItem_shouldReturnFalse_whenItemNotInCart() {
        // Arrange
        Product hat = new Product("Hat", 250.0);
        // Act
        boolean removed = cart.removeItem(hat);
        // Assert
        assertThat(removed).isFalse();
        assertThat(cart.getItemCount()).isZero();
    }

    @Test
    void removeItem_shouldThrowException_whenProductIsNull() {
        // Act + Assert
        assertThatThrownBy(() -> cart.removeItem(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Product cannot be null");
    }

    @Test
    void applyDiscount_shouldReduceTotalPrice() {
        // Arrange
        Product hat = new Product("Hat", 250.0);
        cart.addItem(hat);
        // Act
        cart.applyDiscount(0.20);
        // Assert
        assertThat(cart.getTotalPrice()).isEqualTo(200.0);
    }

    @Test
    void applyDiscount_shouldNotChangeTotal_whenNoDiscountApplied() {
        // Arrange
        Product hat = new Product("Hat", 250.0);
        cart.addItem(hat);
        // Act
        cart.applyDiscount(0.0);
        // Assert
        assertThat(cart.getTotalPrice()).isEqualTo(250.0);
    }

    @Test
    void applyDiscount_shouldThrowException_whenDiscountIsGreaterThan100Percent() {
        // Act + Assert
        assertThatThrownBy(() -> cart.applyDiscount(1.1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Discount cannot be greater than 100%");
    }

    @Test
    void applyDiscount_shouldThrowException_whenDiscountIsNegative() {
        // Act + Assert
        assertThatThrownBy(() -> cart.applyDiscount(-0.2))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Discount cannot be negative");
    }

    @Test
    void newProduct_shouldThrowException_whenNameIsNull() {
        // Act + Assert
        assertThatThrownBy(() -> new Product(null, 100.0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Product name cannot be null or blank");
    }

    @Test
    void newProduct_shouldThrowException_whenNameIsBlank() {
        // Act + Assert
        assertThatThrownBy(() -> new Product(" ", 100.0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Product name cannot be null or blank");
    }

    @Test
    void newProduct_shouldThrowException_whenPriceIsNegative() {
        // Act + Assert
        assertThatThrownBy(() -> new Product("Hat", -250.0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Price cannot be negative");
    }

}