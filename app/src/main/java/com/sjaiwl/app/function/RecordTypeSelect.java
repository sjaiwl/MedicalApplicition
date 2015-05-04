package com.sjaiwl.app.function;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.sjaiwl.app.medicalapplicition.LoginActivity;
import com.sjaiwl.app.medicalapplicition.R;
import com.sjaiwl.app.medicalapplicition.UploadRecord;

/**
 * Created by sjaiwl on 15/3/25.
 */
public class RecordTypeSelect extends Dialog implements View.OnClickListener {

    private TextView type1;
    private TextView type2;
    private TextView type3;
    private Activity activity;
    private Intent intent;
    private PatientInfo patientInfo;

    public RecordTypeSelect(Activity activity, PatientInfo patientInfo) {
        super(activity);
        this.activity = activity;
        this.patientInfo = patientInfo;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.record_type);
        initView();
    }

    private void initView() {
        intent = new Intent();
        type1 = (TextView) this.findViewById(R.id.recordDialog_type1);
        type2 = (TextView) this.findViewById(R.id.recordDialog_type2);
        type3 = (TextView) this.findViewById(R.id.recordDialog_type3);
        type1.setOnClickListener(this);
        type2.setOnClickListener(this);
        type3.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        intent.putExtra("PatientOnClick", patientInfo);
        intent.setClass(activity, UploadRecord.class);
        switch (v.getId()) {
            case R.id.recordDialog_type1:
                intent.putExtra("RecordSort", "手术");
                activity.startActivity(intent);
                dismiss();
                break;
            case R.id.recordDialog_type2:
                intent.putExtra("RecordSort", "住院查房");
                activity.startActivity(intent);
                dismiss();
                break;
            case R.id.recordDialog_type3:
                intent.putExtra("RecordSort", "鉴定笔记");
                activity.startActivity(intent);
                dismiss();
                break;

        }
    }
}
