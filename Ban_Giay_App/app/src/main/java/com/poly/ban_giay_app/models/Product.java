package com.poly.ban_giay_app.models;

import java.io.Serializable;

public class Product implements Serializable {
    public String id; // ID sản phẩm từ MongoDB
    public String name;
    public String priceOld;
    public String priceNew;
    public int imageRes; // Resource ID từ drawable (dùng khi có ảnh local)
    public String imageUrl; // URL ảnh từ server (dùng khi load từ API)
    public String brand; // Thương hiệu
    public double rating; // Đánh giá
    public String description; // Mô tả
    public String category; // Danh mục

    // Constructor không tham số
    public Product() {
        this.id = null;
        this.name = "";
        this.priceOld = "";
        this.priceNew = "";
        this.imageRes = 0;
        this.imageUrl = null;
        this.brand = "";
        this.rating = 0.0;
        this.description = "";
        this.category = "";
    }

    public Product(String name, String priceOld, String priceNew, int imageRes) {
        this.id = null;
        this.name = name;
        this.priceOld = priceOld;
        this.priceNew = priceNew;
        this.imageRes = imageRes;
        this.imageUrl = null;
        this.brand = "";
        this.rating = 0.0;
        this.description = "";
        this.category = "";
    }

    public Product(String name, String priceOld, String priceNew, String imageUrl) {
        this.id = null;
        this.name = name;
        this.priceOld = priceOld;
        this.priceNew = priceNew;
        this.imageUrl = imageUrl;
        this.imageRes = 0;
        this.brand = "";
        this.rating = 0.0;
        this.description = "";
        this.category = "";
    }
}


