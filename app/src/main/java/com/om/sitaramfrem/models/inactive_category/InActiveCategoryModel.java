package com.om.sitaramfrem.models.inactive_category;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class InActiveCategoryModel {
    @SerializedName("status")
    @Expose
    private Integer status;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("data")
    @Expose
    private List<Data> data = null;

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<Data> getData() {
        return data;
    }

    public void setData(List<Data> data) {
        this.data = data;
    }

    public class Data {
        @SerializedName("category_image_id")
        @Expose
        private int categoryImageId;
        @SerializedName("image")
        @Expose
        private String image;
        @SerializedName("frame_no")
        @Expose
        private String frameNo;
        @SerializedName("active_status")
        @Expose
        private int activeStatus;

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

        public String getFrameNo() {
            return frameNo;
        }

        public void setFrameNo(String frameNo) {
            this.frameNo = frameNo;
        }

        public void setActiveStatus(int activeStatus) {
            this.activeStatus = activeStatus;
        }

        public int getActiveStatus() {
            return activeStatus;
        }
    }
}
