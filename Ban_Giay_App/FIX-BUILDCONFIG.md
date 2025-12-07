# Hướng dẫn Fix Lỗi BuildConfig

## Vấn đề phổ biến: BuildConfig.API_BASE_URL không tìm thấy

### Giải pháp 1: Rebuild Project (Khuyến nghị)

1. Trong Android Studio:
   - **Build** → **Clean Project**
   - **Build** → **Rebuild Project**
   - Hoặc: **File** → **Invalidate Caches / Restart** → **Invalidate and Restart**

2. Đảm bảo `buildFeatures { buildConfig true }` có trong `app/build.gradle`

### Giải pháp 2: Kiểm tra file build.gradle

Đảm bảo có các dòng sau trong `app/build.gradle`:

```gradle
android {
    buildFeatures {
        buildConfig true  // ← Phải có dòng này
    }
    
    defaultConfig {
        buildConfigField "String", "API_BASE_URL", "\"http://10.0.2.2:3000/api/\""
    }
}
```

### Giải pháp 3: Kiểm tra BuildConfig đã được generate chưa

Sau khi rebuild, kiểm tra file:
```
app/build/generated/source/buildConfig/debug/com/poly/ban_giay_app/BuildConfig.java
```

File này phải chứa:
```java
public static final String API_BASE_URL = "http://10.0.2.2:3000/api/";
```

### Giải pháp 4: Nếu vẫn lỗi, dùng hardcode tạm thời

Thay đổi trong `ApiClient.java`:
```java
// Thay vì:
String baseUrl = BuildConfig.API_BASE_URL;

// Dùng:
String baseUrl = "http://10.0.2.2:3000/api/";
```

**Lưu ý:** Chỉ dùng cách này tạm thời để test, sau đó quay lại dùng BuildConfig.

## Các lỗi khác có thể gặp

### Lỗi: "Cannot resolve symbol 'BuildConfig'"
- **Fix:** Rebuild project (Giải pháp 1)
- **Fix:** Kiểm tra package name có đúng không: `com.poly.ban_giay_app.BuildConfig`

### Lỗi: "API_BASE_URL cannot be resolved"
- **Fix:** Đảm bảo `buildConfigField` đã được định nghĩa trong `build.gradle`
- **Fix:** Rebuild project

### Lỗi: "Network security config"
- **Fix:** Đảm bảo có file `network_security_config.xml` trong `app/src/main/res/xml/`
- **Fix:** Đảm bảo `AndroidManifest.xml` có:
  ```xml
  <application
      android:networkSecurityConfig="@xml/network_security_config"
      ...>
  ```

## Kiểm tra nhanh

1. Mở Terminal trong Android Studio
2. Chạy:
   ```bash
   ./gradlew clean
   ./gradlew build
   ```
3. Kiểm tra log xem có lỗi gì không

## Nếu vẫn không được

1. Xóa thư mục `.gradle` và `build` trong project
2. Rebuild lại project
3. Restart Android Studio

