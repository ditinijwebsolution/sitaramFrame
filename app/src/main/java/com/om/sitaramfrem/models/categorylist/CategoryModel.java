package com.om.sitaramfrem.models.categorylist;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CategoryModel {
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

        @SerializedName("id")
        @Expose
        private Integer id;
        @SerializedName("name")
        @Expose
        private String name;
        @SerializedName("category_image")
        @Expose
        private List<CategoryImage> categoryImage = null;



        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public List<CategoryImage> getCategoryImage() {
            return categoryImage;
        }

        public void setCategoryImage(List<CategoryImage> categoryImage) {
            this.categoryImage = categoryImage;
        }

        public class CategoryImage {

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

            private boolean isAddedToCart = false;

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

            public boolean isAddedToCart() {
                return isAddedToCart;
            }

            public void setAddedToCart(boolean addedToCart) {
                isAddedToCart = addedToCart;
            }
        }
    }
}
