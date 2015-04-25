package com.sjaiwl.app.medicalapplicition;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Created by sjaiwl on 15/4/3.
 */
public class AboutPage extends Activity implements View.OnClickListener {
    private TextView cancelButton;
    private RelativeLayout update;
    private RelativeLayout function;
    private RelativeLayout help;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.aboutpage);
        initView();
    }

    private void initView() {
        cancelButton = (TextView) findViewById(R.id.aboutPage_cancelButton);
        update = (RelativeLayout) findViewById(R.id.aboutPage_updateApp);
        function = (RelativeLayout) findViewById(R.id.aboutPage_APPFunction);
        help = (RelativeLayout) findViewById(R.id.aboutPage_help);
        cancelButton.setOnClickListener(this);
        update.setOnClickListener(this);
        function.setOnClickListener(this);
        help.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.aboutPage_cancelButton:
                finish();
                break;
            case R.id.aboutPage_updateApp:
                break;
            case R.id.aboutPage_APPFunction:
                break;
            case R.id.aboutPage_help:
                break;
            default:
                break;
        }
    }
}
