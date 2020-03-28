package com.krisyu.easybox.activity.bottom_navigation.normal.message_fragment;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.krisyu.easybox.R;
import com.krisyu.easybox.base.BaseActivity;
import com.krisyu.easybox.network.JWebSocketClient;
import com.krisyu.easybox.service.JWebSocketClientService;
import com.krisyu.easybox.utils.LogUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;


/**   注意 --修改--
 *    ** 第一次测试的Demo（HandleMsg）   进度：
 *
 *    1、 销毁活动后 传送的数据类型                   进度：√
 *    2、 完成ChatActivity的其余部分: 数据的保存      进度：
 *    3、 服务保活：通知，数据的刷新                  进度：
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


    private JWebSocketClientService jWebSocketClientService;
    private JWebSocketClientService.JWebSocketClientBinder binder;
    private JWebSocketClient client = null;
    private Intent webSocketServiceIntent = null;

// ------------------------------------Activity 的相关方法------------------------------------------
    /**
     * 启动该活动的静态方法
     * @param context 启动该活动的上下文
     * @param friendData 启动该活动需要的数据类型 MessageListItem
     */
    public static void actionStart(Activity context, MessageListItem friendData){
        Intent intent = new Intent(context, ChatActivity.class);
        intent.putExtra("friendData", friendData);
        context.startActivityForResult(intent,1);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtil.e(TAG, "onCreate");
        friendData = (MessageListItem)getIntent().getSerializableExtra("friendData");

        // 数据初始化
        initData();
        // 启动WebSocket客户端服务
//        startJWebSocketServiceClient();
        // 绑定服务
        bindJWebSocketClientService();
        // 控件初始化
        initView();
        // 验证连接
        //verifyConnection();

        //注册广播接收器
        doRegisterReceiver();


    }

    @Override
    protected void onStart(){
        LogUtil.e(TAG, "onStart");
        super.onStart();
    }

    @Override
    protected void onResume(){
        LogUtil.e(TAG, "onResume");
        super.onResume();
    }

    @Override
    protected void onPause(){
        LogUtil.e(TAG, "onPause");
        super.onPause();
    }

    @Override
    protected void onStop(){
        LogUtil.e(TAG, "onStop");
        super.onStop();
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        LogUtil.e(TAG, "onDestroy");
        // 解绑服务
        if(serviceConnection != null){
            LogUtil.e(TAG, "onDestroy: 解绑服务");
            unbindService(serviceConnection);

        }
        // 取消注册广播接收器
        if(chatMsgReciver != null){
            unregisterReceiver(chatMsgReciver);
            chatMsgReciver = null;
        }


    }

    @Override
    protected void onRestart(){
        LogUtil.e(TAG, "onRestart");
        super.onRestart();
    }

    @Override
    protected  int getContentViewId(){
        return R.layout.activity_chat;
    }

    @Override
    public void onBackPressed(){
        String inputContent = inputText.getText().toString();
        if(!TextUtils.isEmpty(inputContent)){
            chatItemList.get(chatItemList.size() - 1).setContent(inputContent);
            chatItemList.get(chatItemList.size() - 1).setTop(true);
            chatItemList.get(chatItemList.size() - 1).setEditing(true);
        }else{
            inputText.setCompoundDrawables(null, null, null ,null);
            chatItemList.get(chatItemList.size() - 1).setTop(false);
            chatItemList.get(chatItemList.size() - 1).setEditing(false);
        }
        Intent intent1 = new Intent();
        intent1.putExtra("returnDataFromChat", chatItemList.get(chatItemList.size() - 1));
        setResult(RESULT_OK, intent1);
        finish();
    }

//---------------------------------------BroadcastReceiver相关方法-----------------------------------

    private ChatMsgReceiver chatMsgReciver = null;

    private void doRegisterReceiver(){
        IntentFilter intentFilter = new IntentFilter("com.kris.serverResponse");
        chatMsgReciver = new ChatMsgReceiver();
        registerReceiver(chatMsgReciver, intentFilter);
    }

    private class ChatMsgReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent){
            String serverResponse = intent.getStringExtra("serverResponse");
            if(serverResponse != null){
                handleServerResponse(serverResponse);
            }else{
                LogUtil.e(TAG, "ChatMsgReceiver-onReceive: serverResponse 为空");
            }
        }
    }

//-----------------------------------------Service相关方法-------------------------------------------
    public boolean isVerification = false;
    public void setVerification(boolean val){
        this.isVerification = val;
    }
    public boolean getVerification(){
        return this.isVerification;
    }

    /**
     * 服务连接 变量
     */
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            LogUtil.e(TAG, "onServiceConnected: 服务与活动成功绑定");
            binder = (JWebSocketClientService.JWebSocketClientBinder)service;
            jWebSocketClientService = binder.getService();
            client = jWebSocketClientService.client;
            LogUtil.e(TAG, "onServiceConnected(): client = " + client.toString() );

            // 实现回调接口
//            jWebSocketClientService.setDataCallback(new JWebSocketClientService.DataCallback() {
//                @Override
//                public void dataChanged(String str) {
//                    Message msg = handler.obtainMessage();
//                    Bundle bundle = new Bundle();
//                    bundle.putString("ServerResponse", str);
//                    msg.setData(bundle);
//                    // 发送通知
//                    LogUtil.i(TAG, "onServiceConnected-->dataChanged: handler发消息");
//                    handler.sendMessage(msg);
//                }
//            });

        }

//        @SuppressLint("HandlerLeak")
//        Handler handler = new Handler(){
//            @Override
//            public void handleMessage(@NonNull android.os.Message msg){
//                final Message message = msg;
//                LogUtil.i(TAG, "onServiceConnected--> 开始handleServerResponse");
//                handleServerResponse(message);
//            }
//        };

        @Override
        public void onServiceDisconnected(ComponentName name) {
            LogUtil.e(TAG, "onServiceDisconnected: 服务与活动成功断开,binder置空");
            binder = null;
        }
    };

    /**
     * 消息滤波器
     * @param serverResponse 从WebSocket服务器 返回的消息
     * @return 0：表示反馈消息正常
     *        -1：表示反馈消息异常
     *
     */
    private int handleServerResponse(String serverResponse){
        final int FEEDBACK_CODE_SUCCESS = 0;
        final int FEEDBACK_CODE_FAILURE = -1;

        String[] splitedMsg = serverResponse.split("`");
        String orderKey = splitedMsg[0];
        String statusValue = splitedMsg[1].split("=")[1];

        switch (orderKey){
            case "$VERIFICATION_FEEDBACK":
                if(statusValue.equals("success")) {
                    LogUtil.e(TAG, "handleServerResponse: isVerification = " + getVerification() );
                    Toast.makeText(ChatActivity.this, "Verify Success", Toast.LENGTH_SHORT).show();
                    setVerification(true);
                    LogUtil.e(TAG, "handleServerResponse: isVerification = " + getVerification() );
                    return FEEDBACK_CODE_SUCCESS;
                }else if(statusValue.equals("failure")){
                    Toast.makeText(ChatActivity.this, "Verify Failed", Toast.LENGTH_SHORT).show();
                    setVerification(false);
                    return  FEEDBACK_CODE_FAILURE;
                }else{
                    LogUtil.e(TAG, "handleServerResponse: Error statusVal = " + statusValue);
                    setVerification(false);
                    return FEEDBACK_CODE_FAILURE;
                }
            case "$CHAT_FEEDBACK":
                if(statusValue.equals("success")) {
                    return FEEDBACK_CODE_SUCCESS;
                }else if(statusValue.equals("failure")){
                    setVerification(false);
                    return FEEDBACK_CODE_FAILURE;
                }else{
                    setVerification(false);
                    return FEEDBACK_CODE_FAILURE;
                }
            case "$CHAT_INCOMING":
                String content = null;
                for (String s : splitedMsg) {
                    if(s.startsWith(JWebSocketClientService.REQUEST_KEY_CONTENT)){
                        content = s.split("=")[1];
                    }
                }
                SimpleDateFormat format = new SimpleDateFormat("y-M-d HH:mm ", Locale.CHINA);
                format.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
                ChatItem chatItem = new ChatItem(format.format(new Date()), friendData.getHeadImageId(),
                        content, ChatItem.MESSAGE_RECEIVED,friendData.getUserName());
                chatItem.setTop(true);
                notifyAdapterDataInsertToTail(chatItem);
                return FEEDBACK_CODE_SUCCESS;
            default:
                LogUtil.e(TAG, "handleServerResponse: Invalid orderKey = " + orderKey);
                return FEEDBACK_CODE_FAILURE;
        }
    }

    private void verifyConnection(){
        Handler myHandler = new Handler();
        myHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        LogUtil.e(TAG, "verifyConnection: client = " + client.toString()
                                + ", jWebSocketClientService = " + jWebSocketClientService.toString());
                        if(client != null && client.isOpen()){
                            // --修改--这里的username为用户自己的名称ID
                            jWebSocketClientService.sendMsg(1,
                                    JWebSocketClientService.REQUEST_KEY_USERNAME, "Rich",
                                    JWebSocketClientService.REQUEST_KEY_PSW, "123");
                        }else{
                            setVerification(false);
                            Toast.makeText(ChatActivity.this,"连接失败，请稍等或重启App", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).start();

                LogUtil.e(TAG, "myHandler--run(): " + Thread.currentThread().toString());
            }
        }, 1000);

    }

    /**
     *  开启JWebSocketClient 服务
     */
    private void startJWebSocketServiceClient(){
        LogUtil.i(TAG, "开启服务");
        webSocketServiceIntent = new Intent(ChatActivity.this, JWebSocketClientService.class);
        startService(webSocketServiceIntent);
    }

    /**
     * 绑定 JWebSocketClientService
     */
    private void bindJWebSocketClientService(){
        LogUtil.i(TAG, "绑定服务");
        Intent bindIntent = new Intent(ChatActivity.this, JWebSocketClientService.class);
        bindService(bindIntent, serviceConnection, BIND_AUTO_CREATE);
    }


// --------------------------------------初始化方法--------------------------------------------------
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
        if(friendData.isEditing()){
            inputText.setText(friendData.getContent());
        }

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

                    ChatItem msg = new ChatItem(format.format(new Date()), R.drawable.friend, content,
                            ChatItem.MESSAGE_SEND,friendData.getUserName());


                    // 发送至服务器
                    if(client != null && client.isOpen()){
                        LogUtil.e(TAG, "发送消息：client=" + client.toString());
                        // --修改-- 聊天好友用户名ID
                        jWebSocketClientService.sendMsg(2,
                                JWebSocketClientService.REQUEST_KEY_CONTENT, content,
                                JWebSocketClientService.REQUEST_KEY_USERNAME, friendData.getUserName());//friendData.getUserName()
                        msg.setTop(true);
                        msg.setEditing(false);
                        notifyAdapterDataInsertToTail(msg);
                        inputText.setText("");  // 清空输入行
                    }else{
                        Toast.makeText(ChatActivity.this, "连接已断开，请稍等或重启App",
                                Toast.LENGTH_SHORT).show();
                        msg.setTop(false);
                        LogUtil.e(TAG, "发送按钮：WebSocket客户端为空或未连接");
                    }

                }else{
                    Toast.makeText(ChatActivity.this, "消息不能为空", Toast.LENGTH_SHORT).show();
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


//--------------------------------------单击接口----------------------------------------------------

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
                String inputContent = inputText.getText().toString();
                if(!TextUtils.isEmpty(inputContent)){
                    chatItemList.get(chatItemList.size() - 1).setContent(inputContent);
                    chatItemList.get(chatItemList.size() - 1).setTop(true);
                    chatItemList.get(chatItemList.size() - 1).setEditing(true);
                }else{
                    chatItemList.get(chatItemList.size() - 1).setTop(false);
                    chatItemList.get(chatItemList.size() - 1).setEditing(false);
                }
                Intent intent1 = new Intent();
                intent1.putExtra("returnDataFromChat", chatItemList.get(chatItemList.size() - 1));
                setResult(RESULT_OK, intent1);
                finish();
                break;
            default:
                Toast.makeText(ChatActivity.this, "onOptionsItemSelected(): 非法ID", Toast.LENGTH_SHORT).show();
                break;
        }

        return true;
    }



//-------------------------------------更新UI的方法-------------------------------------------------

    /**
     *  通知Adapter 有新数据插入
     */
    public void notifyAdapterDataInsertToTail(ChatItem item){
        if(null != item){
            chatItemList.add(item);
        }else{
            LogUtil.i(TAG, "notifyAdapterDataInsertedToTail():插入的item为空");
        }
        chatAdapter.notifyItemInserted(chatItemList.size() - 1); // 当前有新消息时，刷新ListView中的显示
        chatRecyclerView.scrollToPosition(chatItemList.size() - 1); // 将RecyclerView 定位到最后一行
    }

}


