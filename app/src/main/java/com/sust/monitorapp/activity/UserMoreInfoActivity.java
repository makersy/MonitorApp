package com.sust.monitorapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.widget.RelativeLayout;
import android.widget.TextView;
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

public class UserMoreInfoActivity extends AppCompatActivity {

    @BindView(R.id.title_back)
    RelativeLayout titleBack;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_moreinfo_userid)
    TextView tvMoreinfoUserid;
    @BindView(R.id.tv_moreinfo_username)
    TextView tvMoreinfoUsername;
    @BindView(R.id.tv_moreinfo_sex)
    TextView tvMoreinfoSex;
    @BindView(R.id.tv_moreinfo_authority)
    TextView tvMoreinfoAuthority;
    @BindView(R.id.tv_moreinfo_email)
    TextView tvMoreinfoEmail;
    @BindView(R.id.tv_moreinfo_tel)
    TextView tvMoreinfoTel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_more_info);
        ButterKnife.bind(this);

        //获取用户id
        Intent intent = getIntent();
        String userId = intent.getStringExtra("userId");

        initView(userId);
        initData(userId);
    }

    private void initView(String userId) {
        tvTitle.setText("详细用户信息");
        tvMoreinfoUserid.setText(userId);
    }

    //网络访问获取数据
    private void initData(String userId) {
        new Thread(() -> {
            try {
                Response response = MyHttp.get("/api/get_user_info");
                if (response.isSuccessful()) {
                    MyResponse myResponse = JsonUtil.jsonToBean(response.body().string(), MyResponse.class);

                    Message message = new Message();
                    message.obj = myResponse.getData();
                    mHandler.sendMessage(message);
                } else {
                    Looper.prepare();
                    Toast.makeText(UserMoreInfoActivity.this, "网络请求失败", Toast.LENGTH_SHORT).show();
                    Looper.loop();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    //向页面填充数据
    Handler mHandler = new Handler(new Handler.Callback() {

        @Override
        public boolean handleMessage(@NonNull Message message) {
            User user = JsonUtil.jsonToBean(String.valueOf(message.obj), User.class);
            tvMoreinfoUsername.setText(user.getUsername());
            tvMoreinfoSex.setText(user.getSex());
            tvMoreinfoAuthority.setText(user.getAuthority());
            tvMoreinfoEmail.setText(user.getEmail());
            tvMoreinfoTel.setText(user.getTel());
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
