package com.om.sitaramfrem.models.frame_list;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class FrameItemModel {
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
        @SerializedName("frame_no")
        private String frame_no;

        @Expose
        @SerializedName("category_image")
        private String category_image;

        public String getId() {
            return id;
        }

        public String getFrameNumber() {
            return frame_no;
        }

        public String getCategoryImage() {
            return category_image;
        }
    }
}
