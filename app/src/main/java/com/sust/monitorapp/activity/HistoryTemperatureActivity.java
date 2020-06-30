package com.sust.monitorapp.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.reflect.TypeToken;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.interfaces.OnSelectListener;
import com.sust.monitorapp.R;
import com.sust.monitorapp.bean.MyResponse;
import com.sust.monitorapp.ui.LineChartMarkView;
import com.sust.monitorapp.util.DateUtil;
import com.sust.monitorapp.util.JsonUtil;
import com.sust.monitorapp.util.NetUtil;
import com.sust.monitorapp.util.UIUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Response;

/**
 * Created by yhl on 2020/3/12.
 *
 * 绕组温度历史数据显示。由于数据量太大，这里只显示近三天的数据，否则影响显示效果
 */

public class HistoryTemperatureActivity extends AppCompatActivity {

    @BindView(R.id.title_back)
    RelativeLayout titleBack;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.chart_history_temperature)
    LineChart chartHistoryTemperature;
    @BindView(R.id.view_shuxian)
    View viewShuxian;
    @BindView(R.id.bt_choose_time)
    Button btChooseTime;
    @BindView(R.id.rl_choose_time)
    RelativeLayout rlChooseTime;
    @BindView(R.id.tv_time)
    TextView tvTime;

    /**
     * LineChart相关对象
     */
    private XAxis xAxis;                //X轴
    private YAxis leftYAxis;            //左侧Y轴
    private YAxis rightYaxis;           //右侧Y轴
    private Legend legend;              //图例
    private LimitLine limitLine;        //限制线

    //折线图数据
    private List<Entry> entryList = new ArrayList<>();

    //服务器返回的数据
    private LinkedHashMap<String, Float> dataMap = new LinkedHashMap<>();

    //x轴日期数据
    ArrayList<String> dateList = new ArrayList<>();

    //时间长度选项
    private String[] timeOptions = new String[]{"近三天", "近一年", "自开始以来"};

    //不同时间长度对应的http method
    private String[] methods = new String[]{"/api/get_threeday_temperature", "", ""};

    //当前页面数据是哪个设备的
    private String devMac;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_temperature);
        ButterKnife.bind(this);

        initView();
        initChart(chartHistoryTemperature);

        //默认显示近三天数据
        showLineChart(0, UIUtils.getColor(R.color.linechart_line));
    }

    private void initView() {
        tvTitle.setText("历史温度");
        //获取前一个activity传来的id数据
        Intent intent = getIntent();
        devMac = intent.getStringExtra("devMac");

    }

    /**
     * 选择时间按钮点击事件：近三天、近一年、自开始以来
     */
    @OnClick(R.id.bt_choose_time)
    public void onBtChooseTimeClicked() {
        //点击按钮弹出时间跨度选项
        new XPopup.Builder(HistoryTemperatureActivity.this)
                .atView(rlChooseTime)
                .asAttachList(
                        timeOptions,
                        new int[]{R.mipmap.icon_time, R.mipmap.icon_time, R.mipmap.icon_time},
                        new OnSelectListener() {
                            @Override
                            public void onSelect(int position, String text) {
                                Toast.makeText(HistoryTemperatureActivity.this, "click " + text, Toast.LENGTH_SHORT).show();
                                tvTime.setText(text);
                                showLineChart(position, UIUtils.getColor(R.color.alipay_blue));
                            }
                        }).show();
    }

    /**
     * @param position 时间选项中的位置 0近三天、1近一年、2自开始以来
     * @param color 折线颜色
     */
    private void showLineChart(int position, int color) {
        chartHistoryTemperature.clear();
        entryList.clear();

        new Thread(()->{
            //拼装对应时间的url
            String url = methods[position] + "?devMac=" + devMac;
            Looper.prepare();
            try {
                Response response = NetUtil.get(url);
                if (response.isSuccessful()) {
                    //获取数据成功，放入map
                    MyResponse myResponse = JsonUtil.jsonToBean(response.body().string(), MyResponse.class);
                    dataMap = JsonUtil.jsonToBean(myResponse.getData(),
                            new TypeToken<LinkedHashMap<String, Float>>() {
                            }.getType());
                    //将数据解析为对应的格式。这里保留y轴的值。x轴因为是日期不易解析，采用索引位置代替，
                    //等到具体显示时再通过索引从 dateList 中取
                    int i = 0;
                    for (Map.Entry<String, Float> tempEntry : dataMap.entrySet()) {
                        Entry entry = new Entry(i++, tempEntry.getValue());
                        entryList.add(entry);
                        //保存下日期数据，之后方便取用。
                        dateList.add(tempEntry.getKey());
                    }

                    //获取数据成功后，设置chart
                    Message message = new Message();
                    message.arg1 = position;
                    message.arg2 = color;
                    handler.sendMessage(message);
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

    //更新lineChart数据
    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message message) {

            if (!entryList.isEmpty()) {
                LineDataSet lineDataSet = new LineDataSet(entryList, timeOptions[message.arg1] + "数据");
                initLineDataSet(lineDataSet, message.arg2, LineDataSet.Mode.CUBIC_BEZIER);
                LineData lineData = new LineData(lineDataSet);
                chartHistoryTemperature.setData(lineData);
                //设置点击某个点时弹出view显示具体温度和时间
                LineChartMarkView mv = new LineChartMarkView(HistoryTemperatureActivity.this, xAxis.getValueFormatter());
                mv.setChartView(chartHistoryTemperature);
                chartHistoryTemperature.setMarker(mv);
                chartHistoryTemperature.invalidate();
            }
            return false;
        }
    });

    /**
     * 初始化图表
     *
     * x，y轴显示坐标的分隔长度应该根据数据的不同分别设置
     */
    private void initChart(LineChart lineChart) {
        /***图表设置***/
        //是否展示网格线
        lineChart.setDrawGridBackground(false);
        //是否显示边界
        lineChart.setDrawBorders(true);
        //是否可以拖动
        lineChart.setDragEnabled(true);
        //是否有触摸事件
        lineChart.setTouchEnabled(true);
        //设置XY轴动画效果
        lineChart.animateY(2500);
        lineChart.animateX(1500);
        //背景色
        lineChart.setBackgroundColor(Color.WHITE);
        //是否显示边界
        lineChart.setDrawBorders(false);
        //去掉右下角描述标签
        Description description = new Description();
        description.setEnabled(false);
        lineChart.setDescription(description);

        /***XY轴的设置***/
        xAxis = lineChart.getXAxis();
        leftYAxis = lineChart.getAxisLeft();
        rightYaxis = lineChart.getAxisRight();
        //x，y轴文本颜色
        xAxis.setTextColor(UIUtils.getColor(R.color.black));
        leftYAxis.setTextColor(UIUtils.getColor(R.color.black));
        rightYaxis.setTextColor(UIUtils.getColor(R.color.black));
        //X轴设置显示位置在底部
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

        //x轴最值
        xAxis.setAxisMinimum(0f);
        xAxis.setGranularity(1f);

        //x轴标签格式以及等分多少份
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                //获取对应索引位置的日期数据
                String dateStr = dateList.get(((int) value) % dateList.size());
                //对日期进行格式化输出
                return DateUtil.formatDate(dateStr);
            }
        });
        xAxis.setLabelCount(3);
        //y轴显示最值
        leftYAxis.setAxisMaximum(40);
        leftYAxis.setAxisMinimum(-10);

        //y轴标签格式以及等分多少份
        leftYAxis.setLabelCount(10);
        leftYAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return (int) value + "℃";
            }
        });
        //x，y轴网格线设置成虚线
        xAxis.setDrawGridLines(false);
        rightYaxis.setDrawGridLines(false);
        leftYAxis.setDrawGridLines(true);
        leftYAxis.enableGridDashedLine(10f, 10f, 0f);
        //去掉右侧y轴
        rightYaxis.setEnabled(false);

        //设置警告线
        LimitLine highLimit = new LimitLine(33, "警报线");
        highLimit.setLineWidth(1f);
        highLimit.setTextSize(10f);
        highLimit.setLineColor(UIUtils.getColor(R.color.red));
        highLimit.setTextColor(UIUtils.getColor(R.color.red));
        leftYAxis.addLimitLine(highLimit);

        /***折线图例 标签 设置***/
        legend = lineChart.getLegend();
        //设置显示类型，LINE CIRCLE SQUARE EMPTY 等等 多种方式，查看LegendForm 即可
        legend.setForm(Legend.LegendForm.LINE);
        legend.setTextSize(12f);
        //显示位置 左下方
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        //是否绘制在图表里面
        legend.setDrawInside(false);
    }


    /**
     * 曲线初始化设置 一个LineDataSet 代表一条曲线
     *
     * @param lineDataSet 线条
     * @param color       线条颜色
     * @param mode
     */
    private void initLineDataSet(LineDataSet lineDataSet, int color, LineDataSet.Mode mode) {
        lineDataSet.setColor(color);
        lineDataSet.setCircleColor(color);
        //线宽
        lineDataSet.setLineWidth(1f);
        lineDataSet.setCircleRadius(3f);
        //点击某个点时，横竖两条线的颜色
        lineDataSet.setHighLightColor(UIUtils.getColor(R.color.colorPrimaryDark));
        lineDataSet.setDrawHorizontalHighlightIndicator(false);
        //设置曲线值的圆点是实心还是空心 不绘制圆洞，即为实心圆点
        lineDataSet.setDrawCircleHole(false);
        lineDataSet.setValueTextSize(10f);
        //不绘制点
        lineDataSet.setDrawCircles(false);
        //不显示y值
        lineDataSet.setDrawValues(false);
        //设置折线图填充
        lineDataSet.setDrawFilled(true);
        lineDataSet.setFormLineWidth(1f);
        lineDataSet.setFormSize(15.f);
        if (mode == null) {
            //设置曲线展示为圆滑曲线（如果不设置则默认折线）
            lineDataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        } else {
            lineDataSet.setMode(mode);
        }
    }


    /**
     * 顶部返回按钮点击事件
     */
    @OnClick(R.id.title_back)
    public void onViewClicked() {
        finish();
    }
}
