package com.sust.monitorapp.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;
import com.sust.monitorapp.R;
import com.sust.monitorapp.adapter.ActionRecordAdapter;
import com.sust.monitorapp.adapter.DeviceAdapter;
import com.sust.monitorapp.bean.Device;
import com.sust.monitorapp.bean.MyResponse;
import com.sust.monitorapp.common.Constants;
import com.sust.monitorapp.common.MyApplication;
import com.sust.monitorapp.common.ResponseCode;
import com.sust.monitorapp.ui.RecyclerViewDivider;
import com.sust.monitorapp.util.JsonUtil;
import com.sust.monitorapp.util.NetUtil;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Response;

public class HistoryActionActivity extends AppCompatActivity {

    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.rv_action_record)
    RecyclerView rvActionRecord;

    //adapter数据源
    private ArrayList<String> data;
    //recyclerview数据适配器
    private ActionRecordAdapter recordAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_action);
        ButterKnife.bind(this);

        initView();
        initData();
    }

    /**
     * 初始化页面
     */
    private void initView() {
        if (MyApplication.user.getAuthority() == Constants.ADMIN) {
            tvTitle.setText("所有用户操作记录");
        } else {
            tvTitle.setText("我的操作记录");
        }

        //控制布局为LinearLayout或者是GridView或者是瀑布流布局
        rvActionRecord.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        //初始化一个空data
        data = new ArrayList<>();

        //为RecyclerView设置Adapter
        recordAdapter = new ActionRecordAdapter(data, HistoryActionActivity.this);
        rvActionRecord.setAdapter(recordAdapter);

        //设置分隔线
        rvActionRecord.addItemDecoration(new RecyclerViewDivider(this,
                LinearLayoutManager.VERTICAL, R.color.grey, 1));
    }

    //网络访问获取数据
    private void initData() {
        new Thread(() -> {
            //根据用户类别，访问不同的历史操作接口
            String url;
            if (MyApplication.user.getAuthority() == Constants.ADMIN) {
                url = "/api/look_all_history?user_name=" + MyApplication.user.getUserId();
            } else {
                url = "/api/look_history?user_name=" + MyApplication.user.getUserId();
            }
            //发起网络请求，获取操作记录数据
            Looper.prepare();
            try {
                Response response = NetUtil.get(url);
                if (response.isSuccessful()) {
                    Log.i("tag", "操作记录页面请求成功");
                    MyResponse myResponse = JsonUtil.jsonToBean(response.body().string(), MyResponse.class);
                    String[] records = myResponse.getData().split("\\n");
                    data.addAll(Arrays.asList(records));

                    //更新页面数据
                    runOnUiThread(()->{
                        recordAdapter.notifyDataSetChanged();
                    });
                } else {
                    Toast.makeText(HistoryActionActivity.this, "网络请求失败", Toast.LENGTH_SHORT).show();
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
