package com.poly.ban_giay_app.adapter;

import android.content.Context;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.StrikethroughSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.poly.ban_giay_app.R;
import com.poly.ban_giay_app.network.model.OrderResponse;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {
    private List<OrderResponse> orders;
    private OnOrderListener listener;
    private Context context;

    public interface OnOrderListener {
        void onViewDetail(OrderResponse order);
        void onCancelOrder(OrderResponse order);
    }

    public OrderAdapter(List<OrderResponse> orders, OnOrderListener listener, Context context) {
        this.orders = orders;
        this.listener = listener;
        this.context = context;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_order, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        OrderResponse order = orders.get(position);
        holder.bind(order, position);
    }

    @Override
    public int getItemCount() {
        return orders != null ? orders.size() : 0;
    }

    class OrderViewHolder extends RecyclerView.ViewHolder {
        private TextView txtOrderId, txtOrderStatus, txtOrderTotal, txtOrderDate;
        private LinearLayout layoutOrderItems;
        private Button btnCancelOrder, btnViewDetail;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            txtOrderId = itemView.findViewById(R.id.txtOrderId);
            txtOrderStatus = itemView.findViewById(R.id.txtOrderStatus);
            txtOrderTotal = itemView.findViewById(R.id.txtOrderTotal);
            txtOrderDate = itemView.findViewById(R.id.txtOrderDate);
            layoutOrderItems = itemView.findViewById(R.id.layoutOrderItems);
            btnCancelOrder = itemView.findViewById(R.id.btnCancelOrder);
            btnViewDetail = itemView.findViewById(R.id.btnViewDetail);
        }

        public void bind(OrderResponse order, int position) {
            // Mã đơn hàng (lấy 8 ký tự cuối của ID)
            String orderId = order.getId();
            if (orderId != null && orderId.length() > 8) {
                orderId = orderId.substring(orderId.length() - 8).toUpperCase();
            }
            txtOrderId.setText("Đơn hàng #" + (orderId != null ? orderId : "N/A"));

            // Trạng thái
            String trangThai = order.getTrangThai();
            String trangThaiDisplay = order.getTrangThaiDisplay();
            txtOrderStatus.setText(trangThaiDisplay);

            // Màu sắc và background cho trạng thái
            int statusColor;
            int statusBg;
            switch (trangThai) {
                case "pending":
                    statusColor = ContextCompat.getColor(context, android.R.color.holo_orange_dark);
                    statusBg = R.drawable.bg_status_pending;
                    break;
                case "confirmed":
                    statusColor = ContextCompat.getColor(context, android.R.color.holo_blue_dark);
                    statusBg = R.drawable.bg_status_pending;
                    break;
                case "shipping":
                    statusColor = ContextCompat.getColor(context, android.R.color.holo_blue_light);
                    statusBg = R.drawable.bg_status_pending;
                    break;
                case "delivered":
                    statusColor = ContextCompat.getColor(context, android.R.color.holo_green_dark);
                    statusBg = R.drawable.bg_status_pending;
                    break;
                case "cancelled":
                    statusColor = ContextCompat.getColor(context, android.R.color.holo_red_dark);
                    statusBg = R.drawable.bg_status_pending;
                    break;
                default:
                    statusColor = ContextCompat.getColor(context, android.R.color.darker_gray);
                    statusBg = R.drawable.bg_status_pending;
            }
            txtOrderStatus.setTextColor(statusColor);

            // Tổng tiền
            if (order.getTongTien() != null) {
                txtOrderTotal.setText(formatPrice(order.getTongTien()));
            } else {
                txtOrderTotal.setText("0₫");
            }

            // Ngày đặt hàng
            if (order.getCreatedAt() != null) {
                try {
                    SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
                    SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                    Date date = inputFormat.parse(order.getCreatedAt());
                    if (date != null) {
                        txtOrderDate.setText("Ngày đặt: " + outputFormat.format(date));
                    } else {
                        txtOrderDate.setText("Ngày đặt: " + order.getCreatedAt());
                    }
                } catch (Exception e) {
                    txtOrderDate.setText("Ngày đặt: " + order.getCreatedAt());
                }
            } else {
                txtOrderDate.setText("Ngày đặt: N/A");
            }

            // Hiển thị danh sách sản phẩm
            layoutOrderItems.removeAllViews();
            if (order.getItems() != null && !order.getItems().isEmpty()) {
                for (OrderResponse.OrderItemResponse item : order.getItems()) {
                    View itemView = LayoutInflater.from(context)
                            .inflate(R.layout.item_order_product, layoutOrderItems, false);
                    
                    TextView txtItemName = itemView.findViewById(R.id.txtItemName);
                    TextView txtItemInfo = itemView.findViewById(R.id.txtItemInfo);
                    TextView txtItemPrice = itemView.findViewById(R.id.txtItemPrice);

                    txtItemName.setText(item.getTenSanPham());
                    txtItemInfo.setText("Size: " + item.getKichThuoc() + " x " + item.getSoLuong());
                    if (item.getGia() != null) {
                        long itemTotal = item.getGia() * item.getSoLuong();
                        txtItemPrice.setText(formatPrice(itemTotal));
                    } else {
                        txtItemPrice.setText("0₫");
                    }

                    layoutOrderItems.addView(itemView);
                }
            }

            // Hiển thị nút hủy đơn chỉ khi ở trạng thái pending hoặc confirmed
            if ("pending".equals(trangThai) || "confirmed".equals(trangThai)) {
                btnCancelOrder.setVisibility(View.VISIBLE);
            } else {
                btnCancelOrder.setVisibility(View.GONE);
            }

            // Click listeners
            btnViewDetail.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onViewDetail(order);
                }
            });

            btnCancelOrder.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onCancelOrder(order);
                }
            });
        }

        private String formatPrice(long price) {
            return String.format("%,d₫", price).replace(",", ".");
        }
    }
}

