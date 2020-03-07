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
import com.sust.monitorapp.bean.Device;
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

public class DeviceMoreInfoActivity extends AppCompatActivity {

    @BindView(R.id.title_back)
    RelativeLayout titleBack;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_moreinfo_dev_id)
    TextView tvMoreinfoDevId;
    @BindView(R.id.tv_moreinfo_dev_name)
    TextView tvMoreinfoDevName;
    @BindView(R.id.tv_moreinfo_owner)
    TextView tvMoreinfoOwner;
    @BindView(R.id.tv_dev_note)
    TextView tvDevNote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_more_info);
        ButterKnife.bind(this);

        //获取用户id
        Intent intent = getIntent();
        String devId = intent.getStringExtra("devId");

        initView(devId);
        initData(devId);
    }

    private void initView(String devId) {
        tvTitle.setText("详细设备信息");
        tvMoreinfoDevId.setText(devId);
    }

    //网络访问获取数据
    private void initData(String devId) {
        new Thread(() -> {
            try {
                String url = "/api/get_dev_info?devId=" + devId;
                Response response = MyHttp.get(url);
                if (response.isSuccessful()) {
                    MyResponse myResponse = JsonUtil.jsonToBean(response.body().string(), MyResponse.class);

                    Message message = new Message();
                    message.obj = myResponse.getData();
                    mHandler.sendMessage(message);
                } else {
                    Looper.prepare();
                    Toast.makeText(DeviceMoreInfoActivity.this, "网络请求失败", Toast.LENGTH_SHORT).show();
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
            Device device = JsonUtil.jsonToBean(String.valueOf(message.obj), Device.class);
            tvMoreinfoDevName.setText(device.getDevName());
            tvMoreinfoOwner.setText(device.getOwner());
            tvDevNote.setText(device.getNote());
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
