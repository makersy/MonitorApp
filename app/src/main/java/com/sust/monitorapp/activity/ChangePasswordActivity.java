package com.sust.monitorapp.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.sust.monitorapp.R;

import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ChangePasswordActivity extends AppCompatActivity {


    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.et_before_pwd)
    EditText etBeforePwd;
    @BindView(R.id.et_after_pwd)
    EditText etAfterPwd;
    @BindView(R.id.et_repeat_pwd)
    EditText etRepeatPwd;
    @BindView(R.id.bt_change_pwd)
    Button btChangePwd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.bt_change_pwd)
    public void onViewClicked(View view) {

    }


    @OnClick(R.id.title_back)
    public void onViewClicked() {
        finish();
    }
}
