package com.foodrecognition.app.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * 食物实体类
 * 定义食物的数据结构
 */
@Entity(tableName = "foods")
public class Food {
    @PrimaryKey(autoGenerate = true)
    private long id;
    private String name;        // 食物名称
    private double calories;    // 卡路里
    private double protein;     // 蛋白质(g)
    private double carbs;       // 碳水化合物(g)
    private double fat;         // 脂肪(g)
    private double portion;     // 份量(g)
    private String imageUrl;    // 图片URL
    private long createTime;    // 创建时间

    // Getters and Setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public double getPortion() {
        return portion;
    }

    public void setPortion(double portion) {
        this.portion = portion;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }
} 