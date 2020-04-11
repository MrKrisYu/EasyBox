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

public class Resetpwd extends BaseActivity {
    @Override
    protected  int getContentViewId(){ return R.layout.activity_resetpwd; }

    private static final String TAG = "Resetpwd";
    private EditText mAccount;                        //用户名编辑
    private EditText mPwd_old;                            //密码编辑
    private EditText mPwd_new;                            //密码编辑
    private EditText mPwdCheck;                       //密码编辑
    private Button mSureButton;                       //确定按钮
    private Button mCancelButton;                     //取消按钮

    // ViewModel
    private UserViewModel mUserViewModel;


    private AsyncQueryListener mAsyncQueryListener;
    private MainActivityHanlderListener mLocalHandler;

    private final int FUNCTION_RESETPWD_FIND_USERDATA = 1;
    private final int FUNCTION_RESETPWD_UPDATE_USERD_PWD_BY_NAME = 2;


    //------------------------------------Activity生命周期 的相关方法----------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtil.d(TAG, "onCreate");
        // findViewById
        mAccount = (EditText) findViewById(R.id.resetpwd_edit_name);
        mPwd_old = (EditText) findViewById(R.id.resetpwd_edit_pwd_old);
        mPwd_new = (EditText) findViewById(R.id.resetpwd_edit_pwd_new);
        mPwdCheck = (EditText) findViewById(R.id.resetpwd_edit_pwd_check);

        mSureButton = (Button) findViewById(R.id.resetpwd_btn_sure);
        mCancelButton = (Button) findViewById(R.id.resetpwd_btn_cancel);

        //注册界面两个按钮的监听事件
        mSureButton.setOnClickListener(m_resetpwd_Listener);
        mCancelButton.setOnClickListener(m_resetpwd_Listener);


        // ViewModel
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
                    case FUNCTION_RESETPWD_FIND_USERDATA:
                        UserData userData = (UserData)msg.obj;
                        CharSequence[] cookies = msg.getData().getCharSequenceArray("cookie");
                        LogUtil.e(TAG, "FUNCTION_RESETPWD_FIND_USERDATA: userData = " + userData
                                + ", cookies = " + cookies );
                        if(cookies !=null && cookies.length == 4){
                            // 用户不存在
                            if(userData == null){
                                Toast.makeText(Resetpwd.this,
                                        getString(R.string.name_not_exist, cookies[0].toString()),
                                        Toast.LENGTH_SHORT).show();
                            }else{
                                // 密码不匹配用户名
                                if(!userData.getUserPwd().equals(cookies[1].toString())){
                                    Toast.makeText(Resetpwd.this,
                                            getString(R.string.pwd_not_fit_user), Toast.LENGTH_SHORT).show();
                                }else{
                                    // 确认密码和新密码不一致
                                    if(!cookies[2].toString().equals(cookies[3].toString())){
                                        Toast.makeText(Resetpwd.this,
                                                getString(R.string.pwd_not_the_same), Toast.LENGTH_SHORT).show();
                                    }else{ // 更新用户密码
                                        mAsyncQueryListener.startUpdate(UserRepository.UPDATE_USERPWD_BY_NAME,
                                                FUNCTION_RESETPWD_UPDATE_USERD_PWD_BY_NAME,null,
                                                new String[]{UserRepository.SELECTION_USERNAME}, new String[]{userData.getUserName()},
                                                new String[]{UserRepository.UPDATE_FIELD_NEW_USERPWD}, new String[]{cookies[2].toString()},
                                                null);
                                    }
                                }
                            }
                        }
                        break;
                    case FUNCTION_RESETPWD_UPDATE_USERD_PWD_BY_NAME:
                        int rowUpdated = (Integer)msg.obj;
                        LogUtil.e(TAG, "FUNCTION_RESETPWD_UPDATE_USERD_PWD_BY_NAME: rowUpdated = " + rowUpdated);
                        if(rowUpdated >= 0){
                            Toast.makeText(Resetpwd.this, getString(R.string.resetpwd_success),Toast.LENGTH_SHORT).show();

                            Intent intent_Register_to_Login = new Intent(Resetpwd.this,MainActivity.class) ;    //切换User Activity至Login Activity
                            startActivity(intent_Register_to_Login);
                            finish();
                        }else{
                            Toast.makeText(Resetpwd.this, getString(R.string.resetpwd_fail),Toast.LENGTH_SHORT).show();
                        }
                }

                return false;
            }
        });

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


    View.OnClickListener m_resetpwd_Listener = new View.OnClickListener() {    //不同按钮按下的监听事件选择
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.resetpwd_btn_sure:                       //确认按钮的监听事件
                    resetpwd_check();
                    break;
                case R.id.resetpwd_btn_cancel:                     //取消按钮的监听事件,由注册界面返回登录界面
                    Intent intent_Resetpwd_to_Login = new Intent(Resetpwd.this,MainActivity.class) ;    //切换Resetpwd Activity至Login Activity
                    startActivity(intent_Resetpwd_to_Login);
                    finish();
                    break;
            }
        }
    };

    //----------------------------------------按钮和附属的具体方法----------------------------------------
    public void resetpwd_check() {                                //确认按钮的监听事件
        if (isUserNameAndPwdValid()) {
            String userName = mAccount.getText().toString().trim();
            String userPwd_old = mPwd_old.getText().toString().trim();
            String userPwd_new = mPwd_new.getText().toString().trim();
            String userPwdCheck = mPwdCheck.getText().toString().trim();
            mAsyncQueryListener.startQuery(UserRepository.FETCH_USERDATA_BY_NAME_AND_PWD,
                    FUNCTION_RESETPWD_FIND_USERDATA, new CharSequence[]{userName, userPwd_old, userPwd_new, userPwdCheck},
                    new String[]{UserRepository.SELECTION_USERNAME, UserRepository.SELECTION_USERPWD},
                    new String[]{userName, userPwd_old});

        }
    }

    public boolean isUserNameAndPwdValid() {

        if (mAccount.getText().toString().trim().equals("")) {
            Toast.makeText(this, getString(R.string.account_empty),Toast.LENGTH_SHORT).show();
            return false;
        } else if (mPwd_old.getText().toString().trim().equals("")) {
            Toast.makeText(this, getString(R.string.pwd_empty),Toast.LENGTH_SHORT).show();
            return false;
        } else if (mPwd_new.getText().toString().trim().equals("")) {
            Toast.makeText(this, getString(R.string.pwd_new_empty),Toast.LENGTH_SHORT).show();
            return false;
        }else if(mPwdCheck.getText().toString().trim().equals("")) {
            Toast.makeText(this, getString(R.string.pwd_check_empty),Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}
