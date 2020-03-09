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
import com.sust.monitorapp.adapter.UserAdapter;
import com.sust.monitorapp.bean.MyResponse;
import com.sust.monitorapp.bean.User;
import com.sust.monitorapp.common.ResponseCode;
import com.sust.monitorapp.ui.RecyclerViewDivider;
import com.sust.monitorapp.util.JsonUtil;
import com.sust.monitorapp.util.MyHttp;

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

/**
 * Created by yhl on 2020/2/29.
 */

public class SelectUserActivity extends AppCompatActivity {

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
     * <p>
     * 注意：在没有获取到数据时候就开始加载 RecyclerView 就会出现
     * RecyclerView: No adapter attached; skipping layout 致使APP无缘无故崩溃
     * 解决办法是：
     * 在初始化布局的时候设置空的数据源，并设置 RecyclerView 的数据显示。
     * 在加载布局完成之后通过 adapter.notifyDataSetChanged(); 进行刷新数据就可以避免
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

        //设置分隔线
        rvSelectUser.addItemDecoration(new RecyclerViewDivider(this,
                LinearLayoutManager.VERTICAL, R.color.grey, 1));

        //为adapter中的item绑定点击和长按事件
        userAdapter.setOnItemClickListener(new UserAdapter.onItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                switch (view.getId()) {
                    case R.id.bt_user_more_info:
                        //获取当前位置的item的userId
                        LinearLayoutManager manager = (LinearLayoutManager) rvSelectUser.getLayoutManager();
                        View item = manager.findViewByPosition(position);
                        TextView textView = item.findViewById(R.id.tv_userid);
                        String id = textView.getText().toString();
                        //携带当前待查询id 跳转至详情页面
                        Intent intent = new Intent(SelectUserActivity.this, UserMoreInfoActivity.class);
                        intent.putExtra("userId", id);
                        startActivity(intent);
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onItemLongClick(View view, int position) {

//                manager.removeView(item);
                //显示对话框
                showCoverDialog(position);
            }
        });
    }

    //网络访问获取数据
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
                    Toast.makeText(SelectUserActivity.this, "网络请求失败", Toast.LENGTH_SHORT).show();
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
                data.add(User.builder().userId(entry.getKey()).username(entry.getValue()).build());
            }
            //刷新页面数据
            userAdapter.notifyDataSetChanged();


            return false;
        }
    });

    //长按时弹出对话框。确认是否删除当前用户
    private void showCoverDialog(int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        //获取当前位置的item的userId
        LinearLayoutManager manager = (LinearLayoutManager) rvSelectUser.getLayoutManager();
        View item = manager.findViewByPosition(position);
        TextView textView = item.findViewById(R.id.tv_userid);
        String id = StringUtils.defaultIfBlank(textView.getText().toString(), "null");

        builder.setTitle("提示");
        builder.setMessage("确定从数据库中删除此用户(id=" + id + ")吗？");

        //确认点击事件：本地和服务端删除用户
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {


                new Thread(() -> {
                    Looper.prepare();
                    try {
                        String url = "/api/delete_user?userId=" + id;
                        Response response = MyHttp.get(url);
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
            userAdapter.notifyItemChanged(position);
            //重点，更新删除位置之后的数据
            userAdapter.notifyItemRangeChanged(position, userAdapter.getItemCount());
            userAdapter.notifyItemRemoved(position);
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
