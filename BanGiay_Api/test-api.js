// Script test API sản phẩm
const axios = require("axios");

const BASE_URL = "http://localhost:3000/api/product";

// Test data
const testProduct = {
  ten_san_pham: "Giày Test API",
  gia_goc: 2000000,
  gia_khuyen_mai: 1500000,
  hinh_anh: "https://example.com/test.jpg",
  mo_ta: "Sản phẩm test từ API",
  thuong_hieu: "Test Brand",
  danh_muc: "unisex",
  kich_thuoc: ["40", "41", "42"],
  so_luong_ton: 10,
  danh_gia: 4.5,
  trang_thai: "active",
};

async function testAPI() {
  console.log("🧪 Bắt đầu test API sản phẩm...\n");

  try {
    // Test 1: Lấy tất cả sản phẩm
    console.log("1️⃣ Test GET /api/product");
    const getAllResponse = await axios.get(BASE_URL);
    console.log("✅ Thành công:", getAllResponse.data.products?.length || 0, "sản phẩm");
    console.log("");

    // Test 2: Tạo sản phẩm mới
    console.log("2️⃣ Test POST /api/product");
    const createResponse = await axios.post(BASE_URL, testProduct);
    console.log("✅ Thành công:", createResponse.data.message);
    const productId = createResponse.data.product._id;
    console.log("ID sản phẩm:", productId);
    console.log("");

    // Test 3: Lấy sản phẩm theo ID
    console.log("3️⃣ Test GET /api/product/:id");
    const getByIdResponse = await axios.get(`${BASE_URL}/${productId}`);
    console.log("✅ Thành công:", getByIdResponse.data.product.ten_san_pham);
    console.log("");

    // Test 4: Cập nhật sản phẩm
    console.log("4️⃣ Test PUT /api/product/:id");
    const updateResponse = await axios.put(`${BASE_URL}/${productId}`, {
      gia_khuyen_mai: 1400000,
    });
    console.log("✅ Thành công:", updateResponse.data.message);
    console.log("");

    // Test 5: Lấy sản phẩm bán chạy
    console.log("5️⃣ Test GET /api/product/best-selling");
    const bestSellingResponse = await axios.get(`${BASE_URL}/best-selling?limit=5`);
    console.log("✅ Thành công:", bestSellingResponse.data.length, "sản phẩm");
    console.log("");

    // Test 6: Xóa sản phẩm test
    console.log("6️⃣ Test DELETE /api/product/:id");
    const deleteResponse = await axios.delete(`${BASE_URL}/${productId}`);
    console.log("✅ Thành công:", deleteResponse.data.message);
    console.log("");

    console.log("🎉 Tất cả test đều thành công!");
  } catch (error) {
    console.error("❌ Lỗi:", error.response?.data || error.message);
    if (error.response) {
      console.error("Status:", error.response.status);
      console.error("Data:", JSON.stringify(error.response.data, null, 2));
    }
  }
}

// Chạy test
testAPI();

