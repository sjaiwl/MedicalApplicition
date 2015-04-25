package com.sjaiwl.app.tools;

import android.R.color;
import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.sjaiwl.app.medicalapplicition.R;

public class DeletePopupWindow extends PopupWindow {

    private TextView btn_delete_record, btn_delete_file, btn_cancel;
    private View mMenuView;
    private Activity context;
    @SuppressWarnings("unused")
    private LinearLayout layout;

    public DeletePopupWindow(final Activity context,
                             OnClickListener itemsOnClick) {
        super(context);
        this.context = context;
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mMenuView = inflater.inflate(R.layout.filepage_deletewindow, null);
        layout = (LinearLayout) mMenuView.findViewById(R.id.filePage_deleteWindow);
        btn_delete_record = (TextView) mMenuView.findViewById(R.id.filePage_deleteWindow_deleteRecord);
        btn_delete_file = (TextView) mMenuView.findViewById(R.id.filePage_deleteWindow_deleteFile);
        btn_cancel = (TextView) mMenuView.findViewById(R.id.filePage_deleteWindow_deleteCancel);
        btn_cancel.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {
                WindowManager.LayoutParams lp = context.getWindow()
                        .getAttributes();
                lp.alpha = 1f;
                context.getWindow().setAttributes(lp);
                dismiss();
            }
        });
        btn_delete_record.setOnClickListener(itemsOnClick);
        btn_delete_file.setOnClickListener(itemsOnClick);
        this.setContentView(mMenuView);
        this.setWidth(LayoutParams.MATCH_PARENT);
        this.setHeight(LayoutParams.WRAP_CONTENT);
        this.setFocusable(true);
        this.setAnimationStyle(R.style.AnimBottom);
        ColorDrawable dw = new ColorDrawable(color.transparent);
        this.setBackgroundDrawable(dw);
        mMenuView.setOnTouchListener(new OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {

                int height = mMenuView.findViewById(R.id.filePage_deleteWindow)
                        .getTop();
                int y = (int) event.getY();
                if (event.getAction() == MotionEvent.ACTION_UP
                        || event.getAction() == MotionEvent.ACTION_DOWN) {
                    if (y < height) {
                        dismiss();
                    }
                }
                return true;
            }
        });


    }

    @Override
    public void dismiss() {
        WindowManager.LayoutParams lp = context.getWindow()
                .getAttributes();
        lp.alpha = 1f;
        context.getWindow().setAttributes(lp);
        super.dismiss();
    }


}
