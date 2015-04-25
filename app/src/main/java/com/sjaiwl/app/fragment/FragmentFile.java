package com.sjaiwl.app.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.sjaiwl.app.adapter.MyExpandableListViewAdapter;
import com.sjaiwl.app.function.Configuration;
import com.sjaiwl.app.function.PatientInfo;
import com.sjaiwl.app.function.ResourceInfo;
import com.sjaiwl.app.function.UsedTools;
import com.sjaiwl.app.function.UserInfo;
import com.sjaiwl.app.interFace.FileListItemClickHelp;
import com.sjaiwl.app.medicalapplicition.R;
import com.sjaiwl.app.medicalapplicition.ShowResourceActivity;
import com.sjaiwl.app.tools.DeletePopupWindow;
import com.sjaiwl.app.tools.PullToRefreshExpandableListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

/**
 * Created by sjaiwl on 15/3/19.
 */
public class FragmentFile extends Fragment implements FileListItemClickHelp, PullToRefreshExpandableListView.IXListViewListener {
    private LinearLayout tabHost_all;
    private LinearLayout tabHost_word;
    private LinearLayout tabHost_image;
    private LinearLayout tabHost_video;
    private LinearLayout tabHost_audio;

    private TextView tabHost_all_text;
    private TextView tabHost_word_text;
    private TextView tabHost_image_text;
    private TextView tabHost_video_text;
    private TextView tabHost_audio_text;
    private TextView filePage_editButton;

    private View tabHost_all_gap;
    private View tabHost_word_gap;
    private View tabHost_image_gap;
    private View tabHost_video_gap;
    private View tabHost_audio_gap;

    private DeletePopupWindow menuWindow;
    private static boolean isEdit = false;
    private static int selectType = 0;
    private PullToRefreshExpandableListView expandableListView;
    private MyExpandableListViewAdapter myExpandableListViewAdapter;
    private static List<List<ResourceInfo>> dataList = new ArrayList<List<ResourceInfo>>();
    private static List<ResourceInfo> resourceInfoList = new ArrayList<ResourceInfo>();
    private static ArrayList[] arrayLists = new ArrayList[3];
    private static int GroupPosition;
    private static int ChildPosition;
    private String successResponse = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.file_page, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setFragmentFileIndicator();
    }

    private void setFragmentFileIndicator() {
        expandableListView = (PullToRefreshExpandableListView) getActivity().findViewById(R.id.filePage_ExpandableListView);
        tabHost_all = (LinearLayout) getActivity().findViewById(R.id.filePage_tabHost_all);
        tabHost_word = (LinearLayout) getActivity().findViewById(R.id.filePage_tabHost_word);
        tabHost_image = (LinearLayout) getActivity().findViewById(R.id.filePage_tabHost_image);
        tabHost_video = (LinearLayout) getActivity().findViewById(R.id.filePage_tabHost_video);
        tabHost_audio = (LinearLayout) getActivity().findViewById(R.id.filePage_tabHost_audio);

        tabHost_all_text = (TextView) getActivity().findViewById(R.id.filePage_tabHost_all_text);
        tabHost_word_text = (TextView) getActivity().findViewById(R.id.filePage_tabHost_word_text);
        tabHost_image_text = (TextView) getActivity().findViewById(R.id.filePage_tabHost_image_text);
        tabHost_video_text = (TextView) getActivity().findViewById(R.id.filePage_tabHost_video_text);
        tabHost_audio_text = (TextView) getActivity().findViewById(R.id.filePage_tabHost_audio_text);
        filePage_editButton = (TextView) getActivity().findViewById(R.id.filePage_editButton);

        tabHost_all_gap = (View) getActivity().findViewById(R.id.filePage_tabHost_all_gap);
        tabHost_word_gap = (View) getActivity().findViewById(R.id.filePage_tabHost_word_gap);
        tabHost_image_gap = (View) getActivity().findViewById(R.id.filePage_tabHost_image_gap);
        tabHost_video_gap = (View) getActivity().findViewById(R.id.filePage_tabHost_video_gap);
        tabHost_audio_gap = (View) getActivity().findViewById(R.id.filePage_tabHost_audio_gap);

        expandableListView.setPullRefreshEnable(true);
        expandableListView.setXListViewListener(this);
        expandableListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                if (parent.isGroupExpanded(groupPosition)) {
                    parent.collapseGroup(groupPosition);
                } else {
                    //第二个参数false表示展开时是否触发默认滚动动画
                    parent.expandGroup(groupPosition, false);
                }
                //telling the listView we have handled the group click, and don't want the default actions.
                return true;

            }
        });
        tabHost_all.setOnClickListener(mOnClickListener);
        tabHost_word.setOnClickListener(mOnClickListener);
        tabHost_image.setOnClickListener(mOnClickListener);
        tabHost_video.setOnClickListener(mOnClickListener);
        tabHost_audio.setOnClickListener(mOnClickListener);
        filePage_editButton.setOnClickListener(mOnClickListener);
        tabHost_all_text.setTextColor(getResources().getColor(R.color.filePage_tabHost_textColor_pressed));
        tabHost_all_gap.setBackgroundResource(R.color.filePage_tabHost_textColor_pressed);
        initData();
    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.filePage_tabHost_all:
                    initTabHost();
                    tabHost_all_text.setTextColor(getResources().getColor(R.color.filePage_tabHost_textColor_pressed));
                    tabHost_all_gap.setBackgroundResource(R.color.filePage_tabHost_textColor_pressed);
                    SelectData(0);
                    break;
                case R.id.filePage_tabHost_word:
                    initTabHost();
                    tabHost_word_text.setTextColor(getResources().getColor(R.color.filePage_tabHost_textColor_pressed));
                    tabHost_word_gap.setBackgroundResource(R.color.filePage_tabHost_textColor_pressed);
                    SelectData(1);
                    break;
                case R.id.filePage_tabHost_image:
                    initTabHost();
                    tabHost_image_text.setTextColor(getResources().getColor(R.color.filePage_tabHost_textColor_pressed));
                    tabHost_image_gap.setBackgroundResource(R.color.filePage_tabHost_textColor_pressed);
                    SelectData(2);
                    break;
                case R.id.filePage_tabHost_video:
                    initTabHost();
                    tabHost_video_text.setTextColor(getResources().getColor(R.color.filePage_tabHost_textColor_pressed));
                    tabHost_video_gap.setBackgroundResource(R.color.filePage_tabHost_textColor_pressed);
                    SelectData(3);
                    break;
                case R.id.filePage_tabHost_audio:
                    initTabHost();
                    tabHost_audio_text.setTextColor(getResources().getColor(R.color.filePage_tabHost_textColor_pressed));
                    tabHost_audio_gap.setBackgroundResource(R.color.filePage_tabHost_textColor_pressed);
                    SelectData(4);
                    break;
                case R.id.filePage_editButton:
                    editListView();
                    break;
                default:
                    break;
            }
        }
    };

    private void initTabHost() {
        tabHost_all_text.setTextColor(getResources().getColor(R.color.filePage_tabHost_textColor_normal));
        tabHost_all_gap.setBackgroundResource(R.color.filePage_tabHost_color);
        tabHost_word_text.setTextColor(getResources().getColor(R.color.filePage_tabHost_textColor_normal));
        tabHost_word_gap.setBackgroundResource(R.color.filePage_tabHost_color);
        tabHost_image_text.setTextColor(getResources().getColor(R.color.filePage_tabHost_textColor_normal));
        tabHost_image_gap.setBackgroundResource(R.color.filePage_tabHost_color);
        tabHost_video_text.setTextColor(getResources().getColor(R.color.filePage_tabHost_textColor_normal));
        tabHost_video_gap.setBackgroundResource(R.color.filePage_tabHost_color);
        tabHost_audio_text.setTextColor(getResources().getColor(R.color.filePage_tabHost_textColor_normal));
        tabHost_audio_gap.setBackgroundResource(R.color.filePage_tabHost_color);
    }

    private void initData() {
        for (int i = 0; i < arrayLists.length; i++) {
            arrayLists[i] = new ArrayList<ResourceInfo>();
        }
        myExpandableListViewAdapter = new MyExpandableListViewAdapter(getActivity(), dataList, this, false);
        expandableListView.setAdapter(myExpandableListViewAdapter);
        filePage_editButton.setText("编辑");
    }

    @Override
    public void onResume() {
        super.onResume();
        getData();
    }

    private void SelectData(int type) {
        if (type == 0) {
            dataList.clear();
            for (int i = 0; i < arrayLists.length; i++) {
                dataList.add(arrayLists[i]);
            }
        } else {
            ResourceInfo resourceInfo;
            ArrayList[] tempLists = new ArrayList[3];
            for (int i = 0; i < tempLists.length; i++) {
                tempLists[i] = new ArrayList<ResourceInfo>();
                tempLists[i].clear();
            }
            for (int i = 0; i < arrayLists.length; i++) {
                if (!arrayLists[i].isEmpty()) {
                    for (int j = 0; j < arrayLists[i].size(); j++) {
                        resourceInfo = (ResourceInfo) arrayLists[i].get(j);
                        if (resourceInfo.getResource_type() == type) {
                            tempLists[i].add(resourceInfo);
                        }
                    }
                }
            }
            dataList.clear();
            for (int i = 0; i < tempLists.length; i++) {
                dataList.add(tempLists[i]);
            }

        }
        selectType = type;
        myExpandableListViewAdapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View item, View widget, int groupPosition, int childPosition, int which, int type) {
        GroupPosition = groupPosition;
        ChildPosition = childPosition;
        ResourceInfo resourceInfo = dataList.get(groupPosition).get(childPosition);
        if (type == 1) {
            //查看按钮点击
            Intent intent = new Intent();
            intent.putExtra("resource",resourceInfo);
            intent.setClass(getActivity(), ShowResourceActivity.class);
            startActivity(intent);

        } else {
            menuWindow = new DeletePopupWindow(getActivity(),
                    itemsOnClick);
            // 显示窗口
            menuWindow.showAtLocation(
                    getActivity().findViewById(R.id.filePage_topName),
                    Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
            // 设置layout在PopupWindow中显示的位置
            menuWindow.update();
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    WindowManager.LayoutParams params = getActivity().getWindow().getAttributes();
                    params.alpha = 0.5f;
                    getActivity().getWindow().setAttributes(params);
                }
            }, 50);
        }
    }

    private View.OnClickListener itemsOnClick = new View.OnClickListener() {
        public void onClick(View v) {
            menuWindow.dismiss();
            switch (v.getId()) {
                case R.id.filePage_deleteWindow_deleteRecord:
                    deleteFromList(1);
                    break;
                case R.id.filePage_deleteWindow_deleteFile:
                    deleteFromList(2);
                    break;
                default:
                    break;
            }
        }

    };

    private void editListView() {
        if (isEdit) {
            isEdit = false;
            myExpandableListViewAdapter = new MyExpandableListViewAdapter(getActivity(), dataList, this, false);
            filePage_editButton.setText("编辑");
        } else {
            isEdit = true;
            myExpandableListViewAdapter = new MyExpandableListViewAdapter(getActivity(), dataList, this, true);
            filePage_editButton.setText("取消");
        }
        List<Integer> expandList = groupExpandedPosition(expandableListView, myExpandableListViewAdapter.getGroupCount());
        expandableListView.setAdapter(myExpandableListViewAdapter);
        myExpandableListViewAdapter.notifyDataSetChanged();
        for (int i = 0; i < expandList.size(); i++) {
            if (expandList.get(i) > -1) {
                expandableListView.expandGroup(expandList.get(i));
            }
        }
    }

    private List groupExpandedPosition(ExpandableListView expandableListView, int groupCount) {
        List<Integer> expandList = new ArrayList<Integer>();
        expandList.clear();
        for (int i = 0; i < groupCount; i++) {
            if (expandableListView.isGroupExpanded(i)) {
                expandList.add(i);
            } else {
                expandList.add(-1);
            }
        }
        return expandList;
    }

    private void deleteFromList(int type) {
        final ResourceInfo resourceInfo = dataList.get(GroupPosition).get(ChildPosition);
        Log.i("tag", resourceInfo.toString());
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("resource_id", resourceInfo.getId().toString());
        map.put("type", String.valueOf(type));
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        JSONObject jsonObject = new JSONObject(map);
        String url = Configuration.deleteResourceUrl;
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
                            dataList.get(GroupPosition).remove(ChildPosition);
                            myExpandableListViewAdapter.notifyDataSetChanged();
                            for (int i = 0; i < arrayLists[GroupPosition].size(); i++) {
                                if (arrayLists[GroupPosition].get(i).equals(resourceInfo)) {
                                    arrayLists[GroupPosition].remove(i);
                                }
                            }
                            Toast.makeText(getActivity(), "删除成功", Toast.LENGTH_SHORT).show();
                        }
                        if (successResponse.equals("0")) {
                            Toast.makeText(getActivity(), "删除失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @SuppressLint("ShowToast")
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getActivity(), "网络访问异常", Toast.LENGTH_SHORT).show();
                    }
                });
        requestQueue.add(jsonRequest);
    }

    private void onLoad() {
        expandableListView.stopRefresh();
        expandableListView.setRefreshTime(UsedTools.RefreshTime());
    }

    @Override
    public void onRefresh() {
        getData();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        dataList.clear();
        resourceInfoList.clear();
        for (int i = 0; i < arrayLists.length; i++) {
            arrayLists[i].clear();
        }
        isEdit = false;
        selectType = 0;
    }

    private void getData() {
        String url = Configuration.get_allResourceUrl + "?doctor_id=" + UserInfo.user.getDoctor_id();
        RequestQueue mRequestQueue = Volley.newRequestQueue(getActivity());
        JsonArrayRequest jar = new JsonArrayRequest(url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        resourceInfoList.clear();
                        List<ResourceInfo> list = JSON.parseArray(response.toString(), ResourceInfo.class);
                        for (int i = 0; i < list.size(); i++) {
                            resourceInfoList.add(list.get(i));
                        }
                        sortDataList(resourceInfoList);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getActivity(), "文件获取失败", Toast.LENGTH_SHORT).show();
                        onLoad();
                    }
                });
        mRequestQueue.add(jar);
    }

    private void sortDataList(List<ResourceInfo> resourceInfoList) {
        for (int i = 0; i < arrayLists.length; i++) {
            arrayLists[i].clear();
        }
        for (int i = 0; i < resourceInfoList.size(); i++) {
            if (Configuration.classifyFromUTC(resourceInfoList.get(i).getUpdated_at()).equals("今天")) {
                arrayLists[0].add(resourceInfoList.get(i));
            }
            if (Configuration.classifyFromUTC(resourceInfoList.get(i).getUpdated_at()).equals("一周内")) {
                arrayLists[1].add(resourceInfoList.get(i));
            }
            if (Configuration.classifyFromUTC(resourceInfoList.get(i).getUpdated_at()).equals("一月内")) {
                arrayLists[2].add(resourceInfoList.get(i));
            }
        }
        SelectData(selectType);
        expandableListView.expandGroup(0);
        onLoad();
    }
}
