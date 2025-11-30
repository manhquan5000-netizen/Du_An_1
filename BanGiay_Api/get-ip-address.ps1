# Script để lấy IP address của máy tính
Write-Host "=== IP Address của máy tính ===" -ForegroundColor Green
Write-Host ""

# Lấy tất cả IP addresses
$adapters = Get-NetIPAddress -AddressFamily IPv4 | Where-Object { $_.IPAddress -notlike "127.*" -and $_.IPAddress -notlike "169.254.*" }

if ($adapters) {
    Write-Host "Các IP address có thể dùng:" -ForegroundColor Yellow
    foreach ($adapter in $adapters) {
        $interface = Get-NetAdapter | Where-Object { $_.ifIndex -eq $adapter.InterfaceIndex }
        Write-Host "  - $($adapter.IPAddress) ($($interface.Name))" -ForegroundColor Cyan
    }
    Write-Host ""
    Write-Host "Chọn IP address phù hợp (thường là Wi-Fi hoặc Ethernet)" -ForegroundColor Yellow
    Write-Host ""
    Write-Host "Cập nhật trong app/build.gradle:" -ForegroundColor Green
    Write-Host '  buildConfigField "String", "API_BASE_URL", "\"http://IP_ADDRESS:3000/api/\""' -ForegroundColor White
} else {
    Write-Host "Không tìm thấy IP address!" -ForegroundColor Red
}

