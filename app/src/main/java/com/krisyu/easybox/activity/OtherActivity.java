package com.krisyu.easybox.activity;


import android.os.Bundle;

import com.krisyu.easybox.R;
import com.krisyu.easybox.base.BaseActivity;

public class OtherActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other);
    }

    @Override
    protected  int getContentViewId(){
        return R.layout.activity_other;
    }
}
