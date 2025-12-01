# Hướng dẫn khắc phục lỗi API không đẩy dữ liệu lên

## Các bước kiểm tra và khắc phục:

### 1. Kiểm tra MongoDB đang chạy
```bash
# Kiểm tra MongoDB service
# Windows: Services -> MongoDB
# Hoặc chạy:
mongod --version
```

### 2. Kiểm tra kết nối MongoDB
- Đảm bảo MongoDB đang chạy trên port 27017
- Hoặc tạo file `.env` với:
```
MONGODB_URI=mongodb://localhost:27017/BanGiay_App
PORT=3000
```

### 3. Cài đặt dependencies
```bash
npm install
```

### 4. Khởi động server
```bash
npm start
# hoặc
node server.js
```

### 5. Kiểm tra server đang chạy
- Mở browser: `http://localhost:3000`
- Phải thấy: "API BanGiay đang chạy..."

### 6. Test API bằng Postman hoặc cURL

**GET tất cả sản phẩm:**
```bash
curl http://localhost:3000/api/product
```

**POST tạo sản phẩm:**
```bash
curl -X POST http://localhost:3000/api/product \
  -H "Content-Type: application/json" \
  -d '{
    "ten_san_pham": "Giày Test",
    "gia_goc": 1000000,
    "gia_khuyen_mai": 800000,
    "hinh_anh": "https://example.com/image.jpg",
    "thuong_hieu": "Test",
    "danh_muc": "unisex",
    "kich_thuoc": ["40", "41", "42"]
  }'
```

### 7. Kiểm tra lỗi trong console
- Xem console log khi gọi API
- Kiểm tra các thông báo lỗi MongoDB
- Kiểm tra validation errors

### 8. Lỗi thường gặp:

**Lỗi: "Cannot connect to MongoDB"**
- Giải pháp: Khởi động MongoDB service

**Lỗi: "ValidationError"**
- Giải pháp: Kiểm tra các trường bắt buộc:
  - `ten_san_pham` (bắt buộc)
  - `gia_goc` (bắt buộc, > 0)
  - `gia_khuyen_mai` (bắt buộc, > 0)
  - `hinh_anh` (bắt buộc)

**Lỗi: "CORS"**
- Giải pháp: Đã cấu hình CORS cho phép tất cả origin

**Lỗi: "CastError" (ID không hợp lệ)**
- Giải pháp: Kiểm tra format ID MongoDB (24 ký tự hex)

### 9. Test từ mobile app
- Đảm bảo URL đúng: `http://YOUR_IP:3000/api/product`
- Thay `YOUR_IP` bằng IP máy tính (không dùng localhost)
- Kiểm tra firewall không chặn port 3000

### 10. Kiểm tra logs
Server sẽ log:
- Mọi request đến API
- Body của POST/PUT requests
- Lỗi chi tiết khi có

## Test nhanh:
```bash
# Chạy script test
node test-api.js
```

