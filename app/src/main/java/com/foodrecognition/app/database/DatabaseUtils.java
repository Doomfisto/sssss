package com.foodrecognition.app.database;

import android.content.Context;
import android.os.AsyncTask;

import com.foodrecognition.app.model.Food;
import com.foodrecognition.app.model.User;
import com.foodrecognition.app.model.NutritionRecord;
import com.foodrecognition.app.network.model.FoodRecognitionResponse;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * 数据库工具类
 * 提供常用的数据库操作方法
 */
public class DatabaseUtils {
    
    /**
     * 将识别结果转换为Food实体
     */
    public static Food convertToFood(FoodRecognitionResponse.FoodResult result) {
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
        return food;
    }

    /**
     * 创建营养记录
     */
    public static NutritionRecord createNutritionRecord(User user, Food food, String mealType) {
        NutritionRecord record = new NutritionRecord();
        record.setUserId(user.getId());
        record.setFoodId(food.getId());
        record.setMealType(mealType);
        record.setDate(getCurrentDate());
        record.setTimestamp(System.currentTimeMillis());
        record.setPortion(food.getPortion());
        record.setCalories(food.getCalories());
        record.setProtein(food.getProtein());
        record.setCarbs(food.getCarbs());
        record.setFat(food.getFat());
        return record;
    }

    /**
     * 获取当前日期字符串
     */
    public static String getCurrentDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return sdf.format(new Date());
    }

    /**
     * 异步插入食物数据
     */
    public static void insertFoodAsync(Context context, Food food, OnDatabaseOperationListener listener) {
        new AsyncTask<Void, Void, Long>() {
            @Override
            protected Long doInBackground(Void... voids) {
                return AppDatabase.getInstance(context).foodDao().insert(food);
            }

            @Override
            protected void onPostExecute(Long id) {
                if (listener != null) {
                    listener.onSuccess(id);
                }
            }
        }.execute();
    }

    /**
     * 异步插入营养记录
     */
    public static void insertNutritionRecordAsync(Context context, NutritionRecord record, OnDatabaseOperationListener listener) {
        new AsyncTask<Void, Void, Long>() {
            @Override
            protected Long doInBackground(Void... voids) {
                return AppDatabase.getInstance(context).nutritionRecordDao().insert(record);
            }

            @Override
            protected void onPostExecute(Long id) {
                if (listener != null) {
                    listener.onSuccess(id);
                }
            }
        }.execute();
    }

    /**
     * 数据库操作回调接口
     */
    public interface OnDatabaseOperationListener {
        void onSuccess(Long id);
        default void onError(Exception e) {}
    }
} 