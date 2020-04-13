package com.sust.monitorapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sust.monitorapp.R;
import com.sust.monitorapp.bean.MyResponse;
import com.sust.monitorapp.bean.User;
import com.sust.monitorapp.common.MyApplication;
import com.sust.monitorapp.util.JsonUtil;
import com.sust.monitorapp.util.NetUtil;

import java.io.IOException;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Response;

/**
 * Created by yhl on 2020/3/2.
 */

public class UserOwnInfoActivity extends AppCompatActivity {

    @BindView(R.id.title_back)
    RelativeLayout titleBack;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_moreinfo_userid)
    TextView tvMoreinfoUserid;
    @BindView(R.id.tv_moreinfo_username)
    TextView tvMoreinfoUsername;
    @BindView(R.id.ll_to_modify_username)
    LinearLayout llToModifyUsername;
    @BindView(R.id.tv_moreinfo_sex)
    TextView tvMoreinfoSex;
    @BindView(R.id.ll_to_modify_sex)
    LinearLayout llToModifySex;
    @BindView(R.id.tv_moreinfo_authority)
    TextView tvMoreinfoAuthority;
    @BindView(R.id.tv_moreinfo_email)
    TextView tvMoreinfoEmail;
    @BindView(R.id.ll_to_modify_email)
    LinearLayout llToModifyEmail;
    @BindView(R.id.tv_moreinfo_tel)
    TextView tvMoreinfoTel;

    //显示的是谁的信息
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_own_info);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        userId = intent.getStringExtra("userId");

        initView(userId);
        initData(userId);
    }

    /**
     * onResume()通常是当前的 activity 被暂停了，比如被另一个透明或者 Dialog样式的 Activity覆盖了，
     * 之后 dialog取消，activity回到可交互状态，调用onResume()。
     */
    @Override
    protected void onResume() {
        super.onResume();
        //刷新当前页面数据
        mHandler.sendEmptyMessage(0);
    }

    private void initView(String userId) {
        tvMoreinfoUserid.setText(userId);
        tvTitle.setText("详细信息");
    }

    //网络访问获取数据
    private void initData(String userId) {
        new Thread(() -> {
            try {
                String url = "/api/get_user_info?userId=" + userId;
                Response response = NetUtil.get(url);
                if (response.isSuccessful()) {
                    MyResponse myResponse = JsonUtil.jsonToBean(response.body().string(), MyResponse.class);

                    MyApplication.user = JsonUtil.jsonToBean(String.valueOf(myResponse.getData()), User.class);
                    mHandler.sendEmptyMessage(0);
                } else {
                    Looper.prepare();
                    Toast.makeText(UserOwnInfoActivity.this, "网络请求失败", Toast.LENGTH_SHORT).show();
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
            User user = MyApplication.user;
            tvMoreinfoUsername.setText(user.getUsername());
            tvMoreinfoSex.setText(user.getSex());
            tvMoreinfoAuthority.setText(user.getAuthority());
            tvMoreinfoEmail.setText(user.getEmail());
            tvMoreinfoTel.setText(user.getTel());
            return false;
        }
    });

    @OnClick(R.id.ll_to_modify_username)
    public void toModifyUsername() {
        Intent intent = new Intent(UserOwnInfoActivity.this, ModifyOwnInfoActivity.class);
        String[] info = new String[2];
        info[0] = "username";
        info[1] = String.valueOf(tvMoreinfoUsername.getText());
        intent.putExtra("info", info);
        startActivity(intent);
    }

    @OnClick(R.id.ll_to_modify_sex)
    public void toModifySex() {
        Intent intent = new Intent(UserOwnInfoActivity.this, ModifyOwnInfoActivity.class);
        String[] info = new String[2];
        info[0] = "sex";
        info[1] = String.valueOf(tvMoreinfoSex.getText());
        intent.putExtra("info", info);
        startActivity(intent);
    }

    @OnClick(R.id.ll_to_modify_email)
    public void toModifyEmail() {
        Intent intent = new Intent(UserOwnInfoActivity.this, ModifyOwnInfoActivity.class);
        String[] info = new String[2];
        info[0] = "email";
        info[1] = String.valueOf(tvMoreinfoEmail.getText());
        intent.putExtra("info", info);
        startActivity(intent);
    }

    @OnClick(R.id.ll_to_modify_tel)
    public void toModifyTel() {
        Intent intent = new Intent(UserOwnInfoActivity.this, ModifyOwnInfoActivity.class);
        String[] info = new String[2];
        info[0] = "tel";
        info[1] = String.valueOf(tvMoreinfoTel.getText());
        intent.putExtra("info", info);
        startActivity(intent);
    }

    /**
     * 顶部返回按钮点击事件
     */
    @OnClick(R.id.title_back)
    public void onViewClicked() {
        finish();
    }
}
