const mongoose = require("mongoose");
const Cart = require("./models/Cart");
const Product = require("./models/Product");
require("dotenv").config();

// Kết nối MongoDB
const connectDB = async () => {
  try {
    await mongoose.connect(process.env.MONGODB_URI || "mongodb://localhost:27017/ban_giay", {
      useNewUrlParser: true,
      useUnifiedTopology: true,
    });
    console.log("✅ Đã kết nối MongoDB");
  } catch (error) {
    console.error("❌ Lỗi kết nối MongoDB:", error);
    process.exit(1);
  }
};

// Test API Cart
const testCartAPI = async () => {
  try {
    await connectDB();

    console.log("\n========== TEST CART API ==========");
    
    // 1. Kiểm tra có sản phẩm nào trong DB không
    const productCount = await Product.countDocuments();
    console.log("\n1. Số lượng sản phẩm trong DB:", productCount);
    
    if (productCount === 0) {
      console.log("⚠️ Không có sản phẩm nào trong DB. Vui lòng chạy seed-products-sample.js trước!");
      process.exit(1);
    }

    // 2. Lấy một sản phẩm mẫu
    const sampleProduct = await Product.findOne();
    console.log("\n2. Sản phẩm mẫu:");
    console.log("   - ID:", sampleProduct._id);
    console.log("   - Tên:", sampleProduct.ten_san_pham);
    console.log("   - Giá gốc:", sampleProduct.gia_goc);
    console.log("   - Giá khuyến mãi:", sampleProduct.gia_khuyen_mai);

    // 3. Tạo user_id mẫu (ObjectId)
    const testUserId = new mongoose.Types.ObjectId();
    console.log("\n3. User ID mẫu:", testUserId.toString());

    // 4. Test thêm vào giỏ hàng
    console.log("\n4. Test thêm vào giỏ hàng...");
    let cart = await Cart.findOne({ user_id: testUserId });
    
    if (!cart) {
      cart = new Cart({
        user_id: testUserId,
        items: [],
      });
    }

    const price = sampleProduct.gia_khuyen_mai || sampleProduct.gia_goc;
    cart.items.push({
      san_pham_id: sampleProduct._id,
      so_luong: 1,
      kich_thuoc: "37",
      gia: price,
    });

    await cart.save();
    console.log("✅ Đã thêm vào giỏ hàng thành công!");
    console.log("   - Cart ID:", cart._id);
    console.log("   - User ID:", cart.user_id);
    console.log("   - Số lượng items:", cart.items.length);

    // 5. Verify cart trong MongoDB
    console.log("\n5. Verify cart trong MongoDB...");
    const savedCart = await Cart.findById(cart._id);
    if (savedCart) {
      console.log("✅ Cart đã được lưu vào MongoDB!");
      console.log("   - Cart ID:", savedCart._id);
      console.log("   - User ID:", savedCart.user_id);
      console.log("   - Items:", JSON.stringify(savedCart.items, null, 2));
    } else {
      console.error("❌ Cart không tồn tại trong MongoDB!");
    }

    // 6. Test populate product
    console.log("\n6. Test populate product...");
    const cartWithProduct = await Cart.findOne({ user_id: testUserId })
      .populate("items.san_pham_id", "ten_san_pham gia_goc gia_khuyen_mai");
    
    if (cartWithProduct && cartWithProduct.items.length > 0) {
      console.log("✅ Populate thành công!");
      console.log("   - Product name:", cartWithProduct.items[0].san_pham_id?.ten_san_pham);
    }

    // 7. Đếm tổng số cart trong DB
    const cartCount = await Cart.countDocuments();
    console.log("\n7. Tổng số cart trong DB:", cartCount);

    console.log("\n========== TEST HOÀN TẤT ==========\n");
    process.exit(0);
  } catch (error) {
    console.error("\n❌ Lỗi khi test:", error);
    console.error("Error stack:", error.stack);
    process.exit(1);
  }
};

testCartAPI();

