package com.krisyu.easybox.activity.bottom_navigation.normal.message_fragment;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;

import com.krisyu.easybox.R;
import com.krisyu.easybox.base.BaseActivity;


/**
 *    1、 销毁活动后 传送的数据类型
 *    2、 完成ChatActivity的其余部分
 *    3、
 *
 *
 */

public class ChatActivity extends BaseActivity {
    private ActionBar actionBar;
    private Toolbar toolbar;
    private TextView toolbar_title;

    private MessageListItem friendData;


    public static void actionStart(Activity context, MessageListItem friendData){
        Intent intent = new Intent(context, ChatActivity.class);
        intent.putExtra("friendData", friendData);
        context.startActivityForResult(intent,1);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        friendData = (MessageListItem)getIntent().getSerializableExtra("friendData");
        initView();

    }

    @Override
    protected  int getContentViewId(){
        return R.layout.activity_chat;
    }

    @Override
    public void onBackPressed(){
        Intent intent1 = new Intent();
        intent1.putExtra("returnDataFromChat", "returnDataFromChat1111");
        setResult(RESULT_OK, intent1);
        finish();
    }


    /**
     * UI初始化
     */
    private void initView(){
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
