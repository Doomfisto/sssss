package com.foodrecognition.app.viewmodel;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.aliyuncs.exceptions.ClientException;
import com.foodrecognition.app.R;
import com.foodrecognition.app.utils.AlipayUtils;
import com.foodrecognition.app.utils.AliyunUtils;
import com.foodrecognition.app.utils.PreferenceUtils;
import com.foodrecognition.app.utils.WeChatUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * 登录ViewModel
 * 处理登录相关的业务逻辑
 */
public class LoginViewModel extends AndroidViewModel {

    private final MutableLiveData<Boolean> loginResult = new MutableLiveData<>();
    private final MutableLiveData<String> error = new MutableLiveData<>();
    private final MutableLiveData<Boolean> codeSendResult = new MutableLiveData<>();
    
    private final Map<String, String> verificationCodes = new HashMap<>();
    private final Context context;

    public LoginViewModel(@NonNull Application application) {
        super(application);
        this.context = application;
        // 初始化各种服务
        AliyunUtils.init(application);
        WeChatUtils.init(application);
    }

    /**
     * 请求验证码
     */
    public void requestVerificationCode(String phone) {
        new Thread(() -> {
            try {
                String code = AliyunUtils.sendVerificationCode(phone);
                verificationCodes.put(phone, code);
                codeSendResult.postValue(true);
            } catch (ClientException e) {
                error.postValue(context.getString(R.string.verification_code_send_failed));
                codeSendResult.postValue(false);
            }
        }).start();
    }

    /**
     * 手机号登录
     */
    public void loginWithPhone(String phone, String code) {
        String savedCode = verificationCodes.get(phone);
        if (savedCode != null && savedCode.equals(code)) {
            saveLoginState(phone, "phone");
            loginResult.setValue(true);
        } else {
            error.setValue(context.getString(R.string.verification_code_invalid));
        }
    }

    /**
     * 微信登录
     */
    public void loginWithWechat() {
        if (!WeChatUtils.sendAuth()) {
            error.setValue(context.getString(R.string.wechat_not_installed));
            return;
        }
        // 微信登录的回调将在WXEntryActivity中处理
    }

    /**
     * 支付宝登录
     */
    public void loginWithAlipay() {
        AlipayUtils.auth((android.app.Activity) context, new AlipayUtils.AuthCallback() {
            @Override
            public void onSuccess(Map<String, String> authResult) {
                String userId = authResult.get("user_id");
                saveLoginState(userId, "alipay");
                loginResult.postValue(true);
            }

            @Override
            public void onFailure(String resultStatus, String memo) {
                error.postValue(memo);
            }
        });
    }

    /**
     * 保存登录状态
     */
    private void saveLoginState(String userId, String loginType) {
        PreferenceUtils.setLoggedIn(context, true);
        PreferenceUtils.saveUserPhone(context, userId);
        PreferenceUtils.saveLoginType(context, loginType);
    }

    public LiveData<Boolean> getLoginResult() {
        return loginResult;
    }

    public LiveData<String> getError() {
        return error;
    }

    public LiveData<Boolean> getCodeSendResult() {
        return codeSendResult;
    }

    /**
     * 处理微信登录回调
     */
    public void handleWeChatLoginResult(String code) {
        // TODO: 使用code换取access_token和用户信息
        // 这里需要调用微信的API获取用户信息
        saveLoginState("wechat_user_id", "wechat");
        loginResult.postValue(true);
    }
} 