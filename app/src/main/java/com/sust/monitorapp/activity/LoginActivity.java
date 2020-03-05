package com.sust.monitorapp.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.sust.monitorapp.R;
import com.sust.monitorapp.bean.MyResponse;
import com.sust.monitorapp.bean.User;
import com.sust.monitorapp.util.JsonUtil;
import com.sust.monitorapp.util.MyHttp;

import java.io.IOException;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity {


    String username;
    String password;

    //绑定ui和对象

    @BindView(R.id.ename)
    EditText ename;
    @BindView(R.id.epassword)
    EditText epassword;
    @BindView(R.id.log_in_button)
    Button logInButton;
    @BindView(R.id.to_sign_in_button)
    Button toSignInButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

    }

    //登录按钮点击事件
    @OnClick(R.id.log_in_button)
    public void onViewClicked() {
        /*
              发送请求，获取response
              获取返回的状态码和data
              判断是否成功
              判断是管理员还是普通操作员
              跳转至对应页面
             */
        username = ename.getText().toString();
        password = epassword.getText().toString();

        Intent intent = new Intent(LoginActivity.this, AdminMainActivity.class);
        startActivity(intent);
        finish();

//        new Thread(() -> {
//            String method = "http://www.mocky.io/v2/5e4e96f02f0000640016a93c";
//            try {
//                Response response = MyHttp.get(method);
//                if (response.isSuccessful()) {
//                    MyResponse myResponse = JsonUtil.jsonToBean(response.body().string(), MyResponse.class);
//                    if ("101".equals(myResponse.getStatusCode())) {
//                        System.out.println("http test success");
//                        System.out.println("输入为： " + username + " " + password);
//                        System.out.println("返回为： " + myResponse.toString());
//
//                        /**
//                         * 测试，弹出框显示一下返回值
//                         */
//                        Message message = new Message();
//                        Bundle bundle = new Bundle();
//                        bundle.putString("data", myResponse.getData());
//                        message.setData(bundle);
//                        handler1.sendMessage(message);
//                    }
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }).start();
    }


    /**
     * 如果2s内连续点击返回键2次，则退出当前应用
     * https://www.jianshu.com/p/7bf30c52d6f3
     */
    private long firstPressedTime = 0;
    @Override
    public void onBackPressed() {
        if (System.currentTimeMillis() - firstPressedTime < 2000) {
            super.onBackPressed();
        } else {
            Toast.makeText(LoginActivity.this, "再按一次退出", Toast.LENGTH_SHORT).show();
            firstPressedTime = System.currentTimeMillis();
        }
    }

    /**
     * 测试，弹出框显示一下返回值
     */
    @SuppressLint("HandlerLeak")
    Handler handler1 = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            Toast.makeText(LoginActivity.this, msg.getData().get("data").toString(), Toast.LENGTH_SHORT).show();
        }
    };

    /**
     * 跳转注册按钮点击事件，跳转到注册页面
     */
    @OnClick(R.id.to_sign_in_button)
    public void onSignInButtonClicked() {
        Intent intent = new Intent(LoginActivity.this, SignInActivity.class);
        intent.putExtra("title", "sign_in");
        startActivity(intent);
    }
}
