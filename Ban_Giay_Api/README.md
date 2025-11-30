# API Server - Ban Giày

API server cho ứng dụng Bán Giày sử dụng Node.js, Express và MongoDB.

## Cấu trúc thư mục

```
Ban_Giay_Api/
├── config/
│   └── db.js              # Cấu hình kết nối MongoDB
├── controllers/
│   ├── auth.controller.js # Controller xử lý đăng ký, đăng nhập
│   ├── product.controller.js # Controller xử lý sản phẩm
│   └── user.controller.js # Controller xử lý user
├── models/
│   ├── Product.js         # Model sản phẩm
│   └── User.js            # Model user
├── routes/
│   ├── auth.routes.js     # Routes cho authentication
│   ├── product.routes.js  # Routes cho sản phẩm
│   └── user.routes.js     # Routes cho user
├── server.js              # File chính khởi động server
└── package.json           # Dependencies
```

## Cài đặt

```bash
npm install
```

## Chạy server

```bash
npm start
# hoặc
node server.js
```

Server sẽ chạy tại: `http://localhost:3000`

## API Endpoints

### Sản phẩm

- `GET /api/product` - Lấy tất cả sản phẩm (có phân trang, lọc, tìm kiếm)
- `GET /api/product/:id` - Lấy sản phẩm theo ID
- `GET /api/product/best-selling` - Sản phẩm bán chạy
- `GET /api/product/newest` - Sản phẩm mới nhất
- `GET /api/product/category/:danh_muc` - Sản phẩm theo danh mục
- `POST /api/product` - Tạo sản phẩm mới
- `PUT /api/product/:id` - Cập nhật sản phẩm
- `PUT /api/product/:id/stock` - Cập nhật số lượng tồn kho
- `DELETE /api/product/:id` - Xóa sản phẩm

### Authentication

- `POST /api/auth/register` - Đăng ký
- `POST /api/auth/login` - Đăng nhập
- `POST /api/auth/forgot-password` - Quên mật khẩu

### User

- `GET /api/user` - Lấy tất cả user
- `GET /api/user/:id` - Lấy user theo ID
- `POST /api/user` - Tạo user mới
- `PUT /api/user/:id` - Cập nhật user
- `DELETE /api/user/:id` - Xóa user

## Seed dữ liệu

```bash
node seed-products.js
```

## Cấu hình

Tạo file `.env` (tùy chọn):
```
MONGODB_URI=mongodb://localhost:27017/BanGiay_App
PORT=3000
JWT_SECRET=your_secret_key
```

