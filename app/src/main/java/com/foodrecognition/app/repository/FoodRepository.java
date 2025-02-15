package com.foodrecognition.app.repository;

import android.content.Context;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import com.foodrecognition.app.database.AppDatabase;
import com.foodrecognition.app.database.dao.FoodDao;
import com.foodrecognition.app.database.dao.NutritionRecordDao;
import com.foodrecognition.app.model.Food;
import com.foodrecognition.app.model.NutritionRecord;
import com.foodrecognition.app.utils.PreferenceUtils;

import java.util.List;

/**
 * 食物记录仓库
 * 处理食物和营养记录的数据存储
 */
public class FoodRepository {
    private final FoodDao foodDao;
    private final NutritionRecordDao nutritionRecordDao;
    private final Context context;

    public FoodRepository(Context context) {
        AppDatabase db = AppDatabase.getInstance(context);
        this.foodDao = db.foodDao();
        this.nutritionRecordDao = db.nutritionRecordDao();
        this.context = context;
    }

    /**
     * 保存食物和营养记录
     */
    public void saveFoodRecord(Food food, String mealType, OnSaveCallback callback) {
        new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... voids) {
                try {
                    // 保存食物信息
                    long foodId = foodDao.insert(food);
                    
                    // 创建营养记录
                    NutritionRecord record = new NutritionRecord();
                    record.setUserId(PreferenceUtils.getUserId(context));
                    record.setFoodId(foodId);
                    record.setMealType(mealType);
                    record.setDate(getCurrentDate());
                    record.setTimestamp(System.currentTimeMillis());
                    record.setPortion(food.getPortion());
                    record.setCalories(food.getCalories());
                    record.setProtein(food.getProtein());
                    record.setCarbs(food.getCarbs());
                    record.setFat(food.getFat());
                    
                    // 保存营养记录
                    nutritionRecordDao.insert(record);
                    return true;
                } catch (Exception e) {
                    e.printStackTrace();
                    return false;
                }
            }

            @Override
            protected void onPostExecute(Boolean success) {
                if (callback != null) {
                    if (success) {
                        callback.onSuccess();
                    } else {
                        callback.onError("保存失败");
                    }
                }
            }
        }.execute();
    }

    /**
     * 获取今日营养记录
     */
    public LiveData<List<NutritionRecord>> getTodayRecords() {
        return nutritionRecordDao.getDailyRecords(
                PreferenceUtils.getUserId(context),
                getCurrentDate()
        );
    }

    /**
     * 获取历史记录
     */
    public LiveData<List<NutritionRecord>> getHistoryRecords() {
        return nutritionRecordDao.getUserRecords(PreferenceUtils.getUserId(context));
    }

    /**
     * 获取当前日期
     */
    private String getCurrentDate() {
        return new java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
                .format(new java.util.Date());
    }

    /**
     * 保存回调接口
     */
    public interface OnSaveCallback {
        void onSuccess();
        void onError(String message);
    }
} 