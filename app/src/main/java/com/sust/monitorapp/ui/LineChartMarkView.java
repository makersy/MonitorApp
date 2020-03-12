package com.sust.monitorapp.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.widget.TextView;

import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.MPPointF;
import com.sust.monitorapp.R;

import java.text.DecimalFormat;

/**
 * Created by yhl on 2020/3/12.
 */
public class LineChartMarkView extends MarkerView {
    private TextView tvDate;
    private TextView tvValue;
    private ValueFormatter xAxisValueFormatter;
    DecimalFormat df = new DecimalFormat(".00");

    public LineChartMarkView(Context context, ValueFormatter xAxisValueFormatter) {
        super(context, R.layout.layout_markview);
        this.xAxisValueFormatter = xAxisValueFormatter;

        tvDate = findViewById(R.id.tv_date);
        tvValue = findViewById(R.id.tv_value);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void refreshContent(Entry e, Highlight highlight) {
        //展示自定义X轴值 后的X轴内容
        tvDate.setText(xAxisValueFormatter.getFormattedValue(e.getX()));
        tvValue.setText("温度：" + df.format(e.getY()) + "℃");
        super.refreshContent(e, highlight);
    }

    @Override
    public MPPointF getOffset() {
        return new MPPointF(-(getWidth() / 2), -getHeight());
    }
}
