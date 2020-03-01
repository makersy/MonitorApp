package com.sust.monitorapp.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;
import com.sust.monitorapp.R;
import com.sust.monitorapp.adapter.UserAdapter;
import com.sust.monitorapp.bean.MyResponse;
import com.sust.monitorapp.bean.User;
import com.sust.monitorapp.util.JsonUtil;
import com.sust.monitorapp.util.MyHttp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Response;

public class SelectUserActivity extends AppCompatActivity {

    @BindView(R.id.iv_title_back)
    ImageView ivTitleBack;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.rv_select_user)
    RecyclerView rvSelectUser;

    //adapter数据源
    private List<User> data;
    //recyclerview数据适配器
    private UserAdapter userAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_user);
        ButterKnife.bind(this);

        initView();
        initData();
    }

    /**
     * 初始化页面
     *
     * 注意：在没有获取到数据时候就开始加载RecyclerView就会出现
     * RecyclerView: No adapter attached; skipping layout致使APP无缘无故崩溃
     * 解决办法是：
     * 在初始化布局的时候设置空的数据源，并设置RecyclerView的数据显示。
     * 在加载布局完成之后通过adapter.notifyDataSetChanged();进行刷新数据就可以避免
     */
    private void initView() {
        tvTitle.setText("用户列表");

        //控制布局为LinearLayout或者是GridView或者是瀑布流布局
        rvSelectUser.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        //初始化一个空data
        data = new ArrayList<User>();

        //为RecyclerView设置Adapter
        userAdapter = new UserAdapter(data, SelectUserActivity.this);
        rvSelectUser.setAdapter(userAdapter);

        //为adapter中的item绑定点击和长按事件
        userAdapter.setOnItemClickListener(new UserAdapter.onItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                switch (view.getId()) {
                    case R.id.bt_del_user:
                        Toast.makeText(SelectUserActivity.this, "点击第" + position +
                                "行删除按钮！", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.bt_user_more_info:
                        Toast.makeText(SelectUserActivity.this, "点击第" + position +
                                "行更多按钮！", Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        Toast.makeText(SelectUserActivity.this, "点击第" + position +
                                "行！", Toast.LENGTH_SHORT).show();
                        break;
                }
            }

            @Override
            public void onItemLongClick(View view, int position) {
                Toast.makeText(SelectUserActivity.this, "长按第" + position +
                        "行！", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initData() {
        new Thread(() -> {
            try {
                Response response = MyHttp.get("/api/get_all_users");
                if (response.isSuccessful()) {
                    MyResponse myResponse = JsonUtil.jsonToBean(response.body().string(), MyResponse.class);

                    Message message = new Message();
                    message.obj = myResponse.getData();
                    mHandler.sendMessage(message);
                } else {
                    Toast.makeText(SelectUserActivity.this, "网络请求失败", Toast.LENGTH_SHORT);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private Handler mHandler = new Handler(new Handler.Callback(){

        @Override
        public boolean handleMessage(@NonNull Message message) {
            TreeMap<String, String> idAndNameMap = JsonUtil.jsonToBean(String.valueOf(message.obj), new TypeToken<TreeMap<String, String>>(){}.getType());
            for (Map.Entry<String, String> entry : idAndNameMap.entrySet()) {
                data.add(User.builder().userId(entry.getKey()).username(entry.getValue()).build());
            }
            //刷新页面数据
            userAdapter.notifyDataSetChanged();
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
