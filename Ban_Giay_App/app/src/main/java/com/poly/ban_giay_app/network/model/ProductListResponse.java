package com.poly.ban_giay_app.network.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Response cho API lấy danh sách sản phẩm có phân trang
 */
public class ProductListResponse {
    @SerializedName("success")
    private Boolean success;

    @SerializedName("products")
    private List<ProductResponse> products;

    @SerializedName("pagination")
    private Pagination pagination;

    public Boolean getSuccess() {
        return success != null ? success : true;
    }

    public List<ProductResponse> getProducts() {
        return products;
    }

    public Pagination getPagination() {
        return pagination;
    }

    public static class Pagination {
        @SerializedName("page")
        private Integer page;

        @SerializedName("limit")
        private Integer limit;

        @SerializedName("total")
        private Integer total;

        @SerializedName("pages")
        private Integer pages;

        public Integer getPage() {
            return page;
        }

        public Integer getLimit() {
            return limit;
        }

        public Integer getTotal() {
            return total;
        }

        public Integer getPages() {
            return pages;
        }
    }
}

