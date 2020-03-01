package com.sust.monitorapp.fragment;


import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;
import com.sust.monitorapp.R;
import com.sust.monitorapp.bean.MyResponse;
import com.sust.monitorapp.util.JsonUtil;
import com.sust.monitorapp.util.MyHttp;
import com.sust.monitorapp.util.UIUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import okhttp3.Response;



/**
 * A simple {@link Fragment} subclass.
 */
public class TemperMonitorFragment extends Fragment {


    private Spinner spinnerSelectDev;
    private TextView tvRaozuTem;
    private Button btRaozuHistory;
    private Button btRaozuPredict;
    private TextView tvYoumianTem;
    private Button btYoumianHistory;
    private Button btYoumianPredict;
    private SwipeRefreshLayout srlTem;
    private View view;

    public TemperMonitorFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = UIUtils.getView(R.layout.fragment_temper_monitor);

        initView();
        initSpinner();

        return view;
    }

    private void initView() {
        srlTem = view.findViewById(R.id.srl_tem);
        spinnerSelectDev = view.findViewById(R.id.spinner_select_dev);
        tvRaozuTem = view.findViewById(R.id.tv_raozu_tem);
        btRaozuHistory = view.findViewById(R.id.bt_raozu_history);
        btRaozuPredict = view.findViewById(R.id.bt_raozu_predict);
        tvYoumianTem = view.findViewById(R.id.tv_youmian_tem);
        btYoumianHistory = view.findViewById(R.id.bt_youmian_history);
        btYoumianPredict = view.findViewById(R.id.bt_youmian_predict);

        //设置下拉刷新
        srlTem.setColorSchemeColors(
                UIUtils.getColor(R.color.beige),
                UIUtils.getColor(R.color.green),
                UIUtils.getColor(R.color.blue));
        srlTem.setSize(SwipeRefreshLayout.DEFAULT);
        srlTem.setProgressViewOffset(true, 0, 200);
        srlTem.setProgressViewEndTarget(true, 100);


        srlTem.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Thread(() -> {
                    showData();
                }).start();
            }
        });
    }

    /**
     * 页面数据初始化
     */
//    private void initData() {
//        showData();
//    }


    private void showData() {
        new Thread(()->{
            String url = "/api/get_now_data?devId=001";
            try {
                Response response = MyHttp.get(url);
                if (response.isSuccessful()) {
                    MyResponse myResponse = JsonUtil.jsonToBean(response.body().string(), MyResponse.class);

//                Bundle bundle = new Bundle();
//                bundle.putString("userinfo", myResponse.getData());
                    Message message = new Message();
                    message.obj = myResponse.getData();
                    mHandler.sendMessage(message);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();

    }
    //handler
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            List<Float> list = JsonUtil.jsonToBean(String.valueOf(msg.obj), new TypeToken<List<Float>>(){}.getType());
            //处理数据，更新页面

            tvRaozuTem.setText(String.valueOf(list.get(0)));
            tvYoumianTem.setText(String.valueOf(list.get(1)));
            Toast.makeText(view.getContext(), "刷新成功", Toast.LENGTH_SHORT).show();
            srlTem.setRefreshing(false);

        }
    };

    private void initSpinner() {
//      关于spinner
//      https://www.jianshu.com/p/01df33502301
        spinnerSelectDev.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                ((TextView) adapterView.getChildAt(0)).setTextSize(20);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });



        //获取spinner数据，传输给handler处理
        new Thread(()->{
            try {
                Response response = MyHttp.get("/api/get_all_devs");
                if (response.isSuccessful()) {
                    MyResponse myResponse = JsonUtil.jsonToBean(response.body().string(), MyResponse.class);
                    Message message = new Message();
                    message.obj = myResponse.getData();
                    mHandler1.sendMessage(message);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    Handler mHandler1 = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);

            HashMap<String, String> deviceMap = JsonUtil.jsonToBean(String.valueOf(msg.obj), new TypeToken<HashMap<String, String>>() {
            }.getType());

            List<String> deviceNames = new ArrayList<>();

            for (Map.Entry<String, String> entry : deviceMap.entrySet()) {
                deviceNames.add(entry.getValue());
            }

            ArrayAdapter<String> spinnerAdapter =
                    new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, deviceNames);
            spinnerAdapter.setDropDownViewResource(R.layout.item_drop_down);
            spinnerSelectDev.setAdapter(spinnerAdapter);
        }
    };


}
