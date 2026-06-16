package com.om.sitaramfrem.models.item;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ItemModel {
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
        @SerializedName("id")
        private String id;
        @Expose
        @SerializedName("name")
        private String name;

        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }
    }
}
