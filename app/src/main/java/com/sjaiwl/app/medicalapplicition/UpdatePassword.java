package com.sjaiwl.app.medicalapplicition;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.sjaiwl.app.function.Configuration;
import com.sjaiwl.app.function.UserInfo;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by sjaiwl on 15/4/3.
 */
public class UpdatePassword extends Activity {
    private TextView cancelButton;
    private EditText oldPassword;
    private EditText newPassword;
    private EditText confirmNewPassword;
    private RelativeLayout updateButton;
    private String successResponse = null;
    private final String PREFERENCE_NAME = "userInfo";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.update_password);
        initView();
        initData();
    }

    private void initView() {
        cancelButton = (TextView) findViewById(R.id.updatePassword_cancelButton);
        oldPassword = (EditText) findViewById(R.id.updatePassword_userOldPassword);
        newPassword = (EditText) findViewById(R.id.updatePassword_userNewPassword);
        confirmNewPassword = (EditText) findViewById(R.id.updatePassword_userConfirmPassword);
        updateButton = (RelativeLayout) findViewById(R.id.updatePassword_updateButton);
    }

    private void initData() {
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                update();
            }
        });
    }

    private void update() {
        if (!oldPassword.getText().toString().trim().equals(UserInfo.user.getDoctor_password())) {
            Toast.makeText(this, "密码输入有误！", Toast.LENGTH_LONG).show();
            return;
        }
        if (newPassword.getText().toString().trim().isEmpty() || confirmNewPassword.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "新密码不能为空！", Toast.LENGTH_LONG).show();
            return;
        }
        if (!(newPassword.getText().toString().trim().equals(confirmNewPassword.getText().toString().trim()))) {
            Toast.makeText(this, "新密码输入不一致！", Toast.LENGTH_LONG).show();
            return;
        }
        //加入访问服务器代码
        postData("doctor_password", newPassword.getText().toString().trim());
    }

    private void postData(final String type, final String value) {
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put(type, value);
        map.put("doctor_id", UserInfo.user.getDoctor_id());
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JSONObject jsonObject = new JSONObject(map);
        String url = Configuration.updateUserUrl;
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
                            Toast.makeText(UpdatePassword.this, "修改成功", Toast.LENGTH_SHORT).show();
                            UserInfo.user.setDoctor_password(value);
                            SharedPreferences sharedPreferences = getSharedPreferences(PREFERENCE_NAME, Activity.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("PassWord", value);
                            editor.commit();
                            finish();
                        } else {
                            Toast.makeText(UpdatePassword.this, "修改失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @SuppressLint("ShowToast")
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(UpdatePassword.this, "网络访问异常", Toast.LENGTH_SHORT).show();
                    }
                });
        requestQueue.add(jsonRequest);
    }
}
