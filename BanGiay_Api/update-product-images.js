const mongoose = require("mongoose");
const connectDB = require("./config/db");
const Product = require("./models/Product");
require("dotenv").config();

// Danh sách tên ảnh từ giay15 đến giaymau (theo thứ tự)
const imageNames = [
  "giay15",
  "giay14",
  "giay13",
  "giay12",
  "giay11",
  "giay10",
  "giay9",
  "giay8",
  "giay7",
  "giay6",
  "giay5",
  "giay4",
  "giay3",
  "giay2",
  "giaymau"
];

// Base URL cho ảnh
// Nếu ảnh nằm trong Android app (drawable), chỉ cần lưu tên file
// Nếu ảnh nằm trên server, dùng URL đầy đủ
const USE_ANDROID_RESOURCE = true; // true = dùng drawable, false = dùng URL server
const BASE_IMAGE_URL = "http://YOUR_IP:3000/images/"; // Chỉ dùng khi USE_ANDROID_RESOURCE = false

const updateProductImages = async () => {
  try {
    // Kết nối database
    await connectDB();

    // Lấy tất cả sản phẩm
    const products = await Product.find({ trang_thai: "active" }).sort({ createdAt: 1 });
    
    console.log(`Tìm thấy ${products.length} sản phẩm`);

    if (products.length === 0) {
      console.log("Không có sản phẩm nào để cập nhật");
      process.exit(0);
    }

    // Cập nhật ảnh cho từng sản phẩm
    for (let i = 0; i < products.length && i < imageNames.length; i++) {
      const product = products[i];
      const imageName = imageNames[i];
      
      // Tạo URL ảnh hoặc tên file
      let imageValue;
      if (USE_ANDROID_RESOURCE) {
        // Chỉ lưu tên file (không có extension), Android sẽ load từ drawable
        imageValue = imageName;
      } else {
        // Lưu URL đầy đủ từ server
        imageValue = `${BASE_IMAGE_URL}${imageName}.JPG`;
      }
      
      // Cập nhật ảnh
      product.hinh_anh = imageValue;
      await product.save();
      
      console.log(`${i + 1}. Đã cập nhật ảnh cho "${product.ten_san_pham}": ${imageValue}`);
    }

    console.log(`\n✅ Đã cập nhật ảnh cho ${Math.min(products.length, imageNames.length)} sản phẩm!`);
    process.exit(0);
  } catch (error) {
    console.error("❌ Lỗi khi cập nhật ảnh:", error);
    process.exit(1);
  }
};

// Chạy update
updateProductImages();

