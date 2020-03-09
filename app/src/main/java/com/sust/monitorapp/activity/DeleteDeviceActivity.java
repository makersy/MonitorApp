package com.sust.monitorapp.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sust.monitorapp.R;
import com.sust.monitorapp.bean.Device;
import com.sust.monitorapp.bean.MyResponse;
import com.sust.monitorapp.common.ResponseCode;
import com.sust.monitorapp.util.JsonUtil;
import com.sust.monitorapp.util.MyHttp;
import com.sust.monitorapp.util.UIUtils;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Response;

public class DeleteDeviceActivity extends AppCompatActivity {

    @BindView(R.id.title_back)
    RelativeLayout titleBack;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.et_dev_id)
    EditText etDevId;
    @BindView(R.id.ibt_query_devid)
    ImageButton ibtQueryDevid;
    @BindView(R.id.tv_devname)
    TextView tvDevname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_device);
        ButterKnife.bind(this);
    }

    //设置标题
    private void initView() {
        tvTitle.setText("删除设备");
    }

    /**
     * 通过设备 id查询设备存在性，获取设备名。
     */
    @OnClick(R.id.ibt_query_devid)
    public void onBtQueryDevClicked() {
        new Thread(() -> {
            Looper.prepare();
            try {
                Response response = MyHttp.get("/api/get_dev_info");
                if (response.isSuccessful()) {
                    MyResponse myResponse = JsonUtil.jsonToBean(response.body().string(), MyResponse.class);
                    if (StringUtils.equals(myResponse.getStatusCode(), ResponseCode.USER_NOT_EXIST.getCode())) {
                        //设备不存在
                        Toast.makeText(this, "设备不存在", Toast.LENGTH_SHORT).show();
                    } else {
                        //获取设备信息，填充到页面
                        Device device = JsonUtil.jsonToBean(myResponse.getData(), Device.class);
                        Message message = new Message();
                        message.obj = device.getDevName();
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
                //显示设备名
                tvDevname.setText(String.valueOf(message.obj));
            } else if (message.what == 2) {
                //清空输入信息
                etDevId.setText("");
                tvDevname.setText("");
            }
            return false;
        }
    });

    /**
     * 删除 id 对应设备
     */
    @OnClick(R.id.bt_del_dev)
    public void onBtDelDeviceClicked() {
        String devId = etDevId.getText().toString();
        if (StringUtils.isBlank(devId)) {
            //输入空值
            Toast.makeText(UIUtils.getContext(), "输入id为空", Toast.LENGTH_SHORT).show();
        } else {
            //输入id值非null非空，提交删除
            new Thread(() -> {
                Looper.prepare();
                try {
                    String url = "/api/delete_dev?devId=" + devId;
                    Response response = MyHttp.get(url);
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
