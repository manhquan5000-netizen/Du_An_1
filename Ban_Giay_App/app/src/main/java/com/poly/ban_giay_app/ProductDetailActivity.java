package com.poly.ban_giay_app;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.StrikethroughSpan;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.poly.ban_giay_app.models.Product;
import com.poly.ban_giay_app.network.ApiClient;
import com.poly.ban_giay_app.network.ApiService;
import com.poly.ban_giay_app.network.NetworkUtils;
import com.poly.ban_giay_app.network.model.BaseResponse;
import com.poly.ban_giay_app.network.model.ProductResponse;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductDetailActivity extends AppCompatActivity {
    private ImageView btnBack, imgProduct;
    private TextView txtProductName, txtBrand, txtRating, txtPriceOld, txtPriceNew, txtQuantity;
    private Button btnSize37, btnSize38, btnSize39, btnDecrease, btnIncrease, btnAddToCart, btnBuyNow;
    private Product product;
    private int quantity = 1;
    private String selectedSize = "";
    private SessionManager sessionManager;
    private ApiService apiService;
    private List<String> availableSizes; // Danh sách size có sẵn từ server

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_product_detail);

        // Apply insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Get product from intent
        product = (Product) getIntent().getSerializableExtra("product");
        if (product == null) {
            finish();
            return;
        }

        sessionManager = new SessionManager(this);
        
        // Initialize CartManager with context
        CartManager.getInstance().setContext(this);
        
        // Initialize API service
        ApiClient.init(this);
        apiService = ApiClient.getApiService();

        initViews();
        bindActions();
        displayProduct();
        setupBottomNavigation();
        
        // Enable tất cả size buttons mặc định (sẽ được cập nhật sau khi load từ API)
        enableAllSizeButtons();
        
        // Load product details từ API để lấy danh sách size có sẵn
        if (product.id != null && !product.id.isEmpty()) {
            loadProductDetails();
        }
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        imgProduct = findViewById(R.id.imgProduct);
        txtProductName = findViewById(R.id.txtProductName);
        txtBrand = findViewById(R.id.txtBrand);
        txtRating = findViewById(R.id.txtRating);
        txtPriceOld = findViewById(R.id.txtPriceOld);
        txtPriceNew = findViewById(R.id.txtPriceNew);
        txtQuantity = findViewById(R.id.txtQuantity);
        
        btnSize37 = findViewById(R.id.btnSize37);
        btnSize38 = findViewById(R.id.btnSize38);
        btnSize39 = findViewById(R.id.btnSize39);
        btnDecrease = findViewById(R.id.btnDecrease);
        btnIncrease = findViewById(R.id.btnIncrease);
        btnAddToCart = findViewById(R.id.btnAddToCart);
        btnBuyNow = findViewById(R.id.btnBuyNow);
    }

    private void bindActions() {
        btnBack.setOnClickListener(v -> finish());

        // Size selection
        btnSize37.setOnClickListener(v -> selectSize("37", btnSize37));
        btnSize38.setOnClickListener(v -> selectSize("38", btnSize38));
        btnSize39.setOnClickListener(v -> selectSize("39", btnSize39));

        // Quantity controls
        btnDecrease.setOnClickListener(v -> {
            if (quantity > 1) {
                quantity--;
                txtQuantity.setText(String.valueOf(quantity));
                updateBuyNowButtonPrice();
            }
        });

        btnIncrease.setOnClickListener(v -> {
            quantity++;
            txtQuantity.setText(String.valueOf(quantity));
            updateBuyNowButtonPrice();
        });

        // Add to cart
        btnAddToCart.setOnClickListener(v -> {
            if (!ensureLoggedIn()) {
                return;
            }
            if (selectedSize.isEmpty()) {
                Toast.makeText(this, "Vui lòng chọn kích thước", Toast.LENGTH_SHORT).show();
                return;
            }
            
            // Kiểm tra product ID trước khi thêm vào giỏ hàng
            if (product.id == null || product.id.isEmpty()) {
                Toast.makeText(this, "Sản phẩm này không có ID. Vui lòng chọn sản phẩm từ danh sách chính thức.", Toast.LENGTH_LONG).show();
                return;
            }
            
            // Disable button during API call
            btnAddToCart.setEnabled(false);
            btnAddToCart.setText("Đang thêm...");
            
            // Show immediate success message
            Toast.makeText(this, "Đang thêm vào giỏ hàng...", Toast.LENGTH_SHORT).show();
            
            // Add to cart with callback
            CartManager.getInstance().addToCart(product, selectedSize, quantity, new CartManager.CartCallback() {
                @Override
                public void onSuccess(String message) {
                    runOnUiThread(() -> {
                        btnAddToCart.setEnabled(true);
                        btnAddToCart.setText("Thêm vào giỏ hàng");
                        // Hiển thị thông báo thành công
                        Toast.makeText(ProductDetailActivity.this, message, Toast.LENGTH_SHORT).show();
                        
                        // Gửi broadcast để CartActivity reload từ API nếu đang mở
                        android.content.Intent intent = new android.content.Intent("com.poly.ban_giay_app.CART_UPDATED");
                        intent.setPackage(getPackageName()); // Đảm bảo broadcast chỉ gửi trong app
                        sendBroadcast(intent);
                        Log.d("ProductDetailActivity", "✅ Broadcast CART_UPDATED sent");
                    });
                }

                @Override
                public void onError(String error) {
                    runOnUiThread(() -> {
                        btnAddToCart.setEnabled(true);
                        btnAddToCart.setText("Thêm vào giỏ hàng");
                        // Hiển thị lỗi chi tiết hơn
                        String errorMessage = error;
                        if (error == null || error.isEmpty()) {
                            errorMessage = "Không thể thêm vào giỏ hàng. Vui lòng thử lại.";
                        } else if (error.contains("Không tìm thấy tài nguyên") || error.contains("404")) {
                            errorMessage = "Sản phẩm không tồn tại trong hệ thống. Vui lòng thử lại hoặc chọn sản phẩm khác.";
                        } else if (error.contains("kích thước") || error.contains("size") || 
                                   error.contains("không có sẵn") || error.contains("not available")) {
                            // Thông báo lỗi về size đã rõ ràng từ server
                            errorMessage = error;
                            // Nếu size không có sẵn, reset selection và disable button đó
                            if (selectedSize != null && !selectedSize.isEmpty()) {
                                resetSizeButton(selectedSize);
                                selectedSize = "";
                            }
                        }
                        Toast.makeText(ProductDetailActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                        Log.e("ProductDetailActivity", "Error adding to cart: " + errorMessage);
                    });
                }
            });
        });

        // Buy now
        btnBuyNow.setOnClickListener(v -> {
            if (!ensureLoggedIn()) {
                return;
            }
            if (selectedSize.isEmpty()) {
                Toast.makeText(this, "Vui lòng chọn kích thước", Toast.LENGTH_SHORT).show();
                return;
            }
            
            // Kiểm tra product ID
            if (product.id == null || product.id.isEmpty()) {
                Toast.makeText(this, "Sản phẩm này không có ID. Vui lòng chọn sản phẩm từ danh sách chính thức.", Toast.LENGTH_LONG).show();
                return;
            }
            
            // Chuyển sang màn hình chọn phương thức thanh toán
            Intent intent = new Intent(ProductDetailActivity.this, PaymentMethodActivity.class);
            intent.putExtra("product", product);
            intent.putExtra("quantity", quantity);
            intent.putExtra("selectedSize", selectedSize);
            intent.putExtra("isFromCart", false);
            startActivity(intent);
        });
    }

    private void selectSize(String size, Button button) {
        // Cho phép chọn size bất kể API trả về gì
        // Backend sẽ validate khi thêm vào giỏ hàng hoặc thanh toán
        if (!button.isEnabled()) {
            // Nếu button bị disable, enable lại và cho phép chọn
            enableSizeButton(button, size);
        }
        
        selectedSize = size;
        
        // Reset all buttons - chữ màu trắng, không có gạch chân
        resetButton(btnSize37, "37");
        resetButton(btnSize38, "38");
        resetButton(btnSize39, "39");
        
        // Highlight selected button - có gạch chân
        String buttonText = button.getText().toString();
        // Loại bỏ "(Hết)" nếu có
        if (buttonText.contains("(")) {
            buttonText = buttonText.substring(0, buttonText.indexOf("(")).trim();
        }
        SpannableString spannableString = new SpannableString(buttonText);
        spannableString.setSpan(new UnderlineSpan(), 0, buttonText.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        button.setText(spannableString);
        button.setBackgroundResource(R.drawable.bg_size_button_selected);
        button.setTextColor(ContextCompat.getColor(this, android.R.color.white));
    }
    
    private void resetButton(Button button, String text) {
        // Chỉ reset nếu button đang enabled
        if (button.isEnabled()) {
            button.setBackgroundResource(R.drawable.bg_size_button);
            // Loại bỏ "(Hết)" nếu có trong text
            String cleanText = text;
            if (text.contains("(")) {
                cleanText = text.substring(0, text.indexOf("(")).trim();
            }
            button.setText(cleanText);
            button.setTextColor(ContextCompat.getColor(this, android.R.color.white));
            button.setAlpha(1.0f);
        }
    }

    /**
     * Kiểm tra đăng nhập. Nếu chưa đăng nhập thì chuyển sang màn Login.
     *
     * @return true nếu đã đăng nhập, false nếu đã chuyển sang Login.
     */
    private boolean ensureLoggedIn() {
        if (sessionManager != null && sessionManager.isLoggedIn()) {
            return true;
        }

        Toast.makeText(this, "Vui lòng đăng nhập để tiếp tục mua hàng", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        return false;
    }

    private void displayProduct() {
        // Load image from URL if available, otherwise use resource
        if (product.imageUrl != null && !product.imageUrl.isEmpty()) {
            // Nếu là URL từ server
            if (product.imageUrl.startsWith("http://") || product.imageUrl.startsWith("https://")) {
                Glide.with(this)
                        .load(product.imageUrl)
                        .placeholder(R.drawable.giaymau)
                        .error(R.drawable.giaymau)
                        .into(imgProduct);
            } else {
                // Nếu là tên file ảnh (giay15, giay14, etc.), load từ drawable
                int imageResId = getImageResourceId(product.imageUrl);
                if (imageResId != 0) {
                    imgProduct.setImageResource(imageResId);
                } else {
                    imgProduct.setImageResource(R.drawable.giaymau);
                }
            }
        } else if (product.imageRes != 0) {
            imgProduct.setImageResource(product.imageRes);
        } else {
            imgProduct.setImageResource(R.drawable.giaymau);
        }
        
        txtProductName.setText(product.name);
        
        // Extract brand from product name (first word after "Giày")
        String brand = product.name.replace("Giày ", "").split(" ")[0];
        txtBrand.setText(brand);
        
        // Rating (5 stars)
        txtRating.setText("★★★★★");
        
        // Price
        String priceOldText = "Giá gốc: " + product.priceOld;
        SpannableString ss = new SpannableString(priceOldText);
        int startIndex = priceOldText.indexOf(product.priceOld);
        ss.setSpan(new StrikethroughSpan(), startIndex, priceOldText.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        txtPriceOld.setText(ss);
        
        txtPriceNew.setText("Giá khuyến mãi: " + product.priceNew);
        
        // Quantity
        txtQuantity.setText(String.valueOf(quantity));
        
        // Cập nhật giá trên nút "MUA NGAY"
        updateBuyNowButtonPrice();
    }
    
    /**
     * Cập nhật giá trên nút "MUA NGAY" dựa trên số lượng đã chọn
     */
    private void updateBuyNowButtonPrice() {
        if (btnBuyNow == null || product == null) {
            return;
        }
        
        try {
            // Parse giá từ product.priceNew
            long pricePerUnit = 0;
            if (product.priceNew != null && !product.priceNew.isEmpty()) {
                String priceStr = product.priceNew.replaceAll("[^0-9]", "");
                if (!priceStr.isEmpty()) {
                    pricePerUnit = Long.parseLong(priceStr);
                }
            }
            
            // Tính tổng giá = giá đơn vị * số lượng
            long totalPrice = pricePerUnit * quantity;
            
            // Format giá với dấu chấm ngăn cách hàng nghìn
            String formattedPrice = formatPriceForButton(totalPrice);
            
            // Cập nhật text của nút "MUA NGAY"
            String buttonText = "MUA NGAY\n" + formattedPrice;
            btnBuyNow.setText(buttonText);
            
            Log.d("ProductDetailActivity", "Updated Buy Now button price: " + formattedPrice + " (Quantity: " + quantity + ")");
        } catch (Exception e) {
            Log.e("ProductDetailActivity", "Error updating Buy Now button price", e);
        }
    }
    
    /**
     * Format giá với ký tự ₫ cho nút (ví dụ: 500000 -> "500.000₫")
     */
    private String formatPriceForButton(long price) {
        if (price <= 0) {
            return "0₫";
        }
        try {
            // Format với dấu chấm ngăn cách hàng nghìn
            String formatted = String.format("%,d", price).replace(",", ".");
            return formatted + "₫";
        } catch (Exception e) {
            Log.e("ProductDetailActivity", "Error formatting price: " + price, e);
            return String.valueOf(price) + "₫";
        }
    }
    
    /**
     * Lấy resource ID từ tên file ảnh (giay15, giay14, etc.)
     */
    private int getImageResourceId(String imageName) {
        // Loại bỏ extension và path nếu có
        String name = imageName;
        if (name.contains("/")) {
            name = name.substring(name.lastIndexOf("/") + 1);
        }
        if (name.contains(".")) {
            name = name.substring(0, name.lastIndexOf("."));
        }
        
        // Map tên file với resource ID
        return getResources().getIdentifier(name, "drawable", getPackageName());
    }
    
    /**
     * Load product details từ API để lấy danh sách size có sẵn
     */
    private void loadProductDetails() {
        if (apiService == null || product.id == null || product.id.isEmpty()) {
            Log.d("ProductDetailActivity", "Cannot load product details: apiService or product.id is null");
            return;
        }
        
        if (!NetworkUtils.isConnected(this)) {
            Log.w("ProductDetailActivity", "No network connection, keeping all sizes enabled");
            // Không có mạng, giữ nguyên tất cả size enabled
            return;
        }
        
        apiService.getProductById(product.id).enqueue(new Callback<BaseResponse<ProductResponse>>() {
            @Override
            public void onResponse(Call<BaseResponse<ProductResponse>> call, Response<BaseResponse<ProductResponse>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getSuccess()) {
                    ProductResponse productResponse = response.body().getData();
                    if (productResponse != null) {
                        availableSizes = productResponse.getKichThuoc();
                        Log.d("ProductDetailActivity", "Received available sizes from API: " + availableSizes);
                        // Chỉ update nếu có dữ liệu, nếu không giữ nguyên (đã enable mặc định)
                        if (availableSizes != null && !availableSizes.isEmpty()) {
                            updateSizeButtons();
                        } else {
                            Log.d("ProductDetailActivity", "No sizes from API, keeping all sizes enabled");
                        }
                    } else {
                        Log.d("ProductDetailActivity", "ProductResponse is null, keeping all sizes enabled");
                    }
                } else {
                    Log.w("ProductDetailActivity", "API response not successful, keeping all sizes enabled");
                }
            }
            
            @Override
            public void onFailure(Call<BaseResponse<ProductResponse>> call, Throwable t) {
                Log.e("ProductDetailActivity", "Failed to load product details", t);
                // Lỗi API, giữ nguyên tất cả size enabled
            }
        });
    }
    
    /**
     * Enable tất cả size buttons mặc định
     */
    private void enableAllSizeButtons() {
        enableSizeButton(btnSize37, "37");
        enableSizeButton(btnSize38, "38");
        enableSizeButton(btnSize39, "39");
        Log.d("ProductDetailActivity", "All size buttons enabled by default");
    }
    
    /**
     * Cập nhật trạng thái các button size dựa trên danh sách size có sẵn
     * Chỉ disable size khi API xác nhận rõ ràng size không có sẵn
     */
    private void updateSizeButtons() {
        // Nếu không có dữ liệu từ API, giữ nguyên (đã enable mặc định)
        if (availableSizes == null || availableSizes.isEmpty()) {
            Log.d("ProductDetailActivity", "No available sizes from API, keeping all sizes enabled");
            enableAllSizeButtons(); // Đảm bảo tất cả đều enabled
            return;
        }
        
        Log.d("ProductDetailActivity", "Available sizes from API: " + availableSizes);
        
        // Normalize availableSizes để so sánh (trim và convert sang string)
        List<String> normalizedSizes = new ArrayList<>();
        for (String size : availableSizes) {
            if (size != null) {
                String trimmed = size.trim();
                if (!trimmed.isEmpty()) {
                    normalizedSizes.add(trimmed);
                }
            }
        }
        
        // Nếu danh sách normalized rỗng, enable tất cả
        if (normalizedSizes.isEmpty()) {
            Log.d("ProductDetailActivity", "Normalized sizes is empty, enabling all sizes");
            enableAllSizeButtons();
            return;
        }
        
        // Chỉ disable các size không có trong danh sách (và chỉ khi danh sách có ít nhất 1 size)
        // Nếu danh sách có size, nhưng không có 37, 38, 39 thì vẫn enable (có thể là format khác)
        // Chỉ disable khi chắc chắn size không có sẵn
        
        // Enable tất cả trước
        enableSizeButton(btnSize37, "37");
        enableSizeButton(btnSize38, "38");
        enableSizeButton(btnSize39, "39");
        
        // Chỉ disable nếu danh sách có size và không chứa size đó
        // Nhưng để an toàn, chỉ disable khi danh sách có nhiều hơn 0 size và không chứa size đó
        // Và chỉ disable khi chắc chắn (có ít nhất 1 size khác trong danh sách)
        if (normalizedSizes.size() > 0) {
            // Kiểm tra xem có size 37, 38, 39 trong danh sách không
            boolean has37 = normalizedSizes.contains("37");
            boolean has38 = normalizedSizes.contains("38");
            boolean has39 = normalizedSizes.contains("39");
            
            // Chỉ disable nếu chắc chắn size không có (và có ít nhất 1 size khác)
            // Để tránh disable nhầm, chỉ disable khi danh sách có size và không chứa size đó
            // Nhưng để đảm bảo user có thể chọn, tạm thời không disable
            // Chỉ log để debug
            if (!has37) {
                Log.d("ProductDetailActivity", "Size 37 not in available sizes, but keeping enabled for user selection");
            }
            if (!has38) {
                Log.d("ProductDetailActivity", "Size 38 not in available sizes, but keeping enabled for user selection");
            }
            if (!has39) {
                Log.d("ProductDetailActivity", "Size 39 not in available sizes, but keeping enabled for user selection");
            }
        }
    }
    
    /**
     * Enable size button và hiển thị trạng thái có sẵn
     */
    private void enableSizeButton(Button button, String size) {
        if (button == null) {
            Log.e("ProductDetailActivity", "Button is null for size: " + size);
            return;
        }
        
        button.setEnabled(true);
        button.setClickable(true);
        button.setFocusable(true);
        button.setAlpha(1.0f);
        
        // Đảm bảo text chỉ là số size, không có "(Hết)"
        button.setText(size);
        button.setBackgroundResource(R.drawable.bg_size_button);
        button.setTextColor(ContextCompat.getColor(this, android.R.color.white));
        
        Log.d("ProductDetailActivity", "Enabled size button: " + size);
    }
    
    /**
     * Disable size button và hiển thị trạng thái không có sẵn
     */
    private void disableSizeButton(Button button, String size) {
        button.setEnabled(false);
        button.setAlpha(0.5f); // Làm mờ button
        button.setText(size + " (Hết)");
        button.setBackgroundResource(R.drawable.bg_size_button);
        button.setTextColor(ContextCompat.getColor(this, android.R.color.darker_gray));
        
        // Nếu size này đang được chọn, reset selection
        if (selectedSize != null && selectedSize.equals(size)) {
            selectedSize = "";
        }
    }
    
    /**
     * Reset size button về trạng thái ban đầu
     */
    private void resetSizeButton(String size) {
        if ("37".equals(size)) {
            resetButton(btnSize37, "37");
        } else if ("38".equals(size)) {
            resetButton(btnSize38, "38");
        } else if ("39".equals(size)) {
            resetButton(btnSize39, "39");
        }
    }
    
    /**
     * Setup bottom navigation với các click listeners
     */
    private void setupBottomNavigation() {
        try {
            // Trang chủ
            View navHome = findViewById(R.id.navHome);
            if (navHome != null) {
                navHome.setOnClickListener(v -> {
                    Intent intent = new Intent(ProductDetailActivity.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                });
            }
            
            // Danh mục
            View navCategories = findViewById(R.id.navCategories);
            if (navCategories != null) {
                navCategories.setOnClickListener(v -> {
                    Intent intent = new Intent(ProductDetailActivity.this, CategoriesActivity.class);
                    startActivity(intent);
                });
            }
            
            // Giỏ hàng
            View navCart = findViewById(R.id.navCart);
            if (navCart != null) {
                navCart.setOnClickListener(v -> {
                    Intent intent = new Intent(ProductDetailActivity.this, CartActivity.class);
                    startActivity(intent);
                });
            }
            
            // Trợ giúp
            View navHelp = findViewById(R.id.navHelp);
            if (navHelp != null) {
                navHelp.setOnClickListener(v -> {
                    Intent intent = new Intent(ProductDetailActivity.this, HelpActivity.class);
                    startActivity(intent);
                });
            }
            
            // Tài khoản
            View navAccount = findViewById(R.id.navAccount);
            if (navAccount != null) {
                navAccount.setOnClickListener(v -> {
                    if (sessionManager != null && sessionManager.isLoggedIn()) {
                        Intent intent = new Intent(ProductDetailActivity.this, AccountActivity.class);
                        startActivity(intent);
                    } else {
                        Intent intent = new Intent(ProductDetailActivity.this, LoginActivity.class);
                        startActivity(intent);
                    }
                });
            }
            
            Log.d("ProductDetailActivity", "Bottom navigation setup completed");
        } catch (Exception e) {
            Log.e("ProductDetailActivity", "Error setting up bottom navigation", e);
        }
    }
}

