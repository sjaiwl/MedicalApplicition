/*
 * 官网地站:http://www.mob.com
 * 技术支持QQ: 4006852216
 * 官方微信:ShareSDK   （如果发布新版本的话，我们将会第一时间通过微信将版本更新内容推送给您。如果使用过程中有任何问题，也可以通过微信与我们取得联系，我们将会在24小时内给予回复）
 *
 * Copyright (c) 2014年 mob.com. All rights reserved.
 */
package cn.smssdk.gui;

import static cn.smssdk.framework.utils.R.dipToPx;
import static cn.smssdk.framework.utils.R.getBitmapRes;
import static cn.smssdk.framework.utils.R.getColorRes;
import static cn.smssdk.framework.utils.R.getIdRes;
import static cn.smssdk.framework.utils.R.getLayoutRes;
import static cn.smssdk.framework.utils.R.getStringRes;
import static cn.smssdk.framework.utils.R.getStyleRes;

import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.IntentFilter;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import cn.smssdk.framework.FakeActivity;

/** 验证码输入页面*/
public class IdentifyNumPage extends FakeActivity implements OnClickListener,
		TextWatcher {

	private static final int RETRY_INTERVAL = 30;
	private String phone;
	private String code;
	private String formatedPhone;
	private int time = RETRY_INTERVAL;
	private EventHandler handler;
	private Dialog pd;

	private EditText etIdentifyNum;
	private TextView tvTitle;
	private TextView tvPhone;
	private TextView tvIdentifyNotify;
	private TextView tvUnreceiveIdentify;
	private ImageView ivClear;
	private Button btnSubmit;
	private Button btnSounds;
	private BroadcastReceiver smsReceiver;
	private int SHOWDIALOGTYPE = 1;

	public void setPhone(String phone, String code, String formatedPhone) {
		this.phone = phone;
		this.code = code;
		this.formatedPhone = formatedPhone;
	}

	public void onCreate() {
		int resId = getLayoutRes(activity, "smssdk_input_identify_num_page");
		if (resId > 0) {
			activity.setContentView(resId);
			resId = getIdRes(activity, "ll_back");
			activity.findViewById(resId).setOnClickListener(this);
			resId = getIdRes(activity, "btn_submit");
			btnSubmit = (Button) activity.findViewById(resId);
			btnSubmit.setOnClickListener(this);
			btnSubmit.setEnabled(false);

			resId = getIdRes(activity, "tv_title");
			tvTitle = (TextView) activity.findViewById(resId);
			resId = getStringRes(activity, "smssdk_write_identify_code");
			if (resId > 0) {
				tvTitle.setText(resId);
			}
			resId = getIdRes(activity, "et_put_identify");
			etIdentifyNum = (EditText) activity.findViewById(resId);
			etIdentifyNum.addTextChangedListener(this);
			resId = getIdRes(activity, "tv_identify_notify");
			tvIdentifyNotify = (TextView) activity.findViewById(resId);
			resId = getStringRes(activity, "smssdk_send_mobile_detail");
			if (resId > 0) {
				String text = getContext().getString(resId);
				tvIdentifyNotify.setText(Html.fromHtml(text));
			}
			resId = getIdRes(activity, "tv_phone");
			tvPhone = (TextView) activity.findViewById(resId);
			tvPhone.setText(formatedPhone);
			resId = getIdRes(activity, "tv_unreceive_identify");
			tvUnreceiveIdentify = (TextView) activity.findViewById(resId);
			resId = getStringRes(activity, "smssdk_receive_msg");
			if (resId > 0) {
				String unReceive = getContext().getString(resId, time);
				tvUnreceiveIdentify.setText(Html.fromHtml(unReceive));
			}
			tvUnreceiveIdentify.setOnClickListener(this);
			tvUnreceiveIdentify.setEnabled(false);
			resId = getIdRes(activity, "iv_clear");
			ivClear = (ImageView) activity.findViewById(resId);
			ivClear.setOnClickListener(this);
			resId = getIdRes(activity, "btn_sounds");
			btnSounds = (Button) findViewById(resId);
			btnSounds.setOnClickListener(this);

			handler = new EventHandler() {
				public void afterEvent(int event, int result, Object data) {
					if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
						/** 提交验证码 */
						afterSubmit(result, data);
					} else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
						/** 获取验证码成功后的执行动作 */
						afterGet(result, data);
					}else if (event == SMSSDK.EVENT_GET_VOICE_VERIFICATION_CODE) {
						/** 获取语音版验证码成功后的执行动作 */
						afterGetVoice(result, data);
						}
				}
			};
			SMSSDK.registerEventHandler(handler);
			countDown();
		}

		smsReceiver = new SMSReceiver(new SMSSDK.VerifyCodeReadListener() {
			@Override
			public void onReadVerifyCode(final String verifyCode) {
				runOnUIThread(new Runnable() {
					@Override
					public void run() {
						etIdentifyNum.setText(verifyCode);
					}
				});
			}
		});
		activity.registerReceiver(smsReceiver, new IntentFilter(
				"android.provider.Telephony.SMS_RECEIVED"));
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public void onPause() {
		super.onPause();
	}

	public boolean onFinish() {
		SMSSDK.unregisterEventHandler(handler);
		activity.unregisterReceiver(smsReceiver);
		return super.onFinish();
	}

	/** 倒数计时 */
	private void countDown() {
		runOnUIThread(new Runnable() {
			public void run() {
				time--;
				if (time == 0) {
					int resId = getStringRes(activity,
							"smssdk_unreceive_identify_code");
					if (resId > 0) {
						String unReceive = getContext().getString(resId, time);
						tvUnreceiveIdentify.setText(Html.fromHtml(unReceive));
					}
					tvUnreceiveIdentify.setEnabled(true);
					btnSounds.setVisibility(View.VISIBLE);
					time = RETRY_INTERVAL;
				} else {
					int resId = getStringRes(activity, "smssdk_receive_msg");
					if (resId > 0) {
						String unReceive = getContext().getString(resId, time);
						tvUnreceiveIdentify.setText(Html.fromHtml(unReceive));
					}
					tvUnreceiveIdentify.setEnabled(false);
					runOnUIThread(this, 1000);
				}
			}
		}, 1000);
	}

	public void onTextChanged(CharSequence s, int start, int before, int count) {
		// 如果输入框木有，就隐藏delbtn
		if (s.length() > 0) {
			btnSubmit.setEnabled(true);
			ivClear.setVisibility(View.VISIBLE);
			int resId = getBitmapRes(activity, "smssdk_btn_enable");
			if (resId > 0) {
				btnSubmit.setBackgroundResource(resId);
			}
		} else {
			btnSubmit.setEnabled(false);
			ivClear.setVisibility(View.GONE);
			int resId = getBitmapRes(activity, "smssdk_btn_disenable");
			if (resId > 0) {
				btnSubmit.setBackgroundResource(resId);
			}
		}
	}

	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {

	}

	public void afterTextChanged(Editable s) {
//		btnSounds.setVisibility(View.GONE);
	}

	public void onClick(View v) {
		int id = v.getId();
		int id_ll_back = getIdRes(activity, "ll_back");
		int id_btn_submit = getIdRes(activity, "btn_submit");
		int id_tv_unreceive_identify = getIdRes(activity,
				"tv_unreceive_identify");
		int id_iv_clear = getIdRes(activity, "iv_clear");
		int id_btn_sounds = getIdRes(activity, "btn_sounds");

		if (id == id_ll_back) {
			runOnUIThread(new Runnable() {
				public void run() {
					showNotifyDialog();
				}
			});
		} else if (id == id_btn_submit) {
			// 提交验证码
			String verificationCode = etIdentifyNum.getText().toString().trim();
			if (!TextUtils.isEmpty(code)) {
				if (pd != null && pd.isShowing()) {
					pd.dismiss();
				}
				pd = CommonDialog.ProgressDialog(activity);
				if (pd != null) {
					pd.show();
				}
				SMSSDK.submitVerificationCode(code, phone, verificationCode);
			} else {
				int resId = getStringRes(activity, "smssdk_write_identify_code");
				if (resId > 0) {
					Toast.makeText(getContext(), resId, Toast.LENGTH_SHORT)
							.show();
				}
			}
		} else if (id == id_tv_unreceive_identify) {
			SHOWDIALOGTYPE = 1;
			// 没有接收到短信
			showDialog(SHOWDIALOGTYPE);
		} else if (id == id_iv_clear) {
			etIdentifyNum.getText().clear();
		} else if (id == id_btn_sounds) {
			SHOWDIALOGTYPE = 2;
			// 发送语音验证码
			showDialog(SHOWDIALOGTYPE);
		}
	}

	/** 弹出重新发送短信对话框,或发送语音窗口 */
	private void showDialog(int type) {
		if (type == 1) {
			int resId = getStyleRes(activity, "CommonDialog");
			if (resId > 0) {
				final Dialog dialog = new Dialog(getContext(), resId);
				TextView tv = new TextView(getContext());
				if (type == 1) {
					resId = getStringRes(activity,
							"smssdk_resend_identify_code");
				} else {
					resId = getStringRes(activity,
							"smssdk_send_sounds_identify_code");
				}
				if (resId > 0) {
					tv.setText(resId);
				}
				tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
				resId = getColorRes(activity, "smssdk_white");
				if (resId > 0) {
					tv.setTextColor(getContext().getResources().getColor(resId));
				}
				int dp_10 = dipToPx(getContext(), 10);
				tv.setPadding(dp_10, dp_10, dp_10, dp_10);

				dialog.setContentView(tv);
				tv.setOnClickListener(new OnClickListener() {
					public void onClick(View v) {
						dialog.dismiss();
						tvUnreceiveIdentify.setEnabled(false);

						if (pd != null && pd.isShowing()) {
							pd.dismiss();
						}
						pd = CommonDialog.ProgressDialog(activity);
						if (pd != null) {
							pd.show();
						}
						// 重新获取验证码短信
						SMSSDK.getVerificationCode(code, phone.trim());
					}
				});

				dialog.setCanceledOnTouchOutside(true);
				dialog.setOnCancelListener(new OnCancelListener() {

					@Override
					public void onCancel(DialogInterface dialog) {
						tvUnreceiveIdentify.setEnabled(true);
					}
				});
				dialog.show();
			}
		} else if (type == 2) {
			int resId = getStyleRes(activity, "CommonDialog");
			if (resId > 0) {
				final Dialog dialog = new Dialog(getContext(), resId);
				resId = getLayoutRes(activity, "smssdk_send_msg_dialog");
				if (resId > 0) {
					dialog.setContentView(resId);
					resId = getIdRes(activity, "tv_dialog_title");
					TextView tv_title = (TextView) dialog.findViewById(resId);
					resId = getStringRes(activity,
							"smssdk_make_sure_send_sounds");
					if (resId > 0) {
						tv_title.setText(resId);
					}
					resId = getIdRes(activity, "tv_dialog_hint");
					TextView tv = (TextView) dialog.findViewById(resId);
					resId = getStringRes(activity,
							"smssdk_send_sounds_identify_code");
					if (resId > 0) {
						String text = getContext().getString(resId);
						tv.setText(text);
					}
					resId = getIdRes(activity, "btn_dialog_ok");
					if (resId > 0) {
						((Button) dialog.findViewById(resId))
								.setOnClickListener(new OnClickListener() {
									public void onClick(View v) {
										// TODO 发送语言
										dialog.dismiss();
										SMSSDK.getVoiceVerifyCode(phone, code);
									}
								});
					}
					resId = getIdRes(activity, "btn_dialog_cancel");
					if (resId > 0) {
						((Button) dialog.findViewById(resId))
								.setOnClickListener(new OnClickListener() {
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

	}

	/**
	 * 提交验证码成功后的执行事件
	 *
	 * @param result
	 * @param data
	 */
	private void afterSubmit(final int result, final Object data) {
		runOnUIThread(new Runnable() {
			public void run() {
				if (pd != null && pd.isShowing()) {
					pd.dismiss();
				}

				if (result == SMSSDK.RESULT_COMPLETE) {
					HashMap<String, Object> res = new HashMap<String, Object>();
					res.put("res", true);
					res.put("page", 2);
					res.put("phone", data);
					setResult(res);
					finish();
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

	/**
	 * 获取验证码成功后,的执行动作
	 *
	 * @param result
	 * @param data
	 */
	private void afterGet(final int result, final Object data) {
		runOnUIThread(new Runnable() {
			public void run() {
				if (pd != null && pd.isShowing()) {
					pd.dismiss();
				}

				if (result == SMSSDK.RESULT_COMPLETE) {
					int resId = getStringRes(activity,
							"smssdk_virificaition_code_sent");
					if (resId > 0) {
						Toast.makeText(activity, resId, Toast.LENGTH_SHORT)
								.show();
					}
					resId = getStringRes(activity, "smssdk_receive_msg");
					if (resId > 0) {
						String unReceive = getContext().getString(resId, time);
						tvUnreceiveIdentify.setText(Html.fromHtml(unReceive));
					}
					btnSounds.setVisibility(View.GONE);
					time = RETRY_INTERVAL;
					countDown();
				} else {
					((Throwable) data).printStackTrace();
					Throwable throwable = (Throwable) data;
					// 根据服务器返回的网络错误，给toast提示
					try {
						JSONObject object = new JSONObject(
								throwable.getMessage());
						String des = object.optString("detail");
						if (!TextUtils.isEmpty(des)) {
							Toast.makeText(activity, des, Toast.LENGTH_SHORT)
									.show();
							return;
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
					// / 如果木有找到资源，默认提示
					int resId = getStringRes(activity, "smssdk_network_error");
					if (resId > 0) {
						Toast.makeText(activity, resId, Toast.LENGTH_SHORT)
								.show();
					}
				}
			}
		});
	}

	/**
	  * 获取语音验证码成功后,的执行动作
      *
      * @param result
	  * @param data
	  */
	private void afterGetVoice(final int result, final Object data) {
		runOnUIThread(new Runnable() {
			public void run() {
				if (pd != null && pd.isShowing()) {
					pd.dismiss();
				}

				if(result == SMSSDK.RESULT_COMPLETE){
					int resId = getStringRes(activity, "smssdk_send_sounds_success");
					if(resId > 0){
						Toast.makeText(activity, resId, Toast.LENGTH_SHORT).show();
					}
					btnSounds.setVisibility(View.GONE);
				}else{
					((Throwable) data).printStackTrace();
					Throwable throwable = (Throwable) data;
					// 根据服务器返回的网络错误，给toast提示
					try {
						JSONObject object = new JSONObject(
								throwable.getMessage());
						String des = object.optString("detail");
						if (!TextUtils.isEmpty(des)) {
							Toast.makeText(activity, des, Toast.LENGTH_SHORT)
									.show();
							return;
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
					//  如果木有找到资源，默认提示
					int resId = getStringRes(activity, "smssdk_network_error");
					if (resId > 0) {
						Toast.makeText(activity, resId, Toast.LENGTH_SHORT)
								.show();
					}
				}

			}
		});
	}

	/** 按返回键时，弹出的提示对话框 */
	private void showNotifyDialog() {
		int resId = getStyleRes(activity, "CommonDialog");
		if (resId > 0) {
			final Dialog dialog = new Dialog(getContext(), resId);
			resId = getLayoutRes(activity, "smssdk_back_verify_dialog");
			if (resId > 0) {
				dialog.setContentView(resId);
				resId = getIdRes(activity, "tv_dialog_hint");
				TextView tv = (TextView) dialog.findViewById(resId);
				resId = getStringRes(activity,
						"smssdk_close_identify_page_dialog");
				if (resId > 0) {
					tv.setText(resId);
				}
				resId = getIdRes(activity, "btn_dialog_ok");
				Button waitBtn = (Button) dialog.findViewById(resId);
				resId = getStringRes(activity, "smssdk_wait");
				if (resId > 0) {
					waitBtn.setText(resId);
				}
				waitBtn.setOnClickListener(new OnClickListener() {
					public void onClick(View v) {
						dialog.dismiss();
					}
				});
				resId = getIdRes(activity, "btn_dialog_cancel");
				Button backBtn = (Button) dialog.findViewById(resId);
				resId = getStringRes(activity, "smssdk_back");
				if (resId > 0) {
					backBtn.setText(resId);
				}
				backBtn.setOnClickListener(new OnClickListener() {
					public void onClick(View v) {
						dialog.dismiss();
						finish();
					}
				});
				dialog.setCanceledOnTouchOutside(true);
				dialog.show();
			}
		}
	}

	@Override
	public boolean onKeyEvent(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK
				&& event.getAction() == KeyEvent.ACTION_DOWN) {
			runOnUIThread(new Runnable() {
				public void run() {
					showNotifyDialog();
				}
			});
			return true;
		} else {
			return false;
		}
	}

}
