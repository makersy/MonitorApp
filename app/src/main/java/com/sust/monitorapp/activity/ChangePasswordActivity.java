package com.sust.monitorapp.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.sust.monitorapp.R;
import com.sust.monitorapp.bean.MyResponse;
import com.sust.monitorapp.common.MyApplication;
import com.sust.monitorapp.common.ResponseCode;
import com.sust.monitorapp.util.CheckUtil;
import com.sust.monitorapp.util.JsonUtil;
import com.sust.monitorapp.util.NetUtil;
import com.sust.monitorapp.util.UIUtils;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Response;

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
        initView();
    }

    /**
     * 初始化页面标题
     */
    private void initView() {
        tvTitle.setText("修改密码");
    }

    /**
     * 修改密码按钮点击事件
     */
    @OnClick(R.id.bt_change_pwd)
    public void onBtModifyPasswordClicked() {
        //清空所有错误提示
        clearHints();
        //判断密码格式，判断重复密码是否一样
        String pwdBefore = etBeforePwd.getText().toString();
        String pwdAfter = etAfterPwd.getText().toString();
        String pwdRepeat = etRepeatPwd.getText().toString();
        if (!CheckUtil.isPassword(pwdBefore)) {
            //原密码格式错误
            inputErrWarning(etAfterPwd, "请输入格式正确的密码");
            return;
        } else if (!CheckUtil.isPassword(pwdAfter)) {
            //新密码格式错误
            inputErrWarning(etAfterPwd, "请输入格式正确的密码");
            return;
        } else if (!StringUtils.equals(pwdAfter, pwdRepeat)) {
            //两次输入不一致
            inputErrWarning(etRepeatPwd, "两次输入不一致");
            return;
        }
        //校验完成，连接服务端
        new Thread(() -> {
            try {
                String url = "/api/modify_password?userId=" + MyApplication.user.getUserId();
                Response response = NetUtil.get(url);
                Looper.prepare();
                if (response.isSuccessful()) {
                    MyResponse myResponse = JsonUtil.jsonToBean(response.body().string(), MyResponse.class);
                    if (StringUtils.equals(myResponse.getStatusCode(), ResponseCode.SUCCESS.getCode())) {
                        Toast.makeText(ChangePasswordActivity.this, "提交成功", Toast.LENGTH_SHORT).show();
                        //修改成功，清空输入框
                        handler.sendEmptyMessage(0);
                    }
                } else {
                    Toast.makeText(ChangePasswordActivity.this, "网络请求失败", Toast.LENGTH_SHORT).show();
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                Looper.loop();
            }
        }).start();
    }

    //清空所有错误提示
    private void clearHints() {
        etBeforePwd.setHint("");
        etAfterPwd.setHint("");
        etRepeatPwd.setHint("");
    }

    //输入框格式不正确提示
    private void inputErrWarning(EditText et, String warning) {
        et.setText("");
        et.requestFocus();
        et.setHint(warning);
        et.setHintTextColor(UIUtils.getColor(R.color.red));
    }

    //清空输入框
    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message message) {
            etBeforePwd.setText("");
            etAfterPwd.setText("");
            etRepeatPwd.setText("");
            return false;
        }
    });
    /**
     * 顶部返回按钮点击事件
     */
    @OnClick(R.id.title_back)
    public void onViewClicked() {
        finish();
    }
}
