package com.sust.monitorapp.fragment;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;
import com.sust.monitorapp.R;
import com.sust.monitorapp.activity.HistoryRaozuTemperatureActivity;
import com.sust.monitorapp.bean.MyResponse;
import com.sust.monitorapp.common.MyApplication;
import com.sust.monitorapp.util.JsonUtil;
import com.sust.monitorapp.util.MyHttp;
import com.sust.monitorapp.util.UIUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Response;


/**
 * A simple {@link Fragment} subclass.
 */
public class TemperMonitorFragment extends Fragment {


    @BindView(R.id.spinner_select_dev)
    Spinner spinnerSelectDev;
    @BindView(R.id.tv_raozu_tem)
    TextView tvRaozuTem;
    @BindView(R.id.ll_to_raozu_history)
    LinearLayout llToRaozuHistory;
    @BindView(R.id.ll_to_raozu_predict)
    LinearLayout llToRaozuPredict;
    @BindView(R.id.tv_youmian_tem)
    TextView tvYoumianTem;
    @BindView(R.id.ll_to_youmian_history)
    LinearLayout llToYoumianHistory;
    @BindView(R.id.ll_to_youmian_predict)
    LinearLayout llToYoumianPredict;
    @BindView(R.id.srl_tem)
    SwipeRefreshLayout srlTem;

    private View view;

    //设备id,name列表
    private List<String> deviceIdAndNames;

    //当前设备id
    private String currentDevId = "";

    public TemperMonitorFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = UIUtils.getView(R.layout.fragment_temper_monitor);
        ButterKnife.bind(this, view);

        initView();
        getDevices();
        initSpinner();

        return view;
    }

    //绑定页面控件
    private void initView() {

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
                    getDevices();
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    //2s后仍在刷新状态，那么算作超时，结束并提示刷新失败信息
                    if (srlTem.isRefreshing()) {
                        getActivity().runOnUiThread(() -> {
                            srlTem.setRefreshing(false);
                            Toast.makeText(view.getContext(), "获取数据失败", Toast.LENGTH_SHORT).show();
                        });
                    }
                }).start();

            }
        });
    }

    //获取id，name拼接字符串中的id
    private String getDevId(@NonNull String s) {
        String[] strs = s.split("，");
        return strs[0];
    }

    //获取spinner的数据，传输给handler处理
    private void getDevices() {
        new Thread(() -> {
            String url = "/api/get_all_devs?userId=" + MyApplication.user.getUserId();
            try {
                Response response = MyHttp.get(url);
                if (response.isSuccessful()) {
                    MyResponse myResponse = JsonUtil.jsonToBean(response.body().string(), MyResponse.class);

                    TreeMap<String, String> deviceMap = JsonUtil.jsonToBean(myResponse.getData(), new TypeToken<TreeMap<String, String>>() {
                    }.getType());

                    //加载数据源
                    deviceIdAndNames = new ArrayList<>();
                    for (Map.Entry<String, String> entry : deviceMap.entrySet()) {
                        deviceIdAndNames.add(entry.getKey() + "，" + entry.getValue());
                    }
                    //向handler发送消息
                    mHandler1.sendEmptyMessage(0);

                } else {
                    Looper.prepare();
                    Toast.makeText(UIUtils.getContext(), "请求失败", Toast.LENGTH_SHORT).show();
                    Looper.loop();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    /**
     * 页面数据初始化
     */
    private void initData() {
        currentDevId = getDevId(deviceIdAndNames.get(0));
        showData(currentDevId);
    }

    /**
     * 加载当前温度
     *
     * @param devId 被加载的设备id
     */
    private void showData(String devId) {
        new Thread(() -> {
            String url = "/api/get_now_data?devId=" + devId;
            try {
                Response response = MyHttp.get(url);
                if (response.isSuccessful()) {
                    MyResponse myResponse = JsonUtil.jsonToBean(response.body().string(), MyResponse.class);
                    Message message = new Message();
                    message.obj = myResponse.getData();
                    mHandler.sendMessage(message);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    //handler 下拉刷新温度数据
    @SuppressLint("HandlerLeak")
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            List<Float> list = JsonUtil.jsonToBean(String.valueOf(msg.obj), new TypeToken<List<Float>>() {
            }.getType());

            //处理数据，更新页面
            tvRaozuTem.setText(String.valueOf(list.get(0)));
            tvYoumianTem.setText(String.valueOf(list.get(1)));
            Toast.makeText(view.getContext(), "刷新成功", Toast.LENGTH_SHORT).show();
            //停止刷新
            srlTem.setRefreshing(false);

        }
    };

    //初始化spinner中的数据：设备id+name。默认加载列表中第一个设备的温度
    private void initSpinner() {
//      关于spinner
//      https://www.jianshu.com/p/01df33502301
        spinnerSelectDev.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                //下拉框字体颜色及大小
                ((TextView) view).setTextColor(UIUtils.getColor(R.color.black));
                ((TextView) view).setTextSize(18);
                currentDevId = getDevId(deviceIdAndNames.get(i));
                showData(currentDevId);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }

    @SuppressLint("HandlerLeak")
    Handler mHandler1 = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0) {
                //为spinner绑定adapter
                ArrayAdapter<String> spinnerAdapter =
                        new ArrayAdapter<String>(view.getContext(), android.R.layout.simple_spinner_item, deviceIdAndNames);
                spinnerAdapter.setDropDownViewResource(R.layout.item_drop_down);
                spinnerSelectDev.setAdapter(spinnerAdapter);
                //默认选中第1个
                spinnerSelectDev.setSelection(0, true);
            }
        }
    };

    @OnClick(R.id.ll_to_raozu_history)
    void onBtRaozuHistoryClicked() {
        //跳转至历史数据页面时，传输要加载数据的设备id
        Intent intent = new Intent(getActivity(), HistoryRaozuTemperatureActivity.class);
        intent.putExtra("devId", currentDevId);
        startActivity(intent);
    }

}
