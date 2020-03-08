package com.sust.monitorapp.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sust.monitorapp.R;
import com.sust.monitorapp.activity.DeleteUserActivity;
import com.sust.monitorapp.activity.ModifyUserInfoActivity;
import com.sust.monitorapp.activity.SelectUserActivity;
import com.sust.monitorapp.activity.SignInActivity;
import com.sust.monitorapp.util.UIUtils;

import androidx.fragment.app.Fragment;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by yhl on 2020/3/2.
 */

public class UserManageFragment extends Fragment {


    public UserManageFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = UIUtils.getView(R.layout.fragment_user_manage);
        ButterKnife.bind(this, view);
        return view;
    }

    /**
     * 查看所有用户按钮点击事件
     */
    @OnClick(R.id.ll_to_select_user)
    void onBtnSelectUserClicked() {
        Intent intent = new Intent(getActivity(), SelectUserActivity.class);
        startActivity(intent);
    }

    /**
     * 删除用户按钮点击事件
     */
    @OnClick(R.id.ll_to_delete_user)
    void onBtDelUserClicked() {
        Intent intent = new Intent(getActivity(), DeleteUserActivity.class);
        startActivity(intent);
    }

    /**
     * 添加用户按钮点击事件
     */
    @OnClick(R.id.ll_to_add_user)
    void onBtAddUserClicked() {
        Intent intent = new Intent(getActivity(), SignInActivity.class);
        intent.putExtra("title", "add_user");
        startActivity(intent);
    }

    /**
     * 查询用户按钮点击事件
     */
    @OnClick(R.id.ll_to_query_user)
    void onBtQueryUserClicked() {
        Intent intent = new Intent(getActivity(), ModifyUserInfoActivity.class);
        intent.putExtra("title", "query_user");
        startActivity(intent);
    }

    /**
     * 修改用户信息按钮点击事件
     */
    @OnClick(R.id.ll_to_modify_user)
    public void onBtModifyUserClicked() {
        Intent intent = new Intent(getActivity(), ModifyUserInfoActivity.class);
        intent.putExtra("title", "modify_user");
        startActivity(intent);
    }
}
