package com.poly.ban_giay_app;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
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

import com.poly.ban_giay_app.adapter.ProductAdapter;
import com.poly.ban_giay_app.models.Product;
import com.poly.ban_giay_app.network.ApiClient;
import com.poly.ban_giay_app.network.ApiService;
import com.poly.ban_giay_app.network.NetworkUtils;
import com.poly.ban_giay_app.network.model.ProductResponse;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CategoriesActivity extends AppCompatActivity {
    private SessionManager sessionManager;
    private View navAccount;
    private ImageView imgAccountIcon;
    private TextView tvAccountLabel;

    // RecyclerViews and Adapters
    private RecyclerView rvTopSelling, rvHotTrend, rvMen, rvWomen;
    private ProductAdapter topSellingAdapter, hotTrendAdapter, menAdapter, womenAdapter;
    private List<Product> topSellingList = new ArrayList<>();
    private List<Product> hotTrendList = new ArrayList<>();
    private List<Product> menList = new ArrayList<>();
    private List<Product> womenList = new ArrayList<>();
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_categories);

        sessionManager = new SessionManager(this);
        apiService = ApiClient.getApiService();

        // Init account navigation
        initAccountNav();
        updateAccountNavUi();

        // Apply insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Init product lists
        initProductLists();

        // Setup bottom navigation
        setupBottomNavigation();

        // Setup back button
        ImageView btnBack = findViewById(R.id.btnBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        // Load products from API
        loadProductsFromApi();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateAccountNavUi();
    }

    private void initAccountNav() {
        navAccount = findViewById(R.id.navAccount);
        imgAccountIcon = findViewById(R.id.imgAccountIcon);
        tvAccountLabel = findViewById(R.id.tvAccountLabel);

        if (navAccount != null) {
            navAccount.setOnClickListener(v -> {
                if (sessionManager.isLoggedIn()) {
                    Intent intent = new Intent(CategoriesActivity.this, AccountActivity.class);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(CategoriesActivity.this, LoginActivity.class);
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

    private void initProductLists() {
        try {
            // Top selling products
            rvTopSelling = findViewById(R.id.rvTopSelling);
            if (rvTopSelling != null) {
                rvTopSelling.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
                topSellingAdapter = new ProductAdapter(topSellingList);
                rvTopSelling.setAdapter(topSellingAdapter);
            }

            // Hot trend products
            rvHotTrend = findViewById(R.id.rvHotTrend);
            if (rvHotTrend != null) {
                rvHotTrend.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
                hotTrendAdapter = new ProductAdapter(hotTrendList);
                rvHotTrend.setAdapter(hotTrendAdapter);
            }

            // Men's shoes
            rvMen = findViewById(R.id.rvMen);
            if (rvMen != null) {
                rvMen.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
                menAdapter = new ProductAdapter(menList);
                rvMen.setAdapter(menAdapter);
            }

            // Women's shoes
            rvWomen = findViewById(R.id.rvWomen);
            if (rvWomen != null) {
                rvWomen.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
                womenAdapter = new ProductAdapter(womenList);
                rvWomen.setAdapter(womenAdapter);
            }
        } catch (Exception e) {
            Log.e("CategoriesActivity", "Error initializing product lists", e);
            Toast.makeText(this, "Lỗi khởi tạo danh sách sản phẩm", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadProductsFromApi() {
        if (!NetworkUtils.isConnected(this)) {
            Toast.makeText(this, "Không có kết nối mạng", Toast.LENGTH_SHORT).show();
            return;
        }

        // Load top selling products
        apiService.getBestSellingProducts(10).enqueue(new Callback<List<ProductResponse>>() {
            @Override
            public void onResponse(Call<List<ProductResponse>> call, Response<List<ProductResponse>> response) {
                try {
                    if (response.isSuccessful() && response.body() != null && topSellingList != null && topSellingAdapter != null) {
                        topSellingList.clear();
                        for (ProductResponse productResponse : response.body()) {
                            Product product = convertToProduct(productResponse);
                            if (product != null && product.name != null && !product.name.isEmpty()) {
                                topSellingList.add(product);
                            }
                        }
                        runOnUiThread(() -> {
                            if (topSellingAdapter != null) {
                                topSellingAdapter.notifyDataSetChanged();
                            }
                        });
                    }
                } catch (Exception e) {
                    Log.e("CategoriesActivity", "Error loading top selling products", e);
                }
            }

            @Override
            public void onFailure(Call<List<ProductResponse>> call, Throwable t) {
                Log.e("CategoriesActivity", "Failed to load top selling products", t);
            }
        });

        // Load hot trend products (newest products)
        apiService.getNewestProducts(10).enqueue(new Callback<List<ProductResponse>>() {
            @Override
            public void onResponse(Call<List<ProductResponse>> call, Response<List<ProductResponse>> response) {
                try {
                    if (response.isSuccessful() && response.body() != null && hotTrendList != null && hotTrendAdapter != null) {
                        hotTrendList.clear();
                        for (ProductResponse productResponse : response.body()) {
                            Product product = convertToProduct(productResponse);
                            if (product != null && product.name != null && !product.name.isEmpty()) {
                                hotTrendList.add(product);
                            }
                        }
                        runOnUiThread(() -> {
                            if (hotTrendAdapter != null) {
                                hotTrendAdapter.notifyDataSetChanged();
                            }
                        });
                    }
                } catch (Exception e) {
                    Log.e("CategoriesActivity", "Error loading hot trend products", e);
                }
            }

            @Override
            public void onFailure(Call<List<ProductResponse>> call, Throwable t) {
                Log.e("CategoriesActivity", "Failed to load hot trend products", t);
            }
        });

        // Load men's products
        apiService.getProductsByCategory("nam").enqueue(new Callback<List<ProductResponse>>() {
            @Override
            public void onResponse(Call<List<ProductResponse>> call, Response<List<ProductResponse>> response) {
                try {
                    if (response.isSuccessful() && response.body() != null && menList != null && menAdapter != null) {
                        menList.clear();
                        for (ProductResponse productResponse : response.body()) {
                            Product product = convertToProduct(productResponse);
                            if (product != null && product.name != null && !product.name.isEmpty()) {
                                menList.add(product);
                            }
                        }
                        runOnUiThread(() -> {
                            if (menAdapter != null) {
                                menAdapter.notifyDataSetChanged();
                            }
                        });
                    }
                } catch (Exception e) {
                    Log.e("CategoriesActivity", "Error loading men products", e);
                }
            }

            @Override
            public void onFailure(Call<List<ProductResponse>> call, Throwable t) {
                Log.e("CategoriesActivity", "Failed to load men products", t);
            }
        });

        // Load women's products
        apiService.getProductsByCategory("nu").enqueue(new Callback<List<ProductResponse>>() {
            @Override
            public void onResponse(Call<List<ProductResponse>> call, Response<List<ProductResponse>> response) {
                try {
                    if (response.isSuccessful() && response.body() != null && womenList != null && womenAdapter != null) {
                        womenList.clear();
                        for (ProductResponse productResponse : response.body()) {
                            Product product = convertToProduct(productResponse);
                            if (product != null && product.name != null && !product.name.isEmpty()) {
                                womenList.add(product);
                            }
                        }
                        runOnUiThread(() -> {
                            if (womenAdapter != null) {
                                womenAdapter.notifyDataSetChanged();
                            }
                        });
                    }
                } catch (Exception e) {
                    Log.e("CategoriesActivity", "Error loading women products", e);
                }
            }

            @Override
            public void onFailure(Call<List<ProductResponse>> call, Throwable t) {
                Log.e("CategoriesActivity", "Failed to load women products", t);
            }
        });
    }

    private Product convertToProduct(ProductResponse productResponse) {
        if (productResponse == null) {
            return null;
        }

        String name = productResponse.getName();
        String imageUrl = productResponse.getImageUrl();

        if (name == null || name.trim().isEmpty()) {
            return null;
        }

        Integer giaGoc = productResponse.getGiaGoc();
        Integer giaKhuyenMai = productResponse.getGiaKhuyenMai();

        String priceOld = null;
        String priceNew = null;

        if (giaGoc != null && giaGoc > 0) {
            priceOld = formatPrice(giaGoc);
        }

        if (giaKhuyenMai != null && giaKhuyenMai > 0) {
            priceNew = formatPrice(giaKhuyenMai);
        } else if (giaGoc != null && giaGoc > 0) {
            priceNew = formatPrice(giaGoc);
            priceOld = null;
        }

        if (priceNew == null || priceNew.trim().isEmpty()) {
            priceNew = "0₫";
        }

        if (imageUrl != null && !imageUrl.trim().isEmpty()) {
            Product product = new Product(
                name,
                priceOld != null ? priceOld : "",
                priceNew,
                imageUrl
            );
            product.imageUrl = imageUrl;
            return product;
        } else {
            Product product = new Product(
                name,
                priceOld != null ? priceOld : "",
                priceNew,
                R.drawable.giaymau
            );
            product.imageUrl = null;
            return product;
        }
    }

    private String formatPrice(int price) {
        return String.format("%,d₫", price).replace(",", ".");
    }

    private void setupBottomNavigation() {
        try {
            // Trang chủ
            View navHome = findViewById(R.id.navHome);
            if (navHome != null) {
                navHome.setOnClickListener(v -> {
                    Intent intent = new Intent(CategoriesActivity.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                });
            }

            // Danh mục - already on this screen, highlight it
            View navCategories = findViewById(R.id.navCategories);
            if (navCategories != null) {
                // Highlight current screen
                ImageView imgCategories = navCategories.findViewById(R.id.imgCategoriesIcon);
                TextView tvCategories = navCategories.findViewById(R.id.tvCategoriesLabel);
                if (imgCategories != null) {
                    imgCategories.setColorFilter(ContextCompat.getColor(this, R.color.teal_700));
                }
                if (tvCategories != null) {
                    tvCategories.setTextColor(ContextCompat.getColor(this, R.color.teal_700));
                }
            }

            // Giỏ hàng
            View navCart = findViewById(R.id.navCart);
            if (navCart != null) {
                navCart.setOnClickListener(v -> {
                    // TODO: Navigate to cart screen when available
                    Toast.makeText(this, "Tính năng giỏ hàng đang phát triển", Toast.LENGTH_SHORT).show();
                });
            }

            // Trợ giúp
            View navHelp = findViewById(R.id.navHelp);
            if (navHelp != null) {
                navHelp.setOnClickListener(v -> {
                    // TODO: Navigate to help screen when available
                    Toast.makeText(this, "Tính năng trợ giúp đang phát triển", Toast.LENGTH_SHORT).show();
                });
            }
        } catch (Exception e) {
            Log.e("CategoriesActivity", "Error setting up bottom navigation", e);
        }
    }
}

