package com.poly.ban_giay_app.adapter;

import android.content.Intent;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.StrikethroughSpan;
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
        
        // Load image from URL if available, otherwise use resource
        if (p.imageUrl != null && !p.imageUrl.isEmpty()) {
            // Nếu là URL từ server hoặc content URI
            if (p.imageUrl.startsWith("http://") || p.imageUrl.startsWith("https://") || p.imageUrl.startsWith("content://")) {
                Glide.with(holder.itemView.getContext())
                        .load(p.imageUrl)
                        .placeholder(R.drawable.giaymau) // Placeholder while loading
                        .error(R.drawable.giaymau) // Error image if load fails
                        .into(holder.img);
            } else {
                // Nếu là tên file ảnh (giay15, giay14, etc.), load từ drawable
                int imageResId = getImageResourceId(holder.itemView.getContext(), p.imageUrl);
                // Kiểm tra resource ID có hợp lệ không (phải > 0 và không phải là giá trị lỗi)
                if (imageResId > 0) {
                    try {
                        holder.img.setImageResource(imageResId);
                    } catch (Exception e) {
                        // Nếu set resource thất bại, dùng ảnh mặc định
                        holder.img.setImageResource(R.drawable.giaymau);
                    }
                } else {
                    holder.img.setImageResource(R.drawable.giaymau);
                }
            }
        } else if (p.imageRes != 0) {
            try {
                holder.img.setImageResource(p.imageRes);
            } catch (Exception e) {
                holder.img.setImageResource(R.drawable.giaymau);
            }
        } else {
            holder.img.setImageResource(R.drawable.giaymau);
        }
        
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
        try {
            // Loại bỏ extension và path nếu có
            String name = imageName;
            if (name.contains("/")) {
                name = name.substring(name.lastIndexOf("/") + 1);
            }
            if (name.contains(".")) {
                name = name.substring(0, name.lastIndexOf("."));
            }
            
            // Bỏ qua nếu tên rỗng hoặc chứa ký tự đặc biệt không hợp lệ
            if (name == null || name.trim().isEmpty() || name.contains(":") || name.contains(" ")) {
                return 0;
            }
            
            // Map tên file với resource ID
            int resId = context.getResources().getIdentifier(name, "drawable", context.getPackageName());
            // Kiểm tra resource ID có tồn tại thực sự không
            if (resId > 0) {
                try {
                    // Thử truy cập resource để đảm bảo nó tồn tại
                    context.getResources().getDrawable(resId, null);
                    return resId;
                } catch (Exception e) {
                    return 0;
                }
            }
            return 0;
        } catch (Exception e) {
            return 0;
        }
    }
}
