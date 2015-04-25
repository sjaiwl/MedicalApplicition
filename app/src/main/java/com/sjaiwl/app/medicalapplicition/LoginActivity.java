package com.sjaiwl.app.medicalapplicition;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
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
import com.google.gson.Gson;
import com.sjaiwl.app.function.Configuration;
import com.sjaiwl.app.function.UserInfo;
import com.sjaiwl.app.smart.WebImageCache;

import org.json.JSONObject;
import java.util.HashMap;


/**
 * Created by sjaiwl on 15/3/19.
 */
public class LoginActivity extends Activity {

    private EditText username;
    private EditText password;
    private RelativeLayout loginButton;
    private TextView registerButton;
    private TextView forgetPass;

    private final String PREFERENCE_NAME = "userInfo";
    private String userName, passWord;
    private UserInfo userInfo = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        setContentView(R.layout.login_page);
        initView();
        initData();
    }


    private void initView() {
        username = (EditText) this.findViewById(R.id.login_usr);
        password = (EditText) this.findViewById(R.id.login_pass);
        loginButton = (RelativeLayout) this.findViewById(R.id.login_button);
        registerButton = (TextView) this.findViewById(R.id.login_register);
        forgetPass = (TextView) this.findViewById(R.id.login_forget);
        loginButton.setOnClickListener(onClickListener);
        registerButton.setOnClickListener(onClickListener);
        forgetPass.setOnClickListener(onClickListener);
    }

    private void initData() {
        SharedPreferences preferences = getSharedPreferences(PREFERENCE_NAME, Activity.MODE_PRIVATE);
        username.setText(preferences.getString("UserName", null));
        password.setText(preferences.getString("PassWord", null));
    }


    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.login_button:
                    doLogin();
                    break;
                case R.id.login_forget:
                    doForget();
                    break;
                case R.id.login_register:
                    doRegister();
                    break;
                default:
                    break;

            }
        }
    };

    private void doLogin() {
        if (checkData()) {
            postData();
        }
    }

    private void doRegister() {
        Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(intent);
    }

    private void doForget() {
        ForgetPasswordActivity forgetPasswordActivity = new ForgetPasswordActivity();
        forgetPasswordActivity.show(LoginActivity.this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        initData();
    }

    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences sharedPreferences = getSharedPreferences(PREFERENCE_NAME, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        userName = username.getText().toString();
        passWord = password.getText().toString();
        editor.putString("UserName", userName);
        editor.putString("PassWord", passWord);
        editor.commit();
    }

    private void postData() {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("doctor_name", username.getText().toString().trim());
        map.put("doctor_password", password.getText().toString().trim());
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JSONObject jsonObject = new JSONObject(map);
        String url = Configuration.loginUrl;
        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject,
                new Response.Listener<JSONObject>() {
                    @SuppressLint("ShowToast")
                    @Override
                    public void onResponse(JSONObject response) {
                            Gson gson = new Gson();
                            userInfo = gson.fromJson(response.toString(), UserInfo.class);
                            if (userInfo !=null&&userInfo.getDoctor_name() !=null) {
                                UserInfo.setUserInfo(userInfo);
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                startActivity(intent);
                                WebImageCache webImageCache = new WebImageCache(getApplicationContext());
                                webImageCache.clear();
                                finish();
                            }else {
                            Toast.makeText(LoginActivity.this, "用户名或密码不正确", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @SuppressLint("ShowToast")
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(LoginActivity.this, "网络访问异常", Toast.LENGTH_SHORT).show();
                    }
                });
        requestQueue.add(jsonRequest);

    }

    private boolean checkData() {
        if (username.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "请输入用户名", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (password.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "请输入密码", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}
