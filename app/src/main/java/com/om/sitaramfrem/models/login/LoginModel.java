package com.om.sitaramfrem.models.login;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class LoginModel {
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
        @SerializedName("token")
        private String token;

        public void setId(String id) {
            this.id = id;
        }

        public String getId() {
            return id;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public String getToken() {
            return token;
        }
    }
}
