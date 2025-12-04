package com.poly.ban_giay_app;

import android.util.Log;

import com.poly.ban_giay_app.models.CartItem;
import com.poly.ban_giay_app.models.Product;

import java.util.ArrayList;
import java.util.List;

public class CartManager {
    private static CartManager instance;
    private List<CartItem> cartItems;

    private CartManager() {
        cartItems = new ArrayList<>();
    }

    public static CartManager getInstance() {
        if (instance == null) {
            instance = new CartManager();
        }
        return instance;
    }

    public List<CartItem> getCartItems() {
        return cartItems;
    }

    public void addToCart(Product product, String size, int quantity) {
        // Debug: Log thông tin sản phẩm khi add vào cart
        Log.d("CartManager", "=== Adding product to cart ===");
        Log.d("CartManager", "Product name: " + product.name);
        Log.d("CartManager", "Product imageUrl: " + product.imageUrl);
        Log.d("CartManager", "Product imageRes: " + product.imageRes);
        Log.d("CartManager", "Size: " + size + ", Quantity: " + quantity);
        
        // Kiểm tra xem sản phẩm với size này đã có trong giỏ chưa
        for (CartItem item : cartItems) {
            if (item.product.name.equals(product.name) && item.size.equals(size)) {
                // Nếu đã có, tăng số lượng
                item.quantity += quantity;
                Log.d("CartManager", "Product already in cart, increased quantity");
                return;
            }
        }
        // Nếu chưa có, thêm mới
        cartItems.add(new CartItem(product, size, quantity));
        Log.d("CartManager", "Product added to cart. Total items: " + cartItems.size());
    }

    public void removeFromCart(int position) {
        if (position >= 0 && position < cartItems.size()) {
            cartItems.remove(position);
        }
    }

    public void updateQuantity(int position, int quantity) {
        if (position >= 0 && position < cartItems.size()) {
            if (quantity > 0) {
                cartItems.get(position).quantity = quantity;
            } else {
                cartItems.remove(position);
            }
        }
    }

    public void setItemSelected(int position, boolean selected) {
        if (position >= 0 && position < cartItems.size()) {
            cartItems.get(position).isSelected = selected;
        }
    }

    public void selectAll(boolean selectAll) {
        for (CartItem item : cartItems) {
            item.isSelected = selectAll;
        }
    }

    public boolean areAllSelected() {
        if (cartItems.isEmpty()) return false;
        for (CartItem item : cartItems) {
            if (!item.isSelected) return false;
        }
        return true;
    }

    public long getTotalPrice() {
        long total = 0;
        for (CartItem item : cartItems) {
            if (item.isSelected) {
                total += item.getTotalPrice();
            }
        }
        return total;
    }

    public int getSelectedCount() {
        int count = 0;
        for (CartItem item : cartItems) {
            if (item.isSelected) {
                count++;
            }
        }
        return count;
    }

    public void clearCart() {
        cartItems.clear();
    }
}

