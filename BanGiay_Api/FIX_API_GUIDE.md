# Hướng dẫn Fix API không hiển thị

## Các bước kiểm tra và sửa:

### 1. Kiểm tra Server đang chạy
```bash
cd BanGiay_Api
npm start
# Phải thấy: "Server đang chạy tại http://localhost:3000"
# Phải thấy: "MongoDB kết nối thành công!"
```

### 2. Test API bằng browser
Mở browser và test:
```
http://localhost:3000/api/product/best-selling?limit=5
http://localhost:3000/api/product/category/nam
```

### 3. Kiểm tra Base URL trong Android

**File: `app/build.gradle`**

- **Nếu test trên Emulator:**
  ```gradle
  buildConfigField "String", "API_BASE_URL", "\"http://10.0.2.2:3000/api/\""
  ```

- **Nếu test trên thiết bị thật:**
  1. Tìm IP máy tính:
     - Windows: `ipconfig` (tìm IPv4 Address)
     - Mac/Linux: `ifconfig` hoặc `ip addr`
  2. Cập nhật trong `build.gradle`:
     ```gradle
     buildConfigField "String", "API_BASE_URL", "\"http://YOUR_IP:3000/api/\""
     ```
     Ví dụ: `"http://192.168.1.100:3000/api/"`

### 4. Rebuild Project
Sau khi thay đổi `build.gradle`:
```
Build > Clean Project
Build > Rebuild Project
```

### 5. Kiểm tra Logcat

Mở Logcat trong Android Studio và filter: `MainActivity`

**Logs cần tìm:**
- ✅ "Starting to load products from API..."
- ✅ "API Base URL: http://..."
- ✅ "Top products response code: 200"
- ✅ "Top products data: X" (X là số sản phẩm)
- ✅ "Processing product: ..."
- ✅ "Added product: ..."

**Nếu thấy lỗi:**
- ❌ "Failed to load top products"
- ❌ "Error type: ..."
- ❌ "Error message: ..."

### 6. Các lỗi thường gặp:

#### Lỗi: "Unable to resolve host"
- **Nguyên nhân:** IP address không đúng
- **Giải pháp:** Kiểm tra lại IP trong `build.gradle`

#### Lỗi: "Connection refused" hoặc "Failed to connect"
- **Nguyên nhân:** Server không chạy hoặc firewall chặn
- **Giải pháp:** 
  - Kiểm tra server đang chạy
  - Tắt firewall tạm thời
  - Kiểm tra port 3000 không bị chặn

#### Lỗi: "Top products list is empty or null"
- **Nguyên nhân:** Database không có sản phẩm hoặc response format sai
- **Giải pháp:**
  - Chạy: `node seed-products.js` để thêm sản phẩm
  - Kiểm tra response trong Logcat

#### Response code: 404
- **Nguyên nhân:** URL không đúng
- **Giải pháp:** Kiểm tra base URL có `/api/` ở cuối

### 7. Test nhanh bằng Postman hoặc curl

```bash
# Test best-selling
curl http://localhost:3000/api/product/best-selling?limit=5

# Test category
curl http://localhost:3000/api/product/category/nam
```

### 8. Kiểm tra Network Security Config

File: `app/src/main/res/xml/network_security_config.xml`
```xml
<?xml version="1.0" encoding="utf-8"?>
<network-security-config>
    <base-config cleartextTrafficPermitted="true">
        <trust-anchors>
            <certificates src="system" />
        </trust-anchors>
    </base-config>
</network-security-config>
```

### 9. Debug Checklist

- [ ] Server đang chạy trên port 3000
- [ ] MongoDB đã kết nối
- [ ] Database có sản phẩm (chạy `node seed-products.js`)
- [ ] Base URL đúng trong `build.gradle`
- [ ] Đã rebuild project sau khi đổi base URL
- [ ] Permissions đã có trong `AndroidManifest.xml`
- [ ] Network security config cho phép cleartext traffic
- [ ] Kiểm tra Logcat để xem lỗi cụ thể

### 10. Nếu vẫn không được

1. Xem Logcat chi tiết
2. Copy toàn bộ error message
3. Kiểm tra response body trong Logcat
4. Test API trực tiếp bằng browser/Postman

