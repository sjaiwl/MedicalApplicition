/*
 * 官网地站:http://www.mob.com
 * 技术支持QQ: 4006852216
 * 官方微信:ShareSDK   （如果发布新版本的话，我们将会第一时间通过微信将版本更新内容推送给您。如果使用过程中有任何问题，也可以通过微信与我们取得联系，我们将会在24小时内给予回复）
 *
 * Copyright (c) 2014年 mob.com. All rights reserved.
 */
package cn.smssdk.gui;

import static cn.smssdk.framework.utils.R.getLayoutRes;
import static cn.smssdk.framework.utils.R.getStyleRes;
import android.app.Dialog;
import android.content.Context;

public class CommonDialog {
	/**加载对话框*/
	public static final Dialog ProgressDialog(Context context){
		int resId = getStyleRes(context, "CommonDialog");
		if (resId > 0) {
			final Dialog dialog = new Dialog(context, resId);
			resId = getLayoutRes(context, "smssdk_progress_dialog");
			if (resId > 0) {
				dialog.setContentView(resId);
				return dialog;
			}
		}
		return null;
	}

}
