package com.foodrecognition.app.ui;

import android.graphics.Color;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.foodrecognition.app.R;
import com.foodrecognition.app.databinding.ActivityStatisticsBinding;
import com.foodrecognition.app.model.NutritionRecord;
import com.foodrecognition.app.viewmodel.StatisticsViewModel;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.google.android.material.snackbar.Snackbar;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 数据统计页面
 * 显示用户的营养摄入统计数据
 */
public class StatisticsActivity extends AppCompatActivity {

    private ActivityStatisticsBinding binding;
    private StatisticsViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityStatisticsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(StatisticsViewModel.class);

        setupToolbar();
        setupCharts();
        setupChipGroup();
        observeViewModel();
    }

    private void setupToolbar() {
        setSupportActionBar(binding.toolbar);
        binding.toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void setupCharts() {
        // 设置热量趋势图
        binding.chartCalories.getDescription().setEnabled(false);
        binding.chartCalories.setTouchEnabled(true);
        binding.chartCalories.setDragEnabled(true);
        binding.chartCalories.setScaleEnabled(true);
        binding.chartCalories.setPinchZoom(true);
        binding.chartCalories.setDrawGridBackground(false);

        XAxis xAxis = binding.chartCalories.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setValueFormatter(new ValueFormatter() {
            private final SimpleDateFormat sdf = new SimpleDateFormat("MM-dd", Locale.getDefault());
            @Override
            public String getFormattedValue(float value) {
                return sdf.format(new Date((long) value));
            }
        });

        // 设置营养素分布图
        binding.chartNutrition.getDescription().setEnabled(false);
        binding.chartNutrition.setDrawHoleEnabled(true);
        binding.chartNutrition.setHoleColor(Color.WHITE);
        binding.chartNutrition.setTransparentCircleColor(Color.WHITE);
        binding.chartNutrition.setTransparentCircleAlpha(110);
        binding.chartNutrition.setHoleRadius(58f);
        binding.chartNutrition.setTransparentCircleRadius(61f);
        binding.chartNutrition.setDrawCenterText(true);
        binding.chartNutrition.setRotationEnabled(false);
        binding.chartNutrition.setHighlightPerTapEnabled(true);
        binding.chartNutrition.setCenterText("营养素\n分布");
        binding.chartNutrition.animateY(1400);
    }

    private void setupChipGroup() {
        binding.chipGroupRange.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.chip_today) {
                viewModel.setTimeRange(StatisticsViewModel.TimeRange.TODAY);
            } else if (checkedId == R.id.chip_week) {
                viewModel.setTimeRange(StatisticsViewModel.TimeRange.WEEK);
            } else if (checkedId == R.id.chip_month) {
                viewModel.setTimeRange(StatisticsViewModel.TimeRange.MONTH);
            }
        });

        // 默认选中今日
        binding.chipToday.setChecked(true);
    }

    private void observeViewModel() {
        // 观察营养记录
        viewModel.getNutritionRecords().observe(this, this::updateCharts);

        // 观察统计数据
        viewModel.getAverageCalories().observe(this, avgCalories -> {
            if (avgCalories != null) {
                binding.tvAvgCalories.setText(String.format("%.1f kcal", avgCalories));
            }
        });

        viewModel.getRecordDays().observe(this, recordDays -> {
            if (recordDays != null) {
                binding.tvRecordDays.setText(String.format("%d 天", recordDays));
            }
        });

        viewModel.getGoalDays().observe(this, goalDays -> {
            if (goalDays != null) {
                binding.tvGoalDays.setText(String.format("%d 天", goalDays));
                viewModel.getRecordDays().observe(this, recordDays -> {
                    if (recordDays != null && recordDays > 0) {
                        float rate = (float) goalDays / recordDays * 100;
                        binding.tvGoalRate.setText(String.format("%.1f%%", rate));
                    }
                });
            }
        });

        // 观察错误信息
        viewModel.getError().observe(this, this::showError);
    }

    private void updateCharts(List<NutritionRecord> records) {
        if (records == null || records.isEmpty()) {
            binding.chartCalories.clear();
            binding.chartNutrition.clear();
            return;
        }

        // 更新热量趋势图
        List<Entry> entries = records.stream()
                .collect(Collectors.groupingBy(NutritionRecord::getDate))
                .entrySet().stream()
                .map(entry -> {
                    double totalCalories = entry.getValue().stream()
                            .mapToDouble(NutritionRecord::getCalories)
                            .sum();
                    try {
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                        long timestamp = sdf.parse(entry.getKey()).getTime();
                        return new Entry(timestamp, (float) totalCalories);
                    } catch (Exception e) {
                        return null;
                    }
                })
                .filter(entry -> entry != null)
                .collect(Collectors.toList());

        LineDataSet dataSet = new LineDataSet(entries, "每日热量");
        dataSet.setColor(getResources().getColor(R.color.colorPrimary));
        dataSet.setCircleColor(getResources().getColor(R.color.colorPrimary));
        dataSet.setLineWidth(2f);
        dataSet.setCircleRadius(4f);
        dataSet.setDrawValues(true);
        dataSet.setValueTextSize(10f);
        dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);

        LineData lineData = new LineData(dataSet);
        binding.chartCalories.setData(lineData);
        binding.chartCalories.invalidate();

        // 更新营养素分布图
        double totalProtein = records.stream().mapToDouble(NutritionRecord::getProtein).sum();
        double totalCarbs = records.stream().mapToDouble(NutritionRecord::getCarbs).sum();
        double totalFat = records.stream().mapToDouble(NutritionRecord::getFat).sum();

        List<PieEntry> pieEntries = new ArrayList<>();
        pieEntries.add(new PieEntry((float) totalProtein, "蛋白质"));
        pieEntries.add(new PieEntry((float) totalCarbs, "碳水"));
        pieEntries.add(new PieEntry((float) totalFat, "脂肪"));

        PieDataSet pieDataSet = new PieDataSet(pieEntries, "");
        pieDataSet.setColors(
                getResources().getColor(R.color.protein),
                getResources().getColor(R.color.carbs),
                getResources().getColor(R.color.fat)
        );
        pieDataSet.setValueTextSize(12f);
        pieDataSet.setValueTextColor(Color.WHITE);

        PieData pieData = new PieData(pieDataSet);
        pieData.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return String.format("%.1fg", value);
            }
        });

        binding.chartNutrition.setData(pieData);
        binding.chartNutrition.invalidate();
    }

    private void showError(String message) {
        if (message != null) {
            Snackbar.make(binding.getRoot(), message, Snackbar.LENGTH_LONG).show();
        }
    }
} 