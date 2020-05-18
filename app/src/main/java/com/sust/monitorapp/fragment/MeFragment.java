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

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.sust.monitorapp.R;
import com.sust.monitorapp.activity.ChangePasswordActivity;
import com.sust.monitorapp.activity.LoginActivity;
import com.sust.monitorapp.activity.UserOwnInfoActivity;
import com.sust.monitorapp.bean.MyResponse;
import com.sust.monitorapp.bean.User;
import com.sust.monitorapp.common.Constants;
import com.sust.monitorapp.common.MyApplication;
import com.sust.monitorapp.util.JsonUtil;
import com.sust.monitorapp.util.NetUtil;
import com.sust.monitorapp.util.UIUtils;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import jp.wasabeef.glide.transformations.BlurTransformation;
import jp.wasabeef.glide.transformations.CropCircleTransformation;
import okhttp3.Response;

/**
 * Created by yhl on 2020/2/29.
 */

public class MeFragment extends Fragment {

    @BindView(R.id.iv_head_back)
    ImageView ivHeadBack;
    @BindView(R.id.iv_head_about_me)
    ImageView ivHeadAboutMe;
    @BindView(R.id.user_line)
    ImageView userLine;
    @BindView(R.id.personinfo_username)
    TextView personinfoUsername;
    @BindView(R.id.personinfo_auth)
    TextView personinfoAuth;
    @BindView(R.id.ll_to_modify_password)
    LinearLayout llToModifyPassword;
    @BindView(R.id.bt_sign_out)
    Button btSignOut;


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

        return view;
    }

    /**
     * 初始化页面控件
     */
    private void initView() {
        //获取当前登录用户信息
        User user = MyApplication.user;

        //显示用户手机号码
        personinfoUsername.setText(user.getUserId());

        //显示用户类别
        if (user.getAuthority() == Constants.ADMIN) {
            personinfoAuth.setText(Constants.ADMIN_STRING);
        } else if(user.getAuthority() == Constants.USER){
            personinfoAuth.setText(Constants.USER_STRING);
        }

        //根据性别放置不同头像
        Integer picId = R.drawable.head;
        if (StringUtils.equals("男", user.getSex())) {
            picId = R.drawable.head_boy;
        } else if (StringUtils.equals("女", user.getSex())) {
            picId = R.drawable.head_girl;
        }

        //背景模糊
        Glide.with(view.getContext()).load(picId)
                .bitmapTransform(new BlurTransformation(view.getContext(), 25), new CenterCrop(view.getContext()))
                .into(ivHeadBack);

        //圆形头像
        Glide.with(view.getContext()).load(picId)
                .bitmapTransform(new CropCircleTransformation(view.getContext()))
                .into(ivHeadAboutMe);

    }

    @OnClick(R.id.iv_head_about_me)
    void toUserInfo() {
        Intent intent = new Intent(getActivity(), UserOwnInfoActivity.class);
        User user = MyApplication.user;
        intent.putExtra("userId", user.getUserId());
        startActivity(intent);
    }

    @OnClick({R.id.ll_to_modify_password})
    void toModifyPassword() {
        Intent intent = new Intent(getActivity(), ChangePasswordActivity.class);
        User user = MyApplication.user;
        intent.putExtra("userId", user.getUserId());
        startActivity(intent);
    }

    /**
     * 退出登录
     */
    @OnClick(R.id.bt_sign_out)
    void onSignOutClicked() {
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        startActivity(intent);

        //销毁当前登录用户
        MyApplication.user = null;

        getActivity().finish();
    }
}

