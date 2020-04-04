package com.krisyu.easybox.activity.bottom_navigation;


import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.krisyu.easybox.R;
import com.krisyu.easybox.activity.bottom_navigation.normal.home_fragment.HomeFragment;
import com.krisyu.easybox.activity.bottom_navigation.normal.location_fragment.LocationFragment;
import com.krisyu.easybox.activity.bottom_navigation.normal.message_fragment.ChatItem;
import com.krisyu.easybox.activity.bottom_navigation.normal.message_fragment.MessageFragment;
import com.krisyu.easybox.activity.bottom_navigation.normal.message_fragment.MessageListItem;
import com.krisyu.easybox.activity.bottom_navigation.normal.mine_fragment.MineFragment;
import com.krisyu.easybox.activity.bottom_navigation.normal.search_fragment.SearchFragment;
import com.krisyu.easybox.activity.user.UserData;
import com.krisyu.easybox.base.BaseActivity;
import com.krisyu.easybox.network.JWebSocketClient;
import com.krisyu.easybox.service.JWebSocketClientService;
import com.krisyu.easybox.utils.LogUtil;
import com.next.easynavigation.view.EasyNavigationBar;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;


/*****************************************************
 *              Task List
 *
 *  1、 完成主页设计碎片构架----------------> 进度：完成构架，功能待完善
 *      To solve：
 *        √ * groupItem 不能单击，进而展开，显示childItem。
 *
 *
 *  2、 完成个人信息活动构架----------------> 进度：完成构架，功能待完善
 *      To solve：
 *       √ * 解决Fragment 滑动显示更多的内容
 *
 *
 *  3、 完成查快递构架---------------------> 进度： 完成构架，完善搜索模块，数据更新模块
 *      To solve:
 *        √ * 解决应用栏的问题
 *        √ * 搜索功能
 *
 *
 *
 *  4、 完成服务服务网点构架---------------> 进度：
 *      To solve:
 *
 *
 *  5、 完成消息对话构架------------------> 进度： 完成构架
 *      To solve:
 *
 *
 *  *、注意导航栏红点和消息数的显示---------> 进度：
 *      To solve:
 *
 *  *、工具栏还未完善
 *
 *****************************************************/



public class NormalActivity extends BaseActivity {

    private EasyNavigationBar navigationBar;
    private String[] tabText = {"首页", "快递查询", "消息", "我的"};   //  "服务网点",
    // 未选中的icon
    private int[] normalIcon = {R.drawable.home, R.drawable.search, R.drawable.message,
            R.drawable.mine};   // R.drawable.location,
    // 选中的Icon
    private  int[] selectIcon = {R.drawable.home_yes, R.drawable.search_yes, R.drawable.message_yes,
            R.drawable.mine_yes};  // R.drawable.location_yes,

    private List<Fragment> fragments = new ArrayList<>();

    private static final String TAG = "NormalActivity";
    private ChatItem returnedDataFromChat;



//-------------------------------------Activity相关方法---------------------------------------------

    /**
     * 启动该活动的静态方法
     * @param context 启动该活动的上下文
     * @param userData 启动该活动需要的数据类型 MessageListItem
     */
    public static void actionStart(Context context, UserData userData){
        Intent intent = new Intent(context, NormalActivity.class);
        intent.setAction("LoginActivityTONormalActivity");
        intent.putExtra("userData", userData);
        context.startActivity(intent);
    }


    public EasyNavigationBar getNavigationBar() {
        return navigationBar;
    }

    @Override
    protected  int getContentViewId(){
        return R.layout.navigation_tab;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        LogUtil.e(TAG, "onCreate");
        // 初始化UI
        initView();
        // 开启服务
        startJWebSocketServiceClient();
        // 绑定服务
        bindJWebSocketClientService();
        // 验证连接WebSocket客户端
        verifyConnection();
        // 注册广播接收器
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
        LogUtil.e(TAG, "onDestroy");
        super.onDestroy();

        if(serviceConnection != null){
            unbindService(serviceConnection);
            LogUtil.i(TAG, "onDestroy: 解绑JWebSocketClientService成功");
        }
        if(msgListReciver != null){
            unregisterReceiver(msgListReciver);
        }

    }

    @Override
    protected void onRestart(){
        LogUtil.e(TAG, "onRestart");
        super.onRestart();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode,data);
        bindJWebSocketClientService();
        switch (requestCode){
            case 1: // ChatActivity 的请求码
                if(resultCode == RESULT_OK){
                    returnedDataFromChat = (ChatItem) data.getSerializableExtra("returnDataFromChat");
                    MessageFragment messageFragment = (MessageFragment) fragments.get(2);
                    int position = 0;
                    for(MessageListItem item: messageFragment.getMsgLists()){
                        position++;
                        if(item.getUserName().equals(returnedDataFromChat.getFriendName())){
                            item.setContent(returnedDataFromChat.getContent());
                            item.setTime(returnedDataFromChat.getTime().split(" ")[1]);
                            item.setTop(returnedDataFromChat.isTop());
                            item.setEditing(returnedDataFromChat.isEditing());
                            messageFragment.notifyAdapterDataChanged();
                            if(item.isTop() || item.isEditing()){
                                messageFragment.notifyAdapterItemMoved(null, position-1, 0);
                            }
                        }
                    }
                }
                break;
            default:
        }
    }


//---------------------------------------BroadcastReceiver相关方法-----------------------------------

    private MsgListReceiver msgListReciver = null;

    private void doRegisterReceiver(){
        IntentFilter intentFilter = new IntentFilter("com.kris.serverResponse");
        msgListReciver = new MsgListReceiver();
        registerReceiver(msgListReciver, intentFilter);
    }

    private class MsgListReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent){
            String serverResponse = intent.getStringExtra("serverResponse");
            if(null != serverResponse){
                handleServerResponse(serverResponse);
            }else{
                LogUtil.e(TAG, "MsgListReciver-onReceive: serverResponse 为空");
            }

        }
    }

//--------------------------------------Service相关方法-------------------------------------------
private JWebSocketClientService jWebSocketClientService;
    private JWebSocketClientService.JWebSocketClientBinder binder;
    private JWebSocketClient client = null;
    private Intent webSocketServiceIntent = null;

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
            LogUtil.e(TAG, "onServiceConnected()： client = " + client.toString() );
        }

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
                    LogUtil.e(TAG, "HandleMsg: isVerification = " + getVerification() );
                    Toast.makeText(NormalActivity.this, "Verify Success", Toast.LENGTH_SHORT).show();
                    setVerification(true);
                    LogUtil.e(TAG, "HandleMsg: isVerification = " + getVerification() );
                    return FEEDBACK_CODE_SUCCESS;
                }else if(statusValue.equals("failure")){
                    Toast.makeText(NormalActivity.this, "Verify Failed", Toast.LENGTH_SHORT).show();
                    setVerification(false);
                    return  FEEDBACK_CODE_FAILURE;
                }else{
                    LogUtil.e(TAG, "HandleMsg: Error statusVal = " + statusValue);
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
                // 分离出有效数据
                String content = null;
                String userName = null;
                for (String s : splitedMsg) {
                    if(s.startsWith(JWebSocketClientService.REQUEST_KEY_CONTENT)){
                        content = s.split("=")[1];
                    }
                    if(s.startsWith(JWebSocketClientService.REQUEST_KEY_SENDER)){
                        userName = s.split("=")[1];
                    }
                }
                //设置时间格式
                SimpleDateFormat format = new SimpleDateFormat("HH:mm ", Locale.CHINA);
                format.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
                boolean isExistence = false;
                MessageFragment messageFragment = getMessageFragment();
                // 遍历消息列表List的关键词userName，判断有无该聊天记录条
                for(MessageListItem item: messageFragment.getMsgLists()){
                    if(item.getUserName().equals(userName)){
                        LogUtil.i(TAG, "handleServerResponse-->" + orderKey + "存在用户 " + userName
                            + " 的聊天记录条");
                        item.setTime(format.format(new Date()));
                        item.setContent(content);
                        item.setNumberOfunread(item.getNumberOfunread() + 1);
                        item.setTop(true);
                        isExistence = true;
                        messageFragment.notifyAdapterDataChanged();
                        break;
                    }else{
                        isExistence = false;
                    }
                }
                // 若无该记录条，则置顶一个新的记录条
                if(!isExistence){
                    LogUtil.i(TAG, "handleServerResponse-->" + orderKey + "不存在用户 " + userName
                            + " 的聊天记录条");
                    MessageListItem messageListItem = new MessageListItem(R.drawable.default_head_pic,
                            userName, content, format.format(new Date()), 0);
                    messageFragment.notifyAdapterItemMoved(messageListItem, messageFragment.getMsgLists().size() - 1, 0);
                }
                return FEEDBACK_CODE_SUCCESS;
            default:
                LogUtil.e(TAG, "HandleMsg: Invalid orderKey");
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
                        if(client != null && client.isOpen()){
                            LogUtil.e(TAG, "verifyConnection: client = " + client.toString()
                                    + ", jWebSocketClientService = " + jWebSocketClientService.toString());
                            // --修改--这里的username为用户自己的名称ID
                            jWebSocketClientService.sendMsg(1,
                                    JWebSocketClientService.REQUEST_KEY_USERNAME, "KrisYu",
                                    JWebSocketClientService.REQUEST_KEY_PSW, "123");
                        }else{
                            setVerification(false);
                            Toast.makeText(NormalActivity.this,"连接失败，请稍等或重启App", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).start();
            }
        }, 1000);

    }

    /**
     *  开启JWebSocketClient 服务
     */
    private void startJWebSocketServiceClient(){
        LogUtil.i(TAG, "开启服务");
        webSocketServiceIntent = new Intent(NormalActivity.this, JWebSocketClientService.class);
        startService(webSocketServiceIntent);

    }

    /**
     * 绑定 JWebSocketClientService
     */
    private void bindJWebSocketClientService(){
        LogUtil.i(TAG, "绑定服务");
        Intent bindIntent = new Intent(NormalActivity.this, JWebSocketClientService.class);
        bindService(bindIntent, serviceConnection, BIND_AUTO_CREATE);
    }

//--------------------------------------初始化方法---------------------------------------------------

    public HomeFragment getHomeFragment(){
        return (HomeFragment) fragments.get(0);
    }
    public SearchFragment getSearchFragment(){
        return (SearchFragment)fragments.get(1);
    }
    public MessageFragment getMessageFragment(){
        return (MessageFragment)fragments.get(2);
    }
    public MineFragment getMineFragment(){
        return (MineFragment)fragments.get(3);
    }

    /**
     * 初始化 UI
     * fragment：0——HomeFragment
     *           1——SearchFragment
     *           2——MessageFragment
     *           3——LocationFragment
     *           4——MineFragment
     */
    private void initView(){
        // 底部导航栏
        navigationBar = (EasyNavigationBar) findViewById(R.id.navigationBar);
        fragments.add(new HomeFragment());
        fragments.add(new SearchFragment());
        fragments.add(new MessageFragment());
//        fragments.add(new LocationFragment());
        fragments.add(new MineFragment());
        navigationBar.titleItems(tabText)
                .normalIconItems(normalIcon)
                .selectIconItems(selectIcon)
                .iconSize(32)
                .fragmentList(fragments)
                .fragmentManager(getSupportFragmentManager())
                .canScroll(false)
                .normalTextColor(getColor(R.color.tab_black))
                .selectTextColor(getColor(R.color.tab_blue))
                .navigationBackground(getColor(R.color.tab_white))
                .build();

        //用户碎片的 数据获取
        MineFragment mineFragment = getMineFragment();
        UserData userData = (UserData)getIntent().getSerializableExtra("userData");
        LogUtil.i(TAG, "UserData->UserName = " + userData.getUserName());
        mineFragment.setUserData(userData);
    }


}
