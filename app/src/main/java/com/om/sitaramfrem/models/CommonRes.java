package com.om.sitaramfrem.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CommonRes {

    @SerializedName("status")
    @Expose
    private int status;
    @SerializedName("message")
    @Expose
    private String Message;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }




}