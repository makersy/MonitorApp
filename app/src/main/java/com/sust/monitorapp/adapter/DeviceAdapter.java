package com.sust.monitorapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.sust.monitorapp.R;
import com.sust.monitorapp.bean.Device;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by yhl on 2020/3/7.
 */
public class DeviceAdapter extends RecyclerView.Adapter<DeviceAdapter.DeviceViewHolder>{


    //数据源
    private List<Device> data;

    //上下文
    private Context context;

    public DeviceAdapter(List<Device> data, Context context) {
        this.data = data;
        this.context = context;
    }

    @NonNull
    @Override
    public DeviceAdapter.DeviceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //绑定自己实现的 RecyclerView 的每一行 item
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_activity_select_devices, parent, false);
        return new DeviceAdapter.DeviceViewHolder(view);
    }


    //创建MyViewHolder继承RecyclerView.ViewHolder
    public class DeviceViewHolder extends RecyclerView.ViewHolder {

        TextView tvDevId;
        TextView tvDevName;
        Button btDevMoreInfo;

        //ViewHolder构造方法，将控件与对象进行绑定
        public DeviceViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDevId = itemView.findViewById(R.id.tv_dev_id);
            tvDevName = itemView.findViewById(R.id.tv_dev_name);
            btDevMoreInfo = itemView.findViewById(R.id.bt_dev_more_info);

        }
    }

    //自定义一个回调接口来实现Click和LongClick事件
    public interface onItemClickListener {
        //item点击事件
        void onItemClick(View view, int position);
        //item长按事件
        void onItemLongClick(View view, int position);
    }

    //声明自定义的接口，以及封装setter方法
    private DeviceAdapter.onItemClickListener onItemClickListener;

    public void setOnItemClickListener(DeviceAdapter.onItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public void onBindViewHolder(@NonNull DeviceAdapter.DeviceViewHolder holder, int position) {
        Device device = data.get(position);
        holder.tvDevId.setText(device.getDevId());
        holder.tvDevName.setText(device.getDevName());

        if (onItemClickListener != null) {
            //为item绑定点击事件
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onItemClickListener.onItemClick(view, holder.getLayoutPosition());
                }
            });

            holder.btDevMoreInfo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onItemClickListener.onItemClick(view, holder.getLayoutPosition());
                }
            });

            //为item绑定长按事件
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    onItemClickListener.onItemLongClick(view, holder.getLayoutPosition());
                    return false;
                }
            });
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
