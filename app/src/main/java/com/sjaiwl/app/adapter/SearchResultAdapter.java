package com.sjaiwl.app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.sjaiwl.app.function.PatientInfo;
import com.sjaiwl.app.medicalapplicition.R;
import com.sjaiwl.app.tools.CircularLoginImage;

import java.util.List;

/**
 * Created by sjaiwl on 15/3/19.
 */
public class SearchResultAdapter extends BaseAdapter {
    private List<PatientInfo> data;
    private LayoutInflater layoutInflater;
    private Context context;

    public SearchResultAdapter(Context context, List<PatientInfo> data) {
        this.context = context;
        this.data = data;
    }

    public static class ViewHolder {
        public CircularLoginImage image;
        public TextView name;
        public TextView details;
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
            convertView = layoutInflater.inflate(R.layout.searchresult_listitem,
                    null);
            holder.image = (CircularLoginImage) convertView
                    .findViewById(R.id.searchPage_listItem_patientImage);
            holder.name = (TextView) convertView
                    .findViewById(R.id.searchPage_listItem_patientName);
            holder.details = (TextView) convertView
                    .findViewById(R.id.searchPage_listItem_patientDetails);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        if (!data.isEmpty()) {
            holder.image.setImageUrl(data.get(position).getPatient_url(), 1);
            holder.name.setText((String) data.get(position).getPatient_name());
            holder.details.setText("("+(String) data.get(position).getPatient_situation()+")");
        }
        return convertView;
    }
}
