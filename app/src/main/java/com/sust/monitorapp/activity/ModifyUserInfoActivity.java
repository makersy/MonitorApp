package com.sust.monitorapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import com.sust.monitorapp.R;
import com.sust.monitorapp.bean.MyResponse;
import com.sust.monitorapp.bean.User;
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

public class ModifyUserInfoActivity extends AppCompatActivity {

    @BindView(R.id.title_back)
    RelativeLayout titleBack;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.et_user_id)
    EditText etUserId;
    @BindView(R.id.rb_sign_in_sex_male)
    RadioButton rbSignInSexMale;
    @BindView(R.id.rb_sign_in_sex_female)
    RadioButton rbSignInSexFemale;
    @BindView(R.id.et_password)
    EditText etPassword;
    @BindView(R.id.et_email)
    EditText etEmail;
    @BindView(R.id.bt_modify_user)
    Button btModifyUser;
    @BindView(R.id.rg_sex)
    RadioGroup rgSex;
    @BindView(R.id.tv_pwd_info)
    TextView tvPwdInfo;
    @BindView(R.id.til_pwd)
    TextInputLayout tilPwd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_user_info);
        ButterKnife.bind(this);

        initView();
    }

    //此activity复用为查询和修改用户，根据intent传来的信息判断是哪一种，
    // 然后修改页面标题，根据需要决定是否去掉修改按钮
    private void initView() {
        Intent intent = getIntent();
        String info = intent.getStringExtra("title");
        if (StringUtils.equals(info, "query_user")) {
            tvTitle.setText("用户信息查询");
            btModifyUser.setVisibility(View.GONE);
            //令输入功能失效，仅供查看
            etEmail.setEnabled(false);
            tvPwdInfo.setVisibility(View.GONE);
            tilPwd.setVisibility(View.GONE);
//            etPassword.setEnabled(false);
            rbSignInSexFemale.setClickable(false);
            rbSignInSexMale.setClickable(false);
        } else if (StringUtils.equals((info), "modify_user")) {
            tvTitle.setText("用户信息修改");
        }
    }

    /**
     * 查询 id 对应用户的详细信息
     */
    @OnClick(R.id.ibt_query_userid)
    public void onIbtQueryUserIdClicked() {
        new Thread(() -> {
            try {
                //非主线程用Toast要启用Looper
                Looper.prepare();
                String userId = etUserId.getText().toString();
                if (StringUtils.isBlank(userId)) {
                    Toast.makeText(ModifyUserInfoActivity.this, "输入不可为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                String url = "/api/get_user_info?userId=" + userId;
                Response response = NetUtil.get(url);
                if (response.isSuccessful()) {
                    MyResponse myResponse = JsonUtil.jsonToBean(response.body().string(), MyResponse.class);
                    if (StringUtils.equals(myResponse.getStatusCode(), ResponseCode.SUCCESS.getCode())) {
                        User user = JsonUtil.jsonToBean(myResponse.getData(), User.class);
                        Message message = new Message();
                        message.obj = user;
                        Toast.makeText(ModifyUserInfoActivity.this, "获取数据成功", Toast.LENGTH_SHORT).show();
                        //交给handler装配数据
                        handler.sendMessage(message);
                    }
                } else {
                    Toast.makeText(ModifyUserInfoActivity.this, "网络请求失败", Toast.LENGTH_SHORT).show();
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                Looper.loop();
            }
        }).start();
    }

    //ui页面操作，填放查询到的数据
    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message message) {
            User user = (User) message.obj;
            etPassword.setText(StringUtils.defaultString(user.getPassword(), ""));
            etEmail.setText(StringUtils.defaultString(user.getEmail(), "default"));
            if (StringUtils.equals(user.getSex(), "男")) {
                rbSignInSexMale.setChecked(true);
            } else {
                rbSignInSexFemale.setChecked(true);
            }
            return false;
        }
    });

    @OnClick(R.id.bt_modify_user)
    public void onBtModifyUserClicked() {

        String userId = StringUtils.trim(etUserId.getText().toString());
        String password = StringUtils.trim(etPassword.getText().toString());
        String email = StringUtils.trim(etEmail.getText().toString());

        //输入文本校验
        if (StringUtils.isBlank(userId)) {
            //用户名校验不通过
            inputErrWarning(etUserId, "请输入正确的id");
            return;
        } else if (!rbSignInSexMale.isChecked() && !rbSignInSexFemale.isChecked()) {
            //性别未选择
            Looper.prepare();
            Toast.makeText(ModifyUserInfoActivity.this, "请选择性别", Toast.LENGTH_SHORT).show();
            Looper.loop();
            return;
        } else if (!CheckUtil.isPassword(password)) {
            //密码校验不通过
            inputErrWarning(etPassword, "请输入正确的密码");
            return;
        } else if (!CheckUtil.isEmail(email)) {
            //email校验不通过
            inputErrWarning(etEmail, "请输入正确的email");
            return;
        }
        String sex = (rbSignInSexMale.isChecked()) ? "男" : "女";


        new Thread(() -> {
            try {
                //输入无误，传输数据
                String url = "/api/sign_in?userId=" + userId  + "&password=" + password
                        + "&sex=" + sex + "&email=" + email;
                Response response = NetUtil.get(url);
                Looper.prepare();
                if (response.isSuccessful()) {
                    MyResponse myResponse = JsonUtil.jsonToBean(response.body().string(), MyResponse.class);
                    if (StringUtils.equals(myResponse.getStatusCode(), ResponseCode.SUCCESS.getCode())) {
                        Toast.makeText(ModifyUserInfoActivity.this, "提交成功", Toast.LENGTH_SHORT).show();
                        handler1.sendEmptyMessage(0);
                    }
                } else {
                    Toast.makeText(ModifyUserInfoActivity.this, "网络请求失败", Toast.LENGTH_SHORT).show();
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                Looper.loop();
            }
        }).start();
    }

    //ui页面操作，清空所有输入数据以及错误提示
    Handler handler1 = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message message) {
            etPassword.setText("");
            etPassword.setHint("");
            etEmail.setText("");
            etEmail.setHint("");
            //清除radioGroup选择状态
            rgSex.clearCheck();
            return false;
        }
    });

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
