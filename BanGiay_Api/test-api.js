/**
 * Script test API để kiểm tra kết nối và dữ liệu
 * Chạy: node test-api.js
 */

const mongoose = require("mongoose");
const Product = require("./models/Product");

const MONGODB_URI = process.env.MONGODB_URI || "mongodb://localhost:27017/BanGiay_App";

async function testConnection() {
  try {
    console.log("\n========== TESTING API CONNECTION ==========\n");
    
    // 1. Test MongoDB connection
    console.log("1. Testing MongoDB connection...");
    await mongoose.connect(MONGODB_URI);
    console.log("✅ MongoDB connected successfully!");
    
    // 2. Check total products
    console.log("\n2. Checking products in database...");
    const totalProducts = await Product.countDocuments();
    console.log(`   Total products: ${totalProducts}`);
    
    // 3. Check active products
    const activeProducts = await Product.countDocuments({ trang_thai: "active" });
    console.log(`   Active products: ${activeProducts}`);
    
    // 4. Check products by category
    console.log("\n3. Products by category:");
    const categories = ["nam", "nu", "unisex"];
    for (const cat of categories) {
      const count = await Product.countDocuments({ danh_muc: cat, trang_thai: "active" });
      console.log(`   ${cat}: ${count} products`);
    }
    
    // 5. Get sample products
    console.log("\n4. Sample products:");
    const sampleProducts = await Product.find({ trang_thai: "active" }).limit(5);
    if (sampleProducts.length > 0) {
      sampleProducts.forEach((p, index) => {
        console.log(`   ${index + 1}. ${p.ten_san_pham} (ID: ${p._id})`);
        console.log(`      Category: ${p.danh_muc}, Price: ${p.gia_khuyen_mai || p.gia_goc}₫`);
      });
    } else {
      console.log("   ⚠️ No products found!");
      console.log("   💡 Run: node import-products-to-mongodb.js to import sample data");
    }
    
    // 6. Test best selling products
    console.log("\n5. Best selling products:");
    const bestSelling = await Product.find({ trang_thai: "active" })
      .sort({ so_luong_da_ban: -1 })
      .limit(3);
    if (bestSelling.length > 0) {
      bestSelling.forEach((p, index) => {
        console.log(`   ${index + 1}. ${p.ten_san_pham} - Sold: ${p.so_luong_da_ban}`);
      });
    } else {
      console.log("   ⚠️ No best selling products found!");
    }
    
    console.log("\n==========================================\n");
    console.log("✅ Test completed!");
    console.log("\n💡 Next steps:");
    console.log("   1. Make sure server is running: npm start");
    console.log("   2. Check API endpoint: http://localhost:3000/api/product/best-selling");
    console.log("   3. If no products, import data: node import-products-to-mongodb.js");
    console.log("\n");
    
    await mongoose.disconnect();
    process.exit(0);
  } catch (error) {
    console.error("\n❌ Error:", error.message);
    console.error("\n💡 Troubleshooting:");
    console.error("   1. Make sure MongoDB is running");
    console.error("   2. Check MONGODB_URI in .env file");
    console.error("   3. Default URI: mongodb://localhost:27017/BanGiay_App");
    process.exit(1);
  }
}

testConnection();
