package com.krisyu.easybox.activity.bottom_navigation.normal.message_fragment;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.krisyu.easybox.R;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder>{
    private Context mContext;
    private List<ChatItem> chatItems;
    private static final String TAG = "ChatAdapter";
    public ChatAdapter(Context context, List<ChatItem> list){
        this.mContext = context;
        this.chatItems = list;
    }

    static class ChatViewHolder extends RecyclerView.ViewHolder{
        private RelativeLayout leftRelativaLayout;
        private TextView leftTime;
        private ImageView leftHeadImg;
        private TextView leftContent;

        private RelativeLayout rightRelativaLayout;
        private TextView rightTime;
        private ImageView rightHeadImg;
        private TextView rightContent;

        public ChatViewHolder(final View itemView){
            super(itemView);
            // 加载View 的ID
            leftRelativaLayout = (RelativeLayout)itemView.findViewById(R.id.chatitem_left);
            leftTime = (TextView)itemView.findViewById(R.id.chatitem_left_tv_time);
            leftHeadImg = (ImageView)itemView.findViewById(R.id.chatitem_left_head_pic);
            leftContent = (TextView)itemView.findViewById(R.id.chatitem_left_tv_content);

            rightRelativaLayout = (RelativeLayout)itemView.findViewById(R.id.chatitem_right);
            rightTime = (TextView)itemView.findViewById(R.id.chatitem_right_tv_time);
            rightHeadImg = (ImageView)itemView.findViewById(R.id.chatitem_right_head_pic);
            rightContent = (TextView)itemView.findViewById(R.id.chatitem_right_tv_content);
        }
    }


    // 重写Adapter接口的三个方法
    @Override
    public int getItemCount(){
        return chatItems.size();
    }
    @Override
    public ChatAdapter.ChatViewHolder onCreateViewHolder(@NonNull ViewGroup container, int viewType){
        return new ChatAdapter.ChatViewHolder(LayoutInflater.from(mContext).
                inflate(R.layout.activity_chatitem, container, false));
    }
    @Override
    public void onBindViewHolder(@NonNull ChatAdapter.ChatViewHolder holder, int position){

        final ChatItem chatItem = chatItems.get(position);

        if(chatItem.getType() == ChatItem.MESSAGE_RECEIVED){
            // 显示对方聊天消息
            holder.rightRelativaLayout.setVisibility(View.GONE);
            holder.leftRelativaLayout.setVisibility(View.VISIBLE);
            holder.leftHeadImg.setImageResource(chatItem.getHeadImgId());
            holder.leftTime.setText(chatItem.getTime());
            holder.leftContent.setText(chatItem.getContent());
        }else if(chatItem.getType() == ChatItem.MESSAGE_SEND){
            // 显示自己的聊天消息
            holder.leftRelativaLayout.setVisibility(View.GONE);
            holder.rightRelativaLayout.setVisibility(View.VISIBLE);
            holder.rightHeadImg.setImageResource(chatItem.getHeadImgId());
            holder.rightTime.setText(chatItem.getTime());
            holder.rightContent.setText(chatItem.getContent());
        }else{
            Log.e(TAG, "onBindViewHolder: " + "ChatItem的Type 成员变量值 错误。" );
            holder.leftRelativaLayout.setVisibility(View.GONE);
            holder.rightRelativaLayout.setVisibility(View.GONE);
        }
    }


}
