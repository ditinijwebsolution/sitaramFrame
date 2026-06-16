package com.om.sitaramfrem.models.profile;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ProfileModel {
    @Expose
    @SerializedName("data")
    private Data data;
    @Expose
    @SerializedName("message")
    private String message;
    @Expose
    @SerializedName("status")
    private int status;

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
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

    public class Data {
        @Expose
        @SerializedName("id")
        private String id;
        @Expose
        @SerializedName("name")
        private String name;
        @Expose
        @SerializedName("mobile_number")
        private String mobileNumber;
        @Expose
        @SerializedName("address")
        private String address;

        public void setId(String id) {
            this.id = id;
        }

        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getMobileNumber() {
            return mobileNumber;
        }

        public String getAddress() {
            return address;
        }
    }
}
