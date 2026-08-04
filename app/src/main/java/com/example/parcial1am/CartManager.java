package com.example.parcial1am;

import java.util.ArrayList;
import java.util.List;

public class CartManager {
    private static final List<CartItem> cartItems = new ArrayList<>();

    public static void addProduct(Product product) {
        addProduct(product, 1);
    }

    public static void addProduct(Product product, int quantity) {
        CartItem existingItem = findItemByProductId(product.getId());

        if (existingItem != null) {
            for (int i = 0; i < quantity; i++) {
                existingItem.increaseQuantity();
            }
        } else {
            cartItems.add(new CartItem(product, quantity));
        }
    }

    public static List<CartItem> getItems() {
        return cartItems;
    }

    public static void increaseQuantity(String productId) {
        CartItem item = findItemByProductId(productId);

        if (item != null) {
            item.increaseQuantity();
        }
    }

    public static void decreaseQuantity(String productId) {
        CartItem item = findItemByProductId(productId);

        if (item != null) {
            item.decreaseQuantity();
        }
    }

    public static void removeProduct(String productId) {
        CartItem item = findItemByProductId(productId);

        if (item != null) {
            cartItems.remove(item);
        }
    }

    public static void clearCart() {
        cartItems.clear();
    }

    public static boolean isEmpty() {
        return cartItems.isEmpty();
    }

    public static double getTotal() {
        double total = 0;

        for (CartItem item : cartItems) {
            total += item.getTotalPrice();
        }

        return total;
    }

    private static CartItem findItemByProductId(String productId) {
        for (CartItem item : cartItems) {
            if (item.getProduct().getId().equals(productId)) {
                return item;
            }
        }

        return null;
    }
}