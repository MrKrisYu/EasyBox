package com.krisyu.easybox.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.krisyu.easybox.network.JWebSocketClient;
import com.krisyu.easybox.utils.Constants;

import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;

public class JWebSocketClientService extends Service {

    // 具体数据具体设计
    private String data = null;

    private static final String TAG = "JWebSocketClientService";
    private JWebSocketClient client;
    private JWebSocketClientBinder mBinder = new JWebSocketClientBinder();

    public JWebSocketClientService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public class JWebSocketClientBinder extends Binder {
        public JWebSocketClientService getService(){
            return JWebSocketClientService.this;
        }
        public void setData(String data){
            JWebSocketClientService.this.data = data;
        }
    }

    // 服务被创建时调用，之后若服务存在，将不再被调用
    @Override
    public void onCreate(){
        super.onCreate();
    }

    // 每次服务启动时调用
    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        initSocketClient();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public boolean onUnbind(Intent intent){
        Log.e(TAG, "onUnbind: Successful");
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy(){
        closeConnect();
        super.onDestroy();
    }

// ---------------------------------------WebSocket---------------------------------------------

    /**
     * 初始化websocket连接
     */
    private void initSocketClient() {
        URI uri = URI.create(Constants.WEBSOCKET_URL);
        client = new JWebSocketClient(uri) {
            @Override
            public void onMessage(String message) {
                Log.e("JWebSocketClientService", "收到的消息：" + message);

                // 处理收到的信息
            }

            @Override
            public void onOpen(ServerHandshake handshakedata) {
                super.onOpen(handshakedata);
                Log.e("JWebSocketClientService", "websocket连接成功");
            }
        };
        connect();
    }

    /**
     * 连接websocket
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

    /**
     * 发送消息
     *
     * @param msg
     */
    public void sendMsg(String msg) {
        if (null != client) {
            Log.e("JWebSocketClientService", "发送的消息：" + msg);
            client.send(msg);
        }
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

// ---------------------------------------数据回调---------------------------------------------------

    /**
     *  数据回调接口
     */
    DataCallback dataCallback = null;
    public DataCallback getDataCallback() {
        return dataCallback;
    }
    public void setDataCallback(DataCallback dataCallback){
        this.dataCallback = dataCallback;
    }

    // 通过回调机制，将Service内部的变化传递到外部
    public interface DataCallback{
        void dataChanged(String str);
    }
}
