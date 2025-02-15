package com.foodrecognition.app.network.api;

import com.foodrecognition.app.network.model.FoodRecognitionRequest;
import com.foodrecognition.app.network.model.FoodRecognitionResponse;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

/**
 * Calorie Mama API接口定义
 */
public interface CalorieMamaApi {
    
    String BASE_URL = "https://api.caloriemama.ai/";

    /**
     * 食物识别API
     * @param token API访问令牌
     * @param image 食物图片文件
     * @return 识别结果
     */
    @Multipart
    @POST("v1/foodrecognition")
    Call<FoodRecognitionResponse> recognizeFood(
            @Header("Authorization") String token,
            @Part MultipartBody.Part image
    );

    /**
     * 批量食物识别API
     * @param token API访问令牌
     * @param request 包含多个图片的请求体
     * @return 批量识别结果
     */
    @POST("v1/foodrecognition/batch")
    Call<FoodRecognitionResponse> recognizeFoodBatch(
            @Header("Authorization") String token,
            @Body FoodRecognitionRequest request
    );
} 