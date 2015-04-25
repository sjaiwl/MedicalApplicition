package com.sjaiwl.app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.sjaiwl.app.function.Configuration;
import com.sjaiwl.app.function.ResourceInfo;
import com.sjaiwl.app.interFace.FileListItemClickHelp;
import com.sjaiwl.app.medicalapplicition.R;
import com.sjaiwl.app.smart.SmartImageView;

import java.util.List;

/**
 * Created by sjaiwl on 15/3/19.
 */
public class MyExpandableListViewAdapter extends BaseExpandableListAdapter {
    private Context context;
    private LayoutInflater layoutInflater = null;
    private FileListItemClickHelp callback;
    private String[] group_list = null;
    private List<List<ResourceInfo>> mData = null;
    private static boolean isEdit;

    public MyExpandableListViewAdapter(Context context, List<List<ResourceInfo>> list, FileListItemClickHelp callback, boolean isEdit) {
        this.context = context;
        this.layoutInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.group_list = context.getResources().getStringArray(R.array.expand_groups);
        this.mData = list;
        this.callback = callback;
        this.isEdit = isEdit;

    }

    public void setData(List<List<ResourceInfo>> list) {
        mData = list;
    }

    @Override
    public int getGroupCount() {
        return mData.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return mData.get(groupPosition).size();
    }

    @Override
    public List<ResourceInfo> getGroup(int groupPosition) {
        return mData.get(groupPosition);
    }

    @Override
    public ResourceInfo getChild(int groupPosition, int childPosition) {
        return mData.get(groupPosition).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        GroupHolder groupHolder = null;
        if (convertView == null) {
            convertView = (View) (layoutInflater).inflate(
                    R.layout.filepage_expanditem, null);
            groupHolder = new GroupHolder();
            groupHolder.expandTitle = (TextView) convertView.findViewById(R.id.filePage_expandItemText);
            convertView.setTag(groupHolder);
        } else {
            groupHolder = (GroupHolder) convertView.getTag();
        }
        groupHolder.expandTitle.setText(group_list[groupPosition]);
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, final ViewGroup parent) {
        ItemHolder itemHolder = null;
        if (convertView == null) {
            convertView = (View) layoutInflater.inflate(
                    R.layout.filepage_listitem, null);
            itemHolder = new ItemHolder();
            itemHolder.listItem_image = (SmartImageView) convertView.findViewById(R.id.filePage_listItem_patientImage);
            itemHolder.listItem_type = (TextView) convertView.findViewById(R.id.filePage_listItem_type);
            itemHolder.listItem_size = (TextView) convertView.findViewById(R.id.filePage_listItem_size);
            itemHolder.listItem_deadline = (TextView) convertView.findViewById(R.id.filePage_listItem_deadline);
            itemHolder.listItem_button = (TextView) convertView.findViewById(R.id.filePage_listItem_view);
            itemHolder.listItem_delete = (TextView) convertView.findViewById(R.id.filePage_listItem_delete);
            convertView.setTag(itemHolder);
        } else {
            itemHolder = (ItemHolder) convertView.getTag();
        }
        final View v = convertView;
        final int p1 = groupPosition;
        final int p2 = childPosition;
        final int which1 = itemHolder.listItem_button.getId();
        final int which2 = itemHolder.listItem_delete.getId();
        if (!mData.get(groupPosition).get(childPosition).equals("")) {
            itemHolder.listItem_image.setImageUrl(getChild(groupPosition, childPosition).getPatient_url(),2);
            itemHolder.listItem_type.setText(getChild(groupPosition, childPosition).getPatient_name());
            itemHolder.listItem_size.setText(getChild(groupPosition, childPosition).getResource_size());
            itemHolder.listItem_deadline.setText(Configuration.getLocalTimeFromUTC(getChild(groupPosition, childPosition).getUpdated_at(),1));
        }
        if (isEdit == false) {
            itemHolder.listItem_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    callback.onClick(v, parent, p1, p2, which1, 1);
                }
            });
        } else {
            itemHolder.listItem_button.setVisibility(View.GONE);
            itemHolder.listItem_delete.setVisibility(View.VISIBLE);
            itemHolder.listItem_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    callback.onClick(v, parent, p1, p2, which2, 2);
                }
            });
        }
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }


    class GroupHolder {
        public TextView expandTitle;
    }

    class ItemHolder {
        public SmartImageView listItem_image;
        public TextView listItem_button;
        public TextView listItem_type;
        public TextView listItem_size;
        public TextView listItem_deadline;
        public TextView listItem_delete;
    }
}
