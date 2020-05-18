package com.sust.monitorapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sust.monitorapp.R;
import com.sust.monitorapp.bean.Device;
import com.sust.monitorapp.bean.MyResponse;
import com.sust.monitorapp.common.ResponseCode;
import com.sust.monitorapp.util.CheckUtil;
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

public class ModifyDeviceInfoActivity extends AppCompatActivity {

    @BindView(R.id.title_back)
    RelativeLayout titleBack;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.et_dev_id)
    EditText etDevId;
    @BindView(R.id.et_dev_owner)
    EditText etDevOwner;
    @BindView(R.id.et_dev_note)
    EditText etDevNote;
    @BindView(R.id.bt_modify_dev)
    Button btModifyDev;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_device_info);
        ButterKnife.bind(this);

        initView();
    }

    //此activity复用为查询和修改设备，根据intent传来的信息判断是哪一种，
    // 然后修改页面标题，根据需要决定是否去掉修改按钮
    private void initView() {
        Intent intent = getIntent();
        String info = intent.getStringExtra("title");
        if (StringUtils.equals(info, "query_dev")) {
            tvTitle.setText("设备信息查询");
            btModifyDev.setVisibility(View.GONE);
            //令输入功能失效，仅供查看
            etDevOwner.setEnabled(false);
            etDevNote.setEnabled(false);
        } else if (StringUtils.equals((info), "modify_dev")) {
            tvTitle.setText("设备信息修改");
        }
    }

    /**
     * 查询 id 对应用户的详细信息
     */
    @OnClick(R.id.ibt_query_devid)
    public void onIbtQueryDevIdClicked() {
        new Thread(() -> {
            try {
                //非主线程用Toast要启用Looper
                Looper.prepare();
                String devId = etDevId.getText().toString();
                if (StringUtils.isBlank(devId)) {
                    Toast.makeText(ModifyDeviceInfoActivity.this, "输入不可为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                String url = "/api/get_dev_info?devId=" + devId;
                Response response = NetUtil.get(url);
                if (response.isSuccessful()) {
                    MyResponse myResponse = JsonUtil.jsonToBean(response.body().string(), MyResponse.class);
                    if (StringUtils.equals(myResponse.getStatusCode(), ResponseCode.SUCCESS.getCode())) {
                        Device device = JsonUtil.jsonToBean(myResponse.getData(), Device.class);
                        Message message = new Message();
                        message.obj = device;
                        Toast.makeText(ModifyDeviceInfoActivity.this, "获取数据成功", Toast.LENGTH_SHORT).show();
                        //交给handler装配数据
                        handler.sendMessage(message);
                    }
                } else {
                    Toast.makeText(ModifyDeviceInfoActivity.this, "网络请求失败", Toast.LENGTH_SHORT).show();
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                Looper.loop();
            }
        }).start();
    }

    //ui页面操作，填放查询到的数据
    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message message) {
            Device device = (Device) message.obj;
            etDevOwner.setText(StringUtils.defaultString(device.getOwner(), "default"));
            etDevNote.setText(StringUtils.defaultString(device.getNote(), "default"));
            return false;
        }
    });

    @OnClick(R.id.bt_modify_dev)
    public void onBtModifyDevClicked() {

        String devId = StringUtils.trimToEmpty(etDevId.getText().toString());
        String devOwner = StringUtils.trimToEmpty(etDevOwner.getText().toString());
        String devNote = StringUtils.trimToEmpty(etDevNote.getText().toString());

        //输入文本校验
        if (StringUtils.isBlank(devId)) {
            //设备id校验不通过
            inputErrWarning(etDevId, "请输入正确的设备id");
            return;
        }else if (!CheckUtil.isUserName(devOwner)) {
            //所属操作员校验不通过
            inputErrWarning(etDevOwner, "请输入正确的所属操作员");
            return;
        }

        //输入无误，传输数据
        String params = "devId=" + devId + "&owner=" + devOwner
                + "&note=" + devNote;

        new Thread(() -> {
            try {
                Response response = NetUtil.post("/api/modify_dev_info", params);
                Looper.prepare();
                if (response.isSuccessful()) {
                    MyResponse myResponse = JsonUtil.jsonToBean(response.body().string(), MyResponse.class);
                    if (StringUtils.equals(myResponse.getStatusCode(), ResponseCode.SUCCESS.getCode())) {
                        Toast.makeText(ModifyDeviceInfoActivity.this, "提交成功", Toast.LENGTH_SHORT).show();
                        handler1.sendEmptyMessage(0);
                    }
                } else {
                    Toast.makeText(ModifyDeviceInfoActivity.this, "网络请求失败", Toast.LENGTH_SHORT).show();
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                Looper.loop();
            }
        }).start();
    }

    //ui页面操作，清空输入
    Handler handler1 = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message message) {
            etDevId.setText("");
            etDevId.setHint("");
            etDevOwner.setText("");
            etDevOwner.setHint("");
            etDevNote.setText("");
            etDevNote.setHint("");

            return false;
        }
    });

    //输入框格式不正确提示
    private void inputErrWarning(EditText et, String warning) {
        et.setText("");
        et.requestFocus();
        et.setHint(warning);
        et.setHintTextColor(UIUtils.getColor(R.color.red));
    }

    /**
     * 返回按钮点击事件，结束当前activity，返回上一层
     */
    @OnClick(R.id.title_back)
    public void onViewClicked() {
        finish();
    }
}
