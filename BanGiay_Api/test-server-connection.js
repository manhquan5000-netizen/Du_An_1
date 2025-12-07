/**
 * Script test kết nối server và MongoDB
 */

const http = require('http');

async function testServer() {
  console.log("\n========== TEST SERVER CONNECTION ==========\n");
  
  // Test 1: Health check
  console.log("1. Testing /health endpoint...");
  try {
    const healthData = await makeRequest('http://localhost:3000/health');
    console.log("✅ Health check passed:");
    console.log(JSON.stringify(healthData, null, 2));
    console.log();
  } catch (error) {
    console.log("❌ Health check failed:", error.message);
    console.log("💡 Server có thể chưa chạy. Chạy: node start-server.js\n");
    return;
  }
  
  // Test 2: Get products
  console.log("2. Testing /api/product/best-selling endpoint...");
  try {
    const products = await makeRequest('http://localhost:3000/api/product/best-selling?limit=3');
    if (Array.isArray(products) && products.length > 0) {
      console.log(`✅ Found ${products.length} products:`);
      products.forEach((p, i) => {
        console.log(`   ${i + 1}. ${p.ten_san_pham} - ${p.gia_khuyen_mai}₫ (ID: ${p._id})`);
      });
    } else {
      console.log("⚠️  No products found");
    }
    console.log();
  } catch (error) {
    console.log("❌ Get products failed:", error.message);
    console.log();
  }
  
  // Test 3: Get all products
  console.log("3. Testing /api/product endpoint...");
  try {
    const allProducts = await makeRequest('http://localhost:3000/api/product?limit=5');
    if (allProducts.products && allProducts.products.length > 0) {
      console.log(`✅ Found ${allProducts.products.length} products (total: ${allProducts.pagination.total})`);
    } else {
      console.log("⚠️  No products found");
    }
    console.log();
  } catch (error) {
    console.log("❌ Get all products failed:", error.message);
    console.log();
  }
  
  console.log("==========================================\n");
  console.log("✅ Server đang hoạt động tốt!\n");
}

function makeRequest(url) {
  return new Promise((resolve, reject) => {
    http.get(url, (res) => {
      let data = '';
      
      res.on('data', (chunk) => {
        data += chunk;
      });
      
      res.on('end', () => {
        try {
          const json = JSON.parse(data);
          resolve(json);
        } catch (error) {
          reject(new Error('Invalid JSON response'));
        }
      });
    }).on('error', (error) => {
      reject(error);
    });
  });
}

testServer().catch(console.error);

