package com.foodrecognition.app.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.foodrecognition.app.database.AppDatabase;
import com.foodrecognition.app.database.dao.NutritionRecordDao;
import com.foodrecognition.app.database.dao.UserDao;
import com.foodrecognition.app.model.NutritionRecord;
import com.foodrecognition.app.model.User;
import com.foodrecognition.app.utils.PreferenceUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * 数据统计ViewModel
 * 处理营养数据统计相关的业务逻辑
 */
public class StatisticsViewModel extends AndroidViewModel {

    private final NutritionRecordDao nutritionRecordDao;
    private final UserDao userDao;
    private final MutableLiveData<String> startDate = new MutableLiveData<>();
    private final MutableLiveData<String> endDate = new MutableLiveData<>();
    private final MutableLiveData<String> error = new MutableLiveData<>();

    public StatisticsViewModel(@NonNull Application application) {
        super(application);
        AppDatabase db = AppDatabase.getInstance(application);
        nutritionRecordDao = db.nutritionRecordDao();
        userDao = db.userDao();
        
        // 默认显示今日数据
        setTimeRange(TimeRange.TODAY);
    }

    /**
     * 设置时间范围
     */
    public void setTimeRange(TimeRange range) {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        
        String end = sdf.format(calendar.getTime());
        String start;
        
        switch (range) {
            case TODAY:
                start = end;
                break;
            case WEEK:
                calendar.add(Calendar.DAY_OF_WEEK, -(calendar.get(Calendar.DAY_OF_WEEK) - 1));
                start = sdf.format(calendar.getTime());
                break;
            case MONTH:
                calendar.set(Calendar.DAY_OF_MONTH, 1);
                start = sdf.format(calendar.getTime());
                break;
            default:
                return;
        }
        
        startDate.setValue(start);
        endDate.setValue(end);
    }

    /**
     * 获取营养记录
     */
    public LiveData<List<NutritionRecord>> getNutritionRecords() {
        return Transformations.switchMap(startDate, start ->
                Transformations.switchMap(endDate, end ->
                        nutritionRecordDao.getRecordsInRange(
                                PreferenceUtils.getUserId(getApplication()),
                                start,
                                end
                        )));
    }

    /**
     * 获取平均热量
     */
    public LiveData<Double> getAverageCalories() {
        return Transformations.switchMap(startDate, start ->
                Transformations.switchMap(endDate, end ->
                        nutritionRecordDao.getAverageCalories(
                                PreferenceUtils.getUserId(getApplication()),
                                start,
                                end
                        )));
    }

    /**
     * 获取记录天数
     */
    public LiveData<Integer> getRecordDays() {
        return Transformations.switchMap(startDate, start ->
                Transformations.switchMap(endDate, end ->
                        nutritionRecordDao.getRecordDays(
                                PreferenceUtils.getUserId(getApplication()),
                                start,
                                end
                        )));
    }

    /**
     * 获取达标天数
     */
    public LiveData<Integer> getGoalDays() {
        return Transformations.switchMap(startDate, start ->
                Transformations.switchMap(endDate, end ->
                        nutritionRecordDao.getGoalDays(
                                PreferenceUtils.getUserId(getApplication()),
                                start,
                                end,
                                userDao.getUserTargetCalories(PreferenceUtils.getUserId(getApplication())).getValue()
                        )));
    }

    /**
     * 获取用户目标热量
     */
    public LiveData<Integer> getTargetCalories() {
        return userDao.getUserTargetCalories(PreferenceUtils.getUserId(getApplication()));
    }

    /**
     * 获取错误信息
     */
    public LiveData<String> getError() {
        return error;
    }

    /**
     * 时间范围枚举
     */
    public enum TimeRange {
        TODAY,
        WEEK,
        MONTH
    }
} 