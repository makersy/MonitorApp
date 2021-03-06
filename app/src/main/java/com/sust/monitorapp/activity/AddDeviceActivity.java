package com.sust.monitorapp.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.gson.reflect.TypeToken;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.impl.LoadingPopupView;
import com.sust.monitorapp.R;
import com.sust.monitorapp.bean.Device;
import com.sust.monitorapp.bean.Location;
import com.sust.monitorapp.bean.MyResponse;
import com.sust.monitorapp.bean.User;
import com.sust.monitorapp.common.MyApplication;
import com.sust.monitorapp.common.ResponseCode;
import com.sust.monitorapp.util.JsonUtil;
import com.sust.monitorapp.util.NetUtil;
import com.sust.monitorapp.util.NumUtil;
import com.sust.monitorapp.util.UIUtils;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Response;

public class AddDeviceActivity extends AppCompatActivity {

    @BindView(R.id.title_back)
    RelativeLayout titleBack;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.et_dev_note)
    EditText etDevNote;
    @BindView(R.id.bt_add_dev)
    Button btAddDev;
    @BindView(R.id.spinner_choose_user)
    Spinner spinnerChooseUser;
    @BindView(R.id.spinner_choose_device)
    Spinner spinnerChooseDevice;
    @BindView(R.id.tv_dev_id)
    TextView tvDevId;
    @BindView(R.id.tv_location)
    TextView tvLocation;

    private List<String> userIds;
    private List<String> macList;
    private LoadingPopupView popupView;
    private BiMap<String, String> idAndMacMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_device);
        ButterKnife.bind(this);

        initView();
        initData();
        initSpinner();
    }


    /**
     * 初始化页面标题
     */
    private void initView() {
        tvTitle.setText("添加设备");
    }


    /**
     * 获取所有操作员数据
     */
    private void initData() {
        popupView = (LoadingPopupView) new XPopup.Builder(AddDeviceActivity.this)
                .asLoading("正在加载中").show();
        new Thread(() -> {
            Looper.prepare();
            try {
                String url1 = "/api/get_all_devs?userId=" + MyApplication.user.getUserId();
                Response response1 = NetUtil.get(url1);
                Response response = NetUtil.get("/api/get_all_users");
                if (response.isSuccessful()) {
                    //获取所有用户名信息
                    MyResponse myResponse = JsonUtil.jsonToBean(response.body().string(), MyResponse.class);

                    userIds = JsonUtil.jsonToBean(myResponse.getData(), new TypeToken<ArrayList<String>>() {
                    }.getType());

                    //人员spinner初始化
                    handler1.sendEmptyMessage(0);
                } else {
                    Toast.makeText(AddDeviceActivity.this, "get_all_users网络请求失败", Toast.LENGTH_SHORT).show();
                }

                if (response1.isSuccessful()) {
                    //获取所有设备信息，进行spinner初始化
                    MyResponse myResponse1 = JsonUtil.jsonToBean(response1.body().string(), MyResponse.class);

                    //转双向map，好处是可以通过value找key，需要同时保证二者的唯一性
                    HashMap<String, String> map = JsonUtil.jsonToBean(myResponse1.getData(),
                            new TypeToken<HashMap<String, String>>() {}.getType());
                    idAndMacMap = HashBiMap.create(map);
//                    TreeMap<String, String> idAndMacMap = JsonUtil.jsonToBean(myResponse1.getData()
//                            , new TypeToken<TreeMap<String, String>>() {}.getType());

                    System.out.println(idAndMacMap);
                    //获取mac地址list
                    macList = new ArrayList<>(idAndMacMap.values());
                    //设备spinner初始化
                    handler1.sendEmptyMessage(1);

                } else {
                    Toast.makeText(AddDeviceActivity.this, "get_all_devs网络请求失败", Toast.LENGTH_SHORT).show();
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                Looper.loop();
            }
        }).start();
    }

    //spinner数据初始化
    Handler handler1 = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message message) {
            if (message.what == 0) {
                //操作人员spinner
                ArrayAdapter userAdapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, userIds);
                userAdapter.setDropDownViewResource(R.layout.item_drop_down);
                spinnerChooseUser.setAdapter(userAdapter);

                System.out.println(spinnerChooseUser.getSelectedItem());
            } else if (message.what == 1) {
                //已连接设备spinner
                ArrayAdapter deviceAdapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, macList);
                deviceAdapter.setDropDownViewResource(R.layout.item_drop_down);
                spinnerChooseDevice.setAdapter(deviceAdapter);
                popupView.delayDismissWith(500, () -> {
                    Toast.makeText(AddDeviceActivity.this, "加载成功", Toast.LENGTH_SHORT).show();
                });
            }
            return false;
        }
    });

    /**
     * 为spinner绑定监听器
     */
    private void initSpinner() {
        spinnerChooseUser.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                //下拉框字体颜色及大小
                ((TextView) view).setTextColor(UIUtils.getColor(R.color.black));
                ((TextView) view).setTextSize(14);
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        spinnerChooseDevice.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                //下拉框字体颜色及大小
                ((TextView) view).setTextColor(UIUtils.getColor(R.color.black));
                ((TextView) view).setTextSize(14);

                //根据设备mac获取id，调用后台接口，获取device数据
                String curMac = macList.get(i);
                showDeviceData(idAndMacMap.inverse().get(curMac));
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });


    }

    /**
     * 调用后台接口，获取device数据
     *
     * @param devId 设备id
     */
    private void showDeviceData(String devId) {
        new Thread(() -> {
            String url = "/api/get_dev_info?devId=" + devId;
            try {
                Response response = NetUtil.get(url);
                if (response.isSuccessful()) {
                    MyResponse myResponse = JsonUtil.jsonToBean(response.body().string(), MyResponse.class);
                    Device device = JsonUtil.jsonToBean(myResponse.getData(), Device.class);

                    //获取位置信息
                    String url1 = "lac=" + NumUtil.hexValue(device.getLac()) + "&ci=" + NumUtil.hexValue(device.getCellid());
                    Response response1 = NetUtil.queryAddress(url1);
                    if (response1.isSuccessful()) {
                        //解析并放入device
                        Location location = JsonUtil.jsonToBean(response1.body().string(), Location.class);
                        device.setAddress(location.getAddress());

                        //放入handler，更新页面
                        Message message = new Message();
                        message.obj = device;
                        mHandler.sendMessage(message);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message message) {
            Device device = (Device) message.obj;
            tvDevId.setText(device.getDevId());
            tvLocation.setText(device.getAddress());
            return false;
        }
    });

    /**
     * 添加按钮点击事件
     *
     * @param view
     */
    @OnClick(R.id.bt_add_dev)
    public void onAddDevButtonClicked(View view) {
        String devOwner = spinnerChooseUser.getSelectedItem().toString();
        String devNote = StringUtils.trimToEmpty(etDevNote.getText().toString());

        //获取当前选择的设备id
        String devMac = spinnerChooseDevice.getSelectedItem().toString();
        String devId = idAndMacMap.inverse().get(devMac);

        new Thread(() -> {
            //输入无误，发起网络请求
            String url = "/api/add_dev?" + "&devId=" + devId + "&owner=" + devOwner + "&note=" + devNote;
            try {
                Response response = NetUtil.get(url);
                Looper.prepare();
                if (response.isSuccessful()) {
                    MyResponse myResponse = JsonUtil.jsonToBean(response.body().string(), MyResponse.class);
                    //添加成功，清空所有输入框
                    if (StringUtils.equals(myResponse.getStatusCode(), ResponseCode.SUCCESS.getCode())) {
                        Toast.makeText(AddDeviceActivity.this, "提交成功", Toast.LENGTH_SHORT).show();
                        handler.sendEmptyMessage(0);
                    }
                } else {
                    Toast.makeText(AddDeviceActivity.this, "网络请求失败", Toast.LENGTH_SHORT).show();
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                Looper.loop();
            }
        }).start();
    }

    //输入框格式不正确提示
    private void inputErrWarning(EditText et, String warning) {
        et.setText("");
        et.requestFocus();
        et.setHint(warning);
        et.setHintTextColor(UIUtils.getColor(R.color.red));
    }

    //清空所有输入框
    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message message) {
            finish();
            return false;
        }
    });

    /**
     * 返回按钮点击事件，结束当前activity，返回上一层
     */
    @OnClick(R.id.title_back)
    public void onViewClicked() {
        finish();
    }


}
