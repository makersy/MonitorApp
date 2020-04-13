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
import com.sust.monitorapp.bean.Location;
import com.sust.monitorapp.bean.MyResponse;
import com.sust.monitorapp.bean.User;
import com.sust.monitorapp.util.JsonUtil;
import com.sust.monitorapp.util.NetUtil;

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
    @BindView(R.id.tv_location)
    TextView tvLocation;

    private Device device;
    private Location location;

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
            Looper.prepare();

            try {
                //从后台获取device信息
                String url = "/api/get_dev_info?devId=" + devId;
                Response response = NetUtil.get(url);
                if (response.isSuccessful()) {
                    MyResponse myResponse = JsonUtil.jsonToBean(response.body().string(), MyResponse.class);

                    //解析device实体
                    device = JsonUtil.jsonToBean(myResponse.getData(), Device.class);

                    //根据lac、cellid获取位置信息
                    int lac = device.getLac();
                    int cellid = device.getCellid();
                    String url1 = "lac=" + lac + "&ci=" + cellid;
                    Response response1 = NetUtil.queryAddress(url1);
                    if (response1.isSuccessful()) {
                        //解析位置信息
                        location = JsonUtil.jsonToBean(response1.body().string(), Location.class);
                        if (location.getErrcode() == 10000) {
                            Toast.makeText(DeviceMoreInfoActivity.this, "lac和cell ID参数错误", Toast.LENGTH_SHORT).show();
                        } else if (location.getErrcode() == 10001) {
                            Toast.makeText(DeviceMoreInfoActivity.this, "lac和cell ID无查询结果", Toast.LENGTH_SHORT).show();
                        }
                        device.setAddress(location.getAddress());
                        mHandler.sendEmptyMessage(0);
                    } else {
                        Toast.makeText(DeviceMoreInfoActivity.this, "网络请求失败", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(DeviceMoreInfoActivity.this, "网络请求失败", Toast.LENGTH_SHORT).show();
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                Looper.loop();
            }
        }).start();
    }

    //向页面填充device数据
    Handler mHandler = new Handler(new Handler.Callback() {

        @Override
        public boolean handleMessage(@NonNull Message message) {
            if (message.what == 0) {
                tvMoreinfoDevName.setText(device.getDevName());
                tvMoreinfoOwner.setText(device.getOwner());
                tvDevNote.setText(device.getNote());
                tvLocation.setText(device.getAddress());
                return false;
            }
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

    /**
     * 地图标志点击事件。跳转，在地图中查看具体位置
     */
    @OnClick(R.id.ib_location)
    public void onIbLocationClicked() {
        Intent intent = new Intent(DeviceMoreInfoActivity.this, DeviceLocationActivity.class);
        //传递设备信息
        intent.putExtra("device", device);

        //传递位置信息
        intent.putExtra("location", location);
        intent.putExtra("lat", location.getLat());
        intent.putExtra("lon", location.getLon());
        intent.putExtra("address", location.getAddress());
        startActivity(intent);
    }
}
