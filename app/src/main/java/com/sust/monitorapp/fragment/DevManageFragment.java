package com.sust.monitorapp.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sust.monitorapp.R;
import com.sust.monitorapp.activity.AddDeviceActivity;
import com.sust.monitorapp.activity.DeleteDeviceActivity;
import com.sust.monitorapp.activity.ModifyDeviceInfoActivity;
import com.sust.monitorapp.activity.SelectDeviceActivity;
import com.sust.monitorapp.util.UIUtils;

import androidx.fragment.app.Fragment;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 */
public class DevManageFragment extends Fragment {


    public DevManageFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = UIUtils.getView(R.layout.fragment_dev_manage);
        ButterKnife.bind(this, view);
        return view;
    }

    @OnClick(R.id.ll_to_select_dev)
    void onBtSelectDevClicked() {
        Intent intent = new Intent(getActivity(), SelectDeviceActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.ll_to_modify_dev)
    void onBtModifyDeviceClicked() {
        Intent intent = new Intent(getActivity(), ModifyDeviceInfoActivity.class);
        intent.putExtra("title", "modify_dev");
        startActivity(intent);
    }

    @OnClick(R.id.ll_to_delete_dev)
    void onBtDeleteDevClicked() {
        Intent intent = new Intent(getActivity(), DeleteDeviceActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.ll_to_add_dev)
    void onBtAddDevClicked() {
        Intent intent = new Intent(getActivity(), AddDeviceActivity.class);
        startActivity(intent);
    }
}
