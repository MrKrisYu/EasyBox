package com.krisyu.easybox.activity.user_activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.krisyu.easybox.R;
import com.krisyu.easybox.base.BaseActivity;

public class User extends BaseActivity {
    private Button mReturnButton;
    private Button mToMainActivityButton;

    @Override
    protected  int getContentViewId(){ return R.layout.activity_user; }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mReturnButton = (Button)findViewById(R.id.returnback);
        mToMainActivityButton = (Button)findViewById(R.id.to_MainActivity);

    }
    public void back_to_login(View view) {
        //setContentView(R.layout.login);
        Intent intent3 = new Intent(User.this,MainActivity.class) ;
        startActivity(intent3);
        finish();

    }

    public void to_MainActivity_button(View view) {
        //setContentView(R.layout.login);
        Intent intent4 = new Intent(User.this,MainActivity.class) ;
        startActivity(intent4);
    }

}
