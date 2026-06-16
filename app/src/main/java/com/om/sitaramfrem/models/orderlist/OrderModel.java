package com.om.sitaramfrem.models.orderlist;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class OrderModel {
    @Expose
    @SerializedName("message")
    private String message;
    @Expose
    @SerializedName("status")
    private int status;
    @SerializedName("data")
    @Expose
    private List<Data> data = null;

    public int getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public List<Data> getData() {
        return data;
    }

    public class Data {
        @Expose
        @SerializedName("date")
        private String date;
        @Expose
        @SerializedName("frame_measure")
        private String frameMeasure;
        @Expose
        @SerializedName("quantity")
        private String quantity;
        @Expose
        @SerializedName("order_no")
        private String orderNo;
        @Expose
        @SerializedName("order_id")
        private String orderId;
        @Expose
        @SerializedName("notes")
        private String notes;
        @Expose
        @SerializedName("item_name")
        private String itemName;
        @Expose
        @SerializedName("frame_no")
        private String frameNo;
        @Expose
        @SerializedName("order_status")
        private String orderStatus;
        @Expose
        @SerializedName("category_image")
        private String categoryImage;

        public String getDate() {
            return date;
        }

        public String getFrameMeasure() {
            return frameMeasure;
        }

        public String getQuantity() {
            return quantity;
        }

        public String getOrderNo() {
            return orderNo;
        }

        public String getOrderId() {
            return orderId;
        }

        public String getNotes() {
            return notes;
        }

        public String getItemName() {
            return itemName;
        }

        public String getFrameNo() {
            return frameNo;
        }

        public String getOrderStatus() {
            return orderStatus;
        }

        public String getCategoryImage() {
            return categoryImage;
        }
    }
}
