package com.krisyu.easybox.listener;

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



    public MainActivityHanlderListener(Lifecycle lifecycle, Callback callback){
        super(callback);
        this.lifecycle = lifecycle;
        this.mCallback = callback;
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    void start(){
        LogUtil.d(TAG, "enable = " + enabled);
        if(enabled){
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    void stop(){
        LogUtil.d(TAG, "Lifecycle.Event.ON_STOP-->清楚实例的回调和消息，并置实例为null");
        // disconnect if connected
        this.removeCallbacksAndMessages(null);

    }

    public void enable(){
        enabled = true;
        if(lifecycle.getCurrentState().isAtLeast(Lifecycle.State.STARTED)){
            // connect if not connected
        }
    }
}
