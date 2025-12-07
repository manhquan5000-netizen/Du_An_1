package com.poly.ban_giay_app.network.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Model để map với dữ liệu giỏ hàng từ MongoDB
 * Backend trả về Cart có user_id và items (array)
 */
public class CartResponse {
    @SerializedName("_id")
    private String id;

    @SerializedName("user_id")
    private String userId;

    @SerializedName("items")
    private List<CartItemResponse> items;

    @SerializedName("createdAt")
    private String createdAt;

    @SerializedName("updatedAt")
    private String updatedAt;

    /**
     * Model cho từng item trong giỏ hàng
     */
    public static class CartItemResponse {
        @SerializedName("san_pham_id")
        private String sanPhamId;

        @SerializedName("so_luong")
        private Integer soLuong;

        @SerializedName("kich_thuoc")
        private String kichThuoc;

        @SerializedName("gia")
        private Integer gia;

        // Product info khi populate (backend sẽ populate vào field san_pham_id)
        // Khi populate, MongoDB sẽ thay thế ObjectId bằng object Product
        private ProductResponse sanPhamIdObject;

        public String getSanPhamId() {
            return sanPhamId;
        }

        public void setSanPhamId(String sanPhamId) {
            this.sanPhamId = sanPhamId;
        }

        public Integer getSoLuong() {
            return soLuong;
        }

        public void setSoLuong(Integer soLuong) {
            this.soLuong = soLuong;
        }

        public String getKichThuoc() {
            return kichThuoc;
        }

        public void setKichThuoc(String kichThuoc) {
            this.kichThuoc = kichThuoc;
        }

        public Integer getGia() {
            return gia;
        }

        public void setGia(Integer gia) {
            this.gia = gia;
        }

        // Getter cho product khi được populate
        public ProductResponse getProduct() {
            // Nếu san_pham_id là string, return null
            // Nếu san_pham_id là object (populated), return nó
            if (sanPhamIdObject != null) {
                return sanPhamIdObject;
            }
            return null;
        }

        public void setSanPhamIdObject(ProductResponse product) {
            this.sanPhamIdObject = product;
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public List<CartItemResponse> getItems() {
        return items;
    }

    public void setItems(List<CartItemResponse> items) {
        this.items = items;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }
}

