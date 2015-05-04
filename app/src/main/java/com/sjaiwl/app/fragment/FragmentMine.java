package com.sjaiwl.app.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sjaiwl.app.function.Configuration;
import com.sjaiwl.app.function.UsedTools;
import com.sjaiwl.app.function.UserInfo;
import com.sjaiwl.app.medicalapplicition.LoginActivity;
import com.sjaiwl.app.medicalapplicition.MainActivity;
import com.sjaiwl.app.medicalapplicition.MineInformation;
import com.sjaiwl.app.medicalapplicition.MineSetting;
import com.sjaiwl.app.medicalapplicition.R;
import com.sjaiwl.app.medicalapplicition.ViewPicture;
import com.sjaiwl.app.smart.WebImage;
import com.sjaiwl.app.smart.WebImageCache;
import com.sjaiwl.app.tools.CircularLoginImage;

/**
 * Created by sjaiwl on 15/3/19.
 */
public class FragmentMine extends Fragment {

    private CircularLoginImage userImage;
    private TextView userName;
    private RelativeLayout userInformation;
    private RelativeLayout userSetting;
    private RelativeLayout userExit;
    private Intent intent;

    private final String PREFERENCE_NAME = "userInfo";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.mine_page, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
        initData();
    }

    @Override
    public void onResume() {
        super.onResume();
        userName.setText(UserInfo.user.getDoctor_name());
        if (UserInfo.user.getDoctor_url() != null) {
            userImage.setImageUrl(UserInfo.user.getDoctor_url(), 1);
            userImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    intent.setClass(getActivity(), ViewPicture.class);
                    startActivity(intent);
                }
            });
        }
    }

    private void initView() {
        userImage = (CircularLoginImage) getActivity().findViewById(R.id.minePage_userPhoto);
        userName = (TextView) getActivity().findViewById(R.id.minePage_userName);
        userInformation = (RelativeLayout) getActivity().findViewById(R.id.minePage_userInfo);
        userSetting = (RelativeLayout) getActivity().findViewById(R.id.minePage_userSetting);
        userExit = (RelativeLayout) getActivity().findViewById(R.id.minePage_userExitButton);
    }

    private void initData() {
        intent = new Intent();
        if (UserInfo.user.getDoctor_url() != null) {
            userImage.setImageUrl(UserInfo.user.getDoctor_url(), 1, true);
        }
        userInformation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent.setClass(getActivity(), MineInformation.class);
                startActivity(intent);
            }
        });

        userSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent.setClass(getActivity(), MineSetting.class);
                startActivity(intent);
            }
        });

        userExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(getActivity())
                        .setTitle("确认")
                        .setMessage("确定退出当前账号吗？")
                        .setPositiveButton("确定",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        intent.setClass(getActivity(), LoginActivity.class);
                                        startActivity(intent);
                                        exitLogin();
                                        getActivity().finish();
                                    }
                                }).setNegativeButton("取消", null).show();

            }
        });
    }

    private void exitLogin() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(PREFERENCE_NAME, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("UserName", "");
        editor.putString("PassWord", "");
        editor.commit();
        UserInfo.setEmpty();
    }
}
