package com.poly.ban_giay_app.network.request;

import com.google.gson.annotations.SerializedName;

/**
 * Request để cập nhật số lượng tồn kho
 */
public class StockUpdateRequest {
    @SerializedName("so_luong_ton")
    private Integer soLuongTon;

    public StockUpdateRequest() {
    }

    public StockUpdateRequest(Integer soLuongTon) {
        this.soLuongTon = soLuongTon;
    }

    public Integer getSoLuongTon() {
        return soLuongTon;
    }

    public void setSoLuongTon(Integer soLuongTon) {
        this.soLuongTon = soLuongTon;
    }
}

