# Hướng dẫn Import Sản Phẩm vào MongoDB

## Cách 1: Sử dụng Script Node.js (Khuyến nghị)

### Bước 1: Đảm bảo MongoDB đang chạy
```bash
# Kiểm tra MongoDB đang chạy
mongosh
# Hoặc kiểm tra trong MongoDB Compass
```

### Bước 2: Chạy script import
```bash
cd BanGiay_Api
node import-products-to-mongodb.js
```

### Bước 3: Nếu muốn xóa dữ liệu cũ và import lại
```bash
node import-products-to-mongodb.js --force
```

## Cách 2: Import trực tiếp từ MongoDB Compass

1. Mở MongoDB Compass
2. Kết nối đến `localhost:27017`
3. Chọn database `BanGiay_App`
4. Chọn collection `products`
5. Click nút **"Import data"**
6. Chọn file `products-import.json`
7. Chọn format: **JSON**
8. Click **"Import"**

## Cách 3: Sử dụng mongoimport (Command Line)

```bash
mongoimport --db BanGiay_App --collection products --file products-import.json --jsonArray
```

## Kiểm tra kết quả

### Trong MongoDB Compass:
1. Mở collection `products` trong database `BanGiay_App`
2. Bạn sẽ thấy 20 sản phẩm đã được import

### Trong MongoDB Shell:
```bash
mongosh
use BanGiay_App
db.products.countDocuments()
db.products.find().pretty()
```

## Lưu ý

- File `products-import.json` chứa 20 sản phẩm mẫu
- Tất cả sản phẩm đều có `trang_thai: "active"` để hiển thị trong app
- Database name: `BanGiay_App` (theo config trong `config/db.js`)
- Collection name: `products` (theo model trong `models/Product.js`)

## Troubleshooting

### Lỗi kết nối MongoDB
- Đảm bảo MongoDB đang chạy
- Kiểm tra connection string trong `.env` hoặc `config/db.js`

### Lỗi duplicate key
- Nếu sản phẩm đã tồn tại, script sẽ bỏ qua (nếu không dùng `--force`)
- Dùng `--force` để xóa và import lại

### Lỗi validation
- Kiểm tra file JSON có đúng format không
- Đảm bảo tất cả trường bắt buộc đều có giá trị

