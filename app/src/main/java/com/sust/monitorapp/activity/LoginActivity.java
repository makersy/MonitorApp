package com.sust.monitorapp.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.sust.monitorapp.R;
import com.sust.monitorapp.bean.MyResponse;
import com.sust.monitorapp.bean.User;
import com.sust.monitorapp.common.MyApplication;
import com.sust.monitorapp.common.ResponseCode;
import com.sust.monitorapp.util.JsonUtil;
import com.sust.monitorapp.util.NetUtil;
import com.xw.repo.XEditText;

import org.apache.commons.lang3.StringUtils;

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
    XEditText ename;
    @BindView(R.id.epassword)
    XEditText epassword;
    @BindView(R.id.log_in_button)
    Button logInButton;
    @BindView(R.id.to_sign_in_button)
    Button toSignInButton;
    @BindView(R.id.cb_remember_username)
    CheckBox cbRememberUsername;
    @BindView(R.id.cb_remember_password)
    CheckBox cbRememberPassword;
    @BindView(R.id.to_forget_reset_pwd)
    TextView toForgetResetPwd;

    private SharedPreferences sp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        initView();

    }

    //初始化activity
    private void initView() {
        //从本地获取保存的数据
        getLocal();
    }

    /**
     * 记住用户名和密码
     *
     * @param saveAll 是否同时记住二者。false：只记住用户名
     */
    private void saveLocal(boolean saveAll) {
        sp = this.getSharedPreferences("userinfo", MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();

        //保存用户名和密码
        editor.putString("username", username);
        if (saveAll) {
            //保存全部
            //修改记录状态
            editor.putBoolean("remember_username", true);
            editor.putBoolean("remember_password", true);
            //保存数据
            editor.putString("password", password);
        } else {
            //只保存用户名
            editor.putBoolean("remember_username", true);
            editor.putBoolean("remember_password", false);
            editor.putString("password", "");
        }
        editor.apply();
    }

    /**
     * 从本地获取记住的用户名和密码
     */
    private void getLocal() {
        sp = this.getSharedPreferences("userinfo", MODE_PRIVATE);

        ename.setText(sp.getString("username", ""));
        epassword.setText(sp.getString("password", ""));

        if (sp.getBoolean("remember_password", false)) {
            //如果上次选中记住密码
            cbRememberPassword.setChecked(true);
        }
        if (sp.getBoolean("remember_username", false)){
            //如果上次选中记住用户名
            cbRememberUsername.setChecked(true);
        }
    }

    /**
     * 清空本地缓存
     */
    private void clearLocal() {
        sp = this.getSharedPreferences("userinfo", MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.clear();
        editor.apply();
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

        logInButton.setText("正在登录...");

        new Thread(() -> {
            String url = "/api/login?username=" + username + "&password=" + password;
            try {
                Looper.prepare();
                Response response = NetUtil.get(url);
                if (response.isSuccessful()) {
                    //服务器返回返回信息
                    MyResponse myResponse = JsonUtil.jsonToBean(response.body().string(), MyResponse.class);

                    if (StringUtils.equals(myResponse.getStatusCode(), ResponseCode.LOGIN_SUCCESS.getCode())) {
                        //登录成功
                        //保存当前登录用户详细信息，可以全局使用
                        MyApplication.user = JsonUtil.jsonToBean(myResponse.getData(), User.class);
                        //如果选择了记住密码/记住用户名选项，保存至本地
                        if (cbRememberPassword.isChecked()) {
                            saveLocal(true);
                        }else if (cbRememberUsername.isChecked()) {
                            saveLocal(false);
                        } else {
                            clearLocal();
                        }
                        //跳转
                        Intent intent = new Intent(LoginActivity.this, AdminMainActivity.class);
                        startActivity(intent);
                        finish();
                    } else if (ResponseCode.PASSWORD_WRONG.getCode().equals(myResponse.getStatusCode())) {
                        //密码错误
                        Toast.makeText(LoginActivity.this, ResponseCode.PASSWORD_WRONG.getMsg(), Toast.LENGTH_SHORT).show();
                        handler.sendEmptyMessage(0);
                    } else if (ResponseCode.USER_NOT_EXIST.getCode().equals(myResponse.getStatusCode())) {
                        //用户不存在
                        Toast.makeText(LoginActivity.this, ResponseCode.USER_NOT_EXIST.getMsg(), Toast.LENGTH_SHORT).show();
                        handler.sendEmptyMessage(0);
                    } else {
                        //登录失败
                        Toast.makeText(LoginActivity.this, ResponseCode.LOGIN_FAIL.getMsg(), Toast.LENGTH_SHORT).show();
                        handler.sendEmptyMessage(0);
                    }

                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                Looper.loop();
            }
        }).start();
    }

    //ui操作，清空EditText
    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message message) {
            if (message.what == 0) {
                logInButton.setText("登录");
                ename.setText("");
                epassword.setText("");
            }
            return false;
        }
    });

    //跳转
    @SuppressLint("HandlerLeak")
    Handler handler1 = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {

        }
    };

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
     * 跳转注册按钮点击事件，跳转到注册页面
     */
    @OnClick(R.id.to_sign_in_button)
    public void onSignInButtonClicked() {
        Intent intent = new Intent(LoginActivity.this, SignInActivity.class);
        intent.putExtra("title", "sign_in");
        startActivity(intent);
    }
}
