package com.sust.monitorapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.common.collect.HashBiMap;
import com.google.gson.reflect.TypeToken;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.impl.LoadingPopupView;
import com.sust.monitorapp.R;
import com.sust.monitorapp.bean.MyResponse;
import com.sust.monitorapp.common.MyApplication;
import com.sust.monitorapp.util.CheckUtil;
import com.sust.monitorapp.util.JsonUtil;
import com.sust.monitorapp.util.NetUtil;
import com.sust.monitorapp.util.UIUtils;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Response;

public class PredictYoumianTemperatureActivity extends AppCompatActivity {

    @BindView(R.id.title_back)
    RelativeLayout titleBack;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_predict_5min)
    TextView tvPredict5min;
    @BindView(R.id.tv_predict_10min)
    TextView tvPredict10min;

    //当前设备mac
    private String mac;
    //正在加载中弹窗
    private LoadingPopupView popupView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_predict_youmian_temperature);
        ButterKnife.bind(this);

        initView();
    }

    /**
     * 初始化页面
     */
    private void initView() {
        tvTitle.setText("油面温度预测");
        Intent intent = getIntent();
        mac = intent.getStringExtra("devMac");

        getData();
    }

    /**
     * 获取后台预测温度的数据，并显示
     */
    private void getData() {
        popupView = (LoadingPopupView) new XPopup.Builder(PredictYoumianTemperatureActivity.this)
                .asLoading("正在加载中").show();
        new Thread(() -> {
            Looper.prepare();
            try {
                String url = "/api/temperature_prediction?mac=" + mac;
                Response response = NetUtil.get(url);
                if (response.isSuccessful()) {
                    //获取所有用户名信息
                    MyResponse myResponse = JsonUtil.jsonToBean(response.body().string(), MyResponse.class);
                    String[] temperatures = JsonUtil.jsonToBean(myResponse.getData(), new TypeToken<String[]>() {
                    }.getType());

                    System.out.println(temperatures.length);

                    runOnUiThread(() -> {
                        //设置显示温度的文本和颜色
                        if (StringUtils.equals(temperatures[0], "-nan")) {
                            tvPredict5min.setTextColor(UIUtils.getColor(R.color.blue));
                            tvPredict5min.setTextColor(UIUtils.getColor(R.color.blue));
                            tvPredict5min.setText("null");
                            tvPredict10min.setText("null");
                        }else{
                            if (CheckUtil.youmianIsRight(Float.parseFloat(temperatures[0]))) {
                                tvPredict5min.setTextColor(UIUtils.getColor(R.color.green));
                            } else {
                                tvPredict5min.setTextColor(UIUtils.getColor(R.color.red));
                            }
                            if (CheckUtil.youmianIsRight(Float.parseFloat(temperatures[1]))) {
                                tvPredict10min.setTextColor(UIUtils.getColor(R.color.green));
                            } else {
                                tvPredict10min.setTextColor(UIUtils.getColor(R.color.red));
                            }
                            tvPredict5min.setText(temperatures[0] + " ℃");
                            tvPredict10min.setText(temperatures[1] + " ℃");
                        }

                        //停止 正在等待中 弹框
                        popupView.delayDismissWith(500, () -> {
                            Toast.makeText(PredictYoumianTemperatureActivity.this, "加载成功", Toast.LENGTH_SHORT).show();
                        });
                    });

                } else {
                    Toast.makeText(PredictYoumianTemperatureActivity.this, "temperature_prediction网络请求失败", Toast.LENGTH_SHORT).show();
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                Looper.loop();
            }
        }).start();
    }

    /**
     * 顶部返回按钮点击事件
     */
    @OnClick(R.id.title_back)
    public void onViewClicked() {
        finish();
    }
}
