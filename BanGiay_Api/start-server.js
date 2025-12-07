/**
 * Script khởi động server và kiểm tra kết nối MongoDB
 */

const mongoose = require("mongoose");
const express = require("express");
const cors = require("cors");
const connectDB = require("./config/db");
require("dotenv").config();

const app = express();

// Middleware
app.use(cors({
  origin: "*",
  methods: ["GET", "POST", "PUT", "DELETE", "OPTIONS"],
  allowedHeaders: ["Content-Type", "Authorization"],
}));
app.use(express.json());
app.use(express.urlencoded({ extended: true }));

// Middleware logging
app.use((req, res, next) => {
  console.log(`${new Date().toISOString()} - ${req.method} ${req.path}`);
  if (req.method === "POST" || req.method === "PUT") {
    console.log("Body:", JSON.stringify(req.body, null, 2));
  }
  next();
});

// Test endpoint
app.get("/", (req, res) => {
  res.json({
    message: "API BanGiay đang chạy...",
    timestamp: new Date().toISOString(),
    mongodb: mongoose.connection.readyState === 1 ? "connected" : "disconnected",
    endpoints: {
      products: "/api/product",
      bestSelling: "/api/product/best-selling",
      cart: "/api/cart",
      orders: "/api/order",
      auth: "/api/auth"
    }
  });
});

// Health check
app.get("/health", async (req, res) => {
  const Product = require("./models/Product");
  try {
    const dbStatus = mongoose.connection.readyState === 1 ? "connected" : "disconnected";
    const productCount = await Product.countDocuments();
    const activeProductCount = await Product.countDocuments({ trang_thai: "active" });
    
    res.json({
      status: "ok",
      database: dbStatus,
      products: {
        total: productCount,
        active: activeProductCount
      },
      timestamp: new Date().toISOString()
    });
  } catch (error) {
    res.status(500).json({
      status: "error",
      error: error.message
    });
  }
});

// Routes
app.use("/api/auth", require("./routes/auth.routes"));
app.use("/api/user", require("./routes/user.routes"));
app.use("/api/product", require("./routes/product.routes"));
app.use("/api/cart", require("./routes/cart.routes"));
app.use("/api/order", require("./routes/order.routes"));

// Start server
const PORT = process.env.PORT || 3000;

async function startServer() {
  try {
    console.log("\n========== KHỞI ĐỘNG SERVER ==========\n");
    
    // Kết nối MongoDB trước
    console.log("1. Đang kết nối MongoDB...");
    await connectDB();
    console.log("✅ MongoDB đã kết nối!\n");
    
    // Kiểm tra dữ liệu
    const Product = require("./models/Product");
    const productCount = await Product.countDocuments();
    const activeCount = await Product.countDocuments({ trang_thai: "active" });
    console.log(`2. Kiểm tra dữ liệu:`);
    console.log(`   - Tổng sản phẩm: ${productCount}`);
    console.log(`   - Sản phẩm active: ${activeCount}\n`);
    
    if (activeCount === 0) {
      console.log("⚠️  CẢNH BÁO: Không có sản phẩm active trong database!");
      console.log("💡 Chạy: node import-products-to-mongodb.js để import dữ liệu\n");
    }
    
    // Khởi động server
    app.listen(PORT, "0.0.0.0", () => {
      console.log(`3. Server đang chạy:`);
      console.log(`   - Local: http://localhost:${PORT}`);
      console.log(`   - Network: http://0.0.0.0:${PORT}`);
      console.log(`\n✅ SERVER ĐÃ SẴN SÀNG!\n`);
      console.log("📋 Test các endpoint:");
      console.log(`   - Health: http://localhost:${PORT}/health`);
      console.log(`   - Products: http://localhost:${PORT}/api/product/best-selling?limit=10`);
      console.log(`   - API Info: http://localhost:${PORT}/\n`);
      console.log("==========================================\n");
    });
    
  } catch (error) {
    console.error("\n❌ LỖI KHI KHỞI ĐỘNG SERVER:");
    console.error(error.message);
    console.error("\n💡 Kiểm tra:");
    console.error("   1. MongoDB có đang chạy không?");
    console.error("   2. Port 3000 có bị chiếm không?");
    console.error("   3. File .env có đúng không?");
    process.exit(1);
  }
}

startServer();

