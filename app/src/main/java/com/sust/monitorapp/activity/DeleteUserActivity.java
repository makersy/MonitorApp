package com.sust.monitorapp.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sust.monitorapp.R;
import com.sust.monitorapp.bean.MyResponse;
import com.sust.monitorapp.bean.User;
import com.sust.monitorapp.common.ResponseCode;
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

/**
 * Created by yhl on 2020/3/4.
 */

public class DeleteUserActivity extends AppCompatActivity {

    @BindView(R.id.title_back)
    RelativeLayout titleBack;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.et_user_id)
    EditText etUserId;
    @BindView(R.id.tv_username)
    TextView tvUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_user);
        ButterKnife.bind(this);
        initView();
    }

    //设置标题
    private void initView() {
        tvTitle.setText("删除用户");
    }

    /**
     * 通过用户 id查询用户存在性，获取用户名。
     */
    @OnClick(R.id.bt_query_user_id)
    public void onBtQueryUserClicked() {
        new Thread(() -> {
            Looper.prepare();
            try {
                Response response = NetUtil.get("/api/get_user_info");
                if (response.isSuccessful()) {
                    MyResponse myResponse = JsonUtil.jsonToBean(response.body().string(), MyResponse.class);
                    if (StringUtils.equals(myResponse.getStatusCode(), ResponseCode.USER_NOT_EXIST.getCode())) {
                        //用户不存在
                        Toast.makeText(this, ResponseCode.USER_NOT_EXIST.getMsg(), Toast.LENGTH_SHORT).show();
                    } else {
                        //获取用户信息，填充到页面
                        User user = JsonUtil.jsonToBean(myResponse.getData(), User.class);
                        Message message = new Message();
                        message.obj = user.getUsername();
                        message.what = 1;
                        handler.sendMessage(message);
                    }
                } else {
                    Toast.makeText(UIUtils.getContext(), "请求失败", Toast.LENGTH_SHORT).show();
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                Looper.loop();
            }
        }).start();
    }

    //页面ui变化不可以在主线程进行，要交给handler
    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message message) {
            if (message.what == 1) {
                //显示用户名
                tvUsername.setText(String.valueOf(message.obj));
            } else if (message.what == 2) {
                //清空用户id栏
                etUserId.setText("");
            }
            return false;
        }
    });

    /**
     * 删除 id 对应用户
     */
    @OnClick(R.id.bt_del_user)
    public void onBtDelUserClicked() {
        String userid = etUserId.getText().toString();
        if (StringUtils.isBlank(userid)) {
            //输入空值
            Toast.makeText(UIUtils.getContext(), "输入id为空", Toast.LENGTH_SHORT).show();
        } else {
            //输入id值非null非空，提交删除
            new Thread(() -> {
                Looper.prepare();
                try {
                    String url = "/api/delete_dev?userId=" + userid;
                    Response response = NetUtil.get(url);
                    if (response.isSuccessful()) {
                        MyResponse myResponse = JsonUtil.jsonToBean(response.body().string(), MyResponse.class);
                        //删除成功
                        if (StringUtils.equals(myResponse.getStatusCode(), ResponseCode.SUCCESS.getCode())) {
                            //清空EditText框
                            handler.sendEmptyMessage(2);
                            Toast.makeText(UIUtils.getContext(), "删除成功", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(UIUtils.getContext(), "删除失败", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(UIUtils.getContext(), "请求失败", Toast.LENGTH_SHORT).show();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    Looper.loop();
                }

            }).start();
        }
    }

    /**
     * 顶部返回按钮点击事件
     */
    @OnClick(R.id.title_back)
    public void onViewClicked() {
        finish();
    }
}
