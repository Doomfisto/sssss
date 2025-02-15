package com.foodrecognition.app.ui;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.foodrecognition.app.R;
import com.foodrecognition.app.databinding.ActivityMainBinding;
import com.foodrecognition.app.ui.adapter.HistoryAdapter;
import com.foodrecognition.app.viewmodel.MainViewModel;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

/**
 * 主页面
 * 显示今日摄入统计和历史记录
 */
public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private MainViewModel viewModel;
    private HistoryAdapter historyAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // 初始化ViewModel
        viewModel = new ViewModelProvider(this).get(MainViewModel.class);

        // 设置Toolbar
        setSupportActionBar(binding.toolbar);

        // 初始化RecyclerView
        setupRecyclerView();

        // 初始化图表
        setupPieChart();

        // 设置点击事件
        setupClickListeners();

        // 观察数据变化
        observeViewModel();
    }

    private void setupRecyclerView() {
        historyAdapter = new HistoryAdapter();
        binding.rvHistory.setLayoutManager(new LinearLayoutManager(this));
        binding.rvHistory.setAdapter(historyAdapter);
    }

    private void setupPieChart() {
        binding.chartNutrition.setDescription(null);
        binding.chartNutrition.setHoleRadius(50f);
        binding.chartNutrition.setTransparentCircleRadius(55f);
        binding.chartNutrition.setDrawHoleEnabled(true);
        binding.chartNutrition.setHoleColor(Color.WHITE);
        binding.chartNutrition.setDrawEntryLabels(false);
        binding.chartNutrition.getLegend().setEnabled(true);
    }

    private void setupClickListeners() {
        // 拍照按钮
        binding.btnTakePhoto.setOnClickListener(v -> {
            Intent intent = new Intent(this, FoodRecognitionActivity.class);
            intent.putExtra("mode", "camera");
            startActivity(intent);
        });

        // 选择图片按钮
        binding.btnSelectPhoto.setOnClickListener(v -> {
            Intent intent = new Intent(this, FoodRecognitionActivity.class);
            intent.putExtra("mode", "gallery");
            startActivity(intent);
        });

        // 个人中心按钮
        binding.fabProfile.setOnClickListener(v -> {
            startActivity(new Intent(this, ProfileActivity.class));
        });
    }

    private void observeViewModel() {
        // 观察今日营养数据
        viewModel.getDailyNutrition().observe(this, nutrition -> {
            updatePieChart(nutrition);
        });

        // 观察历史记录
        viewModel.getHistoryRecords().observe(this, records -> {
            historyAdapter.submitList(records);
        });

        // 观察错误信息
        viewModel.getError().observe(this, error -> {
            if (error != null) {
                showError(error);
            }
        });
    }

    private void updatePieChart(MainViewModel.NutritionData nutrition) {
        List<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry((float) nutrition.getProtein(), "蛋白质"));
        entries.add(new PieEntry((float) nutrition.getCarbs(), "碳水"));
        entries.add(new PieEntry((float) nutrition.getFat(), "脂肪"));

        PieDataSet dataSet = new PieDataSet(entries, "营养成分");
        dataSet.setColors(Color.rgb(255, 152, 0), Color.rgb(76, 175, 80), Color.rgb(33, 150, 243));
        dataSet.setValueTextSize(14f);
        dataSet.setValueTextColor(Color.WHITE);

        PieData data = new PieData(dataSet);
        binding.chartNutrition.setData(data);
        binding.chartNutrition.invalidate();
    }

    private void showError(String message) {
        Snackbar.make(binding.getRoot(), message, Snackbar.LENGTH_LONG).show();
    }
} 