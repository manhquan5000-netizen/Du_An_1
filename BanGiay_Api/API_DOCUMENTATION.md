# API Documentation - BanGiay Server

## Tổng quan

API server được viết lại hoàn toàn theo cấu trúc dữ liệu trong `products-import.json`, đảm bảo tương thích với Android app.

## Cấu trúc dữ liệu sản phẩm

Theo file `products-import.json`, mỗi sản phẩm có cấu trúc:

```json
{
  "ten_san_pham": "Tên sản phẩm",
  "gia_goc": 1500000,
  "gia_khuyen_mai": 1200000,
  "hinh_anh": "giay14.img",
  "mo_ta": "Mô tả sản phẩm",
  "thuong_hieu": "Converse",
  "danh_muc": "unisex",
  "kich_thuoc": ["37", "38", "39", "40", "41", "42", "43"],
  "so_luong_ton": 100,
  "danh_gia": 4.6,
  "so_luong_da_ban": 250,
  "trang_thai": "active"
}
```

## API Endpoints

### 1. Product APIs

#### GET `/api/product`
Lấy tất cả sản phẩm (có phân trang và lọc)

**Query Parameters:**
- `page` (default: 1) - Số trang
- `limit` (default: 100) - Số lượng sản phẩm mỗi trang
- `danh_muc` - Lọc theo danh mục: `nam`, `nu`, `unisex`
- `thuong_hieu` - Lọc theo thương hiệu
- `min_price` - Giá tối thiểu
- `max_price` - Giá tối đa
- `search` - Tìm kiếm theo tên, mô tả, thương hiệu
- `sort_by` (default: "createdAt") - Sắp xếp theo field
- `sort_order` (default: "desc") - Thứ tự: `asc` hoặc `desc`

**Response:**
```json
{
  "success": true,
  "products": [...],
  "pagination": {
    "page": 1,
    "limit": 100,
    "total": 20,
    "pages": 1
  }
}
```

#### GET `/api/product/:id`
Lấy sản phẩm theo ID

**Response:**
```json
{
  "success": true,
  "product": {...}
}
```

#### GET `/api/product/best-selling?limit=10`
Lấy sản phẩm bán chạy (sắp xếp theo `so_luong_da_ban`)

**Response:** Array trực tiếp
```json
[...]
```

#### GET `/api/product/newest?limit=10`
Lấy sản phẩm mới nhất (sắp xếp theo `createdAt`)

**Response:** Array trực tiếp
```json
[...]
```

#### GET `/api/product/category/:danh_muc`
Lấy sản phẩm theo danh mục

**Path Parameters:**
- `danh_muc`: `nam`, `nu`, hoặc `unisex`

**Response:** Array trực tiếp
```json
[...]
```

#### POST `/api/product`
Tạo sản phẩm mới (Admin)

**Body:**
```json
{
  "ten_san_pham": "Tên sản phẩm",
  "gia_goc": 1500000,
  "gia_khuyen_mai": 1200000,
  "hinh_anh": "giay14.img",
  "mo_ta": "Mô tả",
  "thuong_hieu": "Converse",
  "danh_muc": "unisex",
  "kich_thuoc": ["37", "38", "39"],
  "so_luong_ton": 100,
  "danh_gia": 4.6,
  "so_luong_da_ban": 0,
  "trang_thai": "active"
}
```

#### PUT `/api/product/:id`
Cập nhật sản phẩm (Admin)

#### PUT `/api/product/:id/stock`
Cập nhật số lượng tồn kho (Admin)

**Body:**
```json
{
  "so_luong_ton": 150
}
```

#### DELETE `/api/product/:id`
Xóa sản phẩm (Admin)

### 2. Cart APIs

#### POST `/api/cart`
Thêm sản phẩm vào giỏ hàng

**Body:**
```json
{
  "user_id": "user_id",
  "product_id": "product_id",
  "size": "40",
  "quantity": 1
}
```

#### GET `/api/cart?user_id=xxx`
Lấy giỏ hàng của user

#### PUT `/api/cart/item`
Cập nhật số lượng sản phẩm trong giỏ hàng

#### DELETE `/api/cart/item`
Xóa sản phẩm khỏi giỏ hàng

#### DELETE `/api/cart`
Xóa toàn bộ giỏ hàng

### 3. Order APIs

Xem file `routes/order.routes.js` và `controllers/order.controller.js`

### 4. Auth APIs

Xem file `routes/auth.routes.js` và `controllers/auth.controller.js`

## Health Check

#### GET `/health`
Kiểm tra trạng thái server và database

**Response:**
```json
{
  "status": "ok",
  "database": "connected",
  "products": {
    "total": 20,
    "active": 20
  },
  "timestamp": "2025-01-01T00:00:00.000Z"
}
```

## Import dữ liệu

### Import sản phẩm từ `products-import.json`

```bash
# Import thêm vào dữ liệu hiện có
node import-products-to-mongodb.js

# Xóa dữ liệu cũ và import lại
node import-products-to-mongodb.js --force
```

### Kiểm tra dữ liệu

```bash
# Chạy script test
node test-api.js
```

## Response Format

Tất cả API trả về format nhất quán:

**Success:**
```json
{
  "success": true,
  "message": "Thông báo (nếu có)",
  "data": {...} hoặc [...]
}
```

**Error:**
```json
{
  "success": false,
  "message": "Thông báo lỗi",
  "error": "Chi tiết lỗi (nếu có)"
}
```

## Logging

Tất cả API endpoints đều có logging chi tiết:
- Request method và path
- Query parameters
- Request body (nếu có)
- Số lượng kết quả tìm thấy
- Lỗi (nếu có)

## CORS

Server đã cấu hình CORS để cho phép tất cả origins:
```javascript
app.use(cors({
  origin: "*",
  methods: ["GET", "POST", "PUT", "DELETE", "OPTIONS"],
  allowedHeaders: ["Content-Type", "Authorization"],
}));
```

## Khởi động Server

```bash
# Cài đặt dependencies (nếu chưa có)
npm install

# Khởi động server
npm start

# Hoặc với nodemon (auto-reload)
npm run dev
```

Server sẽ chạy tại: `http://localhost:3000`

## Testing

### Test với cURL

```bash
# Lấy tất cả sản phẩm
curl http://localhost:3000/api/product

# Lấy sản phẩm bán chạy
curl http://localhost:3000/api/product/best-selling?limit=10

# Lấy sản phẩm theo danh mục
curl http://localhost:3000/api/product/category/unisex

# Health check
curl http://localhost:3000/health
```

### Test với Postman

1. Import collection từ file `postman_collection.json` (nếu có)
2. Hoặc tạo request thủ công theo các endpoints trên

## Troubleshooting

### Không kết nối được từ Android app

1. Kiểm tra server có chạy không: `http://localhost:3000/health`
2. Kiểm tra MongoDB có dữ liệu không: `node test-api.js`
3. Kiểm tra API Base URL trong Android app
4. Kiểm tra CORS settings
5. Kiểm tra firewall/network

### Không có sản phẩm

1. Import dữ liệu: `node import-products-to-mongodb.js`
2. Kiểm tra database: `node test-api.js`
3. Kiểm tra logs trong server console

## Notes

- Tất cả sản phẩm phải có `trang_thai: "active"` để hiển thị
- Giá được lưu dưới dạng số (VND), không có dấu phẩy
- Kích thước là array các string: `["37", "38", "39"]`
- Danh mục chỉ chấp nhận: `nam`, `nu`, `unisex`

