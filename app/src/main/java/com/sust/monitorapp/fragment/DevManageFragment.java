package com.sust.monitorapp.fragment;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sust.monitorapp.R;
import com.sust.monitorapp.util.UIUtils;

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
        return view;
    }

}
