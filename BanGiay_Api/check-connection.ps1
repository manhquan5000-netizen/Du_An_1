# Script kiểm tra kết nối server
Write-Host "=== Kiểm tra Server ===" -ForegroundColor Green
Write-Host ""

# Kiểm tra server có đang chạy không
$response = $null
try {
    $response = Invoke-WebRequest -Uri "http://localhost:3000" -TimeoutSec 5 -UseBasicParsing
    Write-Host "✅ Server đang chạy!" -ForegroundColor Green
    Write-Host "   Status: $($response.StatusCode)" -ForegroundColor Cyan
    Write-Host "   Response: $($response.Content)" -ForegroundColor Cyan
} catch {
    Write-Host "❌ Server không chạy hoặc không thể kết nối!" -ForegroundColor Red
    Write-Host "   Error: $($_.Exception.Message)" -ForegroundColor Yellow
    Write-Host ""
    Write-Host "Hãy chạy: npm start" -ForegroundColor Yellow
    exit 1
}

Write-Host ""
Write-Host "=== Test API Endpoints ===" -ForegroundColor Green
Write-Host ""

# Test best-selling
try {
    $apiResponse = Invoke-WebRequest -Uri "http://localhost:3000/api/product/best-selling?limit=3" -UseBasicParsing
    $products = $apiResponse.Content | ConvertFrom-Json
    Write-Host "✅ GET /api/product/best-selling" -ForegroundColor Green
    Write-Host "   Số sản phẩm: $($products.Count)" -ForegroundColor Cyan
} catch {
    Write-Host "❌ GET /api/product/best-selling - Lỗi: $($_.Exception.Message)" -ForegroundColor Red
}

# Test category
try {
    $apiResponse = Invoke-WebRequest -Uri "http://localhost:3000/api/product/category/nam" -UseBasicParsing
    $products = $apiResponse.Content | ConvertFrom-Json
    Write-Host "✅ GET /api/product/category/nam" -ForegroundColor Green
    Write-Host "   Số sản phẩm: $($products.Count)" -ForegroundColor Cyan
} catch {
    Write-Host "❌ GET /api/product/category/nam - Lỗi: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host ""
Write-Host "=== Thông tin kết nối ===" -ForegroundColor Green
Write-Host "IP máy tính: 192.168.0.100" -ForegroundColor Cyan
Write-Host "URL cho thiết bị thật: http://192.168.0.100:3000/api/" -ForegroundColor Cyan
Write-Host ""
Write-Host "⚠️  Lưu ý:" -ForegroundColor Yellow
Write-Host "1. Đảm bảo máy tính và điện thoại cùng mạng Wi-Fi" -ForegroundColor White
Write-Host "2. Tắt Firewall hoặc mở port 3000" -ForegroundColor White
Write-Host "3. Rebuild project sau khi đổi IP trong build.gradle" -ForegroundColor White

