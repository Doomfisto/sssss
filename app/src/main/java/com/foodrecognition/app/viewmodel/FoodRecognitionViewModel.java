package com.foodrecognition.app.viewmodel;

import android.app.Application;
import android.graphics.Bitmap;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.foodrecognition.app.model.Food;
import com.foodrecognition.app.network.NetworkManager;
import com.foodrecognition.app.network.NetworkUtils;
import com.foodrecognition.app.network.api.CalorieMamaApi;
import com.foodrecognition.app.network.model.FoodRecognitionResponse;

import java.io.File;
import java.io.IOException;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 食物识别ViewModel
 * 处理拍照、选图和识别相关的业务逻辑
 */
public class FoodRecognitionViewModel extends AndroidViewModel {

    private final MutableLiveData<Food> recognitionResult = new MutableLiveData<>();
    private final MutableLiveData<String> error = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>();

    private final CalorieMamaApi api;

    public FoodRecognitionViewModel(@NonNull Application application) {
        super(application);
        api = NetworkManager.getInstance().getApi();
    }

    /**
     * 识别图片中的食物
     */
    public void recognizeFood(File imageFile) {
        if (!NetworkUtils.isNetworkAvailable(getApplication())) {
            error.setValue("网络不可用，请检查网络连接");
            return;
        }

        isLoading.setValue(true);
        MultipartBody.Part imagePart = NetworkUtils.createImagePart(imageFile);

        api.recognizeFood("Bearer " + getApplication().getString(R.string.caloriemama_token), imagePart)
                .enqueue(new Callback<FoodRecognitionResponse>() {
                    @Override
                    public void onResponse(Call<FoodRecognitionResponse> call,
                                       Response<FoodRecognitionResponse> response) {
                        isLoading.setValue(false);
                        if (response.isSuccessful() && response.body() != null
                                && !response.body().getResults().isEmpty()) {
                            FoodRecognitionResponse.FoodResult result = response.body().getResults().get(0);
                            Food food = new Food();
                            food.setName(result.getFoodName());
                            food.setCategory(result.getCategory());
                            food.setPortion(result.getPortion());
                            food.setCalories(result.getCalories());
                            food.setProtein(result.getProtein());
                            food.setCarbs(result.getCarbs());
                            food.setFat(result.getFat());
                            food.setConfidence(result.getConfidence());
                            food.setTimestamp(System.currentTimeMillis());
                            recognitionResult.setValue(food);
                        } else {
                            error.setValue("识别失败，请重试");
                        }
                    }

                    @Override
                    public void onFailure(Call<FoodRecognitionResponse> call, Throwable t) {
                        isLoading.setValue(false);
                        error.setValue("网络错误：" + t.getMessage());
                    }
                });
    }

    /**
     * 处理Uri形式的图片
     */
    public void handleImageUri(Uri uri) {
        try {
            Bitmap bitmap = NetworkUtils.getBitmapFromUri(getApplication(), uri);
            File imageFile = NetworkUtils.createImageFile(getApplication());
            NetworkUtils.saveBitmapToFile(bitmap, imageFile);
            recognizeFood(imageFile);
        } catch (IOException e) {
            error.setValue("图片处理失败：" + e.getMessage());
        }
    }

    public LiveData<Food> getRecognitionResult() {
        return recognitionResult;
    }

    public LiveData<String> getError() {
        return error;
    }

    public LiveData<Boolean> isLoading() {
        return isLoading;
    }
} 