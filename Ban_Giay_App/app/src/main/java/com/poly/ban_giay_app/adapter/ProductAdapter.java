package com.poly.ban_giay_app.adapter;

import android.content.Intent;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.StrikethroughSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;

import com.bumptech.glide.Glide;
import com.poly.ban_giay_app.ProductDetailActivity;
import com.poly.ban_giay_app.R;
import com.poly.ban_giay_app.models.Product;

import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.VH> {
    private final List<Product> items;

    public ProductAdapter(List<Product> items) {
        this.items = items;
    }

    public static class VH extends RecyclerView.ViewHolder {
        ImageView img;
        TextView name, priceOld, priceNew;

        public VH(@NonNull View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.imgProduct);
            name = itemView.findViewById(R.id.txtName);
            priceOld = itemView.findViewById(R.id.txtPriceOld);
            priceNew = itemView.findViewById(R.id.txtPriceNew);
        }
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_product, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        Product p = items.get(position);
        
        // Load image - đảm bảo luôn có ảnh hiển thị
        holder.img.setVisibility(View.VISIBLE);
        holder.img.setImageResource(0); // Clear previous image
        
        // Debug log
        Log.d("ProductAdapter", "=== Loading image for: " + p.name + " ===");
        Log.d("ProductAdapter", "imageUrl: " + (p.imageUrl != null ? p.imageUrl : "null"));
        Log.d("ProductAdapter", "imageRes: " + p.imageRes);
        
        boolean imageLoaded = false;
        
        // Ưu tiên 1: Load từ imageUrl (URL từ server)
        if (p.imageUrl != null && !p.imageUrl.trim().isEmpty()) {
            String url = p.imageUrl.trim();
            if (url.startsWith("http://") || url.startsWith("https://")) {
                Log.d("ProductAdapter", "✅ Loading from URL: " + url);
                try {
                    Glide.with(holder.itemView.getContext())
                            .load(url)
                            .placeholder(R.drawable.giaymau)
                            .error(R.drawable.giaymau)
                            .centerCrop()
                            .into(holder.img);
                    imageLoaded = true;
                } catch (Exception e) {
                    Log.e("ProductAdapter", "❌ Error loading from URL: " + e.getMessage());
                }
            } else {
                // Ưu tiên 2: Load từ drawable bằng tên file
                Log.d("ProductAdapter", "Loading from drawable name: " + url);
                int imageResId = getImageResourceId(holder.itemView.getContext(), url);
                Log.d("ProductAdapter", "Image resource ID: " + imageResId);
                if (imageResId != 0) {
                    try {
                        holder.img.setImageResource(imageResId);
                        imageLoaded = true;
                        Log.d("ProductAdapter", "✅ Loaded from drawable: " + url);
                    } catch (Exception e) {
                        Log.e("ProductAdapter", "❌ Error loading from drawable: " + e.getMessage());
                    }
                }
            }
        }
        
        // Ưu tiên 3: Load từ imageRes
        if (!imageLoaded && p.imageRes != 0) {
            Log.d("ProductAdapter", "Loading from imageRes: " + p.imageRes);
            try {
                holder.img.setImageResource(p.imageRes);
                imageLoaded = true;
                Log.d("ProductAdapter", "✅ Loaded from imageRes");
            } catch (Exception e) {
                Log.e("ProductAdapter", "❌ Error loading from imageRes: " + e.getMessage());
            }
        }
        
        // Fallback: Luôn có ảnh mặc định
        if (!imageLoaded) {
            Log.w("ProductAdapter", "⚠️ Using default image (no imageUrl or imageRes)");
            holder.img.setImageResource(R.drawable.giaymau);
        }
        
        Log.d("ProductAdapter", "=========================================");
        
        holder.name.setText(p.name);

        if (p.priceOld != null && !p.priceOld.isEmpty()) {
            SpannableString ss = new SpannableString(p.priceOld);
            ss.setSpan(new StrikethroughSpan(), 0, p.priceOld.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            holder.priceOld.setVisibility(View.VISIBLE);
            holder.priceOld.setText(ss);
        } else {
            holder.priceOld.setVisibility(View.GONE);
        }

        if (p.priceNew != null && !p.priceNew.isEmpty()) {
            holder.priceNew.setVisibility(View.VISIBLE);
            holder.priceNew.setText(holder.itemView.getContext().getString(R.string.price_label, p.priceNew));
        } else {
            holder.priceNew.setVisibility(View.GONE);
        }
        
        // Click listener for product image
        holder.img.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), ProductDetailActivity.class);
            intent.putExtra("product", p);
            v.getContext().startActivity(intent);
        });
        
        // Click listener for entire item
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), ProductDetailActivity.class);
            intent.putExtra("product", p);
            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
    
    /**
     * Lấy resource ID từ tên file ảnh (giay15, giay14, etc.)
     */
    private int getImageResourceId(Context context, String imageName) {
        // Loại bỏ extension và path nếu có
        String name = imageName;
        if (name.contains("/")) {
            name = name.substring(name.lastIndexOf("/") + 1);
        }
        if (name.contains(".")) {
            name = name.substring(0, name.lastIndexOf("."));
        }
        
        // Map tên file với resource ID
        return context.getResources().getIdentifier(name, "drawable", context.getPackageName());
    }
}
