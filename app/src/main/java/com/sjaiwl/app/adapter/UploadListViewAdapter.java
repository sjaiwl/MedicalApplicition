package com.sjaiwl.app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.sjaiwl.app.function.Configuration;
import com.sjaiwl.app.function.PatientInfo;
import com.sjaiwl.app.function.ResourceInfo;
import com.sjaiwl.app.function.UserInfo;
import com.sjaiwl.app.medicalapplicition.R;
import com.sjaiwl.app.smart.SmartImageView;
import com.sjaiwl.app.tools.CircularLoginImage;

import java.util.List;

/**
 * Created by sjaiwl on 15/4/22.
 */
public class UploadListViewAdapter extends BaseAdapter {
    private List<ResourceInfo> data;
    private LayoutInflater layoutInflater;
    private Context context;

    public UploadListViewAdapter(Context context, List<ResourceInfo> data) {
        this.context = context;
        this.data = data;
    }

    public static class ViewHolder {
        public CircularLoginImage userImage;
        public SmartImageView recordImage;
        public TextView recordText,uploadTime;
        public ProgressBar progressBar;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, final ViewGroup parent) {
        // 获得组件，实例化组件
        layoutInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = layoutInflater.inflate(R.layout.upload_listitem,
                    null);
            holder.userImage = (CircularLoginImage) convertView
                    .findViewById(R.id.upload_userImage);
            holder.recordImage = (SmartImageView) convertView
                    .findViewById(R.id.upload_imageView);
            holder.recordText = (TextView) convertView
                    .findViewById(R.id.upload_textView);
            holder.uploadTime = (TextView)convertView
                    .findViewById(R.id.upload_time);
            holder.progressBar = (ProgressBar) convertView
                    .findViewById(R.id.upload_progressBar);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        if (!data.isEmpty()) {
//            if(data.get(position).getResource_type() == 1){
//                holder.recordImage.setVisibility(View.GONE);
//                holder.recordText.setText(data.get(position).getResource_description());
//            }else{
//                holder.recordText.setVisibility(View.GONE);
//                holder.recordImage.setImageUrl(data.get(position).getResource_url(),1);
//            }
            holder.recordText.setText(data.get(position).getResource_description());
            holder.uploadTime.setText(Configuration.getLocalTimeFromUTC(data.get(position).getUpdated_at(),2));
            holder.userImage.setImageUrl(UserInfo.user.getDoctor_url(),1);
        }
        return convertView;
    }
}
