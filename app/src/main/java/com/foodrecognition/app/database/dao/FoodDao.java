package com.foodrecognition.app.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.foodrecognition.app.model.Food;

import java.util.List;

/**
 * 食物数据访问对象
 * 定义食物相关的数据库操作
 */
@Dao
public interface FoodDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(Food food);

    @Update
    void update(Food food);

    @Delete
    void delete(Food food);

    @Query("SELECT * FROM foods WHERE id = :foodId")
    LiveData<Food> getFoodById(long foodId);

    @Query("SELECT * FROM foods ORDER BY createTime DESC")
    LiveData<List<Food>> getAllFoods();

    @Query("SELECT * FROM foods WHERE name LIKE '%' || :keyword || '%'")
    LiveData<List<Food>> searchFoods(String keyword);

    @Query("SELECT * FROM foods WHERE calories BETWEEN :minCalories AND :maxCalories")
    LiveData<List<Food>> getFoodsByCalorieRange(double minCalories, double maxCalories);

    @Query("SELECT * FROM foods WHERE createTime >= :startTime AND createTime <= :endTime")
    LiveData<List<Food>> getFoodsByTimeRange(long startTime, long endTime);

    @Query("DELETE FROM foods")
    void deleteAll();
} 