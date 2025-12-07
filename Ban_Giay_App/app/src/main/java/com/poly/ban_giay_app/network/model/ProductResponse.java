package com.poly.ban_giay_app.network.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Model để map với dữ liệu sản phẩm từ MongoDB
 * Hỗ trợ nhiều tên field khác nhau để tương thích với các cấu trúc backend khác nhau
 */
public class ProductResponse {
    @SerializedName("_id")
    private String id;

    // Tên sản phẩm - hỗ trợ nhiều tên field
    @SerializedName("ten_san_pham")
    private String tenSanPham;
    
    @SerializedName("tenSanPham")
    private String tenSanPhamCamel;
    
    @SerializedName("name")
    private String name;
    
    @SerializedName("product_name")
    private String productName;

    // Giá gốc - hỗ trợ nhiều tên field (từ MongoDB: gia_goc)
    @SerializedName("gia_goc")
    private Integer giaGoc;
    
    @SerializedName("giaGoc")
    private Integer giaGocCamel;
    
    @SerializedName("gia_cu")
    private String giaCu;
    
    @SerializedName("giaCu")
    private String giaCuCamel;
    
    @SerializedName("price_old")
    private String priceOld;
    
    @SerializedName("priceOld")
    private String priceOldCamel;
    
    @SerializedName("old_price")
    private String oldPrice;

    // Giá khuyến mãi - hỗ trợ nhiều tên field (từ MongoDB: gia_khuyen_mai)
    @SerializedName("gia_khuyen_mai")
    private Integer giaKhuyenMai;
    
    @SerializedName("giaKhuyenMai")
    private Integer giaKhuyenMaiCamel;
    
    @SerializedName("gia_moi")
    private String giaMoi;
    
    @SerializedName("giaMoi")
    private String giaMoiCamel;
    
    @SerializedName("price_new")
    private String priceNew;
    
    @SerializedName("priceNew")
    private String priceNewCamel;
    
    @SerializedName("new_price")
    private String newPrice;
    
    @SerializedName("price")
    private String price;

    // Hình ảnh - hỗ trợ nhiều tên field
    @SerializedName("hinh_anh")
    private String hinhAnh;
    
    @SerializedName("hinhAnh")
    private String hinhAnhCamel;
    
    @SerializedName("image")
    private String image;
    
    @SerializedName("image_url")
    private String imageUrl;
    
    @SerializedName("imageUrl")
    private String imageUrlCamel;
    
    @SerializedName("anh")
    private String anh;

    // Danh mục
    @SerializedName("danh_muc")
    private String danhMuc;
    
    @SerializedName("danhMuc")
    private String danhMucCamel;
    
    @SerializedName("category")
    private String category;

    // Mô tả
    @SerializedName("mo_ta")
    private String moTa;
    
    @SerializedName("moTa")
    private String moTaCamel;
    
    @SerializedName("description")
    private String description;

    // Thương hiệu
    @SerializedName("thuong_hieu")
    private String thuongHieu;
    
    @SerializedName("thuongHieu")
    private String thuongHieuCamel;
    
    @SerializedName("brand")
    private String brand;

    // Kích thước
    @SerializedName("kich_thuoc")
    private List<String> kichThuoc;
    
    @SerializedName("kichThuoc")
    private List<String> kichThuocCamel;
    
    @SerializedName("sizes")
    private List<String> sizes;

    // Số lượng tồn kho
    @SerializedName("so_luong_ton")
    private Integer soLuongTon;
    
    @SerializedName("soLuongTon")
    private Integer soLuongTonCamel;
    
    @SerializedName("stock")
    private Integer stock;

    // Đánh giá
    @SerializedName("danh_gia")
    private Double danhGia;
    
    @SerializedName("danhGia")
    private Double danhGiaCamel;
    
    @SerializedName("rating")
    private Double rating;

    // Số lượng đã bán
    @SerializedName("so_luong_da_ban")
    private Integer soLuongDaBan;
    
    @SerializedName("soLuongDaBan")
    private Integer soLuongDaBanCamel;
    
    @SerializedName("sold")
    private Integer sold;

    // Trạng thái
    @SerializedName("trang_thai")
    private String trangThai;
    
    @SerializedName("trangThai")
    private String trangThaiCamel;
    
    @SerializedName("status")
    private String status;

    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        // Ưu tiên các field theo thứ tự
        if (tenSanPham != null && !tenSanPham.isEmpty()) return tenSanPham;
        if (tenSanPhamCamel != null && !tenSanPhamCamel.isEmpty()) return tenSanPhamCamel;
        if (name != null && !name.isEmpty()) return name;
        if (productName != null && !productName.isEmpty()) return productName;
        return "";
    }

    public String getPriceOld() {
        // Ưu tiên các field theo thứ tự (từ MongoDB: gia_goc)
        if (giaGoc != null) return String.valueOf(giaGoc);
        if (giaGocCamel != null) return String.valueOf(giaGocCamel);
        if (giaCu != null && !giaCu.isEmpty()) return giaCu;
        if (giaCuCamel != null && !giaCuCamel.isEmpty()) return giaCuCamel;
        if (priceOld != null && !priceOld.isEmpty()) return priceOld;
        if (priceOldCamel != null && !priceOldCamel.isEmpty()) return priceOldCamel;
        if (oldPrice != null && !oldPrice.isEmpty()) return oldPrice;
        return null;
    }

    public String getPriceNew() {
        // Ưu tiên các field theo thứ tự (từ MongoDB: gia_khuyen_mai)
        if (giaKhuyenMai != null) return String.valueOf(giaKhuyenMai);
        if (giaKhuyenMaiCamel != null) return String.valueOf(giaKhuyenMaiCamel);
        if (giaMoi != null && !giaMoi.isEmpty()) return giaMoi;
        if (giaMoiCamel != null && !giaMoiCamel.isEmpty()) return giaMoiCamel;
        if (priceNew != null && !priceNew.isEmpty()) return priceNew;
        if (priceNewCamel != null && !priceNewCamel.isEmpty()) return priceNewCamel;
        if (newPrice != null && !newPrice.isEmpty()) return newPrice;
        if (price != null && !price.isEmpty()) return price;
        return null;
    }
    
    // Getter cho Integer (để dùng trực tiếp)
    public Integer getGiaGoc() {
        if (giaGoc != null) return giaGoc;
        if (giaGocCamel != null) return giaGocCamel;
        return null;
    }
    
    public void setGiaGoc(Integer giaGoc) {
        this.giaGoc = giaGoc;
    }
    
    public Integer getGiaKhuyenMai() {
        if (giaKhuyenMai != null) return giaKhuyenMai;
        if (giaKhuyenMaiCamel != null) return giaKhuyenMaiCamel;
        return null;
    }
    
    public void setGiaKhuyenMai(Integer giaKhuyenMai) {
        this.giaKhuyenMai = giaKhuyenMai;
    }
    
    public void setName(String name) {
        this.tenSanPham = name;
    }
    
    public void setHinhAnh(String hinhAnh) {
        this.hinhAnh = hinhAnh;
    }
    
    public void setMoTa(String moTa) {
        this.moTa = moTa;
    }
    
    public void setThuongHieu(String thuongHieu) {
        this.thuongHieu = thuongHieu;
    }
    
    public void setDanhMuc(String danhMuc) {
        this.danhMuc = danhMuc;
    }
    
    public void setDanhGia(Double danhGia) {
        this.danhGia = danhGia;
    }

    public String getImageUrl() {
        // Ưu tiên các field theo thứ tự
        if (hinhAnh != null && !hinhAnh.isEmpty()) return hinhAnh;
        if (hinhAnhCamel != null && !hinhAnhCamel.isEmpty()) return hinhAnhCamel;
        if (imageUrl != null && !imageUrl.isEmpty()) return imageUrl;
        if (imageUrlCamel != null && !imageUrlCamel.isEmpty()) return imageUrlCamel;
        if (image != null && !image.isEmpty()) return image;
        if (anh != null && !anh.isEmpty()) return anh;
        return "";
    }

    public String getCategory() {
        if (danhMuc != null && !danhMuc.isEmpty()) return danhMuc;
        if (danhMucCamel != null && !danhMucCamel.isEmpty()) return danhMucCamel;
        if (category != null && !category.isEmpty()) return category;
        return "";
    }

    public String getDescription() {
        if (moTa != null && !moTa.isEmpty()) return moTa;
        if (moTaCamel != null && !moTaCamel.isEmpty()) return moTaCamel;
        if (description != null && !description.isEmpty()) return description;
        return "";
    }

    public String getBrand() {
        if (thuongHieu != null && !thuongHieu.isEmpty()) return thuongHieu;
        if (thuongHieuCamel != null && !thuongHieuCamel.isEmpty()) return thuongHieuCamel;
        if (brand != null && !brand.isEmpty()) return brand;
        return "";
    }

    public List<String> getKichThuoc() {
        if (kichThuoc != null) return kichThuoc;
        if (kichThuocCamel != null) return kichThuocCamel;
        if (sizes != null) return sizes;
        return null;
    }

    public Integer getSoLuongTon() {
        if (soLuongTon != null) return soLuongTon;
        if (soLuongTonCamel != null) return soLuongTonCamel;
        if (stock != null) return stock;
        return 0;
    }

    public Double getDanhGia() {
        if (danhGia != null) return danhGia;
        if (danhGiaCamel != null) return danhGiaCamel;
        if (rating != null) return rating;
        return 0.0;
    }

    public Integer getSoLuongDaBan() {
        if (soLuongDaBan != null) return soLuongDaBan;
        if (soLuongDaBanCamel != null) return soLuongDaBanCamel;
        if (sold != null) return sold;
        return 0;
    }

    public String getTrangThai() {
        if (trangThai != null && !trangThai.isEmpty()) return trangThai;
        if (trangThaiCamel != null && !trangThaiCamel.isEmpty()) return trangThaiCamel;
        if (status != null && !status.isEmpty()) return status;
        return "active";
    }

    /**
     * Debug method: Log tất cả các field ảnh để kiểm tra API trả về field nào
     */
    public void logImageFields(String productName) {
        android.util.Log.d("ProductResponse", "=== DEBUG IMAGE FIELDS for: " + productName + " ===");
        android.util.Log.d("ProductResponse", "hinh_anh: " + (hinhAnh != null ? hinhAnh : "null"));
        android.util.Log.d("ProductResponse", "hinhAnh (camel): " + (hinhAnhCamel != null ? hinhAnhCamel : "null"));
        android.util.Log.d("ProductResponse", "image: " + (image != null ? image : "null"));
        android.util.Log.d("ProductResponse", "image_url: " + (imageUrl != null ? imageUrl : "null"));
        android.util.Log.d("ProductResponse", "imageUrl (camel): " + (imageUrlCamel != null ? imageUrlCamel : "null"));
        android.util.Log.d("ProductResponse", "anh: " + (anh != null ? anh : "null"));
        android.util.Log.d("ProductResponse", "getImageUrl() result: " + getImageUrl());
        android.util.Log.d("ProductResponse", "=========================================");
    }
}
