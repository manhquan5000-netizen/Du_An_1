package com.poly.ban_giay_app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.poly.ban_giay_app.adapter.CartAdapter;
import com.poly.ban_giay_app.models.CartItem;
import com.poly.ban_giay_app.network.ApiClient;
import com.poly.ban_giay_app.network.ApiService;
import com.poly.ban_giay_app.network.NetworkUtils;
import com.poly.ban_giay_app.models.Product;
import com.poly.ban_giay_app.network.model.BaseResponse;
import com.poly.ban_giay_app.network.model.CartResponse;
import com.poly.ban_giay_app.network.model.OrderResponse;
import com.poly.ban_giay_app.network.model.ProductResponse;
import com.poly.ban_giay_app.network.request.OrderRequest;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class CartActivity extends AppCompatActivity {
    private RecyclerView rvCartItems;
    private CartAdapter cartAdapter;
    private CartManager cartManager;
    private CheckBox checkBoxSelectAll;
    private TextView txtTotalPrice;
    private Button btnCheckout;
    private LinearLayout layoutSelectAll, layoutBottom, layoutEmptyCart;
    private EditText edtSearch;
    private ImageView imgBell;
    private ImageView btnBack;
    private ImageView btnViewOrders;
    private View navAccount;
    private ImageView imgAccountIcon;
    private TextView tvAccountLabel;
    private SessionManager sessionManager;
    private ApiService apiService;
    private BroadcastReceiver cartUpdateReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_cart);

        sessionManager = new SessionManager(this);
        cartManager = CartManager.getInstance();
        ApiClient.init(this);
        apiService = ApiClient.getApiService();

        // Apply insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initViews();
        initAccountNav();
        updateAccountNavUi();
        setupRecyclerView();
        setupNavigation();
        setupCartUpdateReceiver();
        // Chỉ load từ API, không dùng local cart
        loadCartFromServer();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Chỉ load từ API, không dùng local cart
        loadCartFromServer();
        updateAccountNavUi();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Unregister receiver
        if (cartUpdateReceiver != null) {
            try {
                unregisterReceiver(cartUpdateReceiver);
            } catch (Exception e) {
                Log.e("CartActivity", "Error unregistering receiver", e);
            }
        }
    }

    private void setupCartUpdateReceiver() {
        cartUpdateReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if ("com.poly.ban_giay_app.CART_UPDATED".equals(intent.getAction())) {
                    Log.d("CartActivity", "✅ Cart updated broadcast received, reloading from API...");
                    // Delay một chút để đảm bảo server đã lưu xong, sau đó load từ API
                    new android.os.Handler(android.os.Looper.getMainLooper()).postDelayed(() -> {
                        Log.d("CartActivity", "Reloading cart from API after broadcast...");
                        loadCartFromServer();
                    }, 600);
                }
            }
        };
        
        IntentFilter filter = new IntentFilter("com.poly.ban_giay_app.CART_UPDATED");
        filter.setPriority(1000); // Đặt priority cao để nhận broadcast sớm
        registerReceiver(cartUpdateReceiver, filter);
        Log.d("CartActivity", "✅ Cart update receiver registered");
    }

    private void initViews() {
        rvCartItems = findViewById(R.id.rvCartItems);
        checkBoxSelectAll = findViewById(R.id.checkBoxSelectAll);
        txtTotalPrice = findViewById(R.id.txtTotalPrice);
        btnCheckout = findViewById(R.id.btnCheckout);
        layoutSelectAll = findViewById(R.id.layoutSelectAll);
        layoutBottom = findViewById(R.id.layoutBottom);
        layoutEmptyCart = findViewById(R.id.layoutEmptyCart);
        edtSearch = findViewById(R.id.edtSearch);
        imgBell = findViewById(R.id.imgBell);
        btnBack = findViewById(R.id.btnBack);
        btnViewOrders = findViewById(R.id.btnViewOrders);
    }

    private void setupNavigation() {
        // Back button
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> {
                finish(); // Quay về màn hình trước
            });
        }

        // Home navigation
        View navHome = findViewById(R.id.navHome);
        if (navHome != null) {
            navHome.setOnClickListener(v -> {
                Intent intent = new Intent(CartActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            });
        }

        // View Orders button
        if (btnViewOrders != null) {
            btnViewOrders.setOnClickListener(v -> {
                if (sessionManager.isLoggedIn()) {
                    Intent intent = new Intent(CartActivity.this, OrderActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(this, "Vui lòng đăng nhập để xem đơn hàng", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(CartActivity.this, LoginActivity.class);
                    startActivity(intent);
                }
            });
        }
    }

    private void initAccountNav() {
        navAccount = findViewById(R.id.navAccount);
        imgAccountIcon = findViewById(R.id.imgAccountIcon);
        tvAccountLabel = findViewById(R.id.tvAccountLabel);

        if (navAccount != null) {
            navAccount.setOnClickListener(v -> {
                if (sessionManager.isLoggedIn()) {
                    Intent intent = new Intent(CartActivity.this, AccountActivity.class);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(CartActivity.this, LoginActivity.class);
                    startActivity(intent);
                }
            });
        }
    }

    private void updateAccountNavUi() {
        if (tvAccountLabel != null) {
            if (sessionManager.isLoggedIn()) {
                tvAccountLabel.setText(sessionManager.getUserName());
            } else {
                tvAccountLabel.setText(R.string.account);
            }
        }

        if (imgAccountIcon != null) {
            imgAccountIcon.setImageResource(R.drawable.ic_user);
            int color = ContextCompat.getColor(this, sessionManager.isLoggedIn()
                    ? android.R.color.holo_green_dark
                    : android.R.color.black);
            imgAccountIcon.setColorFilter(color);
        }
    }

    private void setupRecyclerView() {
        rvCartItems.setLayoutManager(new LinearLayoutManager(this));
        cartAdapter = new CartAdapter(cartManager.getCartItems(), new CartAdapter.OnCartItemListener() {
            @Override
            public void onItemSelectedChanged(int position, boolean isSelected) {
                cartManager.setItemSelected(position, isSelected);
                updateTotalPrice();
                updateSelectAllCheckbox();
            }

            @Override
            public void onItemRemoved(int position) {
                cartManager.removeFromCart(position);
                cartAdapter.notifyDataSetChanged();
                updateUI();
            }
        });
        rvCartItems.setAdapter(cartAdapter);

        // Select all checkbox
        checkBoxSelectAll.setOnCheckedChangeListener((buttonView, isChecked) -> {
            cartManager.selectAll(isChecked);
            cartAdapter.notifyDataSetChanged();
            updateTotalPrice();
        });

        // Checkout button
        btnCheckout.setOnClickListener(v -> {
            if (cartManager.getSelectedCount() == 0) {
                Toast.makeText(this, "Vui lòng chọn ít nhất một sản phẩm", Toast.LENGTH_SHORT).show();
                return;
            }
            
            if (!sessionManager.isLoggedIn()) {
                Toast.makeText(this, "Vui lòng đăng nhập để thanh toán", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(CartActivity.this, LoginActivity.class);
                startActivity(intent);
                return;
            }
            
            createOrder();
        });
    }

    private void updateUI() {
        int itemCount = cartManager.getCartItems().size();
        Log.d("CartActivity", "=== updateUI() ===");
        Log.d("CartActivity", "Cart items count: " + itemCount);
        Log.d("CartActivity", "CartAdapter is null: " + (cartAdapter == null));
        if (cartAdapter != null) {
            Log.d("CartActivity", "CartAdapter item count: " + cartAdapter.getItemCount());
        }
        
        if (itemCount == 0) {
            // Hiển thị giỏ hàng trống
            Log.d("CartActivity", "Cart is empty, showing empty state");
            layoutEmptyCart.setVisibility(View.VISIBLE);
            layoutSelectAll.setVisibility(View.GONE);
            layoutBottom.setVisibility(View.GONE);
            rvCartItems.setVisibility(View.GONE);
        } else {
            // Hiển thị danh sách sản phẩm
            Log.d("CartActivity", "Cart has " + itemCount + " items, showing list");
            layoutEmptyCart.setVisibility(View.GONE);
            layoutSelectAll.setVisibility(View.VISIBLE);
            layoutBottom.setVisibility(View.VISIBLE);
            rvCartItems.setVisibility(View.VISIBLE);
            
            // Đảm bảo adapter được update với dữ liệu mới nhất từ cartManager
            if (cartAdapter != null) {
                Log.d("CartActivity", "Adapter item count before update: " + cartAdapter.getItemCount());
                // Luôn update adapter với danh sách mới nhất từ cartManager
                List<CartItem> currentItems = new ArrayList<>(cartManager.getCartItems());
                cartAdapter.updateCartItems(currentItems);
                Log.d("CartActivity", "✅ Adapter updated. Final item count: " + cartAdapter.getItemCount());
            } else {
                Log.e("CartActivity", "❌ CartAdapter is null! Cannot update UI!");
            }
            
            updateTotalPrice();
            updateSelectAllCheckbox();
        }
    }

    private void updateTotalPrice() {
        long total = cartManager.getTotalPrice();
        txtTotalPrice.setText(formatPrice(total));
    }

    private void updateSelectAllCheckbox() {
        checkBoxSelectAll.setChecked(cartManager.areAllSelected());
    }

    private String formatPrice(long price) {
        // Format giống như MainActivity: "199.000₫"
        return String.format("%,d₫", price).replace(",", ".");
    }

    private void loadCartFromServer() {
        Log.d("CartActivity", "=== loadCartFromServer() ===");
        
        if (!sessionManager.isLoggedIn()) {
            // Nếu chưa đăng nhập, hiển thị giỏ hàng trống
            Log.d("CartActivity", "Not logged in, showing empty cart");
            cartManager.getCartItems().clear();
            if (cartAdapter != null) {
                cartAdapter.updateCartItems(new ArrayList<>());
            }
            updateUI();
            return;
        }

        if (!NetworkUtils.isConnected(this)) {
            // Nếu không có mạng, hiển thị thông báo và giỏ hàng trống
            Log.d("CartActivity", "No network, showing empty cart");
            Toast.makeText(this, "Không có kết nối mạng. Vui lòng kiểm tra lại.", Toast.LENGTH_SHORT).show();
            cartManager.getCartItems().clear();
            if (cartAdapter != null) {
                cartAdapter.updateCartItems(new ArrayList<>());
            }
            updateUI();
            return;
        }

        String userId = sessionManager.getUserId();
        if (userId == null || userId.isEmpty()) {
            Log.w("CartActivity", "User ID is null or empty");
            updateUI();
            return;
        }
        
        Log.d("CartActivity", "Fetching cart for user: " + userId);

        apiService.getCart(userId).enqueue(new Callback<BaseResponse<CartResponse>>() {
            @Override
            public void onResponse(Call<BaseResponse<CartResponse>> call, Response<BaseResponse<CartResponse>> response) {
                runOnUiThread(() -> {
                    if (response.isSuccessful() && response.body() != null && response.body().getSuccess()) {
                        CartResponse cartResponse = response.body().getData();
                        if (cartResponse != null && cartResponse.getItems() != null && !cartResponse.getItems().isEmpty()) {
                            // Tạo danh sách mới thay vì clear và add vào list cũ
                            List<CartItem> newCartItems = new ArrayList<>();
                            
                            // Convert CartItemResponse to CartItem
                            int addedCount = 0;
                            int skippedCount = 0;
                            Log.d("CartActivity", "Processing " + cartResponse.getItems().size() + " items from server");
                            
                            for (CartResponse.CartItemResponse itemResponse : cartResponse.getItems()) {
                                Log.d("CartActivity", "Processing item - Size: " + itemResponse.getKichThuoc() + ", Quantity: " + itemResponse.getSoLuong());
                                
                                ProductResponse productResponse = itemResponse.getProduct();
                                if (productResponse != null) {
                                    Log.d("CartActivity", "✅ ProductResponse is not null: " + productResponse.getName());
                                    // Convert ProductResponse to Product
                                    Product product = convertToProduct(productResponse);
                                    
                                    // Create CartItem
                                    CartItem cartItem = new CartItem(
                                        product,
                                        itemResponse.getKichThuoc() != null ? itemResponse.getKichThuoc() : "",
                                        itemResponse.getSoLuong() != null ? itemResponse.getSoLuong() : 1
                                    );
                                    
                                    newCartItems.add(cartItem);
                                    addedCount++;
                                    Log.d("CartActivity", "✅ Added item: " + product.name + " x" + cartItem.quantity);
                                } else {
                                    skippedCount++;
                                    Log.e("CartActivity", "❌ ProductResponse is NULL for item - Size: " + itemResponse.getKichThuoc() + ", Quantity: " + itemResponse.getSoLuong());
                                    Log.e("CartActivity", "   sanPhamIdRaw: " + itemResponse.getSanPhamId());
                                }
                            }
                            
                            Log.d("CartActivity", "Processed items - Added: " + addedCount + ", Skipped: " + skippedCount);
                            
                            // Cập nhật cart manager với danh sách mới
                            cartManager.getCartItems().clear();
                            cartManager.getCartItems().addAll(newCartItems);
                            
                            Log.d("CartActivity", "✅ Loaded " + addedCount + " items from server. Total in cart: " + cartManager.getCartItems().size());
                            
                            // Log từng item để debug
                            for (int i = 0; i < newCartItems.size(); i++) {
                                CartItem item = newCartItems.get(i);
                                Log.d("CartActivity", "  Item " + i + ": " + item.product.name + " x" + item.quantity + " (Size: " + item.size + ")");
                            }
                            
                            // Update adapter với danh sách mới - ĐẢM BẢO SỬ DỤNG CÙNG REFERENCE
                            if (cartAdapter != null) {
                                // Đảm bảo adapter sử dụng cùng list với cartManager
                                cartAdapter.updateCartItems(cartManager.getCartItems());
                                Log.d("CartActivity", "✅ Adapter updated with " + cartManager.getCartItems().size() + " items");
                                Log.d("CartActivity", "✅ Adapter getItemCount: " + cartAdapter.getItemCount());
                                
                                // Force refresh RecyclerView ngay lập tức
                                rvCartItems.post(() -> {
                                    cartAdapter.notifyDataSetChanged();
                                    Log.d("CartActivity", "✅ RecyclerView forced refresh on UI thread");
                                });
                            } else {
                                Log.e("CartActivity", "❌ CartAdapter is null, cannot update!");
                            }
                            
                            // Update UI - Đảm bảo RecyclerView được refresh
                            updateUI();
                        } else {
                            // Nếu cart rỗng từ server, clear local cart
                            Log.d("CartActivity", "Cart is empty on server");
                            cartManager.getCartItems().clear();
                            if (cartAdapter != null) {
                                cartAdapter.updateCartItems(new ArrayList<>());
                            }
                            updateUI();
                        }
                    } else {
                        // Nếu response không thành công, hiển thị giỏ hàng trống
                        Log.w("CartActivity", "Failed to load cart from server. Code: " + response.code());
                        String errorMsg = NetworkUtils.extractErrorMessage(response);
                        Log.w("CartActivity", "Error: " + errorMsg);
                        Toast.makeText(CartActivity.this, "Không thể tải giỏ hàng: " + errorMsg, Toast.LENGTH_SHORT).show();
                        // Clear cart và hiển thị empty state
                        cartManager.getCartItems().clear();
                        if (cartAdapter != null) {
                            cartAdapter.updateCartItems(new ArrayList<>());
                        }
                        updateUI();
                    }
                });
            }

            @Override
            public void onFailure(Call<BaseResponse<CartResponse>> call, Throwable t) {
                runOnUiThread(() -> {
                    // Nếu lỗi, hiển thị thông báo và giỏ hàng trống
                    Log.e("CartActivity", "Network error loading cart: " + t.getMessage(), t);
                    Toast.makeText(CartActivity.this, "Lỗi kết nối khi tải giỏ hàng: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    cartManager.getCartItems().clear();
                    if (cartAdapter != null) {
                        cartAdapter.updateCartItems(new ArrayList<>());
                    }
                    updateUI();
                });
            }
        });
    }

    private Product convertToProduct(ProductResponse productResponse) {
        Log.d("CartActivity", "=== convertToProduct() ===");
        Log.d("CartActivity", "ProductResponse ID: " + productResponse.getId());
        Log.d("CartActivity", "ProductResponse Name: " + productResponse.getName());
        
        Product product = new Product();
        product.id = productResponse.getId();
        product.name = productResponse.getName();
        product.brand = productResponse.getBrand();
        
        // Format price - handle null values
        Integer giaGoc = productResponse.getGiaGoc();
        Integer giaKhuyenMai = productResponse.getGiaKhuyenMai();
        product.priceOld = giaGoc != null ? formatPrice((long) giaGoc) : "";
        product.priceNew = giaKhuyenMai != null ? formatPrice((long) giaKhuyenMai) : "";
        
        Log.d("CartActivity", "Product priceOld: " + product.priceOld);
        Log.d("CartActivity", "Product priceNew: " + product.priceNew);
        
        // Get rating - handle null
        Double danhGia = productResponse.getDanhGia();
        product.rating = danhGia != null ? danhGia : 0.0;
        
        product.imageUrl = productResponse.getImageUrl();
        product.description = productResponse.getDescription();
        product.category = productResponse.getCategory();
        
        Log.d("CartActivity", "Product imageUrl: " + product.imageUrl);
        
        // Map image name to drawable resource if needed
        if (product.imageUrl != null && !product.imageUrl.isEmpty() && !product.imageUrl.startsWith("http")) {
            String imageName = product.imageUrl.replace(".img", "").replace(".jpg", "").replace(".png", "");
            int imageRes = getResources().getIdentifier(imageName, "drawable", getPackageName());
            if (imageRes != 0) {
                product.imageRes = imageRes;
                Log.d("CartActivity", "Mapped image to drawable: " + imageName + " -> " + imageRes);
            } else {
                Log.w("CartActivity", "Could not find drawable for: " + imageName);
            }
        }
        
        Log.d("CartActivity", "✅ Converted product: " + product.name + " (ID: " + product.id + ")");
        return product;
    }

    private void createOrder() {
        if (!NetworkUtils.isConnected(this)) {
            Toast.makeText(this, "Không có kết nối mạng", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = sessionManager.getUserId();
        if (userId == null || userId.isEmpty()) {
            Toast.makeText(this, "Không tìm thấy thông tin người dùng", Toast.LENGTH_SHORT).show();
            return;
        }

        List<CartItem> selectedItems = cartManager.getSelectedItems();
        if (selectedItems.isEmpty()) {
            Toast.makeText(this, "Vui lòng chọn ít nhất một sản phẩm", Toast.LENGTH_SHORT).show();
            return;
        }

        // Tạo OrderRequest
        OrderRequest request = new OrderRequest();
        request.setUserId(userId);
        
        List<OrderRequest.OrderItemRequest> orderItems = new ArrayList<>();
        long totalPrice = 0;
        
        for (CartItem cartItem : selectedItems) {
            if (cartItem.product.id == null || cartItem.product.id.isEmpty()) {
                Toast.makeText(this, "Sản phẩm " + cartItem.product.name + " không có ID, không thể tạo đơn hàng", Toast.LENGTH_LONG).show();
                return;
            }
            
            long itemPrice = Long.parseLong(cartItem.product.priceNew.replaceAll("[^0-9]", ""));
            long itemTotal = itemPrice * cartItem.quantity;
            totalPrice += itemTotal;
            
            OrderRequest.OrderItemRequest orderItem = new OrderRequest.OrderItemRequest(
                cartItem.product.id,
                cartItem.product.name,
                cartItem.quantity,
                cartItem.size,
                itemPrice
            );
            orderItems.add(orderItem);
        }
        
        request.setItems(orderItems);
        request.setTongTien(totalPrice);
        request.setDiaChiGiaoHang(""); // TODO: Lấy từ form nhập địa chỉ
        request.setSoDienThoai(""); // TODO: Lấy từ form nhập số điện thoại
        request.setGhiChu("");

        btnCheckout.setEnabled(false);
        btnCheckout.setText("Đang xử lý...");

        apiService.createOrder(request).enqueue(new Callback<BaseResponse<OrderResponse>>() {
            @Override
            public void onResponse(Call<BaseResponse<OrderResponse>> call, Response<BaseResponse<OrderResponse>> response) {
                btnCheckout.setEnabled(true);
                btnCheckout.setText("Thanh toán");
                
                if (response.isSuccessful() && response.body() != null) {
                    BaseResponse<OrderResponse> body = response.body();
                    if (body.getSuccess()) {
                        Toast.makeText(CartActivity.this, "Đặt hàng thành công!", Toast.LENGTH_SHORT).show();
                        // Xóa các sản phẩm đã chọn khỏi giỏ hàng
                        cartManager.removeSelectedItems();
                        cartAdapter.notifyDataSetChanged();
                        updateUI();
                        // Chuyển đến màn hình đơn hàng
                        Intent intent = new Intent(CartActivity.this, OrderActivity.class);
                        startActivity(intent);
                    } else {
                        Toast.makeText(CartActivity.this, body.getMessage() != null ? body.getMessage() : "Không thể tạo đơn hàng", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(CartActivity.this, NetworkUtils.extractErrorMessage(response), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<BaseResponse<OrderResponse>> call, Throwable t) {
                btnCheckout.setEnabled(true);
                btnCheckout.setText("Thanh toán");
                Toast.makeText(CartActivity.this, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}

