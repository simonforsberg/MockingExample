package com.example.shop;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Unit tests for {@link ShoppingCart}.
 * <p>
 * Tests cover adding products, removing products,
 * quantity tracking, discount application, and validation.
 * Organized by feature using nested classes.
 */
class ShoppingCartTest {

    private ShoppingCart cart;

    @BeforeEach
    void setUp() {
        cart = new ShoppingCart();
    }

    /**
     * Tests for basic cart initialization and state.
     */
    @Nested
    @DisplayName("Cart initialization")
    class CartInitializationTests {

        /**
         * Verifies that a newly created cart has zero items.
         */
        @Test
        void newCart_shouldHaveZeroItems() {
            // Act
            int itemCount = cart.getItemCount();
            // Assert
            assertThat(itemCount).isZero();
        }

        /**
         * Verifies that a newly created cart has zero total price.
         */
        @Test
        void newCart_shouldHaveZeroTotal() {
            // Act
            double total = cart.getTotalPrice();
            // Assert
            assertThat(total).isZero();
        }
    }

    /**
     * Tests for {@link ShoppingCart#addItem(Product)}.
     * <p>
     * Covers adding single items, multiple items,
     * quantity tracking, and validation.
     */
    @Nested
    @DisplayName("addItem() tests")
    class AddItemTests {

        /**
         * Verifies that adding a product updates the total price.
         */
        @Test
        void addItem_shouldUpdateTotalPrice() {
            // Arrange
            Product keps = new Product("Hat", 250.0);
            // Act
            cart.addItem(keps);
            // Assert
            assertThat(cart.getTotalPrice()).isEqualTo(250.0);
        }

        /**
         * Verifies that adding a product increases the item count.
         */
        @Test
        void addItem_shouldIncreaseItemCount() {
            // Arrange
            Product keps = new Product("Hat", 250.0);
            // Act
            cart.addItem(keps);
            // Assert
            assertThat(cart.getItemCount()).isEqualTo(1);
        }

        /**
         * Verifies that adding multiple different products
         * updates the total price correctly.
         */
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

        /**
         * Verifies that adding the same product twice increases quantity
         * instead of creating duplicates.
         */
        @Test
        void addSameProductTwice_shouldIncreaseQuantity() {
            // Arrange
            Product hat = new Product("Hat", 250.0);
            // Act
            cart.addItem(hat);
            cart.addItem(hat);
            // Assert
            assertThat(cart.getItemCount()).isEqualTo(1);
            assertThat(cart.getQuantity(hat)).isEqualTo(2);
            assertThat(cart.getTotalPrice()).isEqualTo(500.0);
        }

        /**
         * Verifies that adding a null product throws {@link IllegalArgumentException}.
         */
        @Test
        void addItem_shouldThrowException_whenProductIsNull() {
            // Act + Assert
            assertThatThrownBy(() -> cart.addItem(null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Product cannot be null");
        }
    }

    /**
     * Tests for {@link ShoppingCart#removeItem(Product)}.
     * <p>
     * Covers removing items, quantity updates, and validation.
     */
    @Nested
    @DisplayName("removeItem() tests")
    class RemoveItemTests {

        /**
         * Verifies that removing a product decreases the item count
         * when the quantity is greater than one.
         */
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
            assertThat(cart.getQuantity(hat)).isEqualTo(1);
        }

        /**
         * Verifies that removing a product decreases its quantity
         * when the quantity is greater than one.
         */
        @Test
        void removeItem_shouldDecreaseQuantity_whenQuantityGreaterThanOne() {
            // Arrange
            Product hat = new Product("Hat", 250.0);
            cart.addItem(hat);
            cart.addItem(hat);
            cart.addItem(hat);
            // Act
            cart.removeItem(hat);
            // Assert
            assertThat(cart.getItemCount()).isEqualTo(1);
            assertThat(cart.getQuantity(hat)).isEqualTo(2);
            assertThat(cart.getTotalPrice()).isEqualTo(500.0);
        }

        /**
         * Verifies that removing a product completely removes it
         * from the cart when the quantity is one.
         */
        @Test
        void removeItem_shouldRemoveItemFromCart_whenQuantityIsOne() {
            // Arrange
            Product hat = new Product("Hat", 250.0);
            cart.addItem(hat);
            // Act
            boolean removed = cart.removeItem(hat);
            // Assert
            assertThat(removed).isTrue();
            assertThat(cart.getItemCount()).isZero();
            assertThat(cart.getQuantity(hat)).isZero();
        }

        /**
         * Verifies that removing a product updates the total price correctly.
         */
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

        /**
         * Verifies that attempting to remove a product not in the cart returns false.
         */
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

        /**
         * Verifies that removing a null product throws {@link IllegalArgumentException}.
         */
        @Test
        void removeItem_shouldThrowException_whenProductIsNull() {
            // Act + Assert
            assertThatThrownBy(() -> cart.removeItem(null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Product cannot be null");
        }
    }

    /**
     * Tests for {@link ShoppingCart#getQuantity(Product)}.
     * <p>
     * Covers quantity retrieval for products in and out of cart.
     */
    @Nested
    @DisplayName("getQuantity() tests")
    class GetQuantityTests {

        /**
         * Verifies that getQuantity returns the correct count for a product.
         */
        @Test
        void getQuantity_shouldReturnCorrectCount() {
            // Arrange
            Product hat = new Product("Hat", 250.0);
            cart.addItem(hat);
            cart.addItem(hat);
            cart.addItem(hat);
            // Act
            int quantity = cart.getQuantity(hat);
            // Assert
            assertThat(quantity).isEqualTo(3);
        }

        /**
         * Verifies that getQuantity returns zero for a product not in the cart.
         */
        @Test
        void getQuantity_shouldReturnZero_whenItemNotInCart() {
            // Arrange
            Product hat = new Product("Hat", 250.0);
            // Act
            int quantity = cart.getQuantity(hat);
            // Assert
            assertThat(quantity).isZero();
        }
    }

    /**
     * Tests for {@link ShoppingCart#updateQuantity(Product, int)}.
     * <p>
     * Covers setting quantity, removing products, and validation.
     */
    @Nested
    @DisplayName("updateQuantity() tests")
    class UpdateQuantityTests {

        /**
         * Verifies that updateQuantity sets the new quantity correctly.
         */
        @Test
        void updateQuantity_shouldSetNewQuantity() {
            // Arrange
            Product hat = new Product("Hat", 250.0);
            cart.addItem(hat);
            // Act
            cart.updateQuantity(hat, 4);
            // Assert
            assertThat(cart.getQuantity(hat)).isEqualTo(4);
            assertThat(cart.getTotalPrice()).isEqualTo(1000.0);
        }

        /**
         * Verifies that updateQuantity removes the product when quantity is set to zero.
         */
        @Test
        void updateQuantity_shouldRemoveProduct_whenQuantityIsZero() {
            // Arrange
            Product hat = new Product("Hat", 250.0);
            cart.addItem(hat);
            // Act
            cart.updateQuantity(hat, 0);
            // Assert
            assertThat(cart.getItemCount()).isZero();
            assertThat(cart.getQuantity(hat)).isZero();
        }

        /**
         * Verifies that updateQuantity throws exception when product is null.
         */
        @Test
        void updateQuantity_shouldThrowException_whenProductIsNull() {
            // Act & Assert
            assertThatThrownBy(() -> cart.updateQuantity(null, 4))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Product cannot be null");
        }

        /**
         * Verifies that updateQuantity throws an exception when quantity is negative.
         */
        @Test
        void updateQuantity_shouldThrowException_whenQuantityIsNegative() {
            // Arrange
            Product hat = new Product("Hat", 250.0);
            cart.addItem(hat);
            // Act & Assert
            assertThatThrownBy(() -> cart.updateQuantity(hat, -1))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Quantity cannot be negative");
        }
    }

    /**
     * Tests for {@link ShoppingCart#applyDiscount(double)}.
     * <p>
     * Covers discount application with various percentages and validation.
     */
    @Nested
    @DisplayName("applyDiscount() tests")
    class ApplyDiscountTests {

        /**
         * Verifies that applyDiscount calculates the correct total for various discount percentages.
         * <p>
         * Tests 20% discount, 0% discount, and 100% discount.
         */
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

        /**
         * Provides test data for discount calculation tests.
         */
        static Stream<Arguments> discountCases() {
            return Stream.of(
                    Arguments.of(0.20, 200.0),
                    Arguments.of(0.0, 250.0),
                    Arguments.of(1.0, 0.0)
            );
        }

        /**
         * Verifies that applyDiscount throws an exception when the discount is greater than 100%.
         */
        @Test
        void applyDiscount_shouldThrowException_whenDiscountIsGreaterThan100Percent() {
            // Act + Assert
            assertThatThrownBy(() -> cart.applyDiscount(1.1))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Discount cannot be greater than 100%");
        }

        /**
         * Verifies that applyDiscount throws an exception when the discount is negative.
         */
        @Test
        void applyDiscount_shouldThrowException_whenDiscountIsNegative() {
            // Act + Assert
            assertThatThrownBy(() -> cart.applyDiscount(-0.2))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Discount cannot be negative");
        }
    }

    /**
     * Tests for {@link Product} validation.
     * <p>
     * Covers validation of product name and price.
     */
    @Nested
    @DisplayName("Product validation tests")
    class ProductValidationTests {

        /**
         * Verifies that creating a product with a null name throws an exception.
         */
        @Test
        void newProduct_shouldThrowException_whenNameIsNull() {
            // Act + Assert
            assertThatThrownBy(() -> new Product(null, 100.0))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Product name cannot be null or blank");
        }

        /**
         * Verifies that creating a product with a blank name throws an exception.
         */
        @Test
        void newProduct_shouldThrowException_whenNameIsBlank() {
            // Act + Assert
            assertThatThrownBy(() -> new Product(" ", 100.0))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Product name cannot be null or blank");
        }

        /**
         * Verifies that creating a product with a negative price throws an exception.
         */
        @Test
        void newProduct_shouldThrowException_whenPriceIsNegative() {
            // Act + Assert
            assertThatThrownBy(() -> new Product("Hat", -250.0))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Price cannot be negative");
        }
    }

    /**
     * Tests for {@link Product#equals(Object)} and {@link Product#hashCode()}.
     * <p>
     * Verifies equality based on name and price.
     */
    @Nested
    @DisplayName("Product equality tests")
    class ProductEqualityTests {

        /**
         * Verifies that two products with the same name and price are equal.
         */
        @Test
        void twoProducts_shouldBeEqual_whenNameAndPriceAreSame() {
            // Arrange
            Product hat1 = new Product("Hat", 250.0);
            Product hat2 = new Product("Hat", 250.0);
            // Assert
            assertThat(hat1).isEqualTo(hat2);
            assertThat(hat1).hasSameHashCodeAs(hat2);
        }

        /**
         * Verifies that two products with different names are not equal.
         */
        @Test
        void twoProducts_shouldNotBeEqual_whenNamesDiffer() {
            // Arrange
            Product hat = new Product("Hat", 250.0);
            Product tshirt = new Product("T-shirt", 250.0);
            // Assert
            assertThat(hat).isNotEqualTo(tshirt);
        }

        /**
         * Verifies that two products with different prices are not equal.
         */
        @Test
        void twoProducts_shouldNotBeEqual_whenPricesDiffer() {
            // Arrange
            Product hat1 = new Product("Hat", 250.0);
            Product hat2 = new Product("Hat", 350.0);
            // Assert
            assertThat(hat1).isNotEqualTo(hat2);
        }
    }

    /**
     * Tests for additional cart functionality.
     * <p>
     * Covers getTotalItems() and clear() methods.
     */
    @Nested
    @DisplayName("Additional cart operations")
    class AdditionalCartOperationsTests {

        /**
         * Verifies that getTotalItems returns the sum of all quantities.
         */
        @Test
        void getTotalItems_shouldReturnSumOfAllQuantities() {
            // Arrange
            Product hat = new Product("Hat", 250.0);
            Product pants = new Product("Pants", 700.0);
            Product jacket = new Product("Jacket", 900.0);
            cart.addItem(hat);
            cart.addItem(hat);
            cart.addItem(pants);
            cart.addItem(pants);
            cart.addItem(jacket);
            // Act
            int totalItems = cart.getTotalItems();
            // Assert
            assertThat(cart.getItemCount()).isEqualTo(3);
            assertThat(totalItems).isEqualTo(5);
        }

        /**
         * Verifies that clear removes all items from the cart.
         */
        @Test
        void clear_shouldRemoveAllItemsFromCart() {
            // Arrange
            Product hat = new Product("Hat", 250.0);
            Product pants = new Product("Pants", 700.0);
            cart.addItem(hat);
            cart.addItem(pants);
            cart.applyDiscount(0.2);
            // Act
            cart.clear();
            // Assert
            assertThat(cart.getItemCount()).isZero();
            assertThat(cart.getTotalPrice()).isZero();
        }
    }
}