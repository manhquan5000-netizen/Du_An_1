const mongoose = require("mongoose");
const connectDB = require("./config/db");
const Product = require("./models/Product");
const fs = require("fs");
require("dotenv").config();

// Đọc file JSON
const importProducts = async () => {
  try {
    // Kết nối database
    await connectDB();

    // Đọc file JSON
    const productsData = JSON.parse(fs.readFileSync("./products.json", "utf8"));

    // Xóa dữ liệu cũ (tùy chọn - bỏ comment nếu muốn xóa)
    // await Product.deleteMany({});
    // console.log("Đã xóa dữ liệu cũ");

    // Insert sản phẩm
    const products = await Product.insertMany(productsData);
    console.log(`✅ Đã import thành công ${products.length} sản phẩm từ file products.json!`);

    // Hiển thị danh sách
    console.log("\n📦 Danh sách sản phẩm đã import:");
    products.forEach((product, index) => {
      console.log(
        `${index + 1}. ${product.ten_san_pham} - ${product.gia_khuyen_mai.toLocaleString("vi-VN")} VNĐ`
      );
    });

    process.exit(0);
  } catch (error) {
    console.error("❌ Lỗi khi import dữ liệu:", error);
    process.exit(1);
  }
};

// Chạy import
importProducts();

