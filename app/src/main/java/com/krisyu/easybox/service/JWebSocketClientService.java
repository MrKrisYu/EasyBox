package com.krisyu.easybox.service;


import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;

import androidx.collection.ArrayMap;
import androidx.core.app.NotificationCompat;
import androidx.core.app.TaskStackBuilder;

import com.krisyu.easybox.R;
import com.krisyu.easybox.activity.bottom_navigation.fragments.message_fragment.ChatActivity;
import com.krisyu.easybox.mode.MessageListItem;
import com.krisyu.easybox.network.JWebSocketClient;
import com.krisyu.easybox.utils.Constants;
import com.krisyu.easybox.utils.LogUtil;

import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Set;
import java.util.TimeZone;


/**
 *    WebSocket的客户端服务
 *      IP地址见 initSocketClient()方法的 uri
 */

public class JWebSocketClientService extends Service {

    public static final String REQUEST_KEY_CONTENT = "content";
    public static final String REQUEST_KEY_USERNAME = "username";
    public static final String REQUEST_KEY_SENDER = "sender";
    public static final String REQUEST_KEY_PSW = "password";
    private static final String REQUEST_KEY_VERIFICATION = "$VERIFICATION";
    private static final String REQUEST_KEY_CHAT = "$CHAT";
    private static final String RESPONSE_KEY_VERIFICATION = "$VERIFICATION_FEEDBACK";
    private static final String RESPONSE_KEY_CHAT_FEEDBACK = "$CHAT_FEEDBACK";
    private static final String RESPONSE_KEY_CHAT_INCOMING = "$CHAT_INCOMING";

    // 具体数据具体设计
//    private String data = null;
    private static final String TAG = "JWebSocketClientService";

    public JWebSocketClient client;
    private JWebSocketClientBinder mBinder = new JWebSocketClientBinder();

    public JWebSocketClientService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        LogUtil.i(TAG, "onBind: ");
        return mBinder;
    }

    public class JWebSocketClientBinder extends Binder {

        public JWebSocketClientService getService(){
            return JWebSocketClientService.this;
        }


//        public void setData(String data){
//            JWebSocketClientService.this.data = data;
//        }
    }

    // 服务被创建时调用，之后若服务存在，将不再被调用
    @Override
    public void onCreate(){
        super.onCreate();
        LogUtil.i(TAG, "onCreate: ");
    }

    // 每次服务启动时调用
    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        LogUtil.i(TAG, "onStartCommand: initSocketClient");
        // 初始化WebSocket客户端的连接
        initSocketClient();
        // 开启心跳检测
        mHandler.postDelayed(heartBeatRunnable, HEART_BEAT_RATE);//开启心跳检测


        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public boolean onUnbind(Intent intent){
        LogUtil.i(TAG, "onUnbind: Successful");
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy(){
        LogUtil.i(TAG, "onDestroy: closeConnect");
        closeConnect();
        super.onDestroy();
    }

// ---------------------------------------WebSocket---------------------------------------------
    private String buffServerResponse = null;
    /**
     * 初始化websocket连接
     */
    private void initSocketClient() {
        URI uri = URI.create(Constants.WEBSOCKET_URL);
        client = new JWebSocketClient(uri) {
            @Override
            public void onMessage(String message) {
                LogUtil.i("JWebSocketClientService", "收到的消息：" + message);

                // 处理收到的信息
                  // 回调机制
//                if(dataCallback != null ){
//                    LogUtil.i(TAG, "onMessage: dataChanged");
//                    dataCallback.dataChanged(message);
//                }

                // 缓存消息
                buffServerResponse = message;
                // 广播机制
                Intent intent = new Intent("com.kris.serverResponse");
                intent.putExtra("serverResponse", message);
                sendBroadcast(intent);

                // 发送消息通知
                Thread notificationThread = new Thread(notificationRunnable);
                notificationThread.setName("notificationThread");
                notificationThread.start();
            }

            @Override
            public void onOpen(ServerHandshake handshakedata) {
                super.onOpen(handshakedata);
                LogUtil.i("JWebSocketClientService", "websocket连接成功");
            }
        };
        connect();
    }

    /**
     * 开启 连接websocket的线程
     */
    private void connect() {
        new Thread() {
            @Override
            public void run() {
                try {
                    //connectBlocking多出一个等待操作，会先连接再发送，否则未连接发送会报错
                    client.connectBlocking();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }.start();

    }


    public ArrayMap<String,String> msgMap = new ArrayMap<>();

    /**
     * 开启向服务器发送请求的线程
     * @param msgType 消息类型：1——验证请求
     *                         2——转发消息请求
     * @param keys 请求类型 键值
     * @param values 具体请求内容
     */
    public void sendMsg(final int msgType, final String[] keys, final String[] values) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (null != client) {
                        //填充msgMap内容
                        if(keys.length != values.length){
                            return;
                        }else{
                            msgMap.clear();
                            for(int i=0; i<keys.length; i++ ){
                                msgMap.put(keys[i], values[i]);
                            }
                        }
                        // 发送消息逻辑
                        Set<String> msgKeySet = msgMap.keySet();
                        String delimiter = "`";  // 分隔符为中文逗号
                        String keyToValDelimiter = "=";
                        if(msgKeySet.size() < 1){
                            LogUtil.e(TAG, "sendMsg: msgMap的键值集合为空");
                            return;
                        }else{
                            switch (msgType){
                                case 1: // 验证消息 "$VERFICATION`username=xxx`password=xxx`..."
                                    if(msgKeySet.contains(REQUEST_KEY_USERNAME) && msgKeySet.contains(REQUEST_KEY_PSW)){
                                        String msg = REQUEST_KEY_VERIFICATION + delimiter
                                                + REQUEST_KEY_USERNAME + keyToValDelimiter + msgMap.get(REQUEST_KEY_USERNAME) + delimiter
                                                + REQUEST_KEY_PSW + keyToValDelimiter + msgMap.get("password");

                                        LogUtil.e("JWebSocketClientService", "发送的消息：" + msg);
                                        client.send(msg);
                                    }else{
                                        LogUtil.e(TAG, "sendMsg: msgMap中无键值username和password");
                                    }
                                    break;
                                case 2: // 转发聊天消息"$CHAT`content=xxx`username=xxx`..."
                                    if(msgKeySet.contains(REQUEST_KEY_CONTENT) && msgKeySet.contains(REQUEST_KEY_USERNAME)){
                                        String msg = REQUEST_KEY_CHAT + delimiter
                                                + REQUEST_KEY_CONTENT + keyToValDelimiter + msgMap.get(REQUEST_KEY_CONTENT) + delimiter
                                                + REQUEST_KEY_USERNAME + keyToValDelimiter + msgMap.get(REQUEST_KEY_USERNAME);
                                        LogUtil.e("JWebSocketClientService", "发送的消息：" + msg);
                                        client.send(msg);
                                    }else{
                                        LogUtil.e(TAG, "sendMsg: msgMap中无键值username和password");
                                    }
                            }
                        }


                    }

            }
        },"WebSocketRequestThread").start();
    }

    /**
     * 断开连接
     */
    private void closeConnect() {
        try {
            if (null != client) {
                client.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            client = null;
        }
    }

//--------------------------------------------消息通知---------------------------------------------

    private void sendNotification(String sender, String content){
        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class iot in the ss new and support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            LogUtil.i(TAG, "CreateNotification: Get Into Channel Creation");
            NotificationChannel channel1 = new NotificationChannel("1", "channel1", NotificationManager.IMPORTANCE_DEFAULT);
            channel1.setDescription("channelDescription");
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel1);
            }
        }
        SimpleDateFormat format = new SimpleDateFormat("HH:mm ", Locale.CHINA);
        format.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));

        MessageListItem notificationData = new MessageListItem(R.drawable.default_head_pic, "KrisYu",
                "NotificationTest", format.format(new Date()), 1);
        Intent resultIntent = new Intent(this, ChatActivity.class);
        resultIntent.putExtra("friendData", notificationData);
        // 创建包含ChatActivity的返回栈
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addNextIntentWithParentStack(resultIntent);
        // 获得包含整个返回栈的PendingIntent
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        Notification notification= new NotificationCompat.Builder(this, "1")
                .setSmallIcon(R.mipmap.icon_express)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(),R.mipmap.icon_express))
                .setContentTitle(sender)
                .setContentText(content)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setContentIntent(resultPendingIntent)
                .setAutoCancel(true)
                .setWhen(System.currentTimeMillis())
                .setDefaults(Notification.DEFAULT_VIBRATE | Notification.DEFAULT_ALL | Notification.DEFAULT_SOUND)
                .build();
        if (notificationManager != null) {
            notificationManager.notify(1, notification);
        }
    }

    private Runnable notificationRunnable = new Runnable() {
        @Override
        public void run() {
            if(buffServerResponse != null){
                final String msg = buffServerResponse;
                buffServerResponse = null;
                ArrayMap<String, String> userNameAndContent = ChatMsgFilter(msg);
                if(null != userNameAndContent){
                    sendNotification(userNameAndContent.get(REQUEST_KEY_SENDER), userNameAndContent.get(REQUEST_KEY_CONTENT));
                }
            }
        }
    };

    private ArrayMap<String, String> ChatMsgFilter(String serverResponse){
        ArrayMap<String, String> userNameAndContent = new ArrayMap<>();
        String[] splitedResponse = serverResponse.split("`");
        if(splitedResponse[0].equals(RESPONSE_KEY_CHAT_INCOMING)){
            for (String responseItem: splitedResponse) {
                if(responseItem.contains(REQUEST_KEY_CONTENT)){
                    userNameAndContent.put(REQUEST_KEY_CONTENT,responseItem.split("=")[1]);
                }else if(responseItem.contains(REQUEST_KEY_SENDER)){
                    userNameAndContent.put(REQUEST_KEY_SENDER,responseItem.split("=")[1]);
                }
            }
        }else{
            return null;
        }
        return userNameAndContent;
    }
//--------------------------------------websocket心跳检测--------------------------------------------
    private static final long HEART_BEAT_RATE = 10 * 1000;//每隔10秒进行一次对长连接的心跳检测
    private Handler mHandler = new Handler();
    private Runnable heartBeatRunnable = new Runnable() {
        @Override
        public void run() {
            if (client != null) {
                if (client.isClosed()) {
                    reconnectWs();
                }
            } else {
                //如果client已为空，重新初始化连接
                client = null;
                initSocketClient();
            }
            //每隔一定的时间，对长连接进行一次心跳检测
            mHandler.postDelayed(heartBeatRunnable, HEART_BEAT_RATE);
        }
    };

    /**
     * 开启重连
     */
    private void reconnectWs() {
        mHandler.removeCallbacks(heartBeatRunnable);
        new Thread() {
            @Override
            public void run() {
                try {
                    LogUtil.e(TAG, "开启重连");
                    client.reconnectBlocking();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

}
