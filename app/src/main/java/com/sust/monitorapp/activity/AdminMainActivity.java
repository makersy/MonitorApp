package com.sust.monitorapp.activity;

import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.sust.monitorapp.R;
import com.sust.monitorapp.adapter.AdminFragmentPagerAdapter;
import com.sust.monitorapp.bean.User;
import com.sust.monitorapp.fragment.DevManageFragment;
import com.sust.monitorapp.fragment.MeFragment;
import com.sust.monitorapp.fragment.TemperMonitorFragment;
import com.sust.monitorapp.fragment.UserManageFragment;
import com.sust.monitorapp.util.UIUtils;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by yhl on 2020/2/28.
 *
 * 管理员登录后页面，4个 fragment 任意选择
 */

public class AdminMainActivity extends AppCompatActivity implements RadioGroup.OnCheckedChangeListener,ViewPager.OnPageChangeListener {

    @BindView(R.id.rb_temper_monitor)
    RadioButton rbTemperMonitor;
    @BindView(R.id.rb_dev_manage)
    RadioButton rbDevManage;
    @BindView(R.id.rb_user_manage)
    RadioButton rbUserManage;
    @BindView(R.id.rb_me)
    RadioButton rbMe;
    @BindView(R.id.radiogroup_usermain)
    RadioGroup radiogroupUsermain;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.vp_admin_main)
    ViewPager vpAdminMain;

    //fragment适配器
    private AdminFragmentPagerAdapter mAdapter;

    //放入viewpager的fragment
    private List<Fragment> fragments = new ArrayList<>();
    private MeFragment meFragment;
    private TemperMonitorFragment temperMonitorFragment;
    private DevManageFragment devManageFragment;
    private UserManageFragment userManageFragment;

    //四个页面的标题
    String[] titles = new String[]{"运行状况", "变压器管理", "用户管理", "我的"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_main);
        ButterKnife.bind(this);

        initView();
    }

    //初始化页面，使 温度数据显示页面 为选中状态
    private void initView() {
        //初始化fragment页面对象
        meFragment = new MeFragment();
        temperMonitorFragment = new TemperMonitorFragment();
        devManageFragment = new DevManageFragment();
        userManageFragment = new UserManageFragment();

        //注意添加顺序按照layout中排列顺序
        fragments.add(temperMonitorFragment);
        fragments.add(devManageFragment);
        fragments.add(userManageFragment);
        fragments.add(meFragment);

        //初始化适配器，设置数据源
        mAdapter = new AdminFragmentPagerAdapter(this.getSupportFragmentManager(), fragments);

        //设置缓存帧为4
        vpAdminMain.setOffscreenPageLimit(4);

        //为viewpager绑定适配器和监听器
        vpAdminMain.setAdapter(mAdapter);
        vpAdminMain.setOnPageChangeListener(this);

        //进入时，默认 数据显示页面 为选中状态
        vpAdminMain.setCurrentItem(0);
        tvTitle.setText(titles[0]);
        changeButtonAndText(0);

        //设置radiogroup监听
        radiogroupUsermain.setOnCheckedChangeListener(this);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    /**
     * viewpager 中的页面选中事件
     * @param position 选中页面的位置
     */
    @Override
    public void onPageSelected(int position) {
        //设置对应标题
        tvTitle.setText(titles[position]);
        //改变radiobutton的选中状态以及文本颜色
        changeButtonAndText(position);
    }

    /**
     * 页面选中状态改变事件
     * @param state state的状态有三个，0表示什么都没做，1正在滑动，2滑动完毕
     */
    @Override
    public void onPageScrollStateChanged(int state) {

    }

    //改变radiobutton的选中状态以及文本颜色
    private void changeButtonAndText(int position) {
        switch (position) {
            case 0:
                //先重置所有，然后设置当前选中
                resetRadioButtons();
                rbTemperMonitor.setChecked(true);
                rbTemperMonitor.setTextColor(UIUtils.getColor(R.color.white));
                break;
            case 1:
                resetRadioButtons();
                rbDevManage.setChecked(true);
                rbDevManage.setTextColor(UIUtils.getColor(R.color.white));
                break;
            case 2:
                resetRadioButtons();
                rbUserManage.setChecked(true);
                rbUserManage.setTextColor(UIUtils.getColor(R.color.white));
                break;
            case 3:
                resetRadioButtons();
                rbMe.setChecked(true);
                rbMe.setTextColor(UIUtils.getColor(R.color.white));
                break;
            default:
                break;
        }
    }

    //重置所有 radiobutton 的选中状态和文本颜色
    private void resetRadioButtons() {
        if (rbMe != null) {
            rbMe.setChecked(false);
            rbMe.setTextColor(UIUtils.getColor(R.color.text_bottom_lightblue));
        }
        if (rbTemperMonitor != null) {
            rbTemperMonitor.setChecked(false);
            rbTemperMonitor.setTextColor(UIUtils.getColor(R.color.text_bottom_lightblue));
        }
        if (rbDevManage != null) {
            rbDevManage.setChecked(false);
            rbDevManage.setTextColor(UIUtils.getColor(R.color.text_bottom_lightblue));
        }
        if (rbUserManage != null) {
            rbUserManage.setChecked(false);
            rbUserManage.setTextColor(UIUtils.getColor(R.color.text_bottom_lightblue));
        }
    }

    /**
     * 底部导航栏选中状态改变事件
     */
    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int i) {
        switch (i) {
            case R.id.rb_temper_monitor:
                vpAdminMain.setCurrentItem(0, true);
                break;
            case R.id.rb_dev_manage:
                vpAdminMain.setCurrentItem(1, true);
                break;
            case R.id.rb_user_manage:
                vpAdminMain.setCurrentItem(2, true);
                break;
            case R.id.rb_me:
                vpAdminMain.setCurrentItem(3, true);
                break;
            default:
                break;
        }
    }


    /**
     * 如果 2s 内连续点击返回键 2 次，则退出当前应用
     */
    private long firstPressedTime = 0;

    @Override
    public void onBackPressed() {
        if (System.currentTimeMillis() - firstPressedTime < 2000) {
            super.onBackPressed();
        } else {
            Toast.makeText(AdminMainActivity.this, "再按一次退出", Toast.LENGTH_SHORT).show();
            firstPressedTime = System.currentTimeMillis();
        }
    }


}
