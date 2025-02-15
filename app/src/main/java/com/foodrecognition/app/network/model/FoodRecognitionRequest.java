package com.foodrecognition.app.network.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * 食物识别请求模型
 */
public class FoodRecognitionRequest {
    @SerializedName("images")
    private List<String> base64Images;  // Base64编码的图片列表

    public FoodRecognitionRequest(List<String> base64Images) {
        this.base64Images = base64Images;
    }

    public List<String> getBase64Images() {
        return base64Images;
    }

    public void setBase64Images(List<String> base64Images) {
        this.base64Images = base64Images;
    }
} 