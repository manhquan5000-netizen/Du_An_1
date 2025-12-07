/**
 * Script kiểm tra xem dữ liệu MongoDB có khớp với products-import.json không
 */

const mongoose = require("mongoose");
const Product = require("./models/Product");
const fs = require("fs");
const path = require("path");

const MONGODB_URI = process.env.MONGODB_URI || "mongodb://localhost:27017/BanGiay_App";

async function checkDataMatch() {
  try {
    console.log("\n========== KIỂM TRA DỮ LIỆU MONGODB ==========\n");
    
    // Kết nối MongoDB
    await mongoose.connect(MONGODB_URI);
    console.log("✅ Đã kết nối MongoDB\n");
    
    // Đọc file JSON
    const jsonPath = path.join(__dirname, "products-import.json");
    const jsonData = JSON.parse(fs.readFileSync(jsonPath, "utf8"));
    console.log(`📄 File products-import.json có: ${jsonData.length} sản phẩm\n`);
    
    // Lấy dữ liệu từ MongoDB
    const dbProducts = await Product.find({});
    console.log(`💾 MongoDB có: ${dbProducts.length} sản phẩm\n`);
    
    // So sánh số lượng
    if (jsonData.length !== dbProducts.length) {
      console.log(`⚠️  Số lượng không khớp! JSON: ${jsonData.length}, MongoDB: ${dbProducts.length}\n`);
    } else {
      console.log(`✅ Số lượng khớp: ${jsonData.length} sản phẩm\n`);
    }
    
    // Kiểm tra từng sản phẩm
    console.log("🔍 Kiểm tra chi tiết:\n");
    
    let matchCount = 0;
    let mismatchCount = 0;
    const mismatches = [];
    
    for (const jsonProduct of jsonData) {
      // Tìm sản phẩm trong MongoDB theo tên
      const dbProduct = dbProducts.find(p => p.ten_san_pham === jsonProduct.ten_san_pham);
      
      if (!dbProduct) {
        mismatchCount++;
        mismatches.push({
          name: jsonProduct.ten_san_pham,
          reason: "Không tìm thấy trong MongoDB"
        });
        continue;
      }
      
      // So sánh các trường quan trọng
      const fieldsToCheck = [
        'gia_goc', 'gia_khuyen_mai', 'thuong_hieu', 
        'danh_muc', 'so_luong_ton', 'danh_gia', 'so_luong_da_ban', 'trang_thai'
      ];
      
      let isMatch = true;
      const differences = [];
      
      for (const field of fieldsToCheck) {
        const jsonValue = jsonProduct[field];
        const dbValue = dbProduct[field];
        
        // So sánh array (kich_thuoc)
        if (field === 'kich_thuoc') {
          const jsonSizes = JSON.stringify(jsonProduct.kich_thuoc?.sort() || []);
          const dbSizes = JSON.stringify((dbProduct.kich_thuoc || []).sort());
          if (jsonSizes !== dbSizes) {
            isMatch = false;
            differences.push(`${field}: JSON=${jsonSizes}, DB=${dbSizes}`);
          }
        } else if (jsonValue !== dbValue) {
          isMatch = false;
          differences.push(`${field}: JSON=${jsonValue}, DB=${dbValue}`);
        }
      }
      
      if (isMatch) {
        matchCount++;
      } else {
        mismatchCount++;
        mismatches.push({
          name: jsonProduct.ten_san_pham,
          reason: "Dữ liệu không khớp",
          differences
        });
      }
    }
    
    // Hiển thị kết quả
    console.log(`✅ Khớp: ${matchCount} sản phẩm`);
    console.log(`⚠️  Không khớp: ${mismatchCount} sản phẩm\n`);
    
    if (mismatches.length > 0) {
      console.log("📋 Chi tiết sản phẩm không khớp:\n");
      mismatches.forEach((m, index) => {
        console.log(`${index + 1}. ${m.name}`);
        console.log(`   Lý do: ${m.reason}`);
        if (m.differences) {
          m.differences.forEach(diff => {
            console.log(`   - ${diff}`);
          });
        }
        console.log();
      });
    }
    
    // Thống kê theo danh mục
    console.log("\n📊 Thống kê theo danh mục:\n");
    const categories = ["nam", "nu", "unisex"];
    for (const cat of categories) {
      const jsonCount = jsonData.filter(p => p.danh_muc === cat).length;
      const dbCount = await Product.countDocuments({ danh_muc: cat });
      console.log(`   ${cat}: JSON=${jsonCount}, MongoDB=${dbCount}`);
    }
    
    // Thống kê theo thương hiệu
    console.log("\n📊 Thống kê theo thương hiệu:\n");
    const brands = [...new Set(jsonData.map(p => p.thuong_hieu))];
    for (const brand of brands) {
      const jsonCount = jsonData.filter(p => p.thuong_hieu === brand).length;
      const dbCount = await Product.countDocuments({ thuong_hieu: brand });
      console.log(`   ${brand}: JSON=${jsonCount}, MongoDB=${dbCount}`);
    }
    
    // Kiểm tra sản phẩm có ID hợp lệ
    console.log("\n🔍 Kiểm tra ID sản phẩm:\n");
    const productsWithId = dbProducts.filter(p => p._id);
    console.log(`   Sản phẩm có ID: ${productsWithId.length}/${dbProducts.length}`);
    
    if (productsWithId.length > 0) {
      console.log("\n   Mẫu ID sản phẩm:");
      productsWithId.slice(0, 3).forEach(p => {
        console.log(`   - ${p.ten_san_pham}: ${p._id}`);
      });
    }
    
    console.log("\n==========================================\n");
    
    if (matchCount === jsonData.length && dbProducts.length === jsonData.length) {
      console.log("✅ TẤT CẢ DỮ LIỆU ĐÃ KHỚP HOÀN TOÀN!\n");
    } else {
      console.log("⚠️  CÓ SỰ KHÁC BIỆT. Cần kiểm tra lại.\n");
      console.log("💡 Chạy lại import: node import-products-to-mongodb.js --force\n");
    }
    
    await mongoose.disconnect();
    process.exit(0);
  } catch (error) {
    console.error("\n❌ Lỗi:", error.message);
    console.error(error.stack);
    process.exit(1);
  }
}

checkDataMatch();

