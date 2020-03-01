package com.sust.monitorapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.sust.monitorapp.R;
import com.sust.monitorapp.bean.User;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by yhl on 2020/2/29.
 */

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    //数据源
    private List<User> data;

    //上下文
    private Context context;

    public UserAdapter(List<User> data, Context context) {
        this.data = data;
        this.context = context;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //绑定自己实现的 RecyclerView 的每一行 item
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_activity_select_users, parent, false);
        return new UserViewHolder(view);
    }


    //创建MyViewHolder继承RecyclerView.ViewHolder
    public class UserViewHolder extends RecyclerView.ViewHolder {

        TextView tvUserid;
        TextView tvUsername;
        Button btDelUser;
        Button btUserMoreInfo;

        //ViewHolder构造方法，将控件与对象进行绑定
        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUserid = itemView.findViewById(R.id.tv_userid);
            tvUsername = itemView.findViewById(R.id.tv_username);
            btDelUser = itemView.findViewById(R.id.bt_del_user);
            btUserMoreInfo = itemView.findViewById(R.id.bt_user_more_info);

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
    private onItemClickListener onItemClickListener;

    public void setOnItemClickListener(onItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = data.get(position);
        holder.tvUserid.setText(user.getUserId());
        holder.tvUsername.setText(user.getUsername());

        if (onItemClickListener != null) {
            //为item绑定点击事件
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onItemClickListener.onItemClick(view, holder.getLayoutPosition());
                }
            });

            holder.btDelUser.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onItemClickListener.onItemClick(view, holder.getLayoutPosition());
                }
            });

            holder.btUserMoreInfo.setOnClickListener(new View.OnClickListener() {
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
