package com.foodrecognition.app.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.foodrecognition.app.model.User;

/**
 * 用户数据访问对象
 * 定义用户相关的数据库操作
 */
@Dao
public interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(User user);

    @Update
    void update(User user);

    @Query("SELECT * FROM users WHERE id = :userId")
    LiveData<User> getUserById(long userId);

    @Query("SELECT * FROM users WHERE phone = :phone LIMIT 1")
    LiveData<User> getUserByPhone(String phone);

    @Query("DELETE FROM users WHERE id = :userId")
    void deleteUser(long userId);

    @Query("SELECT * FROM users LIMIT 1")
    LiveData<User> getCurrentUser();

    @Query("DELETE FROM users")
    void deleteAll();

    // 用户目标相关查询
    @Query("SELECT targetCalories FROM users WHERE id = :userId")
    LiveData<Integer> getUserTargetCalories(long userId);

    @Query("SELECT weightGoal FROM users WHERE id = :userId")
    LiveData<String> getUserWeightGoal(long userId);

    @Query("SELECT weeklyChange FROM users WHERE id = :userId")
    LiveData<Float> getUserWeeklyChange(long userId);
} 