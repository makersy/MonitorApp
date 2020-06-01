package com.sust.monitorapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.sust.monitorapp.R;
import com.sust.monitorapp.bean.Device;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by yhl on 2020/5/18.
 */
public class ActionRecordAdapter extends RecyclerView.Adapter<ActionRecordAdapter.RecordViewHolder> {


    //数据源
    private ArrayList<String> data;

    //上下文
    private Context context;

    public ActionRecordAdapter(ArrayList<String> data, Context context) {
        this.data = data;
        this.context = context;
    }

    @NonNull
    @Override
    public ActionRecordAdapter.RecordViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //绑定自己实现的 RecyclerView 的每一行 item
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_acticity_history_action, parent, false);
        return new ActionRecordAdapter.RecordViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecordViewHolder holder, int position) {
        String record = data.get(position);
        holder.tvActionRecord.setText(record);
    }


    //创建MyViewHolder继承RecyclerView.ViewHolder
    public class RecordViewHolder extends RecyclerView.ViewHolder {

        TextView tvActionRecord;

        //ViewHolder构造方法，将控件与对象进行绑定
        public RecordViewHolder(@NonNull View itemView) {
            super(itemView);
            tvActionRecord = itemView.findViewById(R.id.tv_action_record);
        }
    }

    /**
     * 有多少个 item？
     *
     * @return
     */
    @Override
    public int getItemCount() {
        return data.size();
    }
}
