package com.sust.monitorapp.adapter;

import com.sust.monitorapp.fragment.DevManageFragment;
import com.sust.monitorapp.fragment.MeFragment;
import com.sust.monitorapp.fragment.TemperMonitorFragment;
import com.sust.monitorapp.fragment.UserManageFragment;

import java.util.ArrayList;
import java.util.List;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

/**
 * Created by yhl on 2020/3/8.
 */
public class AdminFragmentPagerAdapter extends FragmentPagerAdapter {

    private List<Fragment> fragmentList = new ArrayList<Fragment>();

    public AdminFragmentPagerAdapter(FragmentManager fm, List<Fragment> fragmentList) {
        super(fm);
        this.fragmentList = fragmentList;
    }

    @Override
    public Fragment getItem(int position) {
        return fragmentList.get(position);
    }

    @Override
    public int getCount() {
        return fragmentList.size();
    }
}
