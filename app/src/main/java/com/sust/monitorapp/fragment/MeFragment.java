package com.sust.monitorapp.fragment;


import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sust.monitorapp.R;
import com.sust.monitorapp.bean.MyResponse;
import com.sust.monitorapp.bean.User;
import com.sust.monitorapp.common.AppConfig;
import com.sust.monitorapp.util.JsonUtil;
import com.sust.monitorapp.util.MyHttp;
import com.sust.monitorapp.util.UIUtils;

import java.io.IOException;

import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Response;

import static android.content.ContentValues.TAG;

/**
 * A simple {@link Fragment} subclass.
 */
public class MeFragment extends Fragment {


    private SwipeRefreshLayout srlMe;
    private TextView personinfoUsername;
    private TextView personinfoAuth;
    private LinearLayout llUserinfo;
    private LinearLayout llModifyPassword;
    private Button btSignOut;

    private View view;

    public MeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = UIUtils.getView(R.layout.fragment_me);

        //初始化页面控件
        initView();
        //页面数据初始化
        initData();

        return view;
    }

    /**
     * 初始化页面控件
     */
    private void initView() {
        srlMe = view.findViewById(R.id.srl_me);
        personinfoUsername = view.findViewById(R.id.personinfo_username);
        personinfoAuth = view.findViewById(R.id.personinfo_auth);
        llUserinfo = view.findViewById(R.id.ll_userinfo);
        llModifyPassword = view.findViewById(R.id.ll_modify_password);

        //设置下拉刷新
        srlMe.setColorSchemeColors(
                UIUtils.getColor(R.color.orange),
                UIUtils.getColor(R.color.green),
                UIUtils.getColor(R.color.red));
        srlMe.setSize(SwipeRefreshLayout.DEFAULT);
        srlMe.setProgressViewOffset(true, 0, 200);
        srlMe.setProgressViewEndTarget(true, 100);


        srlMe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Thread(() -> {
                    showData();
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    getActivity().runOnUiThread(()->{
                        srlMe.setRefreshing(false);
                        Toast.makeText(view.getContext(), "获取数据失败", Toast.LENGTH_SHORT).show();
                    });
                }).start();

            }
        });
    }

    /**
     * 页面数据初始化
     */
    private void initData() {
        showData();
    }


    private void showData() {
        new Thread(() -> {
            try {
                Response response = MyHttp.get("/api/login");
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

    //处理数据，更新页面ui
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            User user = JsonUtil.jsonToBean(String.valueOf(msg.obj), User.class);

            personinfoUsername.setText(user.getUsername());
            personinfoAuth.setText(user.getAuthority());
            Toast.makeText(view.getContext(), "刷新成功", Toast.LENGTH_SHORT).show();
            srlMe.setRefreshing(false);

        }
    };

}

