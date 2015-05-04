/*
 * 官网地站:http://www.mob.com
 * 技术支持QQ: 4006852216
 * 官方微信:ShareSDK   （如果发布新版本的话，我们将会第一时间通过微信将版本更新内容推送给您。如果使用过程中有任何问题，也可以通过微信与我们取得联系，我们将会在24小时内给予回复）
 *
 * Copyright (c) 2014年 mob.com. All rights reserved.
 */
package com.sjaiwl.app.tools;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.sjaiwl.app.medicalapplicition.R;

public class UploadDialog extends Dialog {
    private TextView textView;
    private ProgressBar progressBar;
    private Activity activity;
    private String text;

    public UploadDialog(Activity activity, int theme, int text) {
        super(activity, theme);
        this.activity = activity;
        this.text = activity.getResources().getString(text);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.upload_progress_dialog);
        initView();
    }

    private void initView() {
        textView = (TextView) this.findViewById(R.id.uploadDialog_textView);
        progressBar = (ProgressBar) this.findViewById(R.id.uploadDialog_progressBar);
        textView.setText(text);
    }
}
