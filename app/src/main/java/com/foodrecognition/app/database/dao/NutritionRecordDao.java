package com.foodrecognition.app.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.foodrecognition.app.model.NutritionRecord;

import java.util.List;

/**
 * 营养记录数据访问对象
 * 定义营养记录相关的数据库操作
 */
@Dao
public interface NutritionRecordDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(NutritionRecord record);

    @Update
    void update(NutritionRecord record);

    @Delete
    void delete(NutritionRecord record);

    @Query("SELECT * FROM nutrition_records WHERE id = :recordId")
    LiveData<NutritionRecord> getRecordById(long recordId);

    @Query("SELECT * FROM nutrition_records WHERE userId = :userId ORDER BY timestamp DESC")
    LiveData<List<NutritionRecord>> getUserRecords(long userId);

    @Query("SELECT * FROM nutrition_records WHERE userId = :userId AND date = :date")
    LiveData<List<NutritionRecord>> getDailyRecords(long userId, String date);

    @Query("SELECT * FROM nutrition_records WHERE userId = :userId AND date BETWEEN :startDate AND :endDate")
    LiveData<List<NutritionRecord>> getRecordsInRange(long userId, String startDate, String endDate);

    // 统计查询
    @Query("SELECT SUM(calories) FROM nutrition_records WHERE userId = :userId AND date = :date")
    LiveData<Double> getDailyCalories(long userId, String date);

    @Query("SELECT SUM(protein) FROM nutrition_records WHERE userId = :userId AND date = :date")
    LiveData<Double> getDailyProtein(long userId, String date);

    @Query("SELECT SUM(carbs) FROM nutrition_records WHERE userId = :userId AND date = :date")
    LiveData<Double> getDailyCarbs(long userId, String date);

    @Query("SELECT SUM(fat) FROM nutrition_records WHERE userId = :userId AND date = :date")
    LiveData<Double> getDailyFat(long userId, String date);

    // 按餐次统计
    @Query("SELECT SUM(calories) FROM nutrition_records WHERE userId = :userId AND date = :date AND mealType = :mealType")
    LiveData<Double> getMealTypeCalories(long userId, String date, String mealType);

    // 周期统计
    @Query("SELECT AVG(calories) FROM nutrition_records WHERE userId = :userId AND date BETWEEN :startDate AND :endDate")
    LiveData<Double> getAverageCalories(long userId, String startDate, String endDate);

    @Query("SELECT COUNT(DISTINCT date) FROM nutrition_records WHERE userId = :userId AND date BETWEEN :startDate AND :endDate")
    LiveData<Integer> getRecordDays(long userId, String startDate, String endDate);

    // 删除操作
    @Query("DELETE FROM nutrition_records WHERE userId = :userId")
    void deleteUserRecords(long userId);

    @Query("DELETE FROM nutrition_records WHERE userId = :userId AND date = :date")
    void deleteDailyRecords(long userId, String date);
} 