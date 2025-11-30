const mongoose = require("mongoose");
const connectDB = require("./config/db");
const Product = require("./models/Product");
require("dotenv").config();

// Danh sách ảnh còn lại (từ giay6 đến giaymau)
const remainingImages = ["giay6", "giay5", "giay4", "giay3", "giay2", "giaymau"];

// Sản phẩm nam mới cần thêm
const newMenProducts = [
  {
    ten_san_pham: "Giày Thể Thao Nam Nike Air Force 1",
    gia_goc: 2800000,
    gia_khuyen_mai: 2200000,
    hinh_anh: "giay6",
    mo_ta: "Giày thể thao Nike Air Force 1 cổ điển với thiết kế đơn giản, thanh lịch. Chất liệu da cao cấp, đế cao su bền, phù hợp mọi hoạt động hàng ngày.",
    thuong_hieu: "Nike",
    danh_muc: "nam",
    kich_thuoc: ["40", "41", "42", "43", "44", "45"],
    so_luong_ton: 45,
    danh_gia: 4.6,
    so_luong_da_ban: 95,
    trang_thai: "active",
  },
  {
    ten_san_pham: "Giày Sneaker Nam Adidas Stan Smith",
    gia_goc: 2400000,
    gia_khuyen_mai: 1900000,
    hinh_anh: "giay5",
    mo_ta: "Giày sneaker Adidas Stan Smith với thiết kế cổ điển, phong cách minimal. Chất liệu da thật, đế cao su, phù hợp phong cách casual.",
    thuong_hieu: "Adidas",
    danh_muc: "nam",
    kich_thuoc: ["40", "41", "42", "43", "44"],
    so_luong_ton: 50,
    danh_gia: 4.5,
    so_luong_da_ban: 110,
    trang_thai: "active",
  },
  {
    ten_san_pham: "Giày Chạy Bộ Nam New Balance 574",
    gia_goc: 2600000,
    gia_khuyen_mai: 2100000,
    hinh_anh: "giay4",
    mo_ta: "Giày chạy bộ New Balance 574 với công nghệ đệm ENCAP, mang lại sự thoải mái và hỗ trợ tốt. Phù hợp cho chạy bộ và đi bộ hàng ngày.",
    thuong_hieu: "New Balance",
    danh_muc: "nam",
    kich_thuoc: ["40", "41", "42", "43", "44", "45"],
    so_luong_ton: 40,
    danh_gia: 4.7,
    so_luong_da_ban: 85,
    trang_thai: "active",
  },
  {
    ten_san_pham: "Giày Thể Thao Nam Puma Suede Classic",
    gia_goc: 1800000,
    gia_khuyen_mai: 1400000,
    hinh_anh: "giay3",
    mo_ta: "Giày thể thao Puma Suede Classic với thiết kế retro, phong cách streetwear. Chất liệu da suede mềm mại, đế cao su cổ điển.",
    thuong_hieu: "Puma",
    danh_muc: "nam",
    kich_thuoc: ["39", "40", "41", "42", "43", "44"],
    so_luong_ton: 55,
    danh_gia: 4.4,
    so_luong_da_ban: 130,
    trang_thai: "active",
  },
  {
    ten_san_pham: "Giày Sneaker Nam Reebok Classic Leather",
    gia_goc: 2000000,
    gia_khuyen_mai: 1600000,
    hinh_anh: "giay2",
    mo_ta: "Giày sneaker Reebok Classic Leather với thiết kế cổ điển, chất liệu da thật cao cấp. Đế cao su bền, phù hợp mọi dịp.",
    thuong_hieu: "Reebok",
    danh_muc: "nam",
    kich_thuoc: ["40", "41", "42", "43", "44"],
    so_luong_ton: 48,
    danh_gia: 4.5,
    so_luong_da_ban: 100,
    trang_thai: "active",
  },
  {
    ten_san_pham: "Giày Thể Thao Nam Fila Disruptor II",
    gia_goc: 2200000,
    gia_khuyen_mai: 1750000,
    hinh_anh: "giaymau",
    mo_ta: "Giày thể thao Fila Disruptor II với thiết kế chunky, phong cách 90s. Chất liệu da tổng hợp, đế dày, phù hợp giới trẻ.",
    thuong_hieu: "Fila",
    danh_muc: "nam",
    kich_thuoc: ["40", "41", "42", "43", "44", "45"],
    so_luong_ton: 42,
    danh_gia: 4.6,
    so_luong_da_ban: 88,
    trang_thai: "active",
  },
];

const addMoreProducts = async () => {
  try {
    // Kết nối database
    await connectDB();

    console.log("Đang thêm sản phẩm nam mới...\n");

    // Thêm từng sản phẩm
    for (let i = 0; i < newMenProducts.length; i++) {
      const productData = newMenProducts[i];
      const newProduct = new Product(productData);
      await newProduct.save();
      console.log(
        `${i + 1}. Đã thêm: "${productData.ten_san_pham}" - ${productData.gia_khuyen_mai.toLocaleString("vi-VN")} VNĐ (Ảnh: ${productData.hinh_anh})`
      );
    }

    // Đếm tổng số sản phẩm nam
    const totalMenProducts = await Product.countDocuments({ danh_muc: "nam" });
    console.log(`\n✅ Đã thêm ${newMenProducts.length} sản phẩm nam mới!`);
    console.log(`📦 Tổng số sản phẩm nam hiện tại: ${totalMenProducts}`);

    process.exit(0);
  } catch (error) {
    console.error("❌ Lỗi khi thêm sản phẩm:", error);
    process.exit(1);
  }
};

// Chạy script
addMoreProducts();

