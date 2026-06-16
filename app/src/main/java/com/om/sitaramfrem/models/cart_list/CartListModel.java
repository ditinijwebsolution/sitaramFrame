package com.om.sitaramfrem.models.cart_list;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class CartListModel {


    @Expose
    @SerializedName("data")
    private List<Data> data;
    @Expose
    @SerializedName("message")
    private String message;
    @Expose
    @SerializedName("status")
    private int status;

    public List<Data> getData() {
        return data;
    }

    public void setData(List<Data> data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public static class Data implements Serializable {
        @Expose
        @SerializedName("date")
        private String date;
        @Expose
        @SerializedName("quantity")
        private String quantity;
        @Expose
        @SerializedName("category_image_id")
        private int categoryImageId;
        @Expose
        @SerializedName("image")
        private String image;
        @Expose
        @SerializedName("cart_id")
        private int cartId;
        @SerializedName("frame_no")
        @Expose
        private String frameNo = "";

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public String getQuantity() {
            return quantity;
        }

        public void setQuantity(String quantity) {
            this.quantity = quantity;
        }

        public int getCategoryImageId() {
            return categoryImageId;
        }

        public void setCategoryImageId(int categoryImageId) {
            this.categoryImageId = categoryImageId;
        }

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }

        public int getCartId() {
            return cartId;
        }

        public void setCartId(int cartId) {
            this.cartId = cartId;
        }

        public String getFrameNo() {
            return frameNo;
        }
    }
}
