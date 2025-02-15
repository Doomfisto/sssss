package com.foodrecognition.app.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.foodrecognition.app.database.dao.FoodDao;
import com.foodrecognition.app.database.dao.UserDao;
import com.foodrecognition.app.database.dao.NutritionRecordDao;
import com.foodrecognition.app.model.Food;
import com.foodrecognition.app.model.User;
import com.foodrecognition.app.model.NutritionRecord;

/**
 * Room数据库配置类
 * 定义数据库版本、表和数据访问对象
 */
@Database(
    entities = {Food.class, User.class, NutritionRecord.class},
    version = 1,
    exportSchema = false
)
public abstract class AppDatabase extends RoomDatabase {
    private static final String DATABASE_NAME = "food_recognition.db";
    private static volatile AppDatabase instance;

    // 定义数据访问对象
    public abstract FoodDao foodDao();
    public abstract UserDao userDao();
    public abstract NutritionRecordDao nutritionRecordDao();

    // 单例模式获取数据库实例
    public static synchronized AppDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(
                    context.getApplicationContext(),
                    AppDatabase.class,
                    DATABASE_NAME)
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }
} 