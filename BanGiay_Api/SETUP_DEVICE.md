# Hướng dẫn Setup cho Thiết bị Thật

## IP Address của máy tính: `192.168.0.100`

## Các bước setup:

### 1. Đã cập nhật build.gradle
File `app/build.gradle` đã được cập nhật với IP: `192.168.0.100`

### 2. Rebuild Project
Sau khi cập nhật build.gradle:
```
Build > Clean Project
Build > Rebuild Project
```

### 3. Đảm bảo Server đang chạy
```bash
cd BanGiay_Api
npm start
```

### 4. Đảm bảo máy tính và điện thoại cùng mạng Wi-Fi
- Máy tính và điện thoại phải kết nối cùng một mạng Wi-Fi
- Kiểm tra IP máy tính: `ipconfig` (Windows) hoặc `ifconfig` (Mac/Linux)

### 5. Tắt Firewall tạm thời (nếu cần)
- Windows: Control Panel > Windows Defender Firewall > Turn Windows Defender Firewall on or off
- Tắt Firewall cho Private networks tạm thời để test

### 6. Test kết nối từ điện thoại
Trên điện thoại, mở browser và test:
```
http://192.168.0.100:3000/api/product/best-selling?limit=5
```

Nếu không mở được, có thể firewall đang chặn.

### 7. Kiểm tra Logcat
Trong Android Studio, mở Logcat và filter: `MainActivity`

Tìm các log:
- "API Base URL: http://192.168.0.100:3000/api/"
- "Top products response code: 200"
- "Added product: ..."

### 8. Nếu vẫn không kết nối được

**Kiểm tra Firewall:**
```powershell
# Mở port 3000 trong Windows Firewall
netsh advfirewall firewall add rule name="Node.js Server" dir=in action=allow protocol=TCP localport=3000
```

**Hoặc tắt Firewall tạm thời:**
- Control Panel > Windows Defender Firewall
- Turn Windows Defender Firewall on or off
- Tắt cho Private networks

**Kiểm tra server đang listen trên tất cả interfaces:**
Đảm bảo server.js đang chạy và có thể truy cập từ mạng local.

### 9. Test từ điện thoại bằng browser
Mở Chrome/Safari trên điện thoại và truy cập:
```
http://192.168.0.100:3000
```
Phải thấy: "API BanGiay đang chạy..."

### 10. Nếu IP thay đổi
Nếu IP máy tính thay đổi, cập nhật lại trong `app/build.gradle` và rebuild.

