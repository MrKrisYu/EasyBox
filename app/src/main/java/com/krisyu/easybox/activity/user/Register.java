package com.krisyu.easybox.activity.user;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;

import com.krisyu.easybox.R;
import com.krisyu.easybox.activity.user.listener.AsyncQueryListener;
import com.krisyu.easybox.activity.user.listener.MainActivityHanlderListener;
import com.krisyu.easybox.base.BaseActivity;
import com.krisyu.easybox.room_framework.entities.UserData;
import com.krisyu.easybox.room_framework.reposity.UserRepository;
import com.krisyu.easybox.room_framework.viewModels.UserViewModel;
import com.krisyu.easybox.utils.LogUtil;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class Register extends BaseActivity {
    private static final String TAG = "Register";
    private EditText mAccount;                        //用户名编辑
    private EditText mPwd;                            //密码编辑
    private EditText mPwdCheck;                       //密码编辑
    private Button mSureButton;                       //确定按钮
    private Button mCancelButton;                     //取消按钮


    private UserViewModel mUserViewModel;
    private AsyncQueryListener mAsyncQueryListener;
    private MainActivityHanlderListener mLocalHandler;

    //--------------------------------------Activity生命周期的相关方法-------------------------------------
    @Override
    protected  int getContentViewId(){
        return R.layout.activity_register;
    }

    private final int FUNCTION_REGISTER_FIND_USERDATA = 1;
    private final int FUNCTION_REGISTER_INSERT_USERDATA = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtil.d(TAG, "onCreate");
        // findViewById
        mAccount = (EditText) findViewById(R.id.resetpwd_edit_name);
        mPwd = (EditText) findViewById(R.id.resetpwd_edit_pwd_old);
        mPwdCheck = (EditText) findViewById(R.id.resetpwd_edit_pwd_new);
        mSureButton = (Button) findViewById(R.id.register_btn_sure);
        mCancelButton = (Button) findViewById(R.id.register_btn_cancel);

        //注册界面两个按钮的监听事件
        mSureButton.setOnClickListener(m_register_Listener);
        mCancelButton.setOnClickListener(m_register_Listener);


        mUserViewModel = new ViewModelProvider(this, new UserViewModel.UserInstanceFactory(this)).get(UserViewModel.class);
        LogUtil.e(TAG, "onCreate----------->Thread = " + Thread.currentThread().toString());

        //---------------------------------------AsyncQueryHandler---------------------------------
        mAsyncQueryListener = new AsyncQueryListener(mUserViewModel.getUserRepository(),
                getLifecycle(), new AsyncQueryListener.Callback() {
                    @Override
                    public void onQueryComplete(int token, int functionFlag, Object cookie, Object result) {
                        LogUtil.i(TAG, "onQueryComplete-->result = " + result);
                        Message message = mLocalHandler.obtainMessage(functionFlag);
                        message.arg1 = token;
                        Bundle bundle = new Bundle();
                        bundle.putCharSequenceArray("cookie", (CharSequence[]) cookie);
                        message.setData(bundle);
                        message.obj = result;
                        message.sendToTarget();
                    }

                    @Override
                    public void onInsertComplete(int token, int functionFlag, Object cookie, long rowInserted) {
                        LogUtil.i(TAG, "onInsertComplete-->result = " + rowInserted);
                        Message message = mLocalHandler.obtainMessage(functionFlag);
                        message.arg1 = token;
                        Bundle bundle = new Bundle();
                        bundle.putCharSequenceArray("cookie", (CharSequence[]) cookie);
                        message.setData(bundle);
                        message.obj = rowInserted;
                        message.sendToTarget();
                    }

                    @Override
                    public void onUpdateComplete(int token, int functionFlag, Object cookie, int result) {
                        LogUtil.i(TAG, "onUpdateComplete-->result = " + result);
                        Message message = mLocalHandler.obtainMessage(functionFlag);
                        message.arg1 = token;
                        Bundle bundle = new Bundle();
                        bundle.putCharSequenceArray("cookie", (CharSequence[]) cookie);
                        message.setData(bundle);
                        message.obj = result;
                        message.sendToTarget();
                    }

                    @Override
                    public void onDeleteComplete(int token, int functionFlag, Object cookie, int result) {
                        LogUtil.i(TAG, "onDeleteComplete-->result = " + result);
                        Message message = mLocalHandler.obtainMessage(functionFlag);
                        message.arg1 = token;
                        Bundle bundle = new Bundle();
                        bundle.putCharSequenceArray("cookie", (CharSequence[]) cookie);
                        message.setData(bundle);
                        message.obj = result;
                        message.sendToTarget();
                    }
                });


        //------------------------------------------localHandler---------------------------------------
        mLocalHandler = new MainActivityHanlderListener(getLifecycle(), new Handler.Callback() {
            @Override
            public boolean handleMessage(@NonNull Message msg) {
                switch (msg.what){
                    case FUNCTION_REGISTER_FIND_USERDATA:
                        UserData userData = (UserData)msg.obj;
                        CharSequence[] cookie = msg.getData().getCharSequenceArray("cookie");
                        LogUtil.e(TAG, "FUNCTION_REGISTER_FIND_USERDATA: userData = " + userData
                                + ", cookie = " + cookie);
                        if(userData == null){
                            // 不存在该用户
                            if(cookie != null && cookie.length == 3){
                                if(cookie[1].toString().equals(cookie[2].toString())){
                                    UserData mUser = new UserData(cookie[0].toString(), cookie[1].toString());
                                    mAsyncQueryListener.startInsert(UserRepository.INSERT_USERDATAS,
                                            FUNCTION_REGISTER_INSERT_USERDATA, null, mUser);
                                }else{
                                    Toast.makeText(Register.this, getString(R.string.pwd_not_the_same),
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        }else{
                            Toast.makeText(Register.this,
                                    getString(R.string.name_already_exist,userData.getUserName()),
                                    Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case FUNCTION_REGISTER_INSERT_USERDATA:
                        long rowInserted = (Long)msg.obj;
                        LogUtil.e(TAG, "FUNCTION_REGISTER_INSERT_USERDATA: rowInseted = " + rowInserted);
                        if(rowInserted >= 0){
                            Toast.makeText(Register.this, getString(R.string.register_success),Toast.LENGTH_SHORT).show();
                            //切换User Activity至Login Activity
                            Intent intent_Register_to_Login = new Intent(Register.this,MainActivity.class) ;
                            startActivity(intent_Register_to_Login);
                            finish();
                        }else{
                            Toast.makeText(Register.this, getString(R.string.register_fail),Toast.LENGTH_SHORT).show();
                        }
                }

                return false;
            }
        });

        // 注册Observer
        getLifecycle().addObserver(mAsyncQueryListener);
        getLifecycle().addObserver(mLocalHandler);

    }

    @Override
    protected void onStart(){
        super.onStart();
        LogUtil.d(TAG, "onStart");
    }

    /**
     * 活动可见:
     */
    @Override
    protected void onResume() {
        super.onResume();
        LogUtil.d(TAG, "onResume");
    }

    /**
     * 在活动处于暂停时
     */
    @Override
    protected void onPause() {
        super.onPause();
        LogUtil.d(TAG, "onPause");
    }

    @Override
    protected void onStop(){
        super.onStop();
        LogUtil.d(TAG, "onStop");
    }

    /**
     * 在活动销毁时，停止SQLiteStudioService的使用
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        LogUtil.d(TAG, "onDestroy");
    }





    View.OnClickListener m_register_Listener = new View.OnClickListener() {    //不同按钮按下的监听事件选择
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.register_btn_sure:                       //确认按钮的监听事件
                    register_check();
                    break;
                case R.id.register_btn_cancel:                     //取消按钮的监听事件,由注册界面返回登录界面
                    Intent intent_Register_to_Login = new Intent(Register.this,MainActivity.class) ;    //切换User Activity至Login Activity
                    startActivity(intent_Register_to_Login);
                    finish();
                    break;
            }
        }
    };

//----------------------------------------按钮和附属的具体方法----------------------------------------
    public void register_check() {                                //确认按钮的监听事件
        if (isUserNameAndPwdValid()) {
            String userName = mAccount.getText().toString().trim();
            String userPwd = mPwd.getText().toString().trim();
            String userPwdCheck = mPwdCheck.getText().toString().trim();

            mAsyncQueryListener.startQuery(UserRepository.FETCH_USERDATA_BY_NAME,
                    FUNCTION_REGISTER_FIND_USERDATA, new CharSequence[]{userName,userPwd, userPwdCheck},
                    new String[]{UserRepository.SELECTION_USERNAME}, new String[]{userName});
        }
    }

    public boolean isUserNameAndPwdValid() {
        if (mAccount.getText().toString().trim().equals("")) {
            Toast.makeText(this, getString(R.string.account_empty),
                    Toast.LENGTH_SHORT).show();
            return false;
        } else if (mPwd.getText().toString().trim().equals("")) {
            Toast.makeText(this, getString(R.string.pwd_empty),
                    Toast.LENGTH_SHORT).show();
            return false;
        }else if(mPwdCheck.getText().toString().trim().equals("")) {
            Toast.makeText(this, getString(R.string.pwd_check_empty),
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }


}
