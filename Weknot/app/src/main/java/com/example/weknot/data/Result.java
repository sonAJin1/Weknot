package com.example.weknot.data;

import com.google.gson.annotations.SerializedName;

public class Result {

    @SerializedName("result")
    private String result;

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
}
