package com.foodrecognition.app.utils;

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import com.alipay.sdk.app.AuthTask;

import java.util.Map;

/**
 * 支付宝登录工具类
 */
public class AlipayUtils {
    private static final String APP_ID = "your_alipay_app_id"; // 需要替换为实际的支付宝AppID
    private static final String PID = "your_alipay_pid"; // 需要替换为实际的支付宝PID
    private static final String TARGET_ID = "your_alipay_target_id"; // 需要替换为实际的支付宝TargetID

    /**
     * 发起支付宝登录
     */
    public static void auth(Activity activity, AuthCallback callback) {
        new Thread(() -> {
            // 构造授权参数
            Map<String, String> authInfo = OrderInfoUtil2_0.buildAuthInfoMap(APP_ID, PID, TARGET_ID);
            String info = OrderInfoUtil2_0.buildOrderParam(authInfo);
            String sign = OrderInfoUtil2_0.getSign(authInfo, "your_private_key"); // 需要替换为实际的私钥
            final String authParam = info + "&" + sign;

            // 调用授权接口
            AuthTask authTask = new AuthTask(activity);
            Map<String, String> result = authTask.authV2(authParam, true);

            new Handler(Looper.getMainLooper()).post(() -> {
                // 解析结果
                String resultStatus = result.get("resultStatus");
                if (TextUtils.equals(resultStatus, "9000")) {
                    // 授权成功
                    callback.onSuccess(result);
                } else {
                    // 授权失败
                    callback.onFailure(resultStatus, result.get("memo"));
                }
            });
        }).start();
    }

    /**
     * 授权回调接口
     */
    public interface AuthCallback {
        void onSuccess(Map<String, String> authResult);
        void onFailure(String resultStatus, String memo);
    }
} 