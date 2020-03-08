package com.sust.monitorapp.fragment;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sust.monitorapp.R;
import com.sust.monitorapp.activity.LoginActivity;
import com.sust.monitorapp.activity.UserOwnInfoActivity;
import com.sust.monitorapp.bean.MyResponse;
import com.sust.monitorapp.bean.User;
import com.sust.monitorapp.common.MyApplication;
import com.sust.monitorapp.util.JsonUtil;
import com.sust.monitorapp.util.MyHttp;
import com.sust.monitorapp.util.UIUtils;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Response;

/**
 * Created by yhl on 2020/2/29.
 */

public class MeFragment extends Fragment {

    ImageView ivHeadAboutMe;
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
        ButterKnife.bind(this, view);

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
        llUserinfo = view.findViewById(R.id.ll_to_userinfo);
        llModifyPassword = view.findViewById(R.id.ll_to_modify_password);
        ivHeadAboutMe = view.findViewById(R.id.iv_head_about_me);

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
                    getActivity().runOnUiThread(() -> {
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
    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            User user = JsonUtil.jsonToBean(String.valueOf(msg.obj), User.class);

            personinfoUsername.setText(user.getUsername());
            personinfoAuth.setText(user.getAuthority());
            //根据性别放置不同头像
            if (StringUtils.equals("男", user.getSex())) {
                ivHeadAboutMe.setImageResource(R.drawable.head_boy);
            } else if (StringUtils.equals("女", user.getSex())) {
                ivHeadAboutMe.setImageResource(R.drawable.head_girl);
            }

            Toast.makeText(view.getContext(), "刷新成功", Toast.LENGTH_SHORT).show();
            srlMe.setRefreshing(false);

        }
    };

    @OnClick(R.id.ll_to_userinfo)
    void toUserInfo() {
        Intent intent = new Intent(getActivity(), UserOwnInfoActivity.class);
        User user = MyApplication.user;
        intent.putExtra("userId", user.getUserId());
        startActivity(intent);
    }

    @OnClick(R.id.bt_sign_out)
    void onSignOutClicked() {
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        startActivity(intent);

        //销毁当前登录用户
        MyApplication.user = null;

        getActivity().finish();
    }
}

