package com.poly.ban_giay_app.network.request;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Request để tạo đơn hàng
 */
public class OrderRequest {
    @SerializedName("user_id")
    private String userId;

    @SerializedName("items")
    private List<OrderItemRequest> items;

    @SerializedName("tong_tien")
    private Long tongTien;

    @SerializedName("dia_chi_giao_hang")
    private String diaChiGiaoHang;

    @SerializedName("so_dien_thoai")
    private String soDienThoai;

    @SerializedName("ghi_chu")
    private String ghiChu;

    public static class OrderItemRequest {
        @SerializedName("san_pham_id")
        private String sanPhamId;

        @SerializedName("ten_san_pham")
        private String tenSanPham;

        @SerializedName("so_luong")
        private Integer soLuong;

        @SerializedName("kich_thuoc")
        private String kichThuoc;

        @SerializedName("gia")
        private Long gia;

        public OrderItemRequest() {
        }

        public OrderItemRequest(String sanPhamId, String tenSanPham, Integer soLuong, String kichThuoc, Long gia) {
            this.sanPhamId = sanPhamId;
            this.tenSanPham = tenSanPham;
            this.soLuong = soLuong;
            this.kichThuoc = kichThuoc;
            this.gia = gia;
        }

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

        public Long getGia() {
            return gia;
        }

        public void setGia(Long gia) {
            this.gia = gia;
        }
    }

    public OrderRequest() {
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public List<OrderItemRequest> getItems() {
        return items;
    }

    public void setItems(List<OrderItemRequest> items) {
        this.items = items;
    }

    public Long getTongTien() {
        return tongTien;
    }

    public void setTongTien(Long tongTien) {
        this.tongTien = tongTien;
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
}

