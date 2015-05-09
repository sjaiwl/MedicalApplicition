package com.sjaiwl.app.medicalapplicition;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.sjaiwl.app.function.AppConfiguration;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by sjaiwl on 15/3/30.
 */
public class SettingPasswordActivity extends Activity {

    private TextView cancelButton;
    private RelativeLayout doneButton;
    private EditText settingPassword;
    private CheckBox checkBox;
    private LinearLayout beforeLayout;
    private LinearLayout afterLayout;
    private RelativeLayout afterDoneButton;

    private final String PREFERENCE_NAME = "userInfo";
    private String successResponse = null;
    private String doctor_telephone = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.settingpassword_page);
        initView();
        initData();
    }

    private void initView() {
        cancelButton = (TextView) findViewById(R.id.settingPassword_cancelButton);
        doneButton = (RelativeLayout) findViewById(R.id.settingPassword_actionDone);
        settingPassword = (EditText) findViewById(R.id.settingPassword_editText);
        checkBox = (CheckBox) findViewById(R.id.settingPassword_checkBox);
        beforeLayout = (LinearLayout) findViewById(R.id.settingPassword_beforeLayout);
        afterLayout = (LinearLayout) findViewById(R.id.settingPassword_afterLayout);
        afterDoneButton = (RelativeLayout) findViewById(R.id.settingPassword_afterActionDone);
        beforeLayout.setVisibility(View.VISIBLE);
        afterLayout.setVisibility(View.GONE);
    }

    private void initData() {
        doctor_telephone = getIntent().getStringExtra("doctor_telephone");
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    settingPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    settingPassword.setSelection(settingPassword.getText().toString().trim().length());
                } else {
                    settingPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    settingPassword.setSelection(settingPassword.getText().toString().trim().length());
                }
            }
        });

        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) settingPassword
                        .getContext().getSystemService(
                                Context.INPUT_METHOD_SERVICE);
                if (imm.isActive()) {
                    imm.hideSoftInputFromWindow(settingPassword.getWindowToken(), 0);
                }
                if (!settingPassword.getText().toString().trim().isEmpty()) {
                    postData("doctor_password", settingPassword.getText().toString().trim());
                } else {
                    Toast.makeText(getApplicationContext(), "密码不能为空！", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void postData(final String type, final String value) {
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("doctor_telephone", doctor_telephone);
        map.put(type, value);
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JSONObject jsonObject = new JSONObject(map);
        String url = AppConfiguration.settingPasswordUrl;
        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject,
                new Response.Listener<JSONObject>() {
                    @SuppressLint("ShowToast")
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            successResponse = response.get("success").toString();
                        } catch (JSONException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                        if (successResponse.equals("1")) {
                            Toast.makeText(SettingPasswordActivity.this, "重置成功", Toast.LENGTH_SHORT).show();
                            SharedPreferences sharedPreferences = getSharedPreferences(PREFERENCE_NAME, Activity.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("PassWord", value);
                            editor.commit();
                            goToLogin();
                        }
                        if (successResponse.equals("0")) {
                            Toast.makeText(SettingPasswordActivity.this, "重置失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @SuppressLint("ShowToast")
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(SettingPasswordActivity.this, "网络访问异常", Toast.LENGTH_SHORT).show();
                    }
                });
        requestQueue.add(jsonRequest);
    }

    private void goToLogin() {
        beforeLayout.setVisibility(View.GONE);
        afterLayout.setVisibility(View.VISIBLE);
        afterDoneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(1);
                finish();
            }
        });
    }
}
