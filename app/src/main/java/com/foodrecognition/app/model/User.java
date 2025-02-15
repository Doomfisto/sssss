package com.foodrecognition.app.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * 用户实体类
 * 定义用户的数据结构
 */
@Entity(tableName = "users")
public class User {
    @PrimaryKey(autoGenerate = true)
    private long id;
    private String phone;
    private String nickname;
    private String avatar;
    private String loginType; // PHONE, WECHAT, ALIPAY
    private String thirdPartyId; // 第三方登录ID
    private long createTime;
    private long updateTime;

    // 基本信息
    private String gender;          // 性别
    private int age;               // 年龄
    private float height;          // 身高(cm)
    private float weight;          // 体重(kg)
    private float activityLevel;   // 活动水平(1.2-1.9)

    // 目标设置
    private int targetCalories;    // 目标卡路里
    private String weightGoal;     // 体重目标(减重/维持/增重)
    private float weeklyChange;    // 每周预期变化(kg)

    // 计算属性
    public float getBMI() {
        if (height <= 0) return 0;
        float heightInMeters = height / 100;
        return weight / (heightInMeters * heightInMeters);
    }

    public float getBMR() {
        // 基础代谢率计算（修正的Harris-Benedict公式）
        if ("男".equals(gender)) {
            return (float) (10 * weight + 6.25 * height - 5 * age + 5);
        } else {
            return (float) (10 * weight + 6.25 * height - 5 * age - 161);
        }
    }

    public float getTDEE() {
        // 每日总能量消耗
        return getBMR() * activityLevel;
    }

    // Getters and Setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getLoginType() {
        return loginType;
    }

    public void setLoginType(String loginType) {
        this.loginType = loginType;
    }

    public String getThirdPartyId() {
        return thirdPartyId;
    }

    public void setThirdPartyId(String thirdPartyId) {
        this.thirdPartyId = thirdPartyId;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(long updateTime) {
        this.updateTime = updateTime;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public float getWeight() {
        return weight;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }

    public float getActivityLevel() {
        return activityLevel;
    }

    public void setActivityLevel(float activityLevel) {
        this.activityLevel = activityLevel;
    }

    public int getTargetCalories() {
        return targetCalories;
    }

    public void setTargetCalories(int targetCalories) {
        this.targetCalories = targetCalories;
    }

    public String getWeightGoal() {
        return weightGoal;
    }

    public void setWeightGoal(String weightGoal) {
        this.weightGoal = weightGoal;
    }

    public float getWeeklyChange() {
        return weeklyChange;
    }

    public void setWeeklyChange(float weeklyChange) {
        this.weeklyChange = weeklyChange;
    }
} 