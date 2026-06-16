package com.om.sitaramfrem.models.slider;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SliderModel {
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
        @SerializedName("image")
        private String image;

        public String getId() {
            return id;
        }

        public String getImage() {
            return image;
        }
    }
}
