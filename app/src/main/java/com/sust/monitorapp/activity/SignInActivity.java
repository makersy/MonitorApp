package com.sust.monitorapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sust.monitorapp.R;
import com.sust.monitorapp.bean.MyResponse;
import com.sust.monitorapp.common.ResponseCode;
import com.sust.monitorapp.util.CheckUtil;
import com.sust.monitorapp.util.JsonUtil;
import com.sust.monitorapp.util.MyHttp;
import com.sust.monitorapp.util.UIUtils;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;

import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Response;

public class SignInActivity extends AppCompatActivity {


    @BindView(R.id.title_back)
    RelativeLayout titleBack;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.et_sign_in_name)
    EditText etSignInName;
    @BindView(R.id.rb_sign_in_sex_male)
    RadioButton rbSignInSexMale;
    @BindView(R.id.rb_sign_in_sex_female)
    RadioButton rbSignInSexFemale;
    @BindView(R.id.et_sign_in_password)
    EditText etSignInPassword;
    @BindView(R.id.et_sign_in_repeat_password)
    EditText etSignInRepeatPassword;
    @BindView(R.id.et_sign_in_email)
    EditText etSignInEmail;
    @BindView(R.id.et_sign_in_tel)
    EditText etSignInTel;
    @BindView(R.id.bt_sign_in)
    Button btSignIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        ButterKnife.bind(this);
        initView();

    }

    /**
     * 初始化页面标题
     */
    private void initView() {
        Intent intent = getIntent();
        String title = intent.getStringExtra("title");
        //此activity复用为注册和添加用户，根据intent传来的信息判断是哪一种，然后修改页面标题和提交按钮
        if (StringUtils.equals(title, "sign_in")) {
            tvTitle.setText("注册");
            btSignIn.setText("注册");
        } else if (StringUtils.equals(title, "add_user")) {
            tvTitle.setText("添加");
            btSignIn.setText("添加用户");
        }
    }


    /**
     * 注册按钮点击事件
     *
     * @param view
     */
    @OnClick(R.id.bt_sign_in)
    public void onSignInButtonClicked(View view) {
        String username = etSignInName.getText().toString();
        String password = etSignInPassword.getText().toString();
        String repeatPwd = etSignInRepeatPassword.getText().toString();
        String email = etSignInEmail.getText().toString();
        String tel = etSignInTel.getText().toString();

        if (!CheckUtil.isUserName(username)) {
            //用户名校验不通过
            inputErrWarning(etSignInName, "请输入正确的用户名");
            return;
        } else if (!rbSignInSexMale.isChecked() && !rbSignInSexFemale.isChecked()) {
            //性别未选择
            Looper.prepare();
            Toast.makeText(SignInActivity.this, "请选择性别", Toast.LENGTH_SHORT).show();
            Looper.loop();
            return;
        } else if (!CheckUtil.isPassword(password)) {
            //密码校验不通过
            inputErrWarning(etSignInPassword, "请输入正确的密码");
            return;
        } else if (!CheckUtil.isPassword(repeatPwd)) {
            //两次密码不一致
            inputErrWarning(etSignInRepeatPassword, "两次输入密码不一致");
            return;
        } else if (!CheckUtil.isEmail(email)) {
            //email校验不通过
            inputErrWarning(etSignInEmail, "请输入正确的email");
            return;
        } else if (!CheckUtil.isTel(tel)) {
            //手机号校验不通过
            inputErrWarning(etSignInTel, "请输入正确的手机号");
            return;
        }

        String sex = (rbSignInSexMale.isChecked()) ? "男" : "女";
        //输入无误
        String params = "username=" + username + "&password=" + password + "&sex=" + sex
                + "&email=" + email + "&tel=" + tel + "&authority=普通用户";

        new Thread(() -> {
            try {
                Response response = MyHttp.post("/api/sign_in", params);
                Looper.prepare();
                if (response.isSuccessful()) {
                    MyResponse myResponse = JsonUtil.jsonToBean(response.body().string(), MyResponse.class);
                    if (StringUtils.equals(myResponse.getStatusCode(), ResponseCode.SUCCESS.getCode())) {
                        Toast.makeText(SignInActivity.this, "提交成功", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(SignInActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }
                } else {
                    Toast.makeText(SignInActivity.this, "网络请求失败", Toast.LENGTH_SHORT).show();
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                Looper.loop();
            }
        }).start();
    }

    //输入框格式不正确提示
    private void inputErrWarning(EditText et, String warning) {
        et.setText("");
        et.requestFocus();
        et.setHint(warning);
        et.setHintTextColor(UIUtils.getColor(R.color.red));
    }


    /**
     * 返回按钮点击事件，结束当前activity，返回上一层
     */
    @OnClick(R.id.title_back)
    public void onViewClicked() {
        finish();
    }
}
