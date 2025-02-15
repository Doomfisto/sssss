package com.foodrecognition.app.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.foodrecognition.app.R;
import com.foodrecognition.app.databinding.ActivityLoginBinding;
import com.foodrecognition.app.viewmodel.LoginViewModel;
import com.google.android.material.snackbar.Snackbar;

/**
 * 登录页面
 * 支持手机号、微信和支付宝登录
 */
public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private LoginViewModel viewModel;
    private CountDownTimer countDownTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(LoginViewModel.class);

        setupClickListeners();
        observeViewModel();
    }

    private void setupClickListeners() {
        // 获取验证码按钮
        binding.btnGetCode.setOnClickListener(v -> {
            String phone = binding.etPhone.getText().toString().trim();
            if (isValidPhone(phone)) {
                viewModel.requestVerificationCode(phone);
                startCountDown();
            } else {
                showMessage(getString(R.string.phone_number_invalid));
            }
        });

        // 手机号登录按钮
        binding.btnPhoneLogin.setOnClickListener(v -> {
            String phone = binding.etPhone.getText().toString().trim();
            String code = binding.etCode.getText().toString().trim();
            if (isValidPhone(phone) && isValidCode(code)) {
                viewModel.loginWithPhone(phone, code);
            } else {
                showMessage(getString(R.string.verification_code_invalid));
            }
        });

        // 微信登录按钮
        binding.btnWechatLogin.setOnClickListener(v -> {
            viewModel.loginWithWechat();
        });

        // 支付宝登录按钮
        binding.btnAlipayLogin.setOnClickListener(v -> {
            viewModel.loginWithAlipay();
        });
    }

    private void observeViewModel() {
        // 观察登录状态
        viewModel.getLoginResult().observe(this, success -> {
            if (success) {
                startActivity(new Intent(this, MainActivity.class));
                finish();
            }
        });

        // 观察错误信息
        viewModel.getError().observe(this, error -> {
            if (error != null) {
                showMessage(error);
            }
        });

        // 观察验证码发送状态
        viewModel.getCodeSendResult().observe(this, success -> {
            if (success) {
                showMessage(getString(R.string.verification_code_sent));
            } else {
                showMessage(getString(R.string.verification_code_send_failed));
                stopCountDown();
            }
        });
    }

    private void startCountDown() {
        binding.btnGetCode.setEnabled(false);
        countDownTimer = new CountDownTimer(60000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                binding.btnGetCode.setText(String.format("%ds", millisUntilFinished / 1000));
            }

            @Override
            public void onFinish() {
                binding.btnGetCode.setEnabled(true);
                binding.btnGetCode.setText(R.string.get_code);
            }
        }.start();
    }

    private void stopCountDown() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
            binding.btnGetCode.setEnabled(true);
            binding.btnGetCode.setText(R.string.get_code);
        }
    }

    private boolean isValidPhone(String phone) {
        return !TextUtils.isEmpty(phone) && phone.length() == 11;
    }

    private boolean isValidCode(String code) {
        return !TextUtils.isEmpty(code) && code.length() == 6;
    }

    private void showMessage(String message) {
        Snackbar.make(binding.getRoot(), message, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopCountDown();
    }
} 