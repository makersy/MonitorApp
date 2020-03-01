package com.sust.monitorapp.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.sust.monitorapp.R;
import com.sust.monitorapp.activity.SelectUserActivity;
import com.sust.monitorapp.util.UIUtils;

import androidx.fragment.app.Fragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 */
public class UserManageFragment extends Fragment {


    @BindView(R.id.bt_to_select_user)
    Button btToSelectUser;
    @BindView(R.id.bt_to_delete_user)
    Button btToDeleteUser;
    @BindView(R.id.bt_to_modify_user)
    Button btToModifyUser;
    @BindView(R.id.bt_to_add_user)
    Button btToAddUser;

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

    @OnClick(R.id.bt_to_select_user)
    public void onBtnSelectUserClicked() {
        Intent intent = new Intent(getActivity(), SelectUserActivity.class);
        startActivity(intent);
    }
}
