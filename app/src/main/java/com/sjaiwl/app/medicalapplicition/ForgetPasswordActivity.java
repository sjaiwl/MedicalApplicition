package com.sjaiwl.app.medicalapplicition;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import cn.smssdk.framework.FakeActivity;
import cn.smssdk.gui.CommonDialog;
import cn.smssdk.gui.SMSReceiver;

import static cn.smssdk.framework.utils.R.getBitmapRes;
import static cn.smssdk.framework.utils.R.getIdRes;
import static cn.smssdk.framework.utils.R.getLayoutRes;
import static cn.smssdk.framework.utils.R.getStringRes;
import static cn.smssdk.framework.utils.R.getStyleRes;

/**
 * Created by sjaiwl on 15/3/28.
 */
public class ForgetPasswordActivity extends FakeActivity implements TextWatcher {

    private TextView cancelButton;
    private RelativeLayout sendNumberButton;
    private TextView sendNumberButtonText;
    private EditText phone;
    private EditText number;
    private RelativeLayout postButton;

    private Dialog pd;
    private EventHandler handler;
    private BroadcastReceiver smsReceiver;
    private static final int RETRY_INTERVAL = 60;
    private int time = RETRY_INTERVAL;
    private static String phoneNumber;
    private static Boolean smsReceiverState = false;
    private String successResponse = null;

    public void show(Context context) {
        super.show(context, null);
    }


    public void onCreate() {
        activity.setContentView(R.layout.forgetpassword_page);
        initView();
        initData();
    }

    private void initView() {
        cancelButton = (TextView) findViewById(R.id.forgetPassword_cancelButton);
        sendNumberButton = (RelativeLayout) findViewById(R.id.forgetPassword_sendButton);
        sendNumberButtonText = (TextView) findViewById(R.id.forgetPassword_sendButtonText);
        phone = (EditText) findViewById(R.id.forgetPassword_phoneNumber);
        number = (EditText) findViewById(R.id.forgetPassword_codeNumber);
        postButton = (RelativeLayout) findViewById(R.id.forgetPassword_postButton);
    }

    private void initData() {
        sendNumberButtonText.setAlpha(1f);
        sendNumberButtonText.setText("发送短信验证码");
        sendNumberButton.setEnabled(true);

        // TODO Auto-generated method stub
        SMSSDK.initSDK(activity, Configuration.appKey,
                Configuration.appSecret);
        smsReceiverState = false;
        handler = new EventHandler() {
            public void afterEvent(final int event, final int result,
                                   final Object data) {
                runOnUIThread(new Runnable() {
                    public void run() {
                        if (pd != null && pd.isShowing()) {
                            pd.dismiss();
                        }
                        if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
                            // 提交验证码
                            afterSubmit(result, data);
                        }
                        if (result == SMSSDK.RESULT_COMPLETE) {
                            if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
                                // 请求验证码后，跳转到验证码填写页面
                                number.requestFocus();
                                smsReceiver = new SMSReceiver(
                                        new SMSSDK.VerifyCodeReadListener() {
                                            @Override
                                            public void onReadVerifyCode(
                                                    final String verifyCode) {
                                                runOnUIThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        number.setText(verifyCode);
                                                        number.setSelection(verifyCode
                                                                .length());
                                                    }
                                                });
                                            }
                                        });
                                activity.registerReceiver(
                                        smsReceiver,
                                        new IntentFilter(
                                                "android.provider.Telephony.SMS_RECEIVED"));
                                smsReceiverState = true;
                            }
                        } else {
                            // 根据服务器返回的网络错误，给toast提示
                            try {
                                ((Throwable) data).printStackTrace();
                                Throwable throwable = (Throwable) data;

                                JSONObject object = new JSONObject(
                                        throwable.getMessage());
                                String des = object.optString("detail");
                                if (!TextUtils.isEmpty(des)) {
                                    Toast.makeText(activity, des,
                                            Toast.LENGTH_SHORT).show();
                                    return;
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            // 如果木有找到资源，默认提示
                            int resId = getStringRes(activity,
                                    "smssdk_network_error");
                            if (resId > 0) {
                                Toast.makeText(activity, resId,
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
            }
        };
        SMSSDK.registerEventHandler(handler);

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        sendNumberButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phoneNum = phone.getText().toString().trim()
                        .replaceAll("\\s*", "");
                String countryCode = "+86";
                checkPhoneNum(phoneNum, countryCode);
            }
        });

        // 提交验证码
        postButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                SMSSDK.initSDK(activity, Configuration.appKey,
                        Configuration.appSecret);
                String verificationCode = number.getText().toString().trim();
                String phoneNum = phone.getText().toString().trim()
                        .replaceAll("\\s*", "");
                String countryCode = "+86";
                if (countryCode.startsWith("+")) {
                    countryCode = countryCode.substring(1);
                }
                if (!TextUtils.isEmpty(verificationCode)) {
                    if (pd != null && pd.isShowing()) {
                        pd.dismiss();
                    }
                    pd = CommonDialog.ProgressDialog(activity);
                    if (pd != null) {
                        pd.show();
                    }
                    SMSSDK.submitVerificationCode(countryCode, phoneNum,
                            verificationCode);
                } else {
                    int resId = getStringRes(activity,
                            "smssdk_write_identify_code");
                    if (resId > 0) {
                        Toast.makeText(getContext(), resId, Toast.LENGTH_SHORT)
                                .show();
                    }
                }

            }
        });

    }

    // 提交验证码成功后的执行事件
    private void afterSubmit(final int result, final Object data) {
        runOnUIThread(new Runnable() {
            public void run() {
                if (pd != null && pd.isShowing()) {
                    pd.dismiss();
                }
                if (result == SMSSDK.RESULT_COMPLETE) {
                    // 验证成功,跳转到修改密码界面
                    post_phoneNumber(phoneNumber);
                } else {
                    ((Throwable) data).printStackTrace();
                    // 验证码不正确
                    int resId = getStringRes(activity,
                            "smssdk_virificaition_code_wrong");
                    if (resId > 0) {
                        Toast.makeText(activity, resId, Toast.LENGTH_SHORT)
                                .show();
                    }
                }
            }
        });
    }

    // 检查电话号码
    private void checkPhoneNum(String phone, String code) {
        if (code.startsWith("+")) {
            code = code.substring(1);
        }

        if (TextUtils.isEmpty(phone)) {
            int resId = getStringRes(activity, "smssdk_write_mobile_phone");
            if (resId > 0) {
                Toast.makeText(getContext(), resId, Toast.LENGTH_SHORT).show();
            }
            return;
        }
        int resId = 0;
        if (phone.length() != 11) {
            resId = getStringRes(activity, "smssdk_write_right_mobile_phone");
            if (resId > 0) {
                Toast.makeText(getContext(), resId, Toast.LENGTH_SHORT).show();
            }
            return;
        }
        // 弹出对话框，发送验证码
        phoneNumber = phone;
        postData(phone, code);
    }

    // 是否请求发送验证码，对话框
    public void showDialog(final String phone, final String code) {
        int resId = getStyleRes(activity, "CommonDialog");
        if (resId > 0) {
            final String phoneNum = "+" + code + " " + splitPhoneNum(phone);
            final Dialog dialog = new Dialog(getContext(), resId);
            resId = getLayoutRes(activity, "smssdk_send_msg_dialog");
            if (resId > 0) {
                dialog.setContentView(resId);
                resId = getIdRes(activity, "tv_phone");
                ((TextView) dialog.findViewById(resId)).setText(phoneNum);
                resId = getIdRes(activity, "tv_dialog_hint");
                TextView tv = (TextView) dialog.findViewById(resId);
                resId = getStringRes(activity, "smssdk_make_sure_mobile_detail");
                if (resId > 0) {
                    String text = getContext().getString(resId);
                    tv.setText(Html.fromHtml(text));
                }
                resId = getIdRes(activity, "btn_dialog_ok");
                if (resId > 0) {
                    ((Button) dialog.findViewById(resId))
                            .setOnClickListener(new View.OnClickListener() {
                                public void onClick(View v) {
                                    // 跳转到验证码页面
                                    dialog.dismiss();
                                    if (pd != null && pd.isShowing()) {
                                        pd.dismiss();
                                    }
                                    pd = CommonDialog.ProgressDialog(activity);
                                    if (pd != null) {
                                        pd.show();
                                    }
                                    Log.e("verification phone ==>>", phone);
                                    SMSSDK.getVerificationCode(code,
                                            phone.trim());
                                    countDown();
                                }
                            });
                }
                resId = getIdRes(activity, "btn_dialog_cancel");
                if (resId > 0) {
                    ((Button) dialog.findViewById(resId))
                            .setOnClickListener(new View.OnClickListener() {
                                public void onClick(View v) {
                                    dialog.dismiss();
                                }
                            });
                }
                dialog.setCanceledOnTouchOutside(true);
                dialog.show();

            }
        }
    }

    // 倒数计时
    @SuppressLint("ResourceAsColor")
    private void countDown() {
        if (pd != null && pd.isShowing()) {
            pd.dismiss();
        }
        runOnUIThread(new Runnable() {
            public void run() {
                time--;
                if (time == 0) {
                    sendNumberButtonText.setAlpha(1f);
                    sendNumberButtonText.setText("发送短信验证码");
                    sendNumberButton.setEnabled(true);
                    time = RETRY_INTERVAL;
                } else {
                    int resId = getStringRes(activity, "smssdk_receive_msg");
                    if (resId > 0) {
                        sendNumberButtonText.setAlpha(0.5f);
                        String unReceive = getContext().getString(resId, time);
                        sendNumberButtonText.setText(Html.fromHtml(unReceive) + "后重新发送");
                    }
                    sendNumberButton.setEnabled(false);
                    runOnUIThread(this, 1000);
                }
            }
        }, 1000);
    }

    // 分割电话号码
    private String splitPhoneNum(String phone) {
        StringBuilder builder = new StringBuilder(phone);
        builder.reverse();
        for (int i = 4, len = builder.length(); i < len; i += 5) {
            builder.insert(i, ' ');
        }
        builder.reverse();
        return builder.toString();
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count,
                                  int after) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        // TODO Auto-generated method stub
        if (s.length() > 0) {
            postButton.setEnabled(true);
            int resId = getBitmapRes(activity, "smssdk_btn_enable");
            if (resId > 0) {
                postButton.setBackgroundResource(resId);
            }
        } else {
            postButton.setEnabled(false);
            int resId = getBitmapRes(activity, "smssdk_btn_disenable");
            if (resId > 0) {
                postButton.setBackgroundResource(resId);
            }
        }
    }

    @Override
    public void afterTextChanged(Editable s) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onResume() {
        SMSSDK.registerEventHandler(handler);
    }

    @Override
    public void onPause() {
        SMSSDK.unregisterEventHandler(handler);
    }

    @Override
    public boolean onFinish() {
        SMSSDK.unregisterEventHandler(handler);
        if (smsReceiverState) {
            activity.unregisterReceiver(smsReceiver);
            smsReceiverState = false;
        }
        return super.onFinish();
    }

    public void post_phoneNumber(String phone) {
        if (pd != null && pd.isShowing()) {
            pd.dismiss();
        }
        Intent intent = new Intent();
        intent.putExtra("doctor_telephone", phone);
        intent.setClass(activity, SettingPasswordActivity.class);
        startActivityForResult(intent, 0);
    }

    @Override
    public void onActivityResult(int i, int i2, Intent intent) {
        if (i2 == 1) {
            finish();
        }
        super.onActivityResult(i, i2, intent);

    }

    private void postData(final String phone, final String code) {
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("doctor_telephone", phone);
        RequestQueue requestQueue = Volley.newRequestQueue(activity);
        JSONObject jsonObject = new JSONObject(map);
        String url = Configuration.queryUserUrl;
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
                            showDialog(phone, code);
                        }
                        if (successResponse.equals("0")) {
                            Toast.makeText(activity, "未找到相关用户", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @SuppressLint("ShowToast")
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(activity, "网络访问异常", Toast.LENGTH_SHORT).show();
                    }
                });
        requestQueue.add(jsonRequest);
    }
}
