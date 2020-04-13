package com.sust.monitorapp.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;
import com.sust.monitorapp.R;
import com.sust.monitorapp.adapter.DeviceAdapter;
import com.sust.monitorapp.bean.Device;
import com.sust.monitorapp.bean.MyResponse;
import com.sust.monitorapp.common.MyApplication;
import com.sust.monitorapp.common.ResponseCode;
import com.sust.monitorapp.ui.RecyclerViewDivider;
import com.sust.monitorapp.util.JsonUtil;
import com.sust.monitorapp.util.NetUtil;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
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

public class SelectDeviceActivity extends AppCompatActivity {

    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.rv_select_device)
    RecyclerView rvSelectDevice;

    //adapter数据源
    private List<Device> data;
    //recyclerview数据适配器
    private DeviceAdapter deviceAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_device);
        ButterKnife.bind(this);

        initView();
        initData();
    }

    /**
     * 初始化页面
     * <p>
     * 注意：在没有获取到数据时候就开始加载 RecyclerView 就会出现
     * RecyclerView: No adapter attached; skipping layout 致使APP无缘无故崩溃
     * 解决办法是：
     * 在初始化布局的时候设置空的数据源，并设置 RecyclerView 的数据显示。
     * 在加载布局完成之后通过 adapter.notifyDataSetChanged(); 进行刷新数据就可以避免
     */
    private void initView() {
        tvTitle.setText("设备列表");

        //控制布局为LinearLayout或者是GridView或者是瀑布流布局
        rvSelectDevice.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        //初始化一个空data
        data = new ArrayList<Device>();

        //为RecyclerView设置Adapter
        deviceAdapter = new DeviceAdapter(data, SelectDeviceActivity.this);
        rvSelectDevice.setAdapter(deviceAdapter);

        //设置分隔线
        rvSelectDevice.addItemDecoration(new RecyclerViewDivider(this,
                LinearLayoutManager.VERTICAL, R.color.grey, 1));

        //为adapter中的item绑定点击和长按事件
        deviceAdapter.setOnItemClickListener(new DeviceAdapter.onItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                switch (view.getId()) {
                    case R.id.bt_dev_more_info:
                        //获取当前位置的item的userId
                        LinearLayoutManager manager = (LinearLayoutManager) rvSelectDevice.getLayoutManager();
                        View item = manager.findViewByPosition(position);
                        TextView textView = item.findViewById(R.id.tv_dev_id);
                        String id = textView.getText().toString();
                        //携带当前待查询id 跳转至设备详情页面
                        Intent intent = new Intent(SelectDeviceActivity.this, DeviceMoreInfoActivity.class);
                        intent.putExtra("devId", id);
                        startActivity(intent);
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onItemLongClick(View view, int position) {

                //显示对话框
                showCoverDialog(position);
            }
        });
    }

    //网络访问获取数据
    private void initData() {
        new Thread(() -> {
            String url = "/api/get_all_devs?userId=" + MyApplication.user.getUserId();
            try {
                Response response = NetUtil.get(url);
                if (response.isSuccessful()) {
                    MyResponse myResponse = JsonUtil.jsonToBean(response.body().string(), MyResponse.class);

                    Message message = new Message();
                    message.obj = myResponse.getData();
                    mHandler.sendMessage(message);
                } else {
                    Toast.makeText(SelectDeviceActivity.this, "网络请求失败", Toast.LENGTH_SHORT).show();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    //获取到数据后，刷新页面
    private Handler mHandler = new Handler(new Handler.Callback() {

        @Override
        public boolean handleMessage(@NonNull Message message) {
            TreeMap<String, String> idAndNameMap = JsonUtil.jsonToBean(String.valueOf(message.obj), new TypeToken<TreeMap<String, String>>() {
            }.getType());
            //将数据注入adapter
            for (Map.Entry<String, String> entry : idAndNameMap.entrySet()) {
                data.add(Device.builder().devId(entry.getKey()).devName(entry.getValue()).build());
            }
            //刷新页面数据
            deviceAdapter.notifyDataSetChanged();

            return false;
        }
    });

    //长按时弹出对话框。确认是否删除当前用户
    private void showCoverDialog(int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        //获取当前位置的item的userId
        LinearLayoutManager manager = (LinearLayoutManager) rvSelectDevice.getLayoutManager();
        View item = manager.findViewByPosition(position);
        TextView textView = item.findViewById(R.id.tv_dev_id);
        String id = StringUtils.defaultIfBlank(textView.getText().toString(), "null");

        builder.setTitle("提示");
        builder.setMessage("确定从数据库中删除此设备(id=" + id + ")吗？");

        //确认点击事件：本地和服务端删除用户
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {


                new Thread(() -> {
                    Looper.prepare();
                    try {
                        String url = "/api/delete_dev?devId=" + id;
                        Response response = NetUtil.get(url);
                        if (response.isSuccessful()) {
                            MyResponse myResponse = JsonUtil.jsonToBean(response.body().string(), MyResponse.class);
                            //删除成功
                            if (StringUtils.equals(myResponse.getStatusCode(), ResponseCode.SUCCESS.getCode())) {
                                Toast.makeText(getApplicationContext(), "删除成功", Toast.LENGTH_SHORT).show();
                                //服务端确认删除成功之后，更新页面。非主线程更新交给handler处理
                                Message message = new Message();
                                message.arg1 = position;
                                handler.sendMessage(message);
                            } else {
                                Toast.makeText(getApplicationContext(), "操作失败，请检查网络连接", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "操作失败，请检查网络连接", Toast.LENGTH_SHORT).show();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        Looper.loop();
                    }
                }).start();
            }
        });

        //取消时什么都不做
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        builder.show();
    }

    //删除本地的对应用户
    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message message) {
            int position = message.arg1;
            data.remove(position);
            /*
              动态删除有坑
             */
            deviceAdapter.notifyItemChanged(position);
            //重点，更新删除位置之后的数据
            deviceAdapter.notifyItemRangeChanged(position, deviceAdapter.getItemCount());
            deviceAdapter.notifyItemRemoved(position);
            deviceAdapter.notifyDataSetChanged();
            return false;
        }
    });

    /**
     * 顶部返回按钮点击事件
     */
    @OnClick(R.id.title_back)
    public void onViewClicked() {
        finish();
    }
}
