package com.sust.monitorapp.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sust.monitorapp.R;

import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SignInActivity extends AppCompatActivity {


    @BindView(R.id.sign_in_name)
    EditText signInName;
    @BindView(R.id.sign_in_password)
    EditText signInPassword;
    @BindView(R.id.bt_sign_in)
    Button signInButton;
    @BindView(R.id.title_back)
    RelativeLayout titleBack;
    @BindView(R.id.tv_title)
    TextView tvTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        ButterKnife.bind(this);
        initTitle();

    }

    /**
     * 初始化页面标题
     */
    private void initTitle() {
        tvTitle.setText("注册");
    }


    /**
     * 注册按钮点击事件
     *
     * @param view
     */
    @OnClick(R.id.bt_sign_in)
    public void onSignInButtonClicked(View view) {
    }

    /**
     * 返回按钮点击事件，结束当前activity，返回上一层
     */
    @OnClick(R.id.title_back)
    public void onViewClicked() {
        finish();
    }
}
