package com.krisyu.easybox.activity.user.listener;

import android.os.Handler;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;

import com.krisyu.easybox.utils.LogUtil;

public class MainActivityHanlderListener extends Handler implements LifecycleObserver {
    private static final String TAG = "MainActivityHanlderList";
    private boolean enabled = true;
    private Lifecycle lifecycle;
    private Callback mCallback;
//    private static MainActivityHanlderListener myInstance;


//    public static MainActivityHanlderListener getInstance(Lifecycle lifecycle, Callback callback){
//        synchronized (MainActivityHanlderListener.class){
//            if(myInstance == null){
//                myInstance = new MainActivityHanlderListener(lifecycle, callback);
//            }
//        }
//        return myInstance;
//    }


    public MainActivityHanlderListener(Lifecycle lifecycle, Callback callback){
        super(callback);
        this.lifecycle = lifecycle;
        this.mCallback = callback;
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    void start(){
//        LogUtil.d(TAG, "Lifecycle.Event.ON_START--> myInstance = " + myInstance);
        LogUtil.d(TAG, "enable = " + enabled);
        if(enabled){
//            if(myInstance == null){
//                LogUtil.d(TAG, "Lifecycle.Event.ON_START-->重新获得实例");
//                myInstance = getInstance(lifecycle, mCallback);
//            }
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    void stop(){
        LogUtil.d(TAG, "Lifecycle.Event.ON_STOP-->清楚实例的回调和消息，并置实例为null");
        // disconnect if connected
        this.removeCallbacksAndMessages(null);
//        myInstance = null;


    }

    public void enable(){
        enabled = true;
        if(lifecycle.getCurrentState().isAtLeast(Lifecycle.State.STARTED)){
            // connect if not connected
        }
    }
}
