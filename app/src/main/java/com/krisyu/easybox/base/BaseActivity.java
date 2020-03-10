package com.krisyu.easybox.base;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public abstract class BaseActivity extends AppCompatActivity {
   private Toast mToast;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(getContentViewId());
        ActivityCollector.addActivity(this);
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        ActivityCollector.removeActivity(this);
    }

    /**
     * setContentView#layoutResID
     */
    protected abstract int getContentViewId();

    /**
     * find view by id
     */
    protected <T> T find(Class<T> cls, int id) {
        return cls.cast(findViewById(id));
    }

    protected void toast(String text) {
        try {
            if (mToast == null) {
                mToast = Toast.makeText(BaseActivity.this, text, Toast.LENGTH_SHORT);
            } else {
                mToast.setText(text);
            }
            mToast.setDuration(Toast.LENGTH_SHORT);
            mToast.show();
        }catch (Exception e){}

    }

}
