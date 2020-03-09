package com.sust.monitorapp.activity;

import android.os.Bundle;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.sust.monitorapp.R;
import com.sust.monitorapp.adapter.AdminFragmentPagerAdapter;
import com.sust.monitorapp.fragment.MeFragment;
import com.sust.monitorapp.fragment.TemperMonitorFragment;
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
 * 普通用户登录后页面，2个 fragment 任意选择
 */

public class UserMainActivity extends AppCompatActivity implements RadioGroup.OnCheckedChangeListener, ViewPager.OnPageChangeListener {

    @BindView(R.id.vp_user_main)
    ViewPager vpUserMain;
    @BindView(R.id.rb_temper_monitor)
    RadioButton rbTemperMonitor;
    @BindView(R.id.rb_me)
    RadioButton rbMe;
    @BindView(R.id.radiogroup_usermain)
    RadioGroup radiogroup;
    @BindView(R.id.tv_title)
    TextView tvTitle;

    //fragment适配器
    private AdminFragmentPagerAdapter mAdapter;

    //放入viewpager的fragment
    private List<Fragment> fragments = new ArrayList<>();
    private MeFragment meFragment;
    private TemperMonitorFragment temperMonitorFragment;

    //四个页面的标题
    String[] titles = new String[]{"运行状况", "我的"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_main);
        ButterKnife.bind(this);

        initView();
    }

    //初始化页面，使 温度数据显示页面 为选中状态
    private void initView() {
        //初始化fragment页面对象
        meFragment = new MeFragment();
        temperMonitorFragment = new TemperMonitorFragment();


        //注意添加顺序按照layout中排列顺序
        fragments.add(temperMonitorFragment);
        fragments.add(meFragment);

        //初始化适配器，设置数据源
        mAdapter = new AdminFragmentPagerAdapter(this.getSupportFragmentManager(), fragments);

        //设置缓存帧为2
        vpUserMain.setOffscreenPageLimit(2);

        //为viewpager绑定适配器和监听器
        vpUserMain.setAdapter(mAdapter);
        vpUserMain.setOnPageChangeListener(this);

        //进入时，默认 数据显示页面 为选中状态
        vpUserMain.setCurrentItem(0);
        changeButtonAndText(0);

        //设置radiogroup监听
        radiogroup.setOnCheckedChangeListener(this);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        //设置对应标题
        tvTitle.setText(titles[position]);
        //改变radiobutton的选中状态以及文本颜色
        changeButtonAndText(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }


    private void changeButtonAndText(int position) {
        switch (position) {
            case 0:
                resetRadioButtons();
                rbTemperMonitor.setChecked(true);
                rbTemperMonitor.setTextColor(UIUtils.getColor(R.color.white));
                break;
            case 1:
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
    }

    /**
     * 底部导航栏点击事件
     */
    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int i) {
        switch (i) {
            case R.id.rb_temper_monitor:
                vpUserMain.setCurrentItem(0, true);
                break;
            case R.id.rb_me:
                vpUserMain.setCurrentItem(1, true);
                break;
        }
    }

    /**
     * 如果2s内连续点击返回键2次，则退出当前应用
     * https://www.jianshu.com/p/7bf30c52d6f3
     */
    private long firstPressedTime = 0;

    @Override
    public void onBackPressed() {
        if (System.currentTimeMillis() - firstPressedTime < 2000) {
            super.onBackPressed();
        } else {
            Toast.makeText(UserMainActivity.this, "再按一次退出", Toast.LENGTH_SHORT).show();
            firstPressedTime = System.currentTimeMillis();
        }
    }

}
