package com.krisyu.easybox.activity.bottom_navigation.normal.message_fragment;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.krisyu.easybox.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

/**
 *     1、 解决时区的问题
 *     2、 解决聊天界面的问题
 *     3、 解决好友列表和添加列表的问题
 *     4、 解决点击头像生成的界面的问题
 *     5、 解决搜索问题
 *     6、 解决数据发到 Fragment的问题
 */

public class MessageFragment extends Fragment implements View.OnClickListener, PopupMenu.OnMenuItemClickListener{
    private Context mContext = null;
    private static final String TAG = "MessageFragment";

    private SearchView searchView_forMessage;
    private Button friendList;
    private Button plus;
    private TextView numOf_unreadMessage;
    private TextView clearUnread;
    private RecyclerView messageRecyclerView;

    private List<MessageListItem> msgLists;
    private MessageListAdapter msgListAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View messageFragment = inflater.inflate(R.layout.fragment_message, container, false);
        mContext = getActivity();

        initData();
        initView(messageFragment);
        initEvent();
        Log.e(TAG, "onCreateView: MessageFragment" + MessageFragment.this );
        return messageFragment;
    }

    private void initData(){
        msgLists = new ArrayList<>();
        SimpleDateFormat format = new SimpleDateFormat("HH:mm ", Locale.CHINA);
        format.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
        String msgContent = "ContentMessage";

        msgLists.add(new MessageListItem(R.drawable.default_head_pic, "KrisYu",
                msgContent + 10000, format.format(new Date()), 1));
        msgLists.add(new MessageListItem(R.drawable.default_head_pic, "Micheal",
                msgContent + 20000, format.format(new Date()), 30));
        msgLists.add(new MessageListItem(R.drawable.default_head_pic, "Rich",
                msgContent + 30000, format.format(new Date()), 99));


    }

    private void initView(View view){
        searchView_forMessage = (SearchView)view.findViewById(R.id.message_searchView);
        friendList = (Button)view.findViewById(R.id.message_btn_friend_list);
        plus = (Button)view.findViewById(R.id.message_btn_plus);
        numOf_unreadMessage = (TextView)view.findViewById(R.id.message_tv_unread);
        clearUnread = (TextView)view.findViewById(R.id.message_tv_clear_msg);
        messageRecyclerView = (RecyclerView)view.findViewById(R.id.message_recyclerview);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        msgListAdapter = new MessageListAdapter(msgLists, mContext);
        messageRecyclerView.setHasFixedSize(true);
        messageRecyclerView.setLayoutManager(layoutManager);
        messageRecyclerView.setAdapter(msgListAdapter);

    }

    private void initEvent(){
        friendList.setOnClickListener(MessageFragment.this);
        plus.setOnClickListener(MessageFragment.this);
        clearUnread.setOnClickListener(MessageFragment.this);
    }

    // 重写onClick方法
    @Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.message_btn_friend_list:
                Toast.makeText(mContext, "好友列表", Toast.LENGTH_SHORT).show();
                break;
            case R.id.message_btn_plus:
                // 创建弹出式菜单对象
                PopupMenu popupMenu = new PopupMenu(mContext, v);
                // 获取菜单填充器
                MenuInflater inflater = popupMenu.getMenuInflater();
                // 填充菜单
                inflater.inflate(R.menu.message_plus_menu, popupMenu.getMenu());
                // 绑定菜单项的点击事件
                popupMenu.setOnMenuItemClickListener(this);
                popupMenu.show();
                break;
            case R.id.message_tv_clear_msg:
                Toast.makeText(mContext, "清楚未读", Toast.LENGTH_SHORT).show();
                break;
            default:
                Toast.makeText(mContext, "onClick(): 非法 ID", Toast.LENGTH_SHORT).show();
        }
    }

    // 重写 onMenuItemClick 方法
    @Override
    public boolean onMenuItemClick(MenuItem item){
        switch (item.getItemId()){
            case R.id.add_new_friend:
                Toast.makeText(mContext, "添加好友", Toast.LENGTH_SHORT).show();
                break;
            case R.id.for_adding:
                Toast.makeText(mContext, "待添加", Toast.LENGTH_SHORT).show();
                break;
             default:
                 Toast.makeText(mContext, "onMenuItemClick(): 非法MenuItem ID", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    public List<MessageListItem> getMsgLists(){
        return msgLists;
    }
    public MessageListAdapter getMsgListAdapter(){
        return msgListAdapter;
    }
    public void notifyAdapterDataChanged(){
        getMsgListAdapter().notifyDataSetChanged();
    }
}
