package com.sjaiwl.app.medicalapplicition;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sjaiwl.app.function.Configuration;
import com.sjaiwl.app.function.UserInfo;
import com.sjaiwl.app.tools.ToggleButton;

/**
 * Created by sjaiwl on 15/4/2.
 */
public class MineSetting extends Activity implements View.OnClickListener{

    private TextView cancelButton;
    private RelativeLayout editPassword;
    private RelativeLayout aboutButton;
    private ToggleButton uploadSetting;
    private ToggleButton viewSetting;

    private final String PREFERENCE_NAME = "userSetting"+ UserInfo.user.getDoctor_id();
    public static boolean uploadSettingState;
    public static boolean viewSettingState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.mine_setting);
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences preferences = getSharedPreferences(PREFERENCE_NAME, Activity.MODE_PRIVATE);
        uploadSettingState = preferences.getBoolean("uploadSettingState", false);
        viewSettingState = preferences.getBoolean("viewSettingState", true);
        initData();
    }

    private void initView(){
        cancelButton = (TextView)findViewById(R.id.mineSetting_cancelButton);
        editPassword = (RelativeLayout)findViewById(R.id.mineSetting_editPassword);
        aboutButton = (RelativeLayout)findViewById(R.id.mineSetting_aboutButton);
        uploadSetting = (ToggleButton)findViewById(R.id.mineSetting_uploadToggle);
        viewSetting = (ToggleButton)findViewById(R.id.mineSetting_viewToggle);
    }
    private void initData(){
        editPassword.setOnClickListener(this);
        aboutButton.setOnClickListener(this);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        uploadSetting.setToggleState(uploadSettingState);
        uploadSetting.setOnToggleChanged(new ToggleButton.OnToggleChanged() {
            @Override
            public void onToggle(boolean on) {
                uploadSettingState = on;
                if(on){
                    Log.i("state","on");
                }else{
                    Log.i("state","off");
                }
            }
        });
        viewSetting.setToggleState(viewSettingState);
        viewSetting.setOnToggleChanged(new ToggleButton.OnToggleChanged() {
            @Override
            public void onToggle(boolean on) {
                viewSettingState = on;
                if(on){
                    Log.i("state","on");
                }else{
                    Log.i("state","off");
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        switch (v.getId()){
            case R.id.mineSetting_editPassword:
                intent.setClass(this,UpdatePassword.class);
                startActivity(intent);
                break;
            case R.id.mineSetting_aboutButton:
                intent.setClass(this,AboutPage.class);
                startActivity(intent);
                break;
            default:
                break;

        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences sharedPreferences = getSharedPreferences(PREFERENCE_NAME, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("uploadSettingState", uploadSettingState);
        editor.putBoolean("viewSettingState", viewSettingState);
        editor.commit();
    }
}
