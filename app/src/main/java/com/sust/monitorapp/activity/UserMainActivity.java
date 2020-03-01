package com.sust.monitorapp.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sust.monitorapp.R;
import com.sust.monitorapp.fragment.MeFragment;
import com.sust.monitorapp.fragment.TemperMonitorFragment;
import com.sust.monitorapp.util.UIUtils;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class UserMainActivity extends AppCompatActivity implements RadioGroup.OnCheckedChangeListener{

    @BindView(R.id.fl_user_main)
    FrameLayout flUserMain;
    @BindView(R.id.rb_temper_monitor)
    RadioButton rbTemperMonitor;
    @BindView(R.id.rb_me)
    RadioButton rbMe;
    @BindView(R.id.radiogroup_usermain)
    RadioGroup radiogroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_main);
        ButterKnife.bind(this);

        initView();
    }

    //初始化页面，使 温度数据显示页面 为选中状态
    private void initView() {
        //进入时，默认 数据显示页面 为选中状态
        setSelect(0);
        //设置radiogroup监听
        radiogroup.setOnCheckedChangeListener(this);
    }


    /**
     * 底部导航栏点击事件
     * 此处fragment应使用动态加载。
     * 由于每次 replace() 时都会重新实例化fragment，重新加载一遍数据，这样非常消耗性能和用户流量，
     * 所以在切换时，采取 hide()当前,show()另一个的方式，能够做到多个 fragment切换不重新实例化。
     *
     * 数据更新问题：
     * https://blog.csdn.net/u014644594/article/details/83108594
     */
    private MeFragment meFragment;
    private TemperMonitorFragment temperMonitorFragment;
    private FragmentTransaction transaction;

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int i) {
        switch (i) {
            case R.id.rb_temper_monitor:
                setSelect(0);
                break;
            case R.id.rb_me:
                setSelect(1);
                break;
        }
    }

    //按钮相应的fragment显示
    private void setSelect(int i) {
        FragmentManager fragmentManager = this.getSupportFragmentManager();
        transaction = fragmentManager.beginTransaction();

        //隐藏所有的fragment
        hideFragments();

        //重置所有的radiobutton选中状态
        resetRadioButtons();

        switch (i) {
            case 0:
                if (temperMonitorFragment == null) {
                    //创建对象后，不会马上调用生命周期方法，而是在commit后才会调用
                    temperMonitorFragment = new TemperMonitorFragment();
                    transaction.add(R.id.fl_user_main, temperMonitorFragment);
                }
                //显示按钮对应的Fragment
                transaction.show(temperMonitorFragment);

                //改变选中radiobutton的选中状态和文本颜色
                rbTemperMonitor.setChecked(true);
                rbTemperMonitor.setTextColor(UIUtils.getColor(R.color.white));
                break;
            case 1:
                if (meFragment == null) {
                    //创建对象后，不会马上调用生命周期方法，而是在commit后才会调用
                    meFragment = new MeFragment();
                    transaction.add(R.id.fl_user_main, meFragment);
                }
                //显示按钮对应的Fragment
                transaction.show(meFragment);

                //改变选中radiobutton的选中状态和文本颜色
                rbMe.setChecked(true);
                rbMe.setTextColor(UIUtils.getColor(R.color.white));
                break;
        }
        transaction.commit();
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

    //隐藏所有的fragment
    private void hideFragments() {
        if (meFragment != null) {
            transaction.hide(meFragment);
        }
        if (temperMonitorFragment != null) {
            transaction.hide(temperMonitorFragment);
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
