package com.krisyu.easybox.handler;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;

import com.krisyu.easybox.room_framework.entities.UserData;
import com.krisyu.easybox.room_framework.reposity.UserRepository;


import java.lang.ref.WeakReference;



/**
 * Inspiration from {@link android.content.AsyncQueryHandler}
 *
 * A helper class to help make handling asynchronous {@link com.krisyu.easybox.room_framework.reposity.UserRepository}
 * queries easier.
 */
public abstract class AsyncQueryHandler<Repository extends UserRepository> extends Handler {
    private static final String TAG = "AsyncQuery";
    private static final boolean localLOGV = true;

    private static final int EVENT_ARG_QUERY = 1;
    private static final int EVENT_ARG_INSERT = 2;
    private static final int EVENT_ARG_UPDATE = 3;
    private static final int EVENT_ARG_DELETE = 4;

    /* package */
    private final WeakReference<Repository > mRepository;

    /**
     * AsyncQueryHandler类的Looper静态对象
     */
    private static Looper sLooper = null;

    /**
     * Handler类 工作线程的Handler
     */
    private Handler mWorkerThreadHandler;

    /**
     * 请求参数的数据类
     */
    protected static final class WorkerArgs {
        int functionFlag;
        Handler handler;
        String[] selection;
        String[] selectionArgs;
        String[] updateFields;
        String[] updateValues;
        Object result;
        Object cookie;
        UserData userData;
    }



    /**
     * WorkerHandler类 继承自Handler， 处理ContentProvider的事件
     */
    protected class WorkerHandler extends Handler {
        public WorkerHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            final Repository repository = mRepository.get();
            if (repository == null) return;

            WorkerArgs args = (WorkerArgs) msg.obj;

            int token = msg.what;
            int event = msg.arg1;

            /**
             * 数据库的CRUD
             */
            switch (event) {
                case EVENT_ARG_QUERY:
                    Object queryReturn;
                    try {
                        queryReturn = repository.query(token,
                                args.selection, args.selectionArgs);

                    } catch (Exception e) {
                        Log.w(TAG, "Exception thrown during handling EVENT_ARG_QUERY", e);
                        queryReturn = null;
                    }

                    args.result = queryReturn;
                    break;

                case EVENT_ARG_INSERT:
                    args.result = repository.insert(token, args.userData);
                    break;

                case EVENT_ARG_UPDATE:
                    args.result = repository.update(token,
                            args.selection, args.selectionArgs,
                            args.updateFields, args.updateValues,
                            args.userData);
                    break;

                case EVENT_ARG_DELETE:
                    args.result = repository.delete(token,
                            args.selection, args.selectionArgs,
                            args.userData);
                    break;
            }


            // 传递原来的令牌Token值给调用者，他在arg1的event值上方
            Message reply = args.handler.obtainMessage(token);
            reply.obj = args;
            reply.arg1 = msg.arg1;

            if (localLOGV) {
                Log.d(TAG, "WorkerHandler.handleMsg: msg.arg1=" + msg.arg1
                        + ", reply.what=" + reply.what);
            }

            reply.sendToTarget();
        }
    }

    /**
     * Constructor:
     *     若 sLooper为空，则自建HandlerThread线程，并使用其Looper。
     *  并给 mWorkerThreadHandler 赋值{@link WorkerHandler}类对象
     * @param rs
     */
    public AsyncQueryHandler(Repository rs) {
        super();
        mRepository = new WeakReference<>(rs);
        synchronized (AsyncQueryHandler.class) {
            if (sLooper == null) {
                HandlerThread thread = new HandlerThread("AsyncQueryWorker");
                thread.start();
                sLooper = thread.getLooper();
            }
        }
        mWorkerThreadHandler = createHandler(sLooper);
    }

    private Handler createHandler(Looper looper) {
        return new WorkerHandler(looper);
    }

    /**
     * This method begins an asynchronous query. When the query is done
     * {@link #onQueryComplete} is called.
     *
     * @param token A token passed into {@link #onQueryComplete} to identify
     *  the query.
     * @param cookie An object that gets passed into {@link #onQueryComplete}
     * @param selection A filter declaring which rows to return, formatted as an
     *         SQL WHERE clause (excluding the WHERE itself). Passing null will
     *         return all rows for the given URI.
     * @param selectionArgs You may include ?s in selection, which will be
     *         replaced by the values from selectionArgs, in the order that they
     *         appear in the selection. The values will be bound as Strings.
     */
    public void startQuery(int token, int functionFlag, Object cookie,
                           String[] selection, String[] selectionArgs) {
        // Use the token as what so cancelOperations works properly
        Message msg = mWorkerThreadHandler.obtainMessage(token);
        msg.arg1 = EVENT_ARG_QUERY;
        // 数据封装
        WorkerArgs args = new WorkerArgs();
        args.handler = this;
        args.selection = selection;
        args.selectionArgs = selectionArgs;
        args.cookie = cookie;
        args.functionFlag = functionFlag;

        // 将封装好的数据 放入msg
        msg.obj = args;

        mWorkerThreadHandler.sendMessage(msg);
    }

    /**
     * Attempts to cancel operation that has not already started. Note that
     * there is no guarantee that the operation will be canceled. They still may
     * result in a call to on[Query/Insert/Update/Delete]Complete after this
     * call has completed.
     *
     * @param token The token representing the operation to be canceled.
     *  If multiple operations have the same token they will all be canceled.
     */
    public final void cancelOperation(int token) {
        mWorkerThreadHandler.removeMessages(token);
    }

    /**
     * This method begins an asynchronous insert. When the insert operation is
     * done {@link #onInsertComplete} is called.
     *
     * @param token A token passed into {@link #onInsertComplete} to identify
     *  the insert operation.
     * @param cookie An object that gets passed into {@link #onInsertComplete}
     * @param userData the userData passed to the insert operation.
     */
    public final void startInsert(int token, int functionFlag, Object cookie,
                                  UserData userData) {
        // Use the token as what so cancelOperations works properly
        Message msg = mWorkerThreadHandler.obtainMessage(token);
        msg.arg1 = EVENT_ARG_INSERT;

        WorkerArgs args = new WorkerArgs();
        args.handler = this;
        args.cookie = cookie;
        args.userData = userData;
        args.functionFlag = functionFlag;
        msg.obj = args;

        mWorkerThreadHandler.sendMessage(msg);
    }

    /**
     * This method begins an asynchronous update. When the update operation is
     * done {@link #onUpdateComplete} is called.
     *
     * @param token A token passed into {@link #onUpdateComplete} to identify
     *  the update operation.
     * @param cookie An object that gets passed into {@link #onUpdateComplete}
     * @param selection A filter declaring which rows to return, formatted as an
     *         SQL WHERE clause (excluding the WHERE itself). Passing null will
     *         return all rows for the given URI.
     * @param selectionArgs You may include ?s in selection, which will be
     *         replaced by the values from selectionArgs, in the order that they
     *         appear in the selection. The values will be bound as Strings.
     * @param updateFields 更新字段名，使用UserRepostory的标准值
     * @param updateValues 更新的值
     * @param userData 实体对象
     */
    public final void startUpdate(int token, int functionFlag, Object cookie,
                                  String[] selection, String[] selectionArgs,
                                  String[] updateFields, String[] updateValues,
                                  UserData userData) {
        // Use the token as what so cancelOperations works properly
        Message msg = mWorkerThreadHandler.obtainMessage(token);
        msg.arg1 = EVENT_ARG_UPDATE;

        WorkerArgs args = new WorkerArgs();
        args.handler = this;
        args.functionFlag = functionFlag;
        args.cookie = cookie;
        args.selection = selection;
        args.selectionArgs = selectionArgs;
        args.updateFields = updateFields;
        args.updateValues = updateValues;
        args.userData = userData;
        msg.obj = args;

        mWorkerThreadHandler.sendMessage(msg);
    }

    /**
     * This method begins an asynchronous delete. When the delete operation is
     * done {@link #onDeleteComplete} is called.
     *
     * @param token A token passed into {@link #onDeleteComplete} to identify
     *  the delete operation.
     * @param cookie An object that gets passed into {@link #onDeleteComplete}
     * @param selection A filter declaring which rows to return, formatted as an
     *         SQL WHERE clause (excluding the WHERE itself). Passing null will
     *         return all rows for the given URI.
     * @param selectionArgs You may include ?s in selection, which will be
     *         replaced by the values from selectionArgs, in the order that they
     *         appear in the selection. The values will be bound as Strings.
     * @param userData 实体对象
     */
    public final void startDelete(int token, int functionFlag, Object cookie,
                                  String[] selection, String[] selectionArgs,
                                  UserData userData) {
        // Use the token as what so cancelOperations works properly
        Message msg = mWorkerThreadHandler.obtainMessage(token);
        msg.arg1 = EVENT_ARG_DELETE;

        WorkerArgs args = new WorkerArgs();
        args.handler = this;
        args.cookie = cookie;
        args.functionFlag = functionFlag;
        args.selection = selection;
        args.selectionArgs = selectionArgs;
        args.userData = userData;
        msg.obj = args;

        mWorkerThreadHandler.sendMessage(msg);
    }

    /**
     * Called when an asynchronous query is completed.
     *
     * @param token the token to identify the query, passed in from
     *            {@link #startQuery}.
     * @param cookie the cookie object passed in from {@link #startQuery}.
     * @param result 查询对象，需要强制转换.
     */
    protected void onQueryComplete(int token, int functionFlag, Object cookie, Object result) {
        // Empty
        if(localLOGV){
            Log.e(TAG, "onQueryComplete: sLooper " + (sLooper.getQueue().isIdle() ?"空闲":"忙") );
        }
    }

    /**
     * Called when an asynchronous insert is completed.
     *
     * @param token the token to identify the query, passed in from
     *        {@link #startInsert}.
     * @param cookie the cookie object that's passed in from
     *        {@link #startInsert}.
     * @param rowInserted the the number of the row returned from the insert operation.
     */
    protected void onInsertComplete(int token, int functionFlag, Object cookie, long rowInserted) {
        // Empty
        if(localLOGV){
            Log.e(TAG, "onQueryComplete: sLooper " + (sLooper.getQueue().isIdle() ?"空闲":"忙") );
        }
    }

    /**
     * Called when an asynchronous update is completed.
     *
     * @param token the token to identify the query, passed in from
     *        {@link #startUpdate}.
     * @param cookie the cookie object that's passed in from
     *        {@link #startUpdate}.
     * @param result the result returned from the update operation
     */
    protected void onUpdateComplete(int token, int functionFlag, Object cookie, int result) {
        // Empty
        if(localLOGV){
            Log.e(TAG, "onQueryComplete: sLooper " + (sLooper.getQueue().isIdle() ?"空闲":"忙") );
        }
    }

    /**
     * Called when an asynchronous delete is completed.
     *
     * @param token the token to identify the query, passed in from
     *        {@link #startDelete}.
     * @param cookie the cookie object that's passed in from
     *        {@link #startDelete}.
     * @param result the result returned from the delete operation
     */
    protected void onDeleteComplete(int token, int functionFlag, Object cookie, int result) {
        // Empty
        if(localLOGV){
            Log.e(TAG, "onQueryComplete: sLooper " + (sLooper.getQueue().isIdle() ?"空闲":"忙") );
        }
    }

    @Override
    public void handleMessage(Message msg) {
        WorkerArgs args = (WorkerArgs) msg.obj;

        if (localLOGV) {
            Log.d(TAG, "AsyncQueryHandler.handleMessage: msg.what=" + msg.what
                    + ", msg.arg1=" + msg.arg1);
        }

        int token = msg.what;
        int event = msg.arg1;
        // pass token back to caller on each callback.
        switch (event) {
            case EVENT_ARG_QUERY:
                onQueryComplete(token, args.functionFlag, args.cookie,  args.result);
                break;

            case EVENT_ARG_INSERT:
                onInsertComplete(token, args.functionFlag, args.cookie, (Long) args.result);
                break;

            case EVENT_ARG_UPDATE:
                onUpdateComplete(token, args.functionFlag, args.cookie, (Integer) args.result);
                break;

            case EVENT_ARG_DELETE:
                onDeleteComplete(token, args.functionFlag, args.cookie, (Integer) args.result);
                break;
        }

    }

    public final void  removeCallbacksAndMsg(){
        if(mWorkerThreadHandler != null){
            mWorkerThreadHandler.removeCallbacksAndMessages(null);
        }

    }
//    public final void quitSafely(){
//        if(sLooper != null){
//            sLooper.quitSafely();
//            mWorkerThreadHandler = null;
//        }
//    }
//
//    public final String getWorkerThreadName(){
//        LogUtil.d(TAG, "sLooper's Thread = " + sLooper.getThread().toString());
//        return sLooper != null ? sLooper.getThread().toString():null;
//    }



}
