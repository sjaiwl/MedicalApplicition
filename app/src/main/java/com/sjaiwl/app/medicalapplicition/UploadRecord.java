package com.sjaiwl.app.medicalapplicition;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.InputType;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.sjaiwl.app.adapter.UploadListViewAdapter;
import com.sjaiwl.app.function.Configuration;
import com.sjaiwl.app.function.PatientInfo;
import com.sjaiwl.app.function.ResourceInfo;
import com.sjaiwl.app.function.UsedTools;
import com.sjaiwl.app.function.UserInfo;
import com.sjaiwl.app.interFace.AddNewAudioUploadHelp;
import com.sjaiwl.app.interFace.IndexListItemClickHelp;
import com.sjaiwl.app.tools.CircularLoginImage;
import com.sjaiwl.app.tools.GetImagePath;
import com.sjaiwl.app.tools.PullToLoadMoreListView;
import com.sjaiwl.app.zoom.Bimp;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import loopj.android.http.AsyncHttpClient;
import loopj.android.http.JsonHttpResponseHandler;
import loopj.android.http.RequestParams;


/**
 * Created by sjaiwl on 15/3/25.
 */
public class UploadRecord extends Activity implements View.OnClickListener, PullToLoadMoreListView.IXListViewListener
,IndexListItemClickHelp{

    private TextView goBack;
    private TextView patientName;
    private CircularLoginImage patientImage;
    private PullToLoadMoreListView listView;
    private EditText inputBox;
    private TextView addButton;
    private LinearLayout sortBar;
    private LinearLayout typeImage;
    private LinearLayout typePhoto;
    private LinearLayout typeVideo;
    private LinearLayout typeAudio;
    private LinearLayout uploadRecord_selectSort;
    private PatientInfo patientInfo;
    private String category;
    private static Integer type;
    private static String successResponse = null;
    private Intent intent;
    private static final int REQUEST_CODE_TAKE_GALLERY = 1;//图片
    private static final int REQUEST_CODE_TAKE_CAMERA = 2;//拍照
    private static final int REQUEST_CODE_TAKE_VIDEO = 3;//录视频
    private static final int REQUEST_CODE_UPLOAD_GALLERY = 4;//上传图片
    private static final int REQUEST_CODE_UPLOAD_CAMERA = 5;//上传图片
    private static final int REQUEST_CODE_UPLOAD_VIDEO = 6;//上传视频
    private static String dir = Environment.getExternalStorageDirectory()
            .getAbsolutePath() + "/MedicalApplication/Camera/patientResource/";
    private File file;
    private String imagePrefix = "ImageFile"; //要保存的图片文件的前缀
    private String videoPrefix = "VideoFile"; //要保存的视频文件的前缀
    private Intent tempIntent;
    private static File uploadGalleryFile, uploadVideoFile, uploadAudioFile;
    private Bitmap bitmap = null;
    private String filePath = null;
    private AddNewAudio addNewAudio;
    private SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
    private static List<ResourceInfo> recordList = new ArrayList<ResourceInfo>();
    private UploadListViewAdapter uploadListViewAdapter;
    private int index = 0; // 请求列表页 ，0为第一条，lastActivityId为上一条
    private int lastActivityId = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.upload_record);
        initView();
        initData();
    }

    private void initView() {
        goBack = (TextView) findViewById(R.id.uploadRecord_backButton);
        patientName = (TextView) findViewById(R.id.uploadRecord_patientName);
        patientImage = (CircularLoginImage) findViewById(R.id.uploadRecord_patientImage);
        listView = (PullToLoadMoreListView) findViewById(R.id.uploadRecord_listView);
        inputBox = (EditText) findViewById(R.id.uploadRecord_bottomEditBox);
        addButton = (TextView) findViewById(R.id.uploadRecord_bottomAdd);
        sortBar = (LinearLayout) findViewById(R.id.uploadRecord_selectSort);
        typeImage = (LinearLayout) findViewById(R.id.uploadRecord_typeImage);
        typePhoto = (LinearLayout) findViewById(R.id.uploadRecord_typePhoto);
        typeVideo = (LinearLayout) findViewById(R.id.uploadRecord_typeVideo);
        typeAudio = (LinearLayout) findViewById(R.id.uploadRecord_typeAudio);
        uploadRecord_selectSort = (LinearLayout) findViewById(R.id.uploadRecord_selectSort);
    }

    private void initData() {
        intent = new Intent();
        category = getIntent().getStringExtra("RecordSort");
        patientInfo = (PatientInfo) getIntent().getSerializableExtra("PatientOnClick");
        patientName.setText(patientInfo.getPatient_name());
        patientImage.setImageUrl(patientInfo.getPatient_url(), 1);
        uploadListViewAdapter = new UploadListViewAdapter(this, recordList,this);
        listView.setAdapter(uploadListViewAdapter);
        getData();

        uploadRecord_selectSort.setOnClickListener(this);
        goBack.setOnClickListener(this);
        patientImage.setOnClickListener(this);
        addButton.setOnClickListener(this);
        typeImage.setOnClickListener(this);
        typePhoto.setOnClickListener(this);
        typeVideo.setOnClickListener(this);
        typeAudio.setOnClickListener(this);

        listView.setPullRefreshEnable(true);
        listView.setXListViewListener(this);
        listView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (sortBar.getVisibility() == View.VISIBLE) {
                    hideFaceLayout();
                }
                InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                im.hideSoftInputFromWindow(getCurrentFocus().getApplicationWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                return false;
            }
        });

        inputBox.setOnClickListener(this);
        inputBox.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        //文本显示的位置在EditText的最上方
        inputBox.setGravity(Gravity.TOP);
        inputBox.setSingleLine(false);
        //水平滚动设置为False
        inputBox.setHorizontallyScrolling(false);
        inputBox.setMaxLines(3);
        inputBox.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    if (!v.getText().toString().trim().isEmpty()) {
                        //上传文字
                        uploadText(v.getText().toString().trim(), 1);
                        inputBox.setText("");
                    }
                }
                return false;
            }
        });
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.uploadRecord_selectSort:
                break;
            case R.id.uploadRecord_backButton:
                finish();
                break;
            case R.id.uploadRecord_patientImage:
                //点击病人头像事件
                intent.putExtra("patientInfo", patientInfo);
                intent.setClass(this, PatientDetails.class);
                startActivity(intent);
                break;
            case R.id.uploadRecord_bottomEditBox:
                hideFaceLayout();
                break;
            case R.id.uploadRecord_bottomAdd:
                if (sortBar.getVisibility() == View.VISIBLE) {
                    hideFaceLayout();
                    UsedTools.showKeyboard(this);
                } else {
                    showFaceLayout();
                }
                break;
            case R.id.uploadRecord_typeImage:
                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT); //"android.intent.action.GET_CONTENT"
                galleryIntent.setType("image/*"); //查看类型 String IMAGE_UNSPECIFIED = "image/*" ;
                Intent wrapperGalleryIntent = Intent.createChooser(galleryIntent, null);
                startActivityForResult(wrapperGalleryIntent, REQUEST_CODE_TAKE_GALLERY);
                break;
            case R.id.uploadRecord_typePhoto:
                if (isHasSdcard()) {
                    uploadGalleryFile = new File(file, imagePrefix + format.format(new Date()) + ".jpg");
                }
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE); //"android.media.action.IMAGE_CAPTURE";
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(uploadGalleryFile));
                startActivityForResult(cameraIntent, REQUEST_CODE_TAKE_CAMERA);
                break;
            case R.id.uploadRecord_typeVideo:
                if (isHasSdcard()) {
                    uploadVideoFile = new File(file, videoPrefix + format.format(new Date()) + ".3gp");
                }
                Intent videoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                videoIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(uploadVideoFile));
                startActivityForResult(videoIntent, REQUEST_CODE_TAKE_VIDEO);
                break;
            case R.id.uploadRecord_typeAudio:
                addNewAudio = new AddNewAudio(this, myListener);
                addNewAudio.show();
                WindowManager windowManager = this.getWindowManager();
                Display display = windowManager.getDefaultDisplay();
                WindowManager.LayoutParams lp = addNewAudio.getWindow().getAttributes();
                lp.width = (int) (display.getWidth()); // 设置宽度
                lp.gravity = Gravity.CENTER;
                addNewAudio.getWindow().setAttributes(lp);
                break;
            default:
                break;
        }
    }

    //判断是否有可以存储
    private boolean hasSdcard() {
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            return true;
        } else {
            return false;
        }
    }

    //上传音频
    private AddNewAudioUploadHelp myListener = new AddNewAudioUploadHelp() {
        @Override

        public void refreshActivity(String text) {
            type = 4;
            //获取文件路径
            uploadAudioFile = new File(text);
            //上传
            upload(uploadAudioFile, type);
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        ContentResolver resolver = getContentResolver();
        if (requestCode == REQUEST_CODE_TAKE_GALLERY && resultCode == RESULT_OK) {
            Uri originalUri = data.getData();
            type = 2;
            //获取文件的路径
            filePath = GetImagePath.getPath(this, originalUri);
            //将文件转化为bitmap
            clearBitmap();
            bitmap = BitmapFactory.decodeFile(filePath);
            //添加到list中
            Bimp.bitmap = bitmap;
            //预览
            tempIntent = new Intent(UploadRecord.this, ViewResourceActivity.class);
            tempIntent.putExtra("uploadResourcePath", filePath);
            tempIntent.putExtra("uploadResourceType", type);
            startActivityForResult(tempIntent, REQUEST_CODE_UPLOAD_GALLERY);
        }
        if (requestCode == REQUEST_CODE_UPLOAD_GALLERY && resultCode == RESULT_OK) {
            //上传
            int degree = data.getIntExtra("degree", 0);
            if (degree != 0) {
                uploadGalleryFile = generateFile(Bimp.bitmap);
            } else {
                uploadGalleryFile = new File(filePath);
            }
            Bitmap bitmap = UsedTools.getImageThumbnail(uploadGalleryFile.getPath());
            postData(uploadGalleryFile, generateFile(bitmap), type);
        }
        if (requestCode == REQUEST_CODE_TAKE_CAMERA && resultCode == RESULT_OK) {
            type = 2;
            //获取文件的路径
            filePath = uploadGalleryFile.getPath();
            //将文件转化为bitmap
            clearBitmap();
            bitmap = BitmapFactory.decodeFile(filePath);
            //添加到list中
            Bimp.bitmap = bitmap;
            //预览
            tempIntent = new Intent(UploadRecord.this, ViewResourceActivity.class);
            tempIntent.putExtra("uploadResourcePath", filePath);
            tempIntent.putExtra("uploadResourceType", type);
            startActivityForResult(tempIntent, REQUEST_CODE_UPLOAD_CAMERA);
        }
        if (requestCode == REQUEST_CODE_UPLOAD_CAMERA && resultCode == RESULT_OK) {
            //上传
            int degree = data.getIntExtra("degree", 0);
            if (degree != 0) {
                uploadGalleryFile = generateFile(Bimp.bitmap);
            }
            Bitmap bitmap = UsedTools.getImageThumbnail(uploadGalleryFile.getPath());
            postData(uploadGalleryFile, generateFile(bitmap), type);
        }
        if (requestCode == REQUEST_CODE_TAKE_VIDEO && resultCode == RESULT_OK) {
            type = 3;
            filePath = uploadVideoFile.getPath();
            //预览
            tempIntent = new Intent(UploadRecord.this, ViewResourceActivity.class);
            tempIntent.putExtra("uploadResourcePath", filePath);
            tempIntent.putExtra("uploadResourceType", type);
            startActivityForResult(tempIntent, REQUEST_CODE_UPLOAD_VIDEO);
        }
        if (requestCode == REQUEST_CODE_UPLOAD_VIDEO && resultCode == RESULT_OK) {
            //上传
            Bitmap bitmap = UsedTools.getVideoThumbnail(this, resolver, uploadVideoFile.getPath());
            postData(uploadVideoFile, generateFile(bitmap), type);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    //    /*
    //    * 上传文件
    //	  */
    @SuppressLint("ShowToast")
    public void upload(File file, Integer type) {
        RequestParams params = new RequestParams();
        try {
            params.put("doctor_id", UserInfo.user.getDoctor_id().toString());
            params.put("suffer_id", patientInfo.getId().toString());
            params.put("resource_type", type.toString());
            params.put("resource_size", UsedTools.generateFileSize(file));
            params.put("resource_category", category);
            params.put("resource_url", file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String url = Configuration.newResourceUrl;
        AsyncHttpClient client = new AsyncHttpClient();
        client.post(url, params, new JsonHttpResponseHandler() {
            @SuppressLint("ShowToast")
            @Override
            public void onSuccess(JSONObject response) {
                try {
                    successResponse = response.get("success").toString();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (successResponse.equals("1")) {
                    Toast.makeText(UploadRecord.this, "上传成功", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(UploadRecord.this, "上传失败", Toast.LENGTH_LONG).show();
                }
            }

            @SuppressLint("ShowToast")
            @Override
            public void onFailure(int statusCode, Header[] headers,
                                  byte[] responseBody, Throwable error) {
                Toast.makeText(UploadRecord.this, "网络访问异常,请重试", Toast.LENGTH_LONG).show();

            }
        });
    }

    //    /*
    //    * 上传文字
    //	  */
    @SuppressLint("ShowToast")
    public void uploadText(String text, Integer type) {
        RequestParams params = new RequestParams();
        params.put("doctor_id", UserInfo.user.getDoctor_id().toString());
        params.put("suffer_id", patientInfo.getId().toString());
        params.put("resource_type", type.toString());
        params.put("resource_size", "0.01Kb");
        params.put("resource_category", category);
        params.put("resource_description", text);
        String url = Configuration.newResourceUrl;
        AsyncHttpClient client = new AsyncHttpClient();
        client.post(url, params, new JsonHttpResponseHandler() {
            @SuppressLint("ShowToast")
            @Override
            public void onSuccess(JSONObject response) {
                try {
                    successResponse = response.get("success").toString();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (successResponse.equals("1")) {
                    Toast.makeText(UploadRecord.this, "上传成功", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(UploadRecord.this, "上传失败", Toast.LENGTH_LONG).show();
                }
            }

            @SuppressLint("ShowToast")
            @Override
            public void onFailure(int statusCode, Header[] headers,
                                  byte[] responseBody, Throwable error) {
                Toast.makeText(UploadRecord.this, "网络访问异常,请重试", Toast.LENGTH_LONG).show();

            }
        });
    }

    //上传多个文件
    private void postData(File file, File thumbnailFile, Integer type) {
        com.lidroid.xutils.http.RequestParams params = new com.lidroid.xutils.http.RequestParams();
        // 加入文件参数后默认使用MultipartEntity（"multipart/form-data"），
        // 如需"multipart/related"，xUtils中提供的MultipartEntity支持设置subType为"related"。
        // 使用params.setBodyEntity(httpEntity)可设置更多类型的HttpEntity（如：
        // MultipartEntity,BodyParamsEntity,FileUploadEntity,InputStreamUploadEntity,StringEntity）。
        // 例如发送json参数：params.setBodyEntity(new StringEntity(jsonStr,charset));
        params.addBodyParameter("doctor_id", UserInfo.user.getDoctor_id().toString());
        params.addBodyParameter("suffer_id", patientInfo.getId().toString());
        params.addBodyParameter("resource_type", type.toString());
        params.addBodyParameter("resource_size", UsedTools.generateFileSize(file));
        params.addBodyParameter("resource_category", category);
        params.addBodyParameter("resource_url", file);
        params.addBodyParameter("resource_thumbnailUrl", thumbnailFile);

        HttpUtils http = new HttpUtils();
        http.send(HttpRequest.HttpMethod.POST, Configuration.newResourceUrl, params,
                new RequestCallBack<String>() {
                    @Override
                    public void onStart() {
                        Log.i("tag", "conn...");
                    }

                    @Override
                    public void onLoading(long total, long current, boolean isUploading) {
                        if (isUploading) {
                            Log.i("tag", "upload: " + current + "/" + total);
                        } else {
                            Log.i("tag", "reply: " + current + "/" + total);
                        }
                    }

                    @Override
                    public void onSuccess(ResponseInfo<String> responseInfo) {
                        Log.i("tag", "reply: " + responseInfo.result);
                        Toast.makeText(UploadRecord.this, "上传成功", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onFailure(HttpException error, String msg) {
                        Log.i("tag", error.getExceptionCode() + ":" + msg);
                        Toast.makeText(UploadRecord.this, "网络访问异常,请重试", Toast.LENGTH_LONG).show();
                    }
                });
    }

    private File generateFile(Bitmap bitmap) {
        File picture = null;
        if (isHasSdcard()) {
            picture = new File(file, imagePrefix + format.format(new Date()) + ".jpg");
            try {
                BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(picture));
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
                bos.flush();
                bos.close();
                return picture;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return picture;
    }

    public void showFaceLayout() {
        UsedTools.hideKeyboard(this);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                sortBar.setVisibility(View.VISIBLE);
                addButton.setBackground(getResources().getDrawable(R.mipmap.chat_bottom_keyboard));
            }
        }, 50);
    }

    public void hideFaceLayout() {
        sortBar.setVisibility(View.GONE);
        addButton.setBackground(getResources().getDrawable(R.mipmap.chat_bottom_add));
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            if (sortBar.getVisibility() == View.VISIBLE) {
                hideFaceLayout();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);

    }

    @Override
    public void finish() {
        recordList.clear();
        index = 0;
        lastActivityId = 0;
        InputMethodManager imm = (InputMethodManager) inputBox
                .getContext().getSystemService(
                        Context.INPUT_METHOD_SERVICE);
        if (imm.isActive()) {
            imm.hideSoftInputFromWindow(inputBox.getWindowToken(), 0);
        }
        super.finish();
    }

    //创建缓存文件夹
    private boolean isHasSdcard() {
        if (hasSdcard()) {
            file = new File(dir);
            if (!file.exists()) {
                // file不存在
                file.mkdirs();
            }
            return true;
        } else {
            Toast.makeText(UploadRecord.this, "未找到存储卡，无法存储", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    //下拉加载更多
    @Override
    public void onRefresh() {
        index = lastActivityId;
        getData();
    }

    private void getData() {
        String url = Configuration.get_patientResourceUrl + "?index="
                + index + "&&doctor_id=" + UserInfo.user.getDoctor_id() + "&&suffer_id=" + patientInfo.getId();
        RequestQueue mRequestQueue = Volley.newRequestQueue(this);
        JsonArrayRequest jar = new JsonArrayRequest(url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        List<ResourceInfo> list = JSON.parseArray(
                                response.toString(), ResourceInfo.class);
                        for (int i = 0; i < list.size(); i++) {
                            recordList.add(0, list.get(i));
                        }
                        if (!recordList.isEmpty()) {
                            lastActivityId = recordList.get(0).getId();
                        }
                        uploadListViewAdapter.notifyDataSetChanged();
                        listView.stopRefresh();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(UploadRecord.this, "上传记录获取失败", Toast.LENGTH_SHORT).show();
                        listView.stopRefresh();
                    }
                });
        mRequestQueue.add(jar);
    }

    @Override
    public void onClick(View item, View widget, int position, int which) {
        ResourceInfo resourceInfo = recordList.get(position);
        intent.putExtra("resource", resourceInfo);
        intent.setClass(UploadRecord.this,ShowResourceActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        clearBitmap();
        super.onDestroy();
    }

    //清空bitmap
    private void clearBitmap() {
        if (bitmap != null && !bitmap.isRecycled()) {
            bitmap.isRecycled();
            bitmap = null;
        }
    }
}
