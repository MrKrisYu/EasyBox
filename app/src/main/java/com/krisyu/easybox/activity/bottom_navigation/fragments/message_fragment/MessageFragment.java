package com.krisyu.easybox.activity.bottom_navigation.fragments.message_fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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
import com.krisyu.easybox.activity.bottom_navigation.NormalActivity;
import com.krisyu.easybox.adapter.MessageListAdapter;
import com.krisyu.easybox.mode.MessageListItem;
import com.krisyu.easybox.utils.LogUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

/**
 *   √  1、 解决时区的问题
 *   √  2、 解决聊天界面的问题
 *     3、 解决好友列表和添加列表的问题
 *     4、 解决点击头像生成的界面的问题
 *     5、 解决搜索问题
 *   √  6、 解决数据发到 Fragment的问题
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
//-----------------------------------------Activity相关方法------------------------------------------
    public List<MessageListItem> getMsgLists(){
        return msgLists;
    }
    public MessageListAdapter getMsgListAdapter(){
        return msgListAdapter;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View messageFragment = inflater.inflate(R.layout.fragment_message, container, false);
        mContext = getActivity();

        initData();
        initView(messageFragment);
        initEvent();
        LogUtil.e(TAG, "onCreateView: MessageFragment" + MessageFragment.this );
        return messageFragment;
    }


//---------------------------------------------初始化方法--------------------------------------------
    private void initData(){
        msgLists = new ArrayList<>();
        SimpleDateFormat format = new SimpleDateFormat("HH:mm ", Locale.CHINA);
        format.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
        String msgContent = "ContentMessage";

        msgLists.add(new MessageListItem(R.drawable.default_head_pic, "KrisYu",
                msgContent + 10000, format.format(new Date()), 1));
        msgLists.add(new MessageListItem(R.drawable.default_head_pic, "Micheal",
                msgContent + 20000, format.format(new Date()), 30));
//        msgLists.add(new MessageListItem(R.drawable.default_head_pic, "Rich",
//                msgContent + 30000, format.format(new Date()), 99));


    }

    private void initView(View view){
        searchView_forMessage = (SearchView)view.findViewById(R.id.message_searchView);
        friendList = (Button)view.findViewById(R.id.message_btn_friend_list);
        plus = (Button)view.findViewById(R.id.message_btn_plus);
        numOf_unreadMessage = (TextView)view.findViewById(R.id.message_tv_unread);
        clearUnread = (TextView)view.findViewById(R.id.message_tv_clear_msg);
        clearUnread.setOnClickListener(MessageFragment.this);
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


//------------------------------------------接口方法------------------------------------------------
    // 重写onClick方法
    @Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.message_btn_friend_list:
                startActivity(new Intent(getActivity(), FriendListActivity.class));
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
                Toast.makeText(mContext, "清除未读", Toast.LENGTH_SHORT).show();
                SimpleDateFormat format = new SimpleDateFormat("HH:mm ", Locale.CHINA);
                format.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
                notifyAdapterItemMoved(new MessageListItem(R.drawable.default_head_pic, "111",
                        "TEST", format.format(new Date()), 1),
                        msgLists.size()-1, 0);
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





//-----------------------------------------通知UI数据改变方法------------------------------------------
    /**
     *  通知 适配器数据改变了
     */
    public void notifyAdapterDataChanged(){
        msgListAdapter.notifyDataSetChanged();
    }

    /**
     * 通知RecyclerViewAdapter 要进行将数据源中fromPosition索引的item移至toPosition索引处
     * @param item 要插入的数据，若为null则不插入
     * @param fromPosition 原item的索引
     * @param toPosition    目标索引
     */
    public void notifyAdapterItemMoved(MessageListItem item, int fromPosition, int toPosition){
        if(null != item){
            msgLists.add(item);
            fromPosition++;
        }else{
            LogUtil.i(TAG, "notifyAdapterItemMoved():插入的item为空");
        }
        Collections.swap(msgLists, fromPosition, toPosition);
        msgListAdapter.notifyItemMoved(fromPosition, toPosition); // 当前有新消息时，刷新ListView中的显示
        messageRecyclerView.scrollToPosition(0);

    }
}
