package com.krisyu.easybox.activity.bottom_navigation.normal.message_fragment;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.krisyu.easybox.R;
import com.krisyu.easybox.base.BaseActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;


/**
 *    1、 销毁活动后 传送的数据类型
 *    2、 完成ChatActivity的其余部分: 数据的保存
 *    3、
 *
 *
 */

public class ChatActivity extends BaseActivity {
    private ActionBar actionBar;
    private Toolbar toolbar;
    private TextView toolbar_title;
    private EditText inputText;
    private Button btn_sendMsg;
    private RecyclerView chatRecyclerView;

    private List<ChatItem> chatItemList;
    private MessageListItem friendData;
    private ChatAdapter chatAdapter;
    private static final String TAG = "ChatActivity";

    public static void actionStart(Activity context, MessageListItem friendData){
        Intent intent = new Intent(context, ChatActivity.class);
        intent.putExtra("friendData", friendData);
        context.startActivityForResult(intent,1);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        friendData = (MessageListItem)getIntent().getSerializableExtra("friendData");
        initData();
        initView();

    }

    @Override
    protected  int getContentViewId(){
        return R.layout.activity_chat;
    }

    @Override
    public void onBackPressed(){
        Intent intent1 = new Intent();
        intent1.putExtra("returnDataFromChat", chatItemList.get(chatItemList.size() - 1));
        setResult(RESULT_OK, intent1);
        finish();
    }


    /**
     * UI初始化
     */
    private void initView(){
        // 顶部标题栏 ToolBar
        toolbar = (Toolbar)findViewById(R.id.chat_toolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        if(null !=actionBar){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.back);
        }
        toolbar_title = (TextView)findViewById(R.id.toolbar_friend_name);
        toolbar_title.setText(friendData.getUserName());

        inputText = (EditText)findViewById(R.id.chat_et_input);

        // 聊天消息显示控件 RecyclerView
        chatRecyclerView = (RecyclerView)findViewById(R.id.chat_recyclerview);
        LinearLayoutManager layoutManager = new LinearLayoutManager(ChatActivity.this);
        chatRecyclerView.setHasFixedSize(true);
        chatAdapter = new ChatAdapter(ChatActivity.this,chatItemList);
        chatRecyclerView.setLayoutManager(layoutManager);
        chatRecyclerView.setAdapter(chatAdapter);

        // 发送消息的按钮 Button
        btn_sendMsg = (Button)findViewById(R.id.chat_btn_send);
        btn_sendMsg.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                String content = inputText.getText().toString();
                if(!TextUtils.isEmpty(content)){
                    SimpleDateFormat format = new SimpleDateFormat("y-M-d HH:mm ", Locale.CHINA);
                    format.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
                    ChatItem msg = new ChatItem(format.format(new Date()), R.drawable.friend, content, ChatItem.MESSAGE_SEND,friendData.getUserName());
                    chatItemList.add(msg);
                    chatAdapter.notifyItemInserted(chatItemList.size() - 1); // 当前有新消息时，刷新ListView中的显示
                    chatRecyclerView.scrollToPosition(chatItemList.size() - 1); // 将RecyclerView 定位到最后一行
                    inputText.setText("");  // 清空输入行
                }
            }
        });


    }

    /**
     * 数据初始化
     */
    private void initData(){

        SimpleDateFormat format = new SimpleDateFormat("y-M-d HH:mm ", Locale.CHINA);
        format.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));

        chatItemList = new ArrayList<>();
        chatItemList.add(new ChatItem(format.format(new Date()), friendData.getHeadImageId(), "您好！", ChatItem.MESSAGE_RECEIVED,friendData.getUserName()));
        // 这里应该用自己的用户名，暂用好友名字代替
        chatItemList.add(new ChatItem(format.format(new Date()), R.drawable.friend, "您好！", ChatItem.MESSAGE_SEND,friendData.getUserName()));
        chatItemList.add(new ChatItem(format.format(new Date()), friendData.getHeadImageId(), friendData.getContent(), ChatItem.MESSAGE_RECEIVED,friendData.getUserName()));

    }


    // 重写工具栏菜单的 创建接口方法
    @Override
    public boolean onCreateOptionsMenu(Menu menu){

        getMenuInflater().inflate(R.menu.chat_toolbar_menu, menu);
        return true;
    }
    // 重写工具栏中条目的 单击单击方法
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.friend_info:
                Toast.makeText(ChatActivity.this, "好友信息详情", Toast.LENGTH_SHORT).show();
                break;
            case android.R.id.home:
                Intent intent1 = new Intent();
                intent1.putExtra("returnDataFromChat", "returnDataFromChat1111");
                setResult(RESULT_OK, intent1);
                finish();
                break;
            default:
                Toast.makeText(ChatActivity.this, "onOptionsItemSelected(): 非法ID", Toast.LENGTH_SHORT).show();
                break;
        }

        return true;
    }
}
