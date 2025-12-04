package com.poly.ban_giay_app.adapter;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.poly.ban_giay_app.ProductDetailActivity;
import com.poly.ban_giay_app.R;
import com.poly.ban_giay_app.models.CartItem;

import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {
    private List<CartItem> cartItems;
    private OnCartItemListener listener;

    public interface OnCartItemListener {
        void onItemSelectedChanged(int position, boolean isSelected);
        void onItemRemoved(int position);
    }

    public CartAdapter(List<CartItem> cartItems, OnCartItemListener listener) {
        this.cartItems = cartItems;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_cart, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        CartItem item = cartItems.get(position);
        holder.bind(item, position);
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    class CartViewHolder extends RecyclerView.ViewHolder {
        private CheckBox checkBoxItem;
        private ImageView imgProduct, btnRemove;
        private TextView txtProductName, txtSize, txtQuantity, txtPrice;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            checkBoxItem = itemView.findViewById(R.id.checkBoxItem);
            imgProduct = itemView.findViewById(R.id.imgProduct);
            btnRemove = itemView.findViewById(R.id.btnRemove);
            txtProductName = itemView.findViewById(R.id.txtProductName);
            txtSize = itemView.findViewById(R.id.txtSize);
            txtQuantity = itemView.findViewById(R.id.txtQuantity);
            txtPrice = itemView.findViewById(R.id.txtPrice);
        }

        public void bind(CartItem item, int position) {
            // Set checkbox state
            checkBoxItem.setChecked(item.isSelected);
            checkBoxItem.setOnCheckedChangeListener((buttonView, isChecked) -> {
                item.isSelected = isChecked;
                if (listener != null) {
                    listener.onItemSelectedChanged(position, isChecked);
                }
            });

            // Load image - đảm bảo luôn có ảnh hiển thị
            imgProduct.setVisibility(View.VISIBLE);
            imgProduct.setImageResource(0); // Clear previous image
            
            // Debug log
            Log.d("CartAdapter", "=== Loading image for: " + item.product.name + " ===");
            Log.d("CartAdapter", "imageUrl: " + (item.product.imageUrl != null ? item.product.imageUrl : "null"));
            Log.d("CartAdapter", "imageRes: " + item.product.imageRes);
            
            boolean imageLoaded = false;
            
            // Ưu tiên 1: Load từ imageUrl (URL từ server)
            if (item.product.imageUrl != null && !item.product.imageUrl.trim().isEmpty()) {
                String url = item.product.imageUrl.trim();
                if (url.startsWith("http://") || url.startsWith("https://")) {
                    Log.d("CartAdapter", "✅ Loading from URL: " + url);
                    try {
                        Glide.with(itemView.getContext())
                                .load(url)
                                .placeholder(R.drawable.giaymau)
                                .error(R.drawable.giaymau)
                                .centerCrop()
                                .into(imgProduct);
                        imageLoaded = true;
                    } catch (Exception e) {
                        Log.e("CartAdapter", "❌ Error loading from URL: " + e.getMessage());
                    }
                } else {
                    // Ưu tiên 2: Load từ drawable bằng tên file
                    Log.d("CartAdapter", "Loading from drawable name: " + url);
                    int imageResId = getImageResourceId(url);
                    Log.d("CartAdapter", "Image resource ID: " + imageResId);
                    if (imageResId != 0) {
                        try {
                            imgProduct.setImageResource(imageResId);
                            imageLoaded = true;
                            Log.d("CartAdapter", "✅ Loaded from drawable: " + url);
                        } catch (Exception e) {
                            Log.e("CartAdapter", "❌ Error loading from drawable: " + e.getMessage());
                        }
                    }
                }
            }
            
            // Ưu tiên 3: Load từ imageRes
            if (!imageLoaded && item.product.imageRes != 0) {
                Log.d("CartAdapter", "Loading from imageRes: " + item.product.imageRes);
                try {
                    imgProduct.setImageResource(item.product.imageRes);
                    imageLoaded = true;
                    Log.d("CartAdapter", "✅ Loaded from imageRes");
                } catch (Exception e) {
                    Log.e("CartAdapter", "❌ Error loading from imageRes: " + e.getMessage());
                }
            }
            
            // Fallback: Luôn có ảnh mặc định
            if (!imageLoaded) {
                Log.w("CartAdapter", "⚠️ Using default image (no imageUrl or imageRes)");
                imgProduct.setImageResource(R.drawable.giaymau);
            }
            
            Log.d("CartAdapter", "=========================================");

            // Set product info
            txtProductName.setText(item.product.name);
            txtSize.setText("Size: " + item.size);
            txtQuantity.setText("Số lượng: " + item.quantity);
            txtPrice.setText(item.product.priceNew);

            // Remove button
            btnRemove.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onItemRemoved(position);
                }
            });

            // Click vào ảnh sản phẩm để mở trang chi tiết
            imgProduct.setOnClickListener(v -> {
                Intent intent = new Intent(itemView.getContext(), ProductDetailActivity.class);
                intent.putExtra("product", item.product);
                itemView.getContext().startActivity(intent);
            });
            imgProduct.setClickable(true);
            imgProduct.setFocusable(true);

            // Click vào tên sản phẩm để mở trang chi tiết
            txtProductName.setOnClickListener(v -> {
                Intent intent = new Intent(itemView.getContext(), ProductDetailActivity.class);
                intent.putExtra("product", item.product);
                itemView.getContext().startActivity(intent);
            });
            txtProductName.setClickable(true);
            txtProductName.setFocusable(true);
            
            // Click vào phần thông tin sản phẩm (LinearLayout chứa tên, size, quantity, price)
            View productInfoLayout = itemView.findViewById(R.id.productInfoLayout);
            if (productInfoLayout != null) {
                productInfoLayout.setOnClickListener(v -> {
                    Intent intent = new Intent(itemView.getContext(), ProductDetailActivity.class);
                    intent.putExtra("product", item.product);
                    itemView.getContext().startActivity(intent);
                });
            }
        }

        private int getImageResourceId(String imageName) {
            String name = imageName;
            if (name.contains("/")) {
                name = name.substring(name.lastIndexOf("/") + 1);
            }
            if (name.contains(".")) {
                name = name.substring(0, name.lastIndexOf("."));
            }
            return itemView.getContext().getResources().getIdentifier(name, "drawable", itemView.getContext().getPackageName());
        }
    }
}

