package com.octane.app.Model;

import com.google.gson.annotations.SerializedName;

public class ResponseModel {
    @SerializedName("isSuccess")
    private int isSuccess;
    @SerializedName("message")
    private String message;

    public ResponseModel(int isSuccess, String message) {
        this.isSuccess = isSuccess;
        this.message = message;
    }

    public int getIsSuccess() {
        return isSuccess;
    }

    public void setIsSuccess(int isSuccess) {
        this.isSuccess = isSuccess;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
