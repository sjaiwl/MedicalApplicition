package com.sjaiwl.app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.sjaiwl.app.function.AppConfiguration;
import com.sjaiwl.app.function.ResourceInfo;
import com.sjaiwl.app.function.UserInfo;
import com.sjaiwl.app.interFace.IndexListItemClickHelp;
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
    private IndexListItemClickHelp callback;
    private Context context;

    public UploadListViewAdapter(Context context, List<ResourceInfo> data, IndexListItemClickHelp callback) {
        this.context = context;
        this.data = data;
        this.callback = callback;
    }

    public static class ViewHolder {
        public CircularLoginImage userImage;
        public SmartImageView recordImage;
        public ImageView videoButton;
        public TextView recordText, uploadTime;
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
            holder.videoButton = (ImageView) convertView
                    .findViewById(R.id.upload_imageView_videoButton);
            holder.recordText = (TextView) convertView
                    .findViewById(R.id.upload_textView);
            holder.uploadTime = (TextView) convertView
                    .findViewById(R.id.upload_time);
            holder.progressBar = (ProgressBar) convertView
                    .findViewById(R.id.upload_progressBar);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        if (!data.isEmpty()) {
            final int p = position;
            final int which = holder.uploadTime.getId();
            holder.userImage.setImageUrl(UserInfo.user.getDoctor_url(), 1);
            holder.uploadTime.setText(AppConfiguration.getLocalTimeFromUTC(data.get(position).getUpdated_at(), 2));
            holder.recordImage.setVisibility(View.GONE);
            holder.videoButton.setVisibility(View.GONE);
            holder.recordText.setVisibility(View.GONE);
            switch (data.get(position).getResource_type()) {
                case 1:
                    holder.recordText.setVisibility(View.VISIBLE);
                    holder.recordText.setText(data.get(position).getResource_description());
                    holder.recordText.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            callback.onClick(v, parent, p, which);
                        }
                    });
                    break;
                case 2:
                    holder.recordImage.setVisibility(View.VISIBLE);
                    holder.recordImage.setImageUrl(data.get(position).getResource_thumbnailUrl(), 1);
                    holder.recordImage.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            callback.onClick(v, parent, p, which);
                        }
                    });
                    break;
                case 3:
                    holder.recordImage.setVisibility(View.VISIBLE);
                    holder.videoButton.setVisibility(View.VISIBLE);
                    holder.recordImage.setImageUrl(data.get(position).getResource_thumbnailUrl(), 1);
                    holder.recordImage.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            callback.onClick(v, parent, p, which);
                        }
                    });
                    break;
                case 4:
                    holder.recordImage.setVisibility(View.VISIBLE);
                    holder.recordImage.setImageResource(R.mipmap.show_audio_button);
                    holder.recordImage.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            callback.onClick(v, parent, p, which);
                        }
                    });
                    break;
            }
        }
        return convertView;
    }
}
