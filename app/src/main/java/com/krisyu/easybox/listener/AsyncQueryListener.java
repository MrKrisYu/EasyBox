package com.krisyu.easybox.listener;


import com.krisyu.easybox.handler.AsyncQueryHandler;
import com.krisyu.easybox.room_framework.reposity.UserRepository;
import com.krisyu.easybox.utils.LogUtil;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;

public class AsyncQueryListener extends AsyncQueryHandler<UserRepository> implements LifecycleObserver{
    private static final String TAG = "AsyncQueryListener";

    private boolean enabled = true; // 测试
    private Lifecycle lifecycle;
    private Callback mCallback;
    private UserRepository ur;


    public interface Callback{
        void onQueryComplete(int token, int functionFlag, Object cookie, Object result);

        void onInsertComplete(int token, int functionFlag, Object cookie, long rowInserted);

        void onUpdateComplete(int token, int functionFlag, Object cookie, int result);

        void onDeleteComplete(int token, int functionFlag, Object cookie, int result);
    }



    public AsyncQueryListener(UserRepository ur, Lifecycle lifecycle, Callback callback){
        super(ur);
        this.ur = ur;
        this.lifecycle = lifecycle;
        this.mCallback = callback;
    }
    //--------------------------------------LifecycleObserve----------------------------------------
    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    void start(){
        if(enabled){

        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    void stop(){
        LogUtil.d(TAG, "Lifecycle.Event.ON_STOP-->清楚实例的回调和消息，并置实例为null");
        // disconnect if connected
        this.removeCallbacksAndMsg();


    }

    public void enable(){
        enabled = true;
        if(lifecycle.getCurrentState().isAtLeast(Lifecycle.State.STARTED)){
            // connect if not connected
        }
    }

    //----------------------------------------AsyncQueryHandler的方法--------------------------------
    @Override
    protected void onQueryComplete(int token, int functionFlag, Object cookie, Object result){
        if(mCallback != null){
            mCallback.onQueryComplete(token, functionFlag, cookie, result);
        }

    }
    @Override
    protected void onInsertComplete(int token, int functionFlag, Object cookie, long rowInserted) {
        if(mCallback != null){
            mCallback.onInsertComplete(token, functionFlag, cookie, rowInserted);
        }

    }
    @Override
    protected void onUpdateComplete(int token, int functionFlag, Object cookie, int result) {
        if(mCallback != null){
            mCallback.onUpdateComplete(token, functionFlag, cookie, result);
        }

    }
    @Override
    protected void onDeleteComplete(int token, int functionFlag, Object cookie, int result) {
        if(mCallback != null){
            mCallback.onDeleteComplete(token, functionFlag, cookie, result);
        }

    }



}