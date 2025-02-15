package com.foodrecognition.app.model;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

/**
 * 营养记录实体类
 * 定义用户的饮食记录数据结构
 */
@Entity(
    tableName = "nutrition_records",
    foreignKeys = {
        @ForeignKey(
            entity = User.class,
            parentColumns = "id",
            childColumns = "userId",
            onDelete = ForeignKey.CASCADE
        ),
        @ForeignKey(
            entity = Food.class,
            parentColumns = "id",
            childColumns = "foodId",
            onDelete = ForeignKey.CASCADE
        )
    },
    indices = {
        @Index("userId"),
        @Index("foodId"),
        @Index(value = {"userId", "date"})
    }
)
public class NutritionRecord {
    @PrimaryKey(autoGenerate = true)
    private long id;
    private long userId;
    private long foodId;
    private String date;        // 日期，格式：yyyy-MM-dd
    private String mealType;    // 餐次：早餐、午餐、晚餐、加餐
    private double portion;     // 食用份量(g)
    private double calories;    // 卡路里
    private double protein;     // 蛋白质(g)
    private double carbs;       // 碳水化合物(g)
    private double fat;         // 脂肪(g)
    private long timestamp;     // 记录时间戳

    // Getters and Setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public long getFoodId() {
        return foodId;
    }

    public void setFoodId(long foodId) {
        this.foodId = foodId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getMealType() {
        return mealType;
    }

    public void setMealType(String mealType) {
        this.mealType = mealType;
    }

    public double getPortion() {
        return portion;
    }

    public void setPortion(double portion) {
        this.portion = portion;
    }

    public double getCalories() {
        return calories;
    }

    public void setCalories(double calories) {
        this.calories = calories;
    }

    public double getProtein() {
        return protein;
    }

    public void setProtein(double protein) {
        this.protein = protein;
    }

    public double getCarbs() {
        return carbs;
    }

    public void setCarbs(double carbs) {
        this.carbs = carbs;
    }

    public double getFat() {
        return fat;
    }

    public void setFat(double fat) {
        this.fat = fat;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
} 