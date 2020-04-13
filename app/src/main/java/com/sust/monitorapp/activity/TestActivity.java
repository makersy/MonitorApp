package com.sust.monitorapp.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.webkit.MimeTypeMap;
import android.webkit.WebView;
import android.widget.TextView;

import com.sust.monitorapp.R;
import com.sust.monitorapp.bean.Location;
import com.sust.monitorapp.util.JsonUtil;

import org.apache.commons.lang3.text.StrBuilder;

import java.io.IOException;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by yhl on 2020/4/11.
 *
 * 通过cellid查询基站位置，获取经纬度信息。
 *
 * http://www.cellocation.com/api/
 * 本站查询接口免费开放
 * 所有免费接口禁止从移动设备端直接访问，请使用固定IP的服务器转发请求。
 *
 * 将请求发送至固定ip服务器，在服务器上转发该请求到该网站。
 */
public class TestActivity extends AppCompatActivity {
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_response)
    TextView tvResponse;

    String res;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.zz_testlayout);
        ButterKnife.bind(this);

        tvTitle.setText("url转发测试");

        String url = "http://eq.makersy.top/lbs.do?mcc=460&mnc=1&lac=4301&ci=20986";

        new Thread(()->{
            OkHttpClient okHttpClient = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(url)
                    .get()
                    .build();


            try {
                Response response = okHttpClient.newCall(request).execute();
                res = response.body().string();

                System.out.println(res);
                Location location = JsonUtil.jsonToBean(res, Location.class);

                res = location.toString();


                handler.sendEmptyMessage(0);

            } catch (IOException e) {
                e.printStackTrace();
            }

        }).start();


    }

    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message message) {
            tvResponse.setText(res);
            return false;
        }
    });
}
