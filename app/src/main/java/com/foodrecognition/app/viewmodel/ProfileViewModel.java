package com.foodrecognition.app.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.foodrecognition.app.database.AppDatabase;
import com.foodrecognition.app.database.dao.UserDao;
import com.foodrecognition.app.model.User;
import com.foodrecognition.app.utils.PreferenceUtils;

/**
 * 个人中心ViewModel
 * 处理用户信息和目标设置相关的业务逻辑
 */
public class ProfileViewModel extends AndroidViewModel {

    private final UserDao userDao;
    private final MutableLiveData<String> error = new MutableLiveData<>();
    private final MutableLiveData<Boolean> logoutSuccess = new MutableLiveData<>();

    public ProfileViewModel(@NonNull Application application) {
        super(application);
        userDao = AppDatabase.getInstance(application).userDao();
    }

    /**
     * 获取当前用户信息
     */
    public LiveData<User> getCurrentUser() {
        return userDao.getUserById(PreferenceUtils.getUserId(getApplication()));
    }

    /**
     * 更新用户信息
     */
    public void updateUser(User user) {
        new Thread(() -> {
            try {
                userDao.update(user);
            } catch (Exception e) {
                error.postValue("更新失败：" + e.getMessage());
            }
        }).start();
    }

    /**
     * 更新身体数据
     */
    public void updateBodyData(float height, float weight, String gender, int age, float activityLevel) {
        new Thread(() -> {
            try {
                User user = userDao.getUserById(PreferenceUtils.getUserId(getApplication())).getValue();
                if (user != null) {
                    user.setHeight(height);
                    user.setWeight(weight);
                    user.setGender(gender);
                    user.setAge(age);
                    user.setActivityLevel(activityLevel);
                    user.setUpdateTime(System.currentTimeMillis());
                    userDao.update(user);
                }
            } catch (Exception e) {
                error.postValue("更新失败：" + e.getMessage());
            }
        }).start();
    }

    /**
     * 更新目标设置
     */
    public void updateGoals(int targetCalories, String weightGoal, float weeklyChange) {
        new Thread(() -> {
            try {
                User user = userDao.getUserById(PreferenceUtils.getUserId(getApplication())).getValue();
                if (user != null) {
                    user.setTargetCalories(targetCalories);
                    user.setWeightGoal(weightGoal);
                    user.setWeeklyChange(weeklyChange);
                    user.setUpdateTime(System.currentTimeMillis());
                    userDao.update(user);
                }
            } catch (Exception e) {
                error.postValue("更新失败：" + e.getMessage());
            }
        }).start();
    }

    /**
     * 退出登录
     */
    public void logout() {
        new Thread(() -> {
            try {
                // 清除用户数据
                PreferenceUtils.clearUserData(getApplication());
                logoutSuccess.postValue(true);
            } catch (Exception e) {
                error.postValue("退出失败：" + e.getMessage());
            }
        }).start();
    }

    public LiveData<String> getError() {
        return error;
    }

    public LiveData<Boolean> getLogoutSuccess() {
        return logoutSuccess;
    }
} 