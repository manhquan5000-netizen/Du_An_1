# API Sản Phẩm - BanGiay API

## Base URL
```
http://localhost:3000/api/product
```

## Endpoints

### 1. Lấy tất cả sản phẩm (có phân trang và lọc)
**GET** `/api/product`

**Query Parameters:**
- `page` (optional): Số trang (mặc định: 1)
- `limit` (optional): Số sản phẩm mỗi trang (mặc định: 10)
- `danh_muc` (optional): Lọc theo danh mục (`nam`, `nu`, `unisex`)
- `thuong_hieu` (optional): Lọc theo thương hiệu
- `min_price` (optional): Giá tối thiểu
- `max_price` (optional): Giá tối đa
- `search` (optional): Tìm kiếm theo tên, mô tả, thương hiệu
- `sort_by` (optional): Sắp xếp theo trường (mặc định: `createdAt`)
- `sort_order` (optional): Thứ tự sắp xếp (`asc` hoặc `desc`, mặc định: `desc`)

**Ví dụ:**
```
GET /api/product?page=1&limit=10&danh_muc=nam&min_price=1000000&max_price=5000000&search=nike&sort_by=gia_khuyen_mai&sort_order=asc
```

**Response:**
```json
{
  "products": [...],
  "pagination": {
    "page": 1,
    "limit": 10,
    "total": 50,
    "pages": 5
  }
}
```

---

### 2. Lấy sản phẩm theo ID
**GET** `/api/product/:id`

**Ví dụ:**
```
GET /api/product/507f1f77bcf86cd799439011
```

**Response:**
```json
{
  "_id": "507f1f77bcf86cd799439011",
  "ten_san_pham": "Giày Thể Thao Nike Air Max 270",
  "gia_goc": 3500000,
  "gia_khuyen_mai": 2800000,
  ...
}
```

---

### 3. Tạo sản phẩm mới
**POST** `/api/product`

**Body (JSON):**
```json
{
  "ten_san_pham": "Giày Thể Thao Nike Air Max 270",
  "gia_goc": 3500000,
  "gia_khuyen_mai": 2800000,
  "hinh_anh": "https://example.com/images/nike-air-max-270.jpg",
  "mo_ta": "Mô tả sản phẩm...",
  "thuong_hieu": "Nike",
  "danh_muc": "nam",
  "kich_thuoc": ["40", "41", "42", "43", "44"],
  "so_luong_ton": 50,
  "danh_gia": 4.5,
  "so_luong_da_ban": 120,
  "trang_thai": "active"
}
```

**Response:**
```json
{
  "message": "Sản phẩm được tạo thành công",
  "product": {...}
}
```

---

### 4. Cập nhật sản phẩm
**PUT** `/api/product/:id`

**Body (JSON):** (Chỉ cần gửi các trường muốn cập nhật)
```json
{
  "gia_khuyen_mai": 2500000,
  "so_luong_ton": 45
}
```

**Response:**
```json
{
  "message": "Cập nhật sản phẩm thành công",
  "product": {...}
}
```

---

### 5. Xóa sản phẩm
**DELETE** `/api/product/:id`

**Response:**
```json
{
  "message": "Xóa sản phẩm thành công"
}
```

---

### 6. Cập nhật số lượng tồn kho
**PUT** `/api/product/:id/stock`

**Body (JSON):**
```json
{
  "so_luong_ton": 100
}
```

**Response:**
```json
{
  "message": "Cập nhật số lượng tồn kho thành công",
  "product": {...}
}
```

---

### 7. Lấy sản phẩm theo danh mục
**GET** `/api/product/category/:danh_muc`

**Ví dụ:**
```
GET /api/product/category/nam
GET /api/product/category/nu
GET /api/product/category/unisex
```

**Response:**
```json
[...]
```

---

### 8. Lấy sản phẩm bán chạy
**GET** `/api/product/best-selling`

**Query Parameters:**
- `limit` (optional): Số sản phẩm (mặc định: 10)

**Ví dụ:**
```
GET /api/product/best-selling?limit=5
```

**Response:**
```json
[...]
```

---

### 9. Lấy sản phẩm mới nhất
**GET** `/api/product/newest`

**Query Parameters:**
- `limit` (optional): Số sản phẩm (mặc định: 10)

**Ví dụ:**
```
GET /api/product/newest?limit=5
```

**Response:**
```json
[...]
```

---

## Mã lỗi

- `200`: Thành công
- `201`: Tạo thành công
- `400`: Dữ liệu không hợp lệ
- `404`: Không tìm thấy sản phẩm
- `500`: Lỗi server

## Ví dụ sử dụng với cURL

### Lấy tất cả sản phẩm
```bash
curl http://localhost:3000/api/product
```

### Tạo sản phẩm mới
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

### Cập nhật sản phẩm
```bash
curl -X PUT http://localhost:3000/api/product/PRODUCT_ID \
  -H "Content-Type: application/json" \
  -d '{
    "gia_khuyen_mai": 750000
  }'
```

### Xóa sản phẩm
```bash
curl -X DELETE http://localhost:3000/api/product/PRODUCT_ID
```

