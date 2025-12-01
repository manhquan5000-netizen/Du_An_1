// Script test API response format
const mongoose = require("mongoose");
const connectDB = require("./config/db");
const Product = require("./models/Product");
require("dotenv").config();

const testAPI = async () => {
  try {
    await connectDB();
    
    // Test best-selling
    console.log("=== Testing /api/product/best-selling ===");
    const bestSelling = await Product.find({ trang_thai: "active" })
      .sort({ so_luong_da_ban: -1 })
      .limit(5);
    console.log("Found", bestSelling.length, "products");
    bestSelling.forEach((p, i) => {
      console.log(`${i+1}. ${p.ten_san_pham}`);
      console.log(`   - gia_goc: ${p.gia_goc} (type: ${typeof p.gia_goc})`);
      console.log(`   - gia_khuyen_mai: ${p.gia_khuyen_mai} (type: ${typeof p.gia_khuyen_mai})`);
      console.log(`   - hinh_anh: ${p.hinh_anh}`);
    });
    
    // Test category
    console.log("\n=== Testing /api/product/category/nam ===");
    const menProducts = await Product.find({
      danh_muc: "nam",
      trang_thai: "active",
    });
    console.log("Found", menProducts.length, "products");
    menProducts.forEach((p, i) => {
      console.log(`${i+1}. ${p.ten_san_pham}`);
      console.log(`   - gia_goc: ${p.gia_goc}`);
      console.log(`   - gia_khuyen_mai: ${p.gia_khuyen_mai}`);
      console.log(`   - hinh_anh: ${p.hinh_anh}`);
    });
    
    process.exit(0);
  } catch (error) {
    console.error("Error:", error);
    process.exit(1);
  }
};

testAPI();

