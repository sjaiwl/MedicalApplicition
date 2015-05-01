package com.sjaiwl.app.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.sjaiwl.app.adapter.MainIndexAdapter;
import com.sjaiwl.app.function.Configuration;
import com.sjaiwl.app.function.PatientInfo;
import com.sjaiwl.app.function.RecordTypeSelect;
import com.sjaiwl.app.function.UsedTools;
import com.sjaiwl.app.function.UserInfo;
import com.sjaiwl.app.interFace.IndexListItemClickHelp;
import com.sjaiwl.app.medicalapplicition.PatientDetails;
import com.sjaiwl.app.medicalapplicition.R;
import com.sjaiwl.app.medicalapplicition.SearchPage;
import com.sjaiwl.app.xlistview.XListView;

import org.json.JSONArray;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by sjaiwl on 15/3/19.
 */
public class FragmentMain extends Fragment implements XListView.IXListViewListener,
        IndexListItemClickHelp {

    private XListView mylistview;
    private RelativeLayout search_box;
    private MainIndexAdapter mainIndexAdapter;
    private List<PatientInfo> patientInfoList;
    private RecordTypeSelect recordTypeSelect;
    private PatientInfo patientOnClick;
    private Intent intent;
    private int index = 0; // 请求列表页 ，0为第一条，lastActivityId为上一条
    private int lastActivityId = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.main_page, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
        initData();
    }

    private void initView() {
        mylistview = (XListView) getActivity().findViewById(R.id.mainPage_listView);
        search_box = (RelativeLayout) getActivity().findViewById(R.id.mainPage_search_box);
    }

    private void initData() {
        intent = new Intent();
        patientInfoList = new ArrayList<PatientInfo>();
        mylistview.setPullRefreshEnable(true);
        mylistview.setPullLoadEnable(true);
        mainIndexAdapter = new MainIndexAdapter(getActivity(), patientInfoList, this);
        mylistview.setAdapter(mainIndexAdapter);
        mylistview.setXListViewListener(this);
        mylistview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapter, View view,
                                    int position, long id) {
                intent.putExtra("patientInfo", patientInfoList.get(position - 1));
                intent.setClass(getActivity(), PatientDetails.class);
                startActivity(intent);
            }
        });
        search_box.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent();
                intent1.setClass(getActivity(), SearchPage.class);
                startActivity(intent1);
            }
        });
        getData(1);
    }

    @Override
    public void onClick(View item, View widget, int position, int which) {
        patientOnClick = patientInfoList.get(position);
        recordTypeSelect = new RecordTypeSelect(getActivity(), patientOnClick);
        recordTypeSelect.setCanceledOnTouchOutside(true);
        recordTypeSelect.show();
        WindowManager windowManager = getActivity().getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        WindowManager.LayoutParams lp = recordTypeSelect.getWindow().getAttributes();
        lp.width = (int) (display.getWidth()); // 设置宽度
        lp.gravity = Gravity.CENTER;
        recordTypeSelect.getWindow().setAttributes(lp);
    }


    private void stopMoreLoad() {
        mylistview.stopRefresh();
        mylistview.stopLoadMore();
    }

    private void stopRefreshLoad() {
        mylistview.stopRefresh();
        mylistview.stopLoadMore();
        mylistview.setRefreshTime(UsedTools.RefreshTime());
    }

    @Override
    public void onRefresh() {
        index = 0;
        lastActivityId = 0;
        getData(1);
    }

    @Override
    public void onLoadMore() {
        index = lastActivityId;
        getData(2);
    }

    private void getData(final int method) { // method=1 重新生成list method=2 增长list
        String url = Configuration.get_allPatientUrl + "?index="
                + index + "&&doctor_id=" + UserInfo.user.getDoctor_id();
        RequestQueue mRequestQueue = Volley.newRequestQueue(getActivity());
        JsonArrayRequest jar = new JsonArrayRequest(url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        List<PatientInfo> list = JSON.parseArray(
                                response.toString(), PatientInfo.class);
                        if (method == 1) {
                            patientInfoList.clear();
                        }
                        for (int i = 0; i < list.size(); i++) {
                            patientInfoList.add(list.get(i));
                        }
                        if (!patientInfoList.isEmpty()) {
                            lastActivityId = patientInfoList.get(patientInfoList.size() - 1).getId();
                        }
                        if (method == 1) {
                            stopRefreshLoad();
                        } else {
                            stopMoreLoad();
                        }
                        mainIndexAdapter.notifyDataSetChanged();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getActivity(), "病人列表获取失败", Toast.LENGTH_SHORT).show();
                        stopMoreLoad();
                    }
                });
        mRequestQueue.add(jar);
    }
}
