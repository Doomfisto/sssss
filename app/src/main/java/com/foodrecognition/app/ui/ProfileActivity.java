package com.foodrecognition.app.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.foodrecognition.app.R;
import com.foodrecognition.app.databinding.ActivityProfileBinding;
import com.foodrecognition.app.model.User;
import com.foodrecognition.app.ui.dialog.EditBodyDataDialog;
import com.foodrecognition.app.ui.dialog.EditGoalsDialog;
import com.foodrecognition.app.viewmodel.ProfileViewModel;
import com.google.android.material.snackbar.Snackbar;

/**
 * 个人中心页面
 * 显示和编辑用户信息、身体数据和目标设置
 */
public class ProfileActivity extends AppCompatActivity {

    private ActivityProfileBinding binding;
    private ProfileViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(ProfileViewModel.class);

        setupToolbar();
        setupClickListeners();
        observeViewModel();
    }

    private void setupToolbar() {
        setSupportActionBar(binding.toolbar);
        binding.toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void setupClickListeners() {
        // 编辑资料按钮
        binding.btnEditProfile.setOnClickListener(v -> {
            // TODO: 实现编辑个人资料功能
        });

        // 身体数据卡片点击
        binding.cardBodyData.setOnClickListener(v -> {
            EditBodyDataDialog dialog = new EditBodyDataDialog();
            dialog.show(getSupportFragmentManager(), "edit_body_data");
        });

        // 目标设置卡片点击
        binding.cardGoals.setOnClickListener(v -> {
            EditGoalsDialog dialog = new EditGoalsDialog();
            dialog.show(getSupportFragmentManager(), "edit_goals");
        });

        // 退出登录按钮
        binding.btnLogout.setOnClickListener(v -> {
            showLogoutConfirmDialog();
        });
    }

    private void observeViewModel() {
        // 观察用户信息
        viewModel.getCurrentUser().observe(this, this::updateUI);

        // 观察错误信息
        viewModel.getError().observe(this, this::showError);

        // 观察退出登录状态
        viewModel.getLogoutSuccess().observe(this, success -> {
            if (success) {
                startActivity(new Intent(this, LoginActivity.class));
                finishAffinity();
            }
        });
    }

    private void updateUI(User user) {
        if (user == null) return;

        // 更新基本信息
        binding.tvNickname.setText(user.getNickname());
        binding.tvPhone.setText(user.getPhone());

        // 更新身体数据
        binding.tvHeight.setText(String.format("%.1f cm", user.getHeight()));
        binding.tvWeight.setText(String.format("%.1f kg", user.getWeight()));
        binding.tvBmi.setText(String.format("%.1f", user.getBMI()));

        // 更新目标设置
        binding.tvTargetCalories.setText(String.format("%d kcal", user.getTargetCalories()));
        binding.tvWeightGoal.setText(user.getWeightGoal());
        binding.tvWeeklyChange.setText(String.format("%.1f kg", user.getWeeklyChange()));
    }

    private void showLogoutConfirmDialog() {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("退出登录")
                .setMessage("确定要退出登录吗？")
                .setPositiveButton("确定", (dialog, which) -> viewModel.logout())
                .setNegativeButton("取消", null)
                .show();
    }

    private void showError(String message) {
        Snackbar.make(binding.getRoot(), message, Snackbar.LENGTH_LONG).show();
    }
} 