package com.sust.monitorapp.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.sust.monitorapp.R;
import com.sust.monitorapp.bean.MyResponse;
import com.sust.monitorapp.bean.User;
import com.sust.monitorapp.common.MyApplication;
import com.sust.monitorapp.common.ResponseCode;
import com.sust.monitorapp.service.WrongDevicesService;
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

    private String username;
    private String password;

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
            Looper.prepare();
            try {
                String url = "/api/login?username=" + username + "&password=" + password;
                Response response = NetUtil.get(url);
                if (response.isSuccessful()) {
                    //服务器返回返回信息
                    System.out.println("login 成功");
                    MyResponse myResponse = JsonUtil.jsonToBean(response.body().string(), MyResponse.class);

                    if (StringUtils.equals(myResponse.getStatusCode(), ResponseCode.LOGIN_SUCCESS.getCode())) {
                        //登录成功后通过该id查询用户信息，保存为本地user对象，可以全局使用

//                        Thread.sleep(1500);
                        System.out.println("进入getuserinfo");

                        String url1 = "/api/get_user_info?userId=" + username;
                        Response response1 = NetUtil.get(url1);


                        if (response1.isSuccessful()) {

                            System.out.println("get user info 成功");

                            MyResponse myResponse1 = JsonUtil.jsonToBean(response1.body().string(), MyResponse.class);
                            MyApplication.user = JsonUtil.jsonToBean(myResponse1.getData(), User.class);
                            Log.i("tag", "当前登录用户：" + MyApplication.user.toString());
                            //跳转至登录成功页面
                            handler1.sendEmptyMessage(0);
                        } else {
                            System.out.println("get user info 失败");
                        }

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

    //ui操作，清空EditText，重置登录按钮
    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message message) {
            if (message.what == 0) {
                logInButton.setText("登录");
                ename.setText("");
//                epassword.setText("");
            }
            return false;
        }
    });

    //跳转
    @SuppressLint("HandlerLeak")
    Handler handler1 = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            //如果选择了记住密码/记住用户名选项，保存至本地
            if (cbRememberPassword.isChecked()) {
                saveLocal(true);
            }else if (cbRememberUsername.isChecked()) {
                saveLocal(false);
            } else {
                clearLocal();
            }
            //启动后台异常设备监控service
            Intent intent = new Intent(LoginActivity.this, WrongDevicesService.class);
            startService(intent);

            //跳转到登录成功页面
            Intent intent1 = new Intent(LoginActivity.this, AdminMainActivity.class);
            startActivity(intent1);

            //结束当前Activity
            finish();
        }
    };

    /**
     * 如果 2s内连续点击返回键 2次，则退出当前应用
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
