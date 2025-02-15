package com.foodrecognition.app.network.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * 食物识别响应模型
 */
public class FoodRecognitionResponse {
    @SerializedName("results")
    private List<FoodResult> results;

    public static class FoodResult {
        @SerializedName("food_name")
        private String foodName;

        @SerializedName("category")
        private String category;

        @SerializedName("portion")
        private double portion;

        @SerializedName("unit")
        private String unit;

        @SerializedName("calories")
        private double calories;

        @SerializedName("protein")
        private double protein;

        @SerializedName("carbs")
        private double carbs;

        @SerializedName("fat")
        private double fat;

        @SerializedName("confidence")
        private double confidence;

        // Getters
        public String getFoodName() {
            return foodName;
        }

        public String getCategory() {
            return category;
        }

        public double getPortion() {
            return portion;
        }

        public String getUnit() {
            return unit;
        }

        public double getCalories() {
            return calories;
        }

        public double getProtein() {
            return protein;
        }

        public double getCarbs() {
            return carbs;
        }

        public double getFat() {
            return fat;
        }

        public double getConfidence() {
            return confidence;
        }
    }

    public List<FoodResult> getResults() {
        return results;
    }
} 