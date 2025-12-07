package com.poly.ban_giay_app.models;

import java.io.Serializable;
import java.util.List;

/**
 * Model đơn hàng
 */
public class Order implements Serializable {
    public String id;
    public String userId;
    public List<OrderItem> items;
    public long tongTien;
    public String trangThai; // "pending", "confirmed", "shipping", "delivered", "cancelled"
    public String diaChiGiaoHang;
    public String soDienThoai;
    public String ghiChu;
    public String createdAt;
    public String updatedAt;

    public Order() {
    }

    public static class OrderItem implements Serializable {
        public String sanPhamId;
        public String tenSanPham;
        public int soLuong;
        public String kichThuoc;
        public long gia;

        public OrderItem() {
        }
    }

    /**
     * Lấy tên trạng thái hiển thị
     */
    public String getTrangThaiDisplay() {
        switch (trangThai) {
            case "pending":
                return "Chờ xác nhận";
            case "confirmed":
                return "Đã xác nhận";
            case "shipping":
                return "Đang giao hàng";
            case "delivered":
                return "Đã giao hàng";
            case "cancelled":
                return "Đã hủy";
            default:
                return trangThai;
        }
    }
}

