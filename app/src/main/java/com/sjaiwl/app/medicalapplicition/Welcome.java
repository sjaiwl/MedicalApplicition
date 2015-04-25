package com.sjaiwl.app.medicalapplicition;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Window;
import android.view.WindowManager;

/**
 * Created by sjaiwl on 15/3/19.
 */
public class Welcome extends Activity {

    private Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams. FLAG_FULLSCREEN ,
                WindowManager.LayoutParams. FLAG_FULLSCREEN);
        setContentView(R.layout.welcome_page);


        mHandler = new Handler() {
            public void handleMessage(Message msg) {
                if (msg.what == 1) {
                    Intent intent = new Intent();
                    intent.setClass(Welcome.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        };
        mHandler.postDelayed(mRunnable, 2000);
    }

    private Runnable mRunnable = new Runnable() {
        public void run() {
            mHandler.sendEmptyMessage(1);
        }
    };
}
