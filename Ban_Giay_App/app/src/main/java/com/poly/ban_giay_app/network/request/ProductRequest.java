package com.poly.ban_giay_app.network.request;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Request để tạo hoặc cập nhật sản phẩm
 */
public class ProductRequest {
    @SerializedName("ten_san_pham")
    private String tenSanPham;

    @SerializedName("gia_goc")
    private Integer giaGoc;

    @SerializedName("gia_khuyen_mai")
    private Integer giaKhuyenMai;

    @SerializedName("hinh_anh")
    private String hinhAnh;

    @SerializedName("mo_ta")
    private String moTa;

    @SerializedName("thuong_hieu")
    private String thuongHieu;

    @SerializedName("danh_muc")
    private String danhMuc; // "nam", "nu", "unisex"

    @SerializedName("kich_thuoc")
    private List<String> kichThuoc;

    @SerializedName("so_luong_ton")
    private Integer soLuongTon;

    @SerializedName("danh_gia")
    private Double danhGia;

    @SerializedName("so_luong_da_ban")
    private Integer soLuongDaBan;

    @SerializedName("trang_thai")
    private String trangThai; // "active", "inactive"

    // Constructors
    public ProductRequest() {
    }

    public ProductRequest(String tenSanPham, Integer giaGoc, Integer giaKhuyenMai, String hinhAnh) {
        this.tenSanPham = tenSanPham;
        this.giaGoc = giaGoc;
        this.giaKhuyenMai = giaKhuyenMai;
        this.hinhAnh = hinhAnh;
    }

    // Getters and Setters
    public String getTenSanPham() {
        return tenSanPham;
    }

    public void setTenSanPham(String tenSanPham) {
        this.tenSanPham = tenSanPham;
    }

    public Integer getGiaGoc() {
        return giaGoc;
    }

    public void setGiaGoc(Integer giaGoc) {
        this.giaGoc = giaGoc;
    }

    public Integer getGiaKhuyenMai() {
        return giaKhuyenMai;
    }

    public void setGiaKhuyenMai(Integer giaKhuyenMai) {
        this.giaKhuyenMai = giaKhuyenMai;
    }

    public String getHinhAnh() {
        return hinhAnh;
    }

    public void setHinhAnh(String hinhAnh) {
        this.hinhAnh = hinhAnh;
    }

    public String getMoTa() {
        return moTa;
    }

    public void setMoTa(String moTa) {
        this.moTa = moTa;
    }

    public String getThuongHieu() {
        return thuongHieu;
    }

    public void setThuongHieu(String thuongHieu) {
        this.thuongHieu = thuongHieu;
    }

    public String getDanhMuc() {
        return danhMuc;
    }

    public void setDanhMuc(String danhMuc) {
        this.danhMuc = danhMuc;
    }

    public List<String> getKichThuoc() {
        return kichThuoc;
    }

    public void setKichThuoc(List<String> kichThuoc) {
        this.kichThuoc = kichThuoc;
    }

    public Integer getSoLuongTon() {
        return soLuongTon;
    }

    public void setSoLuongTon(Integer soLuongTon) {
        this.soLuongTon = soLuongTon;
    }

    public Double getDanhGia() {
        return danhGia;
    }

    public void setDanhGia(Double danhGia) {
        this.danhGia = danhGia;
    }

    public Integer getSoLuongDaBan() {
        return soLuongDaBan;
    }

    public void setSoLuongDaBan(Integer soLuongDaBan) {
        this.soLuongDaBan = soLuongDaBan;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }
}

