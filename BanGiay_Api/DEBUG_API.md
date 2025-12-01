# Hướng dẫn Debug API không hiển thị

## Các bước kiểm tra:

### 1. Kiểm tra Server đang chạy
```bash
# Trong folder BanGiay_Api
npm start
# Hoặc
node server.js
```

### 2. Test API bằng browser hoặc Postman
```
GET http://YOUR_IP:3000/api/product/best-selling?limit=5
GET http://YOUR_IP:3000/api/product/category/nam
```

### 3. Kiểm tra Base URL trong Android
- File: `app/build.gradle` hoặc `local.properties`
- Đảm bảo `API_BASE_URL = "http://YOUR_IP:3000/api/"`
- Thay `YOUR_IP` bằng IP máy tính (không dùng localhost)

### 4. Kiểm tra Log trong Android Studio
- Mở Logcat
- Filter: `MainActivity`
- Xem các log:
  - "Top products response code"
  - "Top products data"
  - "Processing product"
  - "Added product"

### 5. Kiểm tra Response Format
Backend trả về array trực tiếp:
```json
[
  {
    "_id": "...",
    "ten_san_pham": "...",
    "gia_goc": 3500000,
    "gia_khuyen_mai": 2800000,
    "hinh_anh": "giay15",
    ...
  }
]
```

### 6. Các lỗi thường gặp:

**Lỗi: "Failed to load top products"**
- Kiểm tra server có đang chạy không
- Kiểm tra IP address có đúng không
- Kiểm tra firewall có chặn port 3000 không

**Lỗi: "Top products list is empty or null"**
- Kiểm tra database có sản phẩm không
- Kiểm tra `trang_thai: "active"`

**Lỗi: "Failed to convert product"**
- Kiểm tra ProductResponse có map đúng field không
- Kiểm tra log "Processing product" để xem giá trị

### 7. Test nhanh bằng curl
```bash
curl http://YOUR_IP:3000/api/product/best-selling?limit=5
curl http://YOUR_IP:3000/api/product/category/nam
```

