package com.poly.ban_giay_app.network.request;

import com.google.gson.annotations.SerializedName;

/**
 * Request để cập nhật trạng thái đơn hàng
 */
public class UpdateOrderStatusRequest {
    @SerializedName("trang_thai")
    private String trangThai;

    public UpdateOrderStatusRequest() {
    }

    public UpdateOrderStatusRequest(String trangThai) {
        this.trangThai = trangThai;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }
}

