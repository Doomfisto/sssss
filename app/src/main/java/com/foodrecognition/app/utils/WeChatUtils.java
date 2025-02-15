package com.foodrecognition.app.utils;

import android.content.Context;

import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

/**
 * 微信登录工具类
 */
public class WeChatUtils {
    private static final String APP_ID = "your_wx_app_id"; // 需要替换为实际的微信AppID
    private static final String APP_SECRET = "your_wx_app_secret"; // 需要替换为实际的微信AppSecret
    
    private static IWXAPI api;

    public static void init(Context context) {
        // 通过WXAPIFactory获取IWXAPI实例
        api = WXAPIFactory.createWXAPI(context, APP_ID, true);
        // 将应用的appId注册到微信
        api.registerApp(APP_ID);
    }

    /**
     * 发起微信登录
     */
    public static boolean sendAuth() {
        if (!api.isWXAppInstalled()) {
            return false;
        }
        
        // 发送授权请求
        SendAuth.Req req = new SendAuth.Req();
        req.scope = "snsapi_userinfo";
        req.state = "food_recognition_login";
        return api.sendReq(req);
    }

    public static IWXAPI getWXApi() {
        return api;
    }
} 