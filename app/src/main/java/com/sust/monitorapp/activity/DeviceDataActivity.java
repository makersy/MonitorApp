package com.sust.monitorapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.impl.LoadingPopupView;
import com.sust.monitorapp.R;
import com.sust.monitorapp.bean.Device;
import com.sust.monitorapp.bean.MyResponse;
import com.sust.monitorapp.common.MyApplication;
import com.sust.monitorapp.util.CheckUtil;
import com.sust.monitorapp.util.JsonUtil;
import com.sust.monitorapp.util.NetUtil;
import com.sust.monitorapp.util.UIUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Response;

public class DeviceDataActivity extends AppCompatActivity {

    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_dev_id)
    TextView tvDevId;
    @BindView(R.id.tv_dev_mac)
    TextView tvDevMac;
    @BindView(R.id.tv_youmian_tem)
    TextView tvYoumianTem;
    @BindView(R.id.ll_to_youmian_history)
    LinearLayout llToYoumianHistory;
    @BindView(R.id.ll_to_youmian_predict)
    LinearLayout llToYoumianPredict;
    @BindView(R.id.srl_tem)
    SwipeRefreshLayout srlTem;

    //intent传来的devId
    private String devId;
    private String devMac;
    private LoadingPopupView popupView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_data);
        ButterKnife.bind(this);

        initView();
        getData();
    }

    //绑定页面控件
    private void initView() {
        tvTitle.setText("详情数据");

        //获取intent中的数据
        devId = getIntent().getStringExtra("devId");

        //设置下拉刷新
        srlTem.setColorSchemeColors(
                UIUtils.getColor(R.color.red),
                UIUtils.getColor(R.color.orange),
                UIUtils.getColor(R.color.dodgerblue));
        srlTem.setSize(SwipeRefreshLayout.DEFAULT);
        srlTem.setProgressViewOffset(true, 0, 200);
        srlTem.setProgressViewEndTarget(true, 100);

        //设置下拉刷新监听器
        srlTem.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Thread(() -> {
                    getData();
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    //2s后仍在刷新状态，那么算作超时，结束并提示刷新失败信息
                    if (srlTem.isRefreshing()) {
                        runOnUiThread(() -> {
                            srlTem.setRefreshing(false);
                            Toast.makeText(DeviceDataActivity.this, "获取数据失败", Toast.LENGTH_SHORT).show();
                        });
                    }
                }).start();

            }
        });
    }

    /**
     * 获取设备信息
     */
    private void getData() {
        new Thread(()->{
            popupView = (LoadingPopupView) new XPopup.Builder(DeviceDataActivity.this)
                    .asLoading("正在加载中").show();
            Looper.prepare();
            String url = "/api/get_dev_info?devId=" + devId;
//            String url2 = "https://www.fastmock.site/mock/f4ca486163cca9e79d548eb9c85770ec/api/get_dev_info?devId=" + devId;
            try {
                Response response = NetUtil.get(url);
//                Response response2 = NetUtil.urlget(url2);
                if (response.isSuccessful()) {
                    MyResponse myResponse = JsonUtil.jsonToBean(response.body().string(), MyResponse.class);
                    Device device = JsonUtil.jsonToBean(myResponse.getData(), Device.class);
                    devMac = device.getDevMac();

                    //获取当前温度
                    String url1 = "/api/get_now_data?devMac=" + devMac;
                    Response response1 = NetUtil.get(url1);
                    if (response1.isSuccessful()) {
                        MyResponse myResponse1 = JsonUtil.jsonToBean(response1.body().string(), MyResponse.class);
                        float tem = Float.parseFloat(myResponse1.getData());
                        device.setNowYoumianTem(tem);
                    }

                    //更新页面
                    runOnUiThread(()->{
                        tvDevId.setText(devId);
                        tvDevMac.setText(device.getDevMac());
                        //温度判断
                        if (CheckUtil.youmianIsRight(device.getNowYoumianTem())) {
                            tvYoumianTem.setTextColor(UIUtils.getColor(R.color.green));
                        } else {
                            tvYoumianTem.setTextColor(UIUtils.getColor(R.color.red));
                        }
                        tvYoumianTem.setText(device.getNowYoumianTem() + " ℃");

                        //停止正在加载中弹框
                        popupView.dismiss();
                        if (srlTem.isRefreshing()) {
                            srlTem.setRefreshing(false);
                        }
                    });

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

    @OnClick(R.id.ll_to_youmian_history)
    void onBtTemHistoryClicked() {
        //跳转至历史数据页面时，传输要加载数据的设备mac
        Intent intent = new Intent(DeviceDataActivity.this, HistoryTemperatureActivity.class);
        intent.putExtra("devMac", devMac);
        startActivity(intent);
    }

    @OnClick(R.id.ll_to_youmian_predict)
    void onLlYoumianPredictClicked() {
        //跳转至预测数据页面时，传输要加载数据的设备mac
        Intent intent = new Intent(DeviceDataActivity.this, PredictYoumianTemperatureActivity.class);
        intent.putExtra("devMac", devMac);
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
