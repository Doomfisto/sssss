package com.foodrecognition.app.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * 偏好设置工具类
 * 用于管理用户登录状态等信息
 */
public class PreferenceUtils {
    private static final String PREF_NAME = "food_recognition_prefs";
    private static final String KEY_LOGGED_IN = "logged_in";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_USER_PHONE = "user_phone";
    private static final String KEY_LOGIN_TYPE = "login_type";

    private static SharedPreferences getPreferences(Context context) {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public static boolean isLoggedIn(Context context) {
        return getPreferences(context).getBoolean(KEY_LOGGED_IN, false);
    }

    public static void setLoggedIn(Context context, boolean loggedIn) {
        getPreferences(context).edit().putBoolean(KEY_LOGGED_IN, loggedIn).apply();
    }

    public static void saveUserId(Context context, long userId) {
        getPreferences(context).edit().putLong(KEY_USER_ID, userId).apply();
    }

    public static long getUserId(Context context) {
        return getPreferences(context).getLong(KEY_USER_ID, -1);
    }

    public static void saveUserPhone(Context context, String phone) {
        getPreferences(context).edit().putString(KEY_USER_PHONE, phone).apply();
    }

    public static String getUserPhone(Context context) {
        return getPreferences(context).getString(KEY_USER_PHONE, "");
    }

    public static void saveLoginType(Context context, String type) {
        getPreferences(context).edit().putString(KEY_LOGIN_TYPE, type).apply();
    }

    public static String getLoginType(Context context) {
        return getPreferences(context).getString(KEY_LOGIN_TYPE, "");
    }

    public static void clearUserData(Context context) {
        getPreferences(context).edit()
                .remove(KEY_LOGGED_IN)
                .remove(KEY_USER_ID)
                .remove(KEY_USER_PHONE)
                .remove(KEY_LOGIN_TYPE)
                .apply();
    }
} 