package com.foodrecognition.app.ui.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.lifecycle.ViewModelProvider;

import com.foodrecognition.app.utils.WeChatUtils;
import com.foodrecognition.app.viewmodel.LoginViewModel;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;

/**
 * 微信登录回调Activity
 */
public class WXEntryActivity extends Activity implements IWXAPIEventHandler {

    private LoginViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider.AndroidViewModelFactory(getApplication())
                .create(LoginViewModel.class);
        
        // 处理微信回调
        WeChatUtils.getWXApi().handleIntent(getIntent(), this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        WeChatUtils.getWXApi().handleIntent(intent, this);
    }

    @Override
    public void onReq(BaseReq baseReq) {
        // 处理微信请求
    }

    @Override
    public void onResp(BaseResp resp) {
        // 处理微信响应
        if (resp instanceof SendAuth.Resp) {
            SendAuth.Resp authResp = (SendAuth.Resp) resp;
            switch (resp.errCode) {
                case BaseResp.ErrCode.ERR_OK:
                    // 授权成功
                    String code = authResp.code;
                    viewModel.handleWeChatLoginResult(code);
                    break;
                case BaseResp.ErrCode.ERR_USER_CANCEL:
                    // 用户取消
                    break;
                case BaseResp.ErrCode.ERR_AUTH_DENIED:
                    // 用户拒绝授权
                    break;
            }
        }
        finish();
    }
} 