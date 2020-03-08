package com.sust.monitorapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sust.monitorapp.R;
import com.sust.monitorapp.util.MyHttp;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;

import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Response;

/**
 * Created by yhl on 2020/3/4.
 */

public class ModifyOwnInfoActivity extends AppCompatActivity {

    @BindView(R.id.title_back)
    RelativeLayout titleBack;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.bt_title_save)
    Button btTitleSave;
    @BindView(R.id.et_modify_own_info)
    EditText etModifyOwnInfo;
    @BindView(R.id.rg_modify_own_sex)
    RadioGroup rgModifyOwnSex;
    @BindView(R.id.rb_ownsex_male)
    RadioButton rbOwnsexMale;
    @BindView(R.id.rb_ownsex_female)
    RadioButton rbOwnsexFemale;

    /*
    判断要修改的字段
    1:username 2:sex 3:email 4:tel
     */
    private int flag = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_own_info);
        ButterKnife.bind(this);

        initView();
    }

    private void initView() {
        ButterKnife.bind(this);
        btTitleSave.setVisibility(Button.VISIBLE);
        btTitleSave.setClickable(true);
        Intent intent = getIntent();
        String[] info = intent.getStringArrayExtra("info");
        //根据intent中的info[0]判断要修改的是哪个字段，修改对应的标题、flag
        switch (StringUtils.defaultIfBlank(info[0], "default")) {
            case "username":
                flag = 1;
                etModifyOwnInfo.setText(info[1]);
                etModifyOwnInfo.setSelection(etModifyOwnInfo.length());
                tvTitle.setText("修改用户名");
                break;
            case "sex":
                flag = 2;
                tvTitle.setText("修改性别");
                etModifyOwnInfo.setVisibility(View.GONE);
                rgModifyOwnSex.setVisibility(View.VISIBLE);
                if (StringUtils.equals(info[1], "男")) {
                    rbOwnsexMale.setChecked(true);
                } else {
                    rbOwnsexFemale.setChecked(true);
                }
                break;
            case "email":
                flag = 3;
                etModifyOwnInfo.setText(info[1]);
                etModifyOwnInfo.setSelection(etModifyOwnInfo.length());
                tvTitle.setText("修改邮箱地址");
                break;
            case "tel":
                flag = 4;
                etModifyOwnInfo.setText(info[1]);
                etModifyOwnInfo.setSelection(etModifyOwnInfo.length());
                tvTitle.setText("修改电话号码");
                break;
            default:
                break;
        }
    }

    @OnClick(R.id.bt_title_save)
    public void buttonTitleSave() {
        String baseurl = "/api/modify_user_field";
        switch (flag) {
            case 1:
                //username
                String username = String.valueOf(etModifyOwnInfo.getText());
                String url1 = baseurl + "?username=" + username;
                httpConn(url1);
                break;
            case 2:
                //sex
                String sex;
                if (rbOwnsexMale.isChecked()) {
                    sex = "男";
                } else {
                    sex = "女";
                }
                System.out.println("sex=" + sex);
                String url2 = baseurl + "?sex=" + sex;
                httpConn(url2);
                break;
            case 3:
                //email
                String email = String.valueOf(etModifyOwnInfo.getText());
                String url3 = baseurl + "?email=" + email;
                httpConn(url3);
                break;
            case 4:
                //tel
                String tel = String.valueOf(etModifyOwnInfo.getText());
                String url4 = baseurl + "?tel=" + tel;
                httpConn(url4);
                break;
            default:
                break;
        }
    }

    //抽取发送数据的重复代码
    private void httpConn(String url) {
        new Thread(() -> {
            Looper.prepare();
            try {
                Response response = MyHttp.get(url);
                if (response.isSuccessful()) {
                    //修改成功，退出当前页面
                    Toast.makeText(ModifyOwnInfoActivity.this, "成功", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), "操作失败，请检查网络连接", Toast.LENGTH_SHORT).show();
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                Looper.loop();
            }
        }).start();
    }


    /**
     * 顶部返回按钮点击事件
     */
    @OnClick(R.id.title_back)
    public void onViewClicked() {
        finish();
    }
}
