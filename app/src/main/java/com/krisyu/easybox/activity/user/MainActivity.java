package com.krisyu.easybox.activity.user;


/*****************************************************
 *                  Task List
 * 1、
 * 2、
 * 3、 。。。
 *
 *
 *
 *
 ***************************************************/





import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.krisyu.easybox.R;
import com.krisyu.easybox.activity.bottom_navigation.NormalActivity;
import com.krisyu.easybox.activity.user.listener.AsyncQueryListener;
import com.krisyu.easybox.activity.user.listener.MainActivityHanlderListener;
import com.krisyu.easybox.base.BaseActivity;
import com.krisyu.easybox.room_framework.entities.UserData;
import com.krisyu.easybox.room_framework.reposity.UserRepository;
import com.krisyu.easybox.room_framework.viewModels.UserViewModel;
import com.krisyu.easybox.utils.LogUtil;

import java.util.List;

import pl.com.salsoft.sqlitestudioremote.SQLiteStudioService;

public class MainActivity extends BaseActivity {

    private static final String TAG = "MainActivity";
    public int pwdresetFlag=0;
    private EditText mAccount;                        //用户名编辑
    private EditText mPwd;                            //密码编辑
    private Button mRegisterButton;                   //注册按钮
    private Button mLoginButton;                      //登录按钮
    private Button mCancleButton;                     //注销按钮
    private CheckBox mRememberCheck;

    private SharedPreferences login_sp;
    private String userNameValue,passwordValue;

    private View loginView;                           //登录
    private View loginSuccessView;
    private TextView loginSuccessShow;
    private TextView mChangepwdText;

    // ViewModel
    private UserViewModel mUserViewModel;

    private AsyncQueryListener mAsyncQueryListener;
    private MainActivityHanlderListener mLocalHandler;






    //------------------------------------Activity生命周期 的相关方法-------------------------------------
    @Override
    protected  int getContentViewId(){ return R.layout.activity_main; }

    // 功能标志位
    private  final int FUNCTION_LOGIN_FIND_USER = 1;
    private  final int FUNCTION_CANCEL_FIND_USER = 2;
    private  final int FUNCTION_CANCLE_DELETE_USERDATA = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtil.d(TAG, "onCreate");
        SQLiteStudioService.instance().start(this);

        //findViewById
        mAccount = (EditText) findViewById(R.id.login_edit_account);
        mPwd = (EditText) findViewById(R.id.login_edit_pwd);
        mRegisterButton = (Button) findViewById(R.id.login_btn_register);
        mLoginButton = (Button) findViewById(R.id.login_btn_login);
        mCancleButton = (Button) findViewById(R.id.login_btn_cancle);
        loginView=findViewById(R.id.login_view);
        loginSuccessView=findViewById(R.id.login_success_view);
        loginSuccessShow=(TextView) findViewById(R.id.login_success_show);
        mChangepwdText = (TextView) findViewById(R.id.login_text_change_pwd);
        mRememberCheck = (CheckBox) findViewById(R.id.Login_Remember);

        // 记住密码的相关操作
        login_sp = getSharedPreferences("userInfo", 0);
        String name=login_sp.getString("USER_NAME", "");
        String pwd =login_sp.getString("PASSWORD", "");
        boolean choseRemember =login_sp.getBoolean("mRememberCheck", false);
        boolean choseAutoLogin =login_sp.getBoolean("mAutologinCheck", false);
        //如果上次选了记住密码，那进入登录页面也自动勾选记住密码，并填上用户名和密码
        if(choseRemember){
            mAccount.setText(name);
            mPwd.setText(pwd);
            mRememberCheck.setChecked(true);
        }

        // 设置单击监听
        mRegisterButton.setOnClickListener(mListener);                      //采用OnClickListener方法设置不同按钮按下之后的监听事件
        mLoginButton.setOnClickListener(mListener);
        mCancleButton.setOnClickListener(mListener);
        mChangepwdText.setOnClickListener(mListener);

        ImageView image = (ImageView) findViewById(R.id.logo);             //使用ImageView显示logo
        image.setImageResource(R.drawable.logo);


        // Room 初始化
        // ViewModel
        mUserViewModel = new ViewModelProvider(this, new UserViewModel.UserInstanceFactory(this)).get(UserViewModel.class);
        mUserViewModel.getAllUserData().observe(this, new Observer<List<UserData>>() {
            @Override
            public void onChanged(List<UserData> userData) {

            }
        });
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

        mLocalHandler = new MainActivityHanlderListener(getLifecycle(),
                new Handler.Callback() {
            @Override
            public boolean handleMessage(@NonNull Message msg) {
                switch (msg.what){
                    case FUNCTION_LOGIN_FIND_USER:
                        UserData userData = (UserData)msg.obj;
                        LogUtil.e(TAG, "FUNCTION_LOGIN_FIND_USER: userData = " + userData);
                        if(userData == null){
                            //登录失败提示
                            Toast.makeText(MainActivity.this, getString(R.string.login_fail),Toast.LENGTH_SHORT).show();
                            LogUtil.e(TAG, "FUNCTION_LOGIN_FIND_USER: userData 我进来了");

                        }else{
                            SharedPreferences.Editor editor =login_sp.edit();
                            //保存用户名和密码
                            editor.putString("USER_NAME", userData.getUserName());
                            editor.putString("PASSWORD", userData.getUserPwd());

                            //是否记住密码
                            if(mRememberCheck.isChecked()){
                                editor.putBoolean("mRememberCheck", true);
                            }else{
                                editor.putBoolean("mRememberCheck", false);
                            }
                            editor.apply();

                            NormalActivity.actionStart(MainActivity.this,userData);
                            finish();
                            //登录成功提示
                            Toast.makeText(MainActivity.this, getString(R.string.login_success),Toast.LENGTH_SHORT).show();
                        }
                        userData = null;
                        break;
                    case FUNCTION_CANCEL_FIND_USER:
                        UserData userData1 = (UserData)msg.obj;
                        LogUtil.e(TAG, "FUNCTION_CANCEL_FIND_USER: userData1 = " + userData1);
                        if(!userData1.isEmpty()){
                            mPwd.setText("");
                            mAccount.setText("");
                            mAsyncQueryListener.startDelete(UserRepository.DELETE_USERS,
                                    FUNCTION_CANCLE_DELETE_USERDATA,null,
                                    null, null,
                                    userData1);
                        }else{
                            //注销失败提示
                            Toast.makeText(MainActivity.this, getString(R.string.cancel_fail),Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case FUNCTION_CANCLE_DELETE_USERDATA:
                        int deletedRow = (Integer) msg.obj;
                        LogUtil.e(TAG, "FUNCTION_CANCLE_DELETE_USERDATA: deletedRow = " + deletedRow);
                        if(deletedRow >= 0){
                            //注销成功提示
                            Toast.makeText(MainActivity.this, getString(R.string.cancel_success),Toast.LENGTH_SHORT).show();
                        }else{
                            //注销失败提示
                            Toast.makeText(MainActivity.this, getString(R.string.cancel_fail),Toast.LENGTH_SHORT).show();
                        }
                }

                return false;
            }
        });

        // 注册观察者
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
     * 在活动处于暂停时，关闭数据库
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
        SQLiteStudioService.instance().stop();
    }

    /**
     * 在活动销毁时，停止SQLiteStudioService的使用
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        LogUtil.d(TAG, "onDestroy");
    }




    View.OnClickListener mListener = new View.OnClickListener() {                  //不同按钮按下的监听事件选择
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.login_btn_register:                            //登录界面的注册按钮
                    Intent intent_Login_to_Register = new Intent(MainActivity.this, Register.class) ;    //切换Login Activity至User Activity
                    startActivity(intent_Login_to_Register);
                    break;
                case R.id.login_btn_login:                              //登录界面的登录按钮
                    login();
                    break;
                case R.id.login_btn_cancle:                             //登录界面的注销按钮
                    cancel();
                    break;
                case R.id.login_text_change_pwd:                             //登录界面的注销按钮
                    Intent intent_Login_to_reset = new Intent(MainActivity.this,Resetpwd.class) ;    //切换Login Activity至User Activity
                    startActivity(intent_Login_to_reset);
                    finish();
                    break;
            }
        }
    };
//----------------------------------------按钮和附属的具体方法----------------------------------------
    /**
     * 登录至主界面，
     */
    public void login() {                                              //登录按钮监听事件
        if (isUserNameAndPwdValid()) {
            String userName = mAccount.getText().toString().trim();    //获取当前输入的用户名和密码信息
            String userPwd = mPwd.getText().toString().trim();
            LogUtil.e(TAG, "执行Retrieve----------->Thread = " + Thread.currentThread().toString());
            mAsyncQueryListener.startQuery(UserRepository.FETCH_USERDATA_BY_NAME_AND_PWD, FUNCTION_LOGIN_FIND_USER,null,
                    new String[]{UserRepository.SELECTION_USERNAME, UserRepository.SELECTION_USERPWD},
                    new String[]{userName, userPwd});

        }else{
            Toast.makeText(this, getString(R.string.login_fail),Toast.LENGTH_SHORT).show();  //登录失败提示
        }
    }

    /**
     * 注销用户，销毁该用户以及所属的所有资料。
     */
    public void cancel() {           //注销
        if (isUserNameAndPwdValid()) {
            String userName = mAccount.getText().toString().trim();    //获取当前输入的用户名和密码信息
            String userPwd = mPwd.getText().toString().trim();
            mAsyncQueryListener.startQuery(UserRepository.FETCH_USERDATA_BY_NAME_AND_PWD, FUNCTION_CANCEL_FIND_USER,null,
                    new String[]{UserRepository.SELECTION_USERNAME, UserRepository.SELECTION_USERPWD},
                    new String[]{userName, userPwd});
        }else{
            Toast.makeText(this, getString(R.string.cancel_fail),Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 验证用户名和密码是否为空
     * @return 返回true,如果两者均不为空。
     */
    public boolean isUserNameAndPwdValid() {
        if (mAccount.getText().toString().trim().equals("")) {
            Toast.makeText(this, getString(R.string.account_empty),
                    Toast.LENGTH_SHORT).show();
            return false;
        } else if (mPwd.getText().toString().trim().equals("")) {
            Toast.makeText(this, getString(R.string.pwd_empty),
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }




}
