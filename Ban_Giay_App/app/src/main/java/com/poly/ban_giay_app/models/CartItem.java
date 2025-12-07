package com.poly.ban_giay_app.models;

import java.io.Serializable;

public class CartItem implements Serializable {
    public Product product;
    public String size;
    public int quantity;
    public boolean isSelected;

    public CartItem(Product product, String size, int quantity) {
        this.product = product;
        this.size = size;
        this.quantity = quantity;
        this.isSelected = false;
    }

    /**
     * Tính tổng giá của item này (giá * số lượng)
     */
    public long getTotalPrice() {
        try {
            // Lấy giá từ priceNew (bỏ ký tự ₫ và tất cả dấu chấm)
            String priceStr = product.priceNew.replace("₫", "").replaceAll("\\.", "");
            long price = Long.parseLong(priceStr);
            return price * quantity;
        } catch (Exception e) {
            return 0;
        }
    }
}

