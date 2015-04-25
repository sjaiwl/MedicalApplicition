package com.sjaiwl.app.medicalapplicition;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.sjaiwl.app.adapter.MainIndexAdapter;
import com.sjaiwl.app.adapter.SearchResultAdapter;
import com.sjaiwl.app.function.Configuration;
import com.sjaiwl.app.function.PatientInfo;
import com.sjaiwl.app.function.UsedTools;
import com.sjaiwl.app.function.UserInfo;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sjaiwl on 15/3/26.
 */
public class SearchPage extends Activity {

    private TextView cancelButton;
    private TextView blankView;
    private ListView listView;
    private EditText editText;
    private List<PatientInfo> patientInfoList;
    private List<PatientInfo> patientResultList;
    private SearchResultAdapter adapter;
    private Intent intent = new Intent();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.search_page);
        initView();
        initData();
    }

    private void initView() {
        cancelButton = (TextView) findViewById(R.id.searchPage_cancelButton);
        blankView = (TextView) findViewById(R.id.searchPage_blankResult);
        listView = (ListView) findViewById(R.id.searchPage_resultList);
        editText = (EditText) findViewById(R.id.searchPage_searchText);
    }

    private void initData() {
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        patientInfoList = new ArrayList<PatientInfo>();
        getData();
        patientResultList = new ArrayList<PatientInfo>();
        adapter = new SearchResultAdapter(this, patientResultList);
        listView.setAdapter(adapter);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().length() == 0) {
                    blankView.setVisibility(View.GONE);
                    patientResultList.clear();
                    adapter.notifyDataSetChanged();
                } else {
                    if (!patientInfoList.isEmpty()) {
                        doSearch(s, patientInfoList);
                    } else {
                        blankView.setVisibility(View.VISIBLE);
                        patientResultList.clear();
                        adapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                intent.putExtra("patientInfo", patientResultList.get(position));
                intent.setClass(getApplicationContext(), PatientDetails.class);
                startActivity(intent);
            }
        });

    }

    private void doSearch(CharSequence info, List<PatientInfo> patientList) {
        patientResultList.clear();
        for (int i = 0; i < patientList.size(); i++) {
            if (patientList.get(i).getPatient_name().toString().contains(info)) {
                patientResultList.add(patientList.get(i));
            }
        }
        if (patientResultList.isEmpty()) {
            blankView.setVisibility(View.VISIBLE);
        } else {
            blankView.setVisibility(View.GONE);
            adapter.notifyDataSetChanged();
        }
    }

    private void getData() {
        String url = Configuration.get_searchPatientUrl + "?doctor_id=" + UserInfo.user.getDoctor_id();
        RequestQueue mRequestQueue = Volley.newRequestQueue(this);
        JsonArrayRequest jar = new JsonArrayRequest(url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        List<PatientInfo> list = JSON.parseArray(
                                response.toString(), PatientInfo.class);
                        for (int i = 0; i < list.size(); i++) {
                            patientInfoList.add(list.get(i));
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(SearchPage.this, "病人列表获取失败", Toast.LENGTH_SHORT).show();
                    }
                });
        mRequestQueue.add(jar);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        im.hideSoftInputFromWindow(editText.getApplicationWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public void finish() {
        InputMethodManager imm = (InputMethodManager) editText
                .getContext().getSystemService(
                        Context.INPUT_METHOD_SERVICE);
        if (imm.isActive()) {
            imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
        }

        super.finish();
    }
}
