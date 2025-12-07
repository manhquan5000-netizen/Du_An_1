const mongoose = require("mongoose");
const Product = require("./models/Product");
const fs = require("fs");
const path = require("path");
require("dotenv").config();

// Kết nối MongoDB
const connectDB = async () => {
  try {
    const mongoUri = process.env.MONGODB_URI || "mongodb://localhost:27017/BanGiay_App";
    console.log("Đang kết nối đến MongoDB:", mongoUri.replace(/\/\/.*@/, "//***:***@"));
    
    await mongoose.connect(mongoUri, {
      useNewUrlParser: true,
      useUnifiedTopology: true,
    });
    console.log("✅ Đã kết nối MongoDB thành công");
  } catch (error) {
    console.error("❌ Lỗi kết nối MongoDB:", error);
    process.exit(1);
  }
};

// Import sản phẩm từ file JSON
const importProducts = async () => {
  try {
    await connectDB();

    // Đọc file JSON
    const jsonPath = path.join(__dirname, "products-import.json");
    console.log("\n📂 Đang đọc file:", jsonPath);
    
    if (!fs.existsSync(jsonPath)) {
      console.error("❌ File không tồn tại:", jsonPath);
      process.exit(1);
    }

    const jsonData = fs.readFileSync(jsonPath, "utf8");
    const products = JSON.parse(jsonData);

    console.log(`✅ Đã đọc ${products.length} sản phẩm từ file JSON\n`);

    // Kiểm tra xem collection đã có dữ liệu chưa
    const existingCount = await Product.countDocuments();
    if (existingCount > 0) {
      console.log(`⚠️  Collection đã có ${existingCount} sản phẩm.`);
      console.log("Bạn có muốn xóa dữ liệu cũ và import lại? (y/n)");
      // Tự động xóa nếu có flag --force
      if (process.argv.includes("--force")) {
        console.log("Đang xóa dữ liệu cũ...");
        await Product.deleteMany({});
        console.log("✅ Đã xóa dữ liệu cũ\n");
      } else {
        console.log("Sử dụng --force để xóa và import lại, hoặc import thêm vào dữ liệu hiện có.");
        console.log("Đang import thêm vào dữ liệu hiện có...\n");
      }
    }

    // Validate dữ liệu trước khi import
    console.log("🔍 Đang validate dữ liệu...");
    const validProducts = [];
    const invalidProducts = [];

    products.forEach((product, index) => {
      if (!product.ten_san_pham || !product.gia_goc || !product.gia_khuyen_mai) {
        invalidProducts.push({ index: index + 1, product, reason: "Thiếu thông tin bắt buộc" });
      } else {
        validProducts.push(product);
      }
    });

    if (invalidProducts.length > 0) {
      console.log(`⚠️  Có ${invalidProducts.length} sản phẩm không hợp lệ:`);
      invalidProducts.forEach(({ index, reason }) => {
        console.log(`   - Sản phẩm ${index}: ${reason}`);
      });
      console.log();
    }

    if (validProducts.length === 0) {
      console.error("❌ Không có sản phẩm hợp lệ để import!");
      process.exit(1);
    }

    console.log(`✅ Có ${validProducts.length} sản phẩm hợp lệ để import\n`);

    // Import vào MongoDB
    console.log("📦 Đang import sản phẩm vào MongoDB...");
    const insertedProducts = await Product.insertMany(validProducts, { ordered: false });
    
    console.log(`\n✅ Đã import thành công ${insertedProducts.length} sản phẩm vào MongoDB!`);

    // Hiển thị danh sách sản phẩm đã import
    console.log("\n📋 Danh sách sản phẩm đã import:");
    insertedProducts.forEach((product, index) => {
      console.log(
        `${index + 1}. ${product.ten_san_pham} - ${product.thuong_hieu} - ${product.gia_khuyen_mai.toLocaleString('vi-VN')}₫ (ID: ${product._id})`
      );
    });

    // Kiểm tra lại số lượng
    const finalCount = await Product.countDocuments();
    console.log(`\n📊 Tổng số sản phẩm trong database: ${finalCount}`);

    // Hiển thị thống kê theo thương hiệu
    const brandStats = await Product.aggregate([
      {
        $group: {
          _id: "$thuong_hieu",
          count: { $sum: 1 },
          totalStock: { $sum: "$so_luong_ton" }
        }
      },
      { $sort: { count: -1 } }
    ]);

    console.log("\n📊 Thống kê theo thương hiệu:");
    brandStats.forEach(stat => {
      console.log(`   - ${stat._id}: ${stat.count} sản phẩm, Tổng tồn kho: ${stat.totalStock}`);
    });

    console.log("\n✅ Import hoàn tất!\n");
    process.exit(0);
  } catch (error) {
    console.error("\n❌ Lỗi khi import sản phẩm:", error);
    if (error.writeErrors) {
      console.error("Chi tiết lỗi:");
      error.writeErrors.forEach((err, index) => {
        console.error(`   ${index + 1}. ${err.errmsg}`);
      });
    }
    console.error("Error stack:", error.stack);
    process.exit(1);
  }
};

// Chạy import
importProducts();

