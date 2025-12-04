package com.poly.ban_giay_app;

import android.content.Intent;
import android.os.Bundle;
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
    private View navAccount;
    private ImageView imgAccountIcon;
    private TextView tvAccountLabel;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_cart);

        sessionManager = new SessionManager(this);
        cartManager = CartManager.getInstance();

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
        updateUI();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateUI();
        updateAccountNavUi();
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
            // TODO: Navigate to checkout
            Toast.makeText(this, "Đang chuyển đến trang thanh toán...", Toast.LENGTH_SHORT).show();
        });
    }

    private void updateUI() {
        if (cartManager.getCartItems().isEmpty()) {
            // Hiển thị giỏ hàng trống
            layoutEmptyCart.setVisibility(View.VISIBLE);
            layoutSelectAll.setVisibility(View.GONE);
            layoutBottom.setVisibility(View.GONE);
            rvCartItems.setVisibility(View.GONE);
        } else {
            // Hiển thị danh sách sản phẩm
            layoutEmptyCart.setVisibility(View.GONE);
            layoutSelectAll.setVisibility(View.VISIBLE);
            layoutBottom.setVisibility(View.VISIBLE);
            rvCartItems.setVisibility(View.VISIBLE);
            cartAdapter.notifyDataSetChanged();
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
}

