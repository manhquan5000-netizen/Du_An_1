# Hướng dẫn Debug - Kết nối API Sản phẩm

## Vấn đề: Không kết nối được sản phẩm từ server

### Bước 1: Kiểm tra Server có chạy không

```bash
# Trong thư mục BanGiay_Api
npm start
```

Server sẽ chạy tại: `http://localhost:3000`

Kiểm tra bằng cách mở trình duyệt:
- `http://localhost:3000` - Xem thông tin API
- `http://localhost:3000/health` - Kiểm tra trạng thái database và số lượng sản phẩm

### Bước 2: Kiểm tra MongoDB có dữ liệu không

```bash
# Chạy script test
node test-api.js
```

Script này sẽ:
- Kiểm tra kết nối MongoDB
- Đếm số lượng sản phẩm
- Hiển thị sản phẩm mẫu
- Kiểm tra sản phẩm theo danh mục

### Bước 3: Import dữ liệu sản phẩm (nếu chưa có)

```bash
# Import dữ liệu mẫu
node import-products-to-mongodb.js
```

Hoặc import từ file JSON:
```bash
node import-products.js
```

### Bước 4: Kiểm tra API endpoints

Test các endpoint sau trong trình duyệt hoặc Postman:

1. **Lấy tất cả sản phẩm:**
   ```
   GET http://localhost:3000/api/product
   ```

2. **Lấy sản phẩm bán chạy:**
   ```
   GET http://localhost:3000/api/product/best-selling?limit=10
   ```

3. **Lấy sản phẩm theo danh mục:**
   ```
   GET http://localhost:3000/api/product/category/nam
   GET http://localhost:3000/api/product/category/nu
   GET http://localhost:3000/api/product/category/unisex
   ```

### Bước 5: Kiểm tra Android App Configuration

1. **Kiểm tra API Base URL trong Android:**
   - File: `app/src/main/java/com/poly/ban_giay_app/network/ApiClient.java`
   - Mặc định: `http://10.0.2.2:3000/api/` (cho emulator)
   - Nếu dùng thiết bị thật: Thay bằng IP máy tính (ví dụ: `http://192.168.1.100:3000/api/`)

2. **Kiểm tra BuildConfig:**
   - File: `app/build.gradle`
   - Đảm bảo có:
     ```gradle
     buildConfigField "String", "API_BASE_URL", "\"http://10.0.2.2:3000/api/\""
     ```

3. **Rebuild project:**
   ```bash
   # Trong Android Studio: Build > Rebuild Project
   ```

### Bước 6: Kiểm tra Network Connection

Trong Android app, kiểm tra:
- Có kết nối mạng không
- Firewall có chặn port 3000 không
- Emulator có thể truy cập `10.0.2.2` (localhost của máy host)

### Bước 7: Xem Logs

**Backend logs:**
- Xem console khi chạy `npm start`
- Logs sẽ hiển thị:
  - Request method và path
  - Số lượng sản phẩm tìm thấy
  - Lỗi nếu có

**Android logs:**
- Mở Logcat trong Android Studio
- Filter theo tag: `MainActivity`, `ApiClient`, `CartManager`
- Tìm các log:
  - `API Base URL: ...`
  - `Top products response code: ...`
  - `Found X products`

### Bước 8: Troubleshooting

**Vấn đề: "Không có sản phẩm từ server"**

**Nguyên nhân có thể:**
1. MongoDB chưa có dữ liệu → Import dữ liệu
2. Server không chạy → Khởi động server
3. API URL sai → Kiểm tra BuildConfig
4. Network không kết nối → Kiểm tra kết nối mạng
5. CORS error → Đã cấu hình CORS trong server.js

**Giải pháp:**
1. Chạy `node test-api.js` để kiểm tra database
2. Chạy `node import-products-to-mongodb.js` để import dữ liệu
3. Kiểm tra server đang chạy tại `http://localhost:3000`
4. Kiểm tra API URL trong Android app
5. Rebuild Android project

### Bước 9: Test API từ Android

Sau khi đã kiểm tra tất cả, test lại từ Android app:
1. Mở app
2. Xem Logcat
3. Kiểm tra xem có request đến server không
4. Kiểm tra response từ server

### Liên hệ

Nếu vẫn gặp vấn đề, cung cấp:
1. Output của `node test-api.js`
2. Logs từ server (console)
3. Logs từ Android (Logcat)
4. Screenshot lỗi (nếu có)

