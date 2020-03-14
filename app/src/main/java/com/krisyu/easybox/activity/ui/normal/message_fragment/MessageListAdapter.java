package com.krisyu.easybox.activity.ui.normal.message_fragment;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.krisyu.easybox.R;
import com.krisyu.easybox.activity.OtherActivity;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageListAdapter extends RecyclerView.Adapter<MessageListAdapter.MsgListViewHolder>{
    private List<MessageListItem> msgListItems;
    private Context mContext;

    public MessageListAdapter(List<MessageListItem> sourceData, Context context){
        this.msgListItems = sourceData;
        this.mContext = context;
    }

    // Holder类
    static class MsgListViewHolder extends RecyclerView.ViewHolder{
        RelativeLayout relativeLayout;
        CircleImageView headPic;
        TextView userName;
        TextView content;
        TextView time;
        TextView unreadNum;

        public MsgListViewHolder(final View itemView){
            super(itemView);
            relativeLayout = (RelativeLayout)itemView.findViewById(R.id.msgitem_relativeLayout);
            headPic = (CircleImageView)itemView.findViewById(R.id.msgitem_cirimg_headpic);
            userName = (TextView)itemView.findViewById(R.id.msgitem_tv_username);
            content = (TextView)itemView.findViewById(R.id.msgitem_tv_brief_msg);
            time = (TextView)itemView.findViewById(R.id.msgitem_tv_time);
            unreadNum = (TextView)itemView.findViewById(R.id.msgitem_tv_num_of_unread);
        }
    }

    // 重写RecyclerView.Adapter<>的 三个方法
    @Override
    public MessageListAdapter.MsgListViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        return new MessageListAdapter.MsgListViewHolder(LayoutInflater.from(mContext).
                inflate(R.layout.fragment_message_msgitem, parent, false));
    }
    @Override
    public void onBindViewHolder(MessageListAdapter.MsgListViewHolder holder, int position){
        // 给Holder的控件元素赋值
        holder.headPic.setImageResource(msgListItems.get(position).getHeadImageId());
        holder.userName.setText(msgListItems.get(position).getUserName());
        holder.content.setText(msgListItems.get(position).getContent());
        holder.time.setText(msgListItems.get(position).getTime());
        holder.unreadNum.setText(Integer.toString(msgListItems.get(position).getNumberOfunread()));
        holder.relativeLayout.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(mContext, OtherActivity.class);
                mContext.startActivity(intent);
            }
        });
    }
    @Override
    public int getItemCount(){
        return msgListItems.size();
    }




}
