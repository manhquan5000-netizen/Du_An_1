package com.poly.ban_giay_app.network.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Model để map với dữ liệu đơn hàng từ MongoDB
 */
public class OrderResponse {
    @SerializedName("_id")
    private String id;

    @SerializedName("user_id")
    private String userId;

    @SerializedName("items")
    private List<OrderItemResponse> items;

    @SerializedName("tong_tien")
    private Integer tongTien;

    @SerializedName("trang_thai")
    private String trangThai;

    @SerializedName("dia_chi_giao_hang")
    private String diaChiGiaoHang;

    @SerializedName("so_dien_thoai")
    private String soDienThoai;

    @SerializedName("ghi_chu")
    private String ghiChu;

    @SerializedName("createdAt")
    private String createdAt;

    @SerializedName("updatedAt")
    private String updatedAt;

    public static class OrderItemResponse {
        @SerializedName("san_pham_id")
        private String sanPhamId;

        @SerializedName("ten_san_pham")
        private String tenSanPham;

        @SerializedName("so_luong")
        private Integer soLuong;

        @SerializedName("kich_thuoc")
        private String kichThuoc;

        @SerializedName("gia")
        private Integer gia;

        public String getSanPhamId() {
            return sanPhamId;
        }

        public void setSanPhamId(String sanPhamId) {
            this.sanPhamId = sanPhamId;
        }

        public String getTenSanPham() {
            return tenSanPham;
        }

        public void setTenSanPham(String tenSanPham) {
            this.tenSanPham = tenSanPham;
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

    public List<OrderItemResponse> getItems() {
        return items;
    }

    public void setItems(List<OrderItemResponse> items) {
        this.items = items;
    }

    public Integer getTongTien() {
        return tongTien;
    }

    public void setTongTien(Integer tongTien) {
        this.tongTien = tongTien;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }

    public String getDiaChiGiaoHang() {
        return diaChiGiaoHang;
    }

    public void setDiaChiGiaoHang(String diaChiGiaoHang) {
        this.diaChiGiaoHang = diaChiGiaoHang;
    }

    public String getSoDienThoai() {
        return soDienThoai;
    }

    public void setSoDienThoai(String soDienThoai) {
        this.soDienThoai = soDienThoai;
    }

    public String getGhiChu() {
        return ghiChu;
    }

    public void setGhiChu(String ghiChu) {
        this.ghiChu = ghiChu;
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

    /**
     * Lấy tên trạng thái hiển thị
     */
    public String getTrangThaiDisplay() {
        if (trangThai == null) return "Không xác định";
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

