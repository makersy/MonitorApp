package com.sust.monitorapp.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Window;
import android.view.WindowManager;

import com.sust.monitorapp.R;

import androidx.annotation.NonNull;

/**
 * Created by yhl on 2020/2/10.
 */

public class WelcomeActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //去掉窗口标题
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //隐藏顶部状态栏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_welcome);

        //延时3s跳转至登录页面
        handler.sendEmptyMessageDelayed(0, 2000);
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            jump2login();
            super.handleMessage(msg);
        }
    };

    /**
     * 跳转至登录页面
     */
    private void jump2login() {
        Intent intent = new Intent(WelcomeActivity.this, LoginActivity.class);
        startActivity(intent);

        /**
         * 不使用finish()：从activity1中启动activity2，然后在activity2 启动activity3，这时按下返回键
         * 程序就返回到了activity2，再按下返回键 就返回到activity1；
         * 使用finish()：从activity1中启动activity2,在activity2调用finish()，然后在activity2启动activity3，
         * 这时按下返回键,程序就直接返回了activity1
         */
        finish();
    }

}
