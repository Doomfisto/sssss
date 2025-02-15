package com.foodrecognition.app.ui.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.foodrecognition.app.R;
import com.google.android.material.radiobutton.MaterialRadioButton;

/**
 * 选择餐次的对话框
 */
public class MealTypeDialog extends DialogFragment {

    public static final String MEAL_TYPE_BREAKFAST = "早餐";
    public static final String MEAL_TYPE_LUNCH = "午餐";
    public static final String MEAL_TYPE_DINNER = "晚餐";
    public static final String MEAL_TYPE_SNACK = "加餐";

    private OnMealTypeSelectedListener listener;

    public interface OnMealTypeSelectedListener {
        void onMealTypeSelected(String mealType);
    }

    public void setOnMealTypeSelectedListener(OnMealTypeSelectedListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_meal_type, null);
        RadioGroup radioGroup = view.findViewById(R.id.radioGroup);

        // 创建选项
        String[] mealTypes = {MEAL_TYPE_BREAKFAST, MEAL_TYPE_LUNCH, MEAL_TYPE_DINNER, MEAL_TYPE_SNACK};
        for (String mealType : mealTypes) {
            MaterialRadioButton radioButton = new MaterialRadioButton(requireContext());
            radioButton.setText(mealType);
            radioButton.setId(View.generateViewId());
            radioGroup.addView(radioButton);
        }

        // 默认选中第一个
        if (radioGroup.getChildCount() > 0) {
            radioGroup.check(radioGroup.getChildAt(0).getId());
        }

        return new AlertDialog.Builder(requireContext())
                .setTitle("选择餐次")
                .setView(view)
                .setPositiveButton("确定", (dialog, which) -> {
                    int checkedId = radioGroup.getCheckedRadioButtonId();
                    MaterialRadioButton selectedButton = view.findViewById(checkedId);
                    if (selectedButton != null && listener != null) {
                        listener.onMealTypeSelected(selectedButton.getText().toString());
                    }
                })
                .setNegativeButton("取消", null)
                .create();
    }
} 