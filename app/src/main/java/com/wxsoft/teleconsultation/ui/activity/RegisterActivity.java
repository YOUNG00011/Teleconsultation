package com.wxsoft.teleconsultation.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.wxsoft.teleconsultation.R;
import com.wxsoft.teleconsultation.ui.base.SupportBaseActivity;
import com.wxsoft.teleconsultation.util.ViewUtil;

import butterknife.OnClick;
import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.options.RegisterOptionalUserInfo;
import cn.jpush.im.api.BasicCallback;

public class RegisterActivity extends SupportBaseActivity {

    public static void launch(Activity from) {
        from.startActivity(new Intent(from, RegisterActivity.class));
    }

    @OnClick(R.id.btn_register)
    void registerClick() {
        register();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_register;
    }

    @Override
    protected void setupViews(Bundle savedInstanceState) {

    }

    private void register() {
        String username = "liqifei";
        String password = "123456";
        String avatar = "https://www.baidu.com/img/bd_logo1.png";
        String nickname = "李平";

        RegisterOptionalUserInfo registerOptionalUserInfo = new RegisterOptionalUserInfo();
        registerOptionalUserInfo.setAvatar(avatar);
        registerOptionalUserInfo.setNickname(nickname);

        JMessageClient.register(username, password, registerOptionalUserInfo, new BasicCallback() {
            @Override
            public void gotResult(int responseCode, String responseMessage) {
                if (responseCode == 0) {
                    JMessageClient.login(username, password, new BasicCallback() {
                        @Override
                        public void gotResult(int responseCode, String responseMessage) {
                            if (responseCode == 0) {
                                HomeActivity.launch(RegisterActivity.this);
                                finish();
                                LoginActivity.instance.finish();
                            } else {
                                ViewUtil.showMessage(responseCode + ":" + responseMessage);
                            }
                        }
                    });
                } else {
                    ViewUtil.showMessage(responseCode + ":" + responseMessage);
                }
            }
        });
    }
}
