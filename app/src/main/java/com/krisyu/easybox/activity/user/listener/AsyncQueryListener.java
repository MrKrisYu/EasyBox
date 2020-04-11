package com.krisyu.easybox.activity.user.listener;


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
//    private String mThreadName;
//    private static AsyncQueryListener myInstance;
    private UserRepository ur;


    public interface Callback{
        void onQueryComplete(int token, int functionFlag, Object cookie, Object result);

        void onInsertComplete(int token, int functionFlag, Object cookie, long rowInserted);

        void onUpdateComplete(int token, int functionFlag, Object cookie, int result);

        void onDeleteComplete(int token, int functionFlag, Object cookie, int result);
    }

//    public static AsyncQueryListener getInstance(UserRepository ur, Lifecycle lifecycle, Callback callback){
//        synchronized (AsyncQueryListener.class){
//            if(myInstance == null){
//                myInstance = new AsyncQueryListener(ur, lifecycle, callback);
//            }
//        }
//        return myInstance;
//    }

    public AsyncQueryListener(UserRepository ur, Lifecycle lifecycle, Callback callback){
        super(ur);
        this.ur = ur;
        this.lifecycle = lifecycle;
        this.mCallback = callback;
//        mThreadName = this.getWorkerThreadName();
    }
    //--------------------------------------LifecycleObserve----------------------------------------
    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    void start(){
//        LogUtil.d(TAG, "Lifecycle.Event.ON_START--> myInstance = " + myInstance);
        LogUtil.d(TAG, "enable = " + enabled);
//        LogUtil.d(TAG, "WorkThread is changed? " + (!mThreadName.equals(this.getWorkerThreadName())));
        if(enabled){
//            if(!mThreadName.equals(this.getWorkerThreadName())){
//                this.recreateWorkerThread();
//                mThreadName = this.getWorkerThreadName();
//            }
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    void stop(){
        LogUtil.d(TAG, "Lifecycle.Event.ON_STOP-->清楚实例的回调和消息，并置实例为null");
        // disconnect if connected
        this.removeCallbacksAndMsg();
//        this.quitSafely();
//        myInstance = null;


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