package com.sust.monitorapp.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;
import com.sust.monitorapp.R;
import com.sust.monitorapp.bean.MyResponse;
import com.sust.monitorapp.bean.User;
import com.sust.monitorapp.common.ResponseCode;
import com.sust.monitorapp.util.JsonUtil;
import com.sust.monitorapp.util.NetUtil;
import com.sust.monitorapp.util.UIUtils;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
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
    @BindView(R.id.et_devname)
    EditText etDevname;
    @BindView(R.id.et_dev_note)
    EditText etDevNote;
    @BindView(R.id.bt_add_dev)
    Button btAddDev;
    @BindView(R.id.spinner_choose_user)
    Spinner spinnerChooseUser;

    private List<User> data = new ArrayList<>();
    private String[] usernames;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_device);
        ButterKnife.bind(this);
        initView();
        initData();
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
        new Thread(() -> {
            Looper.prepare();
            try {
                Response response = NetUtil.get("/api/get_all_users");
                if (response.isSuccessful()) {
                    //获取所有用户名信息
                    MyResponse myResponse = JsonUtil.jsonToBean(response.body().string(), MyResponse.class);

                    TreeMap<String, String> idAndNameMap = JsonUtil.jsonToBean(myResponse.getData(), new TypeToken<TreeMap<String, String>>() {
                    }.getType());

                    usernames = idAndNameMap.values().toArray(new String[]{});
                    //spinner初始化
                    handler1.sendEmptyMessage(0);
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

    //spinner初始化
    Handler handler1 = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message message) {
            spinnerChooseUser.setAdapter(new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, usernames));

            System.out.println(spinnerChooseUser.getSelectedItem());
            return false;
        }
    });

    /**
     * 添加按钮点击事件
     *
     * @param view
     */
    @OnClick(R.id.bt_add_dev)
    public void onSignInButtonClicked(View view) {
        String devName = etDevname.getText().toString();
        String devOwner = spinnerChooseUser.getSelectedItem().toString();
        String devNote = StringUtils.trimToEmpty(etDevNote.getText().toString());

        if (StringUtils.isBlank(devName)) {
            //设备名校验不通过
            inputErrWarning(etDevname, "请输入正确的设备名");
            return;
        }

        //输入无误
        String params = "devName=" + devName + "&owner=" + devOwner + "&note=" + devNote;

        new Thread(() -> {
            try {
                Response response = NetUtil.post("/api/add_dev", params);
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
            etDevname.setText("");
            etDevname.setHint("");
            etDevNote.setText("");
            etDevNote.setHint("");
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
