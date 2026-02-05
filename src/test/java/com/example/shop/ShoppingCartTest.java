package com.example.shop;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

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

    @ParameterizedTest(name = "discount={0} -> expectedTotal={1}")
    @MethodSource("discountCases")
    void applyDiscount_shouldCalculateCorrectTotal(double discount, double expectedTotal) {
        // Arrange
        Product hat = new Product("Hat", 250.0);
        cart.addItem(hat);
        // Act
        cart.applyDiscount(discount);
        // Assert
        assertThat(cart.getTotalPrice()).isEqualTo(expectedTotal);
    }

    static Stream<Arguments> discountCases() {
        return Stream.of(
                Arguments.of(0.20, 200.0),
                Arguments.of(0.0, 250.0),
                Arguments.of(1.0, 0.0)
        );
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

    @Test
    void twoProducts_shouldBeEqual_whenNameAndPriceAreSame() {
        // Arrange
        Product hat1 = new Product("Hat", 250.0);
        Product hat2 = new Product("Hat", 250.0);
        // Assert
        assertThat(hat1).isEqualTo(hat2);
        assertThat(hat1).hasSameHashCodeAs(hat2);
    }

    @Test
    void twoProducts_shouldNotBeEqual_whenNamesDiffer() {
        // Arrange
        Product hat = new Product("Hat", 250.0);
        Product tshirt = new Product("T-shirt", 250.0);
        // Assert
        assertThat(hat).isNotEqualTo(tshirt);
    }

    @Test
    void twoProducts_shouldNotBeEqual_whenPricesDiffer() {
        // Arrange
        Product hat1 = new Product("Hat", 250.0);
        Product hat2 = new Product("Hat", 350.0);
        // Assert
        assertThat(hat1).isNotEqualTo(hat2);
    }

}