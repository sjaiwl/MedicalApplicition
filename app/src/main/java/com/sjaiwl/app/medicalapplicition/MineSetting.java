package com.sjaiwl.app.medicalapplicition;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.Window;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sjaiwl.app.function.UsedTools;
import com.sjaiwl.app.function.UserInfo;
import com.sjaiwl.app.tools.ToggleButton;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;

/**
 * Created by sjaiwl on 15/4/2.
 */
public class MineSetting extends Activity implements View.OnClickListener {

    private TextView cancelButton;
    private RelativeLayout editPassword;
    private RelativeLayout aboutButton;
    private RelativeLayout cacheButton;
    private TextView cacheSizeTextView;
    private ToggleButton uploadSetting;
    private ToggleButton viewSetting;

    private final String PREFERENCE_NAME = "userSetting" + UserInfo.user.getDoctor_id();
    public static boolean uploadSettingState;
    public static boolean viewSettingState;
    private static String dir = Environment.getExternalStorageDirectory()
            .getAbsolutePath() + "/MedicalApplication/Camera/patientResource/";
    private double cacheSize;
    private DecimalFormat df = new DecimalFormat("0.00");//格式化小数，不足的补0

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
        try {
            cacheSize = UsedTools.getFolderSize(new File(dir));
        } catch (Exception e) {
            e.printStackTrace();
        }
        cacheSizeTextView.setText(df.format(cacheSize) + "MB");
        initData();
    }

    private void initView() {
        cancelButton = (TextView) findViewById(R.id.mineSetting_cancelButton);
        editPassword = (RelativeLayout) findViewById(R.id.mineSetting_editPassword);
        aboutButton = (RelativeLayout) findViewById(R.id.mineSetting_aboutButton);
        cacheButton = (RelativeLayout) findViewById(R.id.mineSetting_cacheButton);
        cacheSizeTextView = (TextView) findViewById(R.id.mineSetting_cacheButton_cacheSize);
        uploadSetting = (ToggleButton) findViewById(R.id.mineSetting_uploadToggle);
        viewSetting = (ToggleButton) findViewById(R.id.mineSetting_viewToggle);
    }

    private void initData() {
        editPassword.setOnClickListener(this);
        aboutButton.setOnClickListener(this);
        cacheButton.setOnClickListener(this);
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
            }
        });
        viewSetting.setToggleState(viewSettingState);
        viewSetting.setOnToggleChanged(new ToggleButton.OnToggleChanged() {
            @Override
            public void onToggle(boolean on) {
                viewSettingState = on;
            }
        });
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        switch (v.getId()) {
            case R.id.mineSetting_editPassword:
                intent.setClass(this, UpdatePassword.class);
                startActivity(intent);
                break;
            case R.id.mineSetting_aboutButton:
                intent.setClass(this, AboutPage.class);
                startActivity(intent);
                break;
            case R.id.mineSetting_cacheButton:
                //清楚缓存
                AlertDialog dialog = new AlertDialog.Builder(this)
                        .setTitle("确认")
                        .setMessage("确定清除缓存吗？")
                        .setPositiveButton("确定",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        try {
                                            UsedTools.deleteFolderFile(dir, false);
                                            cacheSizeTextView.setText("0.00MB");
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }).setNegativeButton("取消", null).show();
                dialog.setCanceledOnTouchOutside(true);
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
