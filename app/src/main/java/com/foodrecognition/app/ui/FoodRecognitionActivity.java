package com.foodrecognition.app.ui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.foodrecognition.app.R;
import com.foodrecognition.app.databinding.ActivityFoodRecognitionBinding;
import com.foodrecognition.app.model.Food;
import com.foodrecognition.app.viewmodel.FoodRecognitionViewModel;
import com.google.android.material.snackbar.Snackbar;
import com.google.common.util.concurrent.ListenableFuture;

import java.io.File;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * 食物识别页面
 * 支持拍照和从相册选择图片进行识别
 */
public class FoodRecognitionActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_PERMISSIONS = 10;
    private static final int REQUEST_CODE_GALLERY = 20;
    private static final String[] REQUIRED_PERMISSIONS = {
            Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    private ActivityFoodRecognitionBinding binding;
    private FoodRecognitionViewModel viewModel;
    private ImageCapture imageCapture;
    private final Executor cameraExecutor = Executors.newSingleThreadExecutor();
    private int lensFacing = CameraSelector.LENS_FACING_BACK;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFoodRecognitionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(FoodRecognitionViewModel.class);

        setupToolbar();
        setupClickListeners();
        observeViewModel();

        if (checkPermissions()) {
            startCamera();
        }
    }

    private void setupToolbar() {
        setSupportActionBar(binding.toolbar);
        binding.toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void setupClickListeners() {
        // 拍照按钮
        binding.btnCapture.setOnClickListener(v -> takePhoto());

        // 相册按钮
        binding.btnGallery.setOnClickListener(v -> openGallery());

        // 切换相机按钮
        binding.btnSwitch.setOnClickListener(v -> switchCamera());

        // 保存按钮
        binding.btnSave.setOnClickListener(v -> saveResult());
    }

    private void observeViewModel() {
        // 观察识别结果
        viewModel.getRecognitionResult().observe(this, this::showResult);

        // 观察错误信息
        viewModel.getError().observe(this, this::showError);

        // 观察加载状态
        viewModel.isLoading().observe(this, isLoading -> {
            binding.progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            binding.btnCapture.setEnabled(!isLoading);
            binding.btnGallery.setEnabled(!isLoading);
        });

        // 观察保存结果
        viewModel.getSaveSuccess().observe(this, success -> {
            if (success) {
                showMessage("保存成功");
                setResult(RESULT_OK);
                finish();
            }
        });
    }

    private boolean checkPermissions() {
        for (String permission : REQUIRED_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(this, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS);
                return false;
            }
        }
        return true;
    }

    private void startCamera() {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture =
                ProcessCameraProvider.getInstance(this);

        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();

                // 设置预览
                Preview preview = new Preview.Builder().build();
                preview.setSurfaceProvider(binding.viewFinder.getSurfaceProvider());

                // 设置图片捕获
                imageCapture = new ImageCapture.Builder()
                        .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                        .build();

                // 选择相机
                CameraSelector cameraSelector = new CameraSelector.Builder()
                        .requireLensFacing(lensFacing)
                        .build();

                // 解绑所有用例
                cameraProvider.unbindAll();

                // 绑定用例到相机
                cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture);

            } catch (ExecutionException | InterruptedException e) {
                showError("相机启动失败：" + e.getMessage());
            }
        }, ContextCompat.getMainExecutor(this));
    }

    private void takePhoto() {
        if (imageCapture == null) return;

        // 创建临时文件
        File photoFile;
        try {
            photoFile = NetworkUtils.createImageFile(this);
        } catch (IOException e) {
            showError("创建文件失败：" + e.getMessage());
            return;
        }

        // 设置输出选项
        ImageCapture.OutputFileOptions outputOptions =
                new ImageCapture.OutputFileOptions.Builder(photoFile).build();

        // 拍照
        imageCapture.takePicture(outputOptions, cameraExecutor,
                new ImageCapture.OnImageSavedCallback() {
                    @Override
                    public void onImageSaved(@NonNull ImageCapture.OutputFileResults output) {
                        runOnUiThread(() -> viewModel.recognizeFood(photoFile));
                    }

                    @Override
                    public void onError(@NonNull ImageCaptureException exception) {
                        runOnUiThread(() -> showError("拍照失败：" + exception.getMessage()));
                    }
                });
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_CODE_GALLERY);
    }

    private void switchCamera() {
        lensFacing = (lensFacing == CameraSelector.LENS_FACING_BACK) ?
                CameraSelector.LENS_FACING_FRONT : CameraSelector.LENS_FACING_BACK;
        startCamera();
    }

    private void showResult(Food food) {
        binding.resultCard.setVisibility(View.VISIBLE);
        binding.tvFoodName.setText(food.getName());
        binding.tvCalories.setText(String.format("%.0f 卡路里", food.getCalories()));
        binding.tvProtein.setText(String.format("蛋白质: %.1fg", food.getProtein()));
        binding.tvCarbs.setText(String.format("碳水: %.1fg", food.getCarbs()));
        binding.tvFat.setText(String.format("脂肪: %.1fg", food.getFat()));
    }

    private void saveResult() {
        MealTypeDialog dialog = new MealTypeDialog();
        dialog.setOnMealTypeSelectedListener(mealType -> {
            viewModel.saveResult(mealType);
        });
        dialog.show(getSupportFragmentManager(), "meal_type_dialog");
    }

    private void showError(String message) {
        Snackbar.make(binding.getRoot(), message, Snackbar.LENGTH_LONG).show();
    }

    private void showMessage(String message) {
        Snackbar.make(binding.getRoot(), message, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                         @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (checkPermissions()) {
                startCamera();
            } else {
                showError(getString(R.string.permission_required));
                finish();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_GALLERY && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            if (imageUri != null) {
                viewModel.handleImageUri(imageUri);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cameraExecutor.shutdown();
    }
} 