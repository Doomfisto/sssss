package com.foodrecognition.app.utils;

import android.content.Context;

import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.foodrecognition.app.R;

import java.util.Random;

/**
 * 阿里云服务工具类
 * 处理短信验证码等功能
 */
public class AliyunUtils {
    private static final String REGION_ID = "cn-hangzhou";
    private static final String DOMAIN = "dysmsapi.aliyuncs.com";
    private static final String VERSION = "2017-05-25";
    private static final String ACTION = "SendSms";
    private static final String SIGN_NAME = "食物识别APP"; // 需要在阿里云控制台配置签名

    private static IAcsClient client;

    public static void init(Context context) {
        String accessKeyId = context.getString(R.string.aliyun_access_key);
        String accessKeySecret = context.getString(R.string.aliyun_access_secret);
        DefaultProfile profile = DefaultProfile.getProfile(REGION_ID, accessKeyId, accessKeySecret);
        client = new DefaultAcsClient(profile);
    }

    /**
     * 发送验证码
     * @param phone 手机号
     * @return 生成的验证码
     */
    public static String sendVerificationCode(String phone) throws ClientException {
        String code = generateVerificationCode();
        CommonRequest request = new CommonRequest();
        request.setSysMethod(MethodType.POST);
        request.setSysDomain(DOMAIN);
        request.setSysVersion(VERSION);
        request.setSysAction(ACTION);
        request.putQueryParameter("PhoneNumbers", phone);
        request.putQueryParameter("SignName", SIGN_NAME);
        request.putQueryParameter("TemplateCode", "SMS_123456"); // 需要在阿里云控制台配置模板
        request.putQueryParameter("TemplateParam", "{\"code\":\"" + code + "\"}");

        CommonResponse response = client.getCommonResponse(request);
        if (response.getHttpStatus() == 200) {
            return code;
        } else {
            throw new ClientException("短信发送失败");
        }
    }

    /**
     * 生成6位随机验证码
     */
    private static String generateVerificationCode() {
        Random random = new Random();
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            code.append(random.nextInt(10));
        }
        return code.toString();
    }
} 