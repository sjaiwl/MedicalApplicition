package com.sjaiwl.app.medicalapplicition;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.sjaiwl.app.function.PatientInfo;
import com.sjaiwl.app.smart.SmartImageView;

/**
 * Created by sjaiwl on 15/4/1.
 */
public class PatientDetails extends Activity {

    private PatientInfo patientInfo;
    private TextView cancelButton;
    private TextView patientTopName;
    private SmartImageView patientImage;
    private TextView patientName;
    private TextView patientGender;
    private TextView patientAge;
    private TextView patientHeight;
    private TextView patientWeight;
    private TextView patientTelephone;
    private TextView patientIDNumber;
    private TextView patientResidence;
    private TextView patientSituation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.patient_details);
        initView();
        initData();
    }

    private void initView() {
        patientInfo = (PatientInfo) getIntent().getSerializableExtra("patientInfo");
        cancelButton = (TextView) findViewById(R.id.patientDetails_cancelButton);
        patientTopName = (TextView) findViewById(R.id.patientDetails_patientTopName);
        patientImage = (SmartImageView) findViewById(R.id.patientDetails_patientImage);
        patientName = (TextView) findViewById(R.id.patientDetails_patientName);
        patientGender = (TextView) findViewById(R.id.patientDetails_patientGender);
        patientAge = (TextView) findViewById(R.id.patientDetails_patientAge);
        patientHeight = (TextView) findViewById(R.id.patientDetails_patientHeight);
        patientWeight = (TextView) findViewById(R.id.patientDetails_patientWeight);
        patientTelephone = (TextView) findViewById(R.id.patientDetails_patientTelephone);
        patientIDNumber = (TextView) findViewById(R.id.patientDetails_patientIDNumber);
        patientResidence = (TextView) findViewById(R.id.patientDetails_patientResidence);
        patientSituation = (TextView) findViewById(R.id.patientDetails_patientSituation);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initData() {
        patientTopName.setText(patientInfo.getPatient_name().toString());
        patientImage.setImageUrl(patientInfo.getPatient_url().toString(), 2);
        patientName.setText(patientInfo.getPatient_name().toString());
        patientGender.setText(patientInfo.getPatient_gender().toString());
        patientAge.setText(patientInfo.getPatient_age().toString());
        patientHeight.setText(patientInfo.getPatient_height() + " CM");
        patientWeight.setText(patientInfo.getPatient_weight() + " KG");
        patientTelephone.setText(patientInfo.getPatient_telephone().toString());
        patientIDNumber.setText(patientInfo.getPatient_idNumber().toString());
        patientResidence.setText(patientInfo.getPatient_residence().toString());
        patientSituation.setText(patientInfo.getPatient_situation().toString());
    }
}
