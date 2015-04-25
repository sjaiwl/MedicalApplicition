package com.sjaiwl.app.medicalapplicition;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sjaiwl.app.function.PatientInfo;
import com.sjaiwl.app.interFace.AddNewAudioUploadHelp;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;

/**
 * Created by sjaiwl on 15/4/16.
 */
public class AddNewAudio extends Dialog implements View.OnClickListener {

    private MediaRecorder recorder = null;
    private File sdcardPath = null;
    private File recordPath = null;
    private String prefix = "VoiceFile"; //要保存的录音文件的前缀
    private static String dir = Environment.getExternalStorageDirectory()
            .getAbsolutePath() + "/MedicalApplication/Camera/patientResource/";
    private ImageView btnStartRecord, btnStopRecord, btnPlayRecord;
    private LinearLayout bottomLayout;
    private TextView uploadButton, cancelButton;
    private TextView stateView;
    private Activity activity;
    private AddNewAudioUploadHelp addNewAudioUploadHelp;
    private static boolean isPlaying = false;
    private MediaPlayer mediaPlayer = null;

    public AddNewAudio(Activity activity, AddNewAudioUploadHelp addNewAudioUploadHelp) {
        super(activity);
        setCanceledOnTouchOutside(true);
        this.activity = activity;
        this.addNewAudioUploadHelp = addNewAudioUploadHelp;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.add_new_audio);
        initView();
        initData();
    }

    private void initView() {
        stateView = (TextView) findViewById(R.id.newAudio_state_text);
        bottomLayout = (LinearLayout) findViewById(R.id.newAudio_bottomGroup);
        btnStartRecord = (ImageView) findViewById(R.id.newAudio_btnStart);
        btnStopRecord = (ImageView) findViewById(R.id.newAudio_btnStop);
        btnPlayRecord = (ImageView) findViewById(R.id.newAudio_btnPlay);
        uploadButton = (TextView) findViewById(R.id.newAudio_bottomGroup_upload);
        cancelButton = (TextView) findViewById(R.id.newAudio_bottomGroup_cancel);
    }

    private void initData() {
        btnStartRecord.setOnClickListener(this);
        btnStopRecord.setOnClickListener(this);
        btnPlayRecord.setOnClickListener(this);
        cancelButton.setOnClickListener(this);
        uploadButton.setOnClickListener(this);
    }

    public void initRecorder() {
        recorder = new MediaRecorder();
        recorder.reset();
        recorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);//设置音频来源，
        recorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);// 设置输出格式
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT); //编码格式采用默认
        recorder.setOutputFile(recordPath.getAbsolutePath()); //设置输出路径
    }

    @Override
    public void onClick(View v) {
        setCanceledOnTouchOutside(false);
        switch (v.getId()) {
            //开始录音
            case R.id.newAudio_btnStart:
                startRecord();
                break;
            //结束录音
            case R.id.newAudio_btnStop:
                stopRecord();
                break;
            //播放录音
            case R.id.newAudio_btnPlay:
                playRecord();
                break;
            //取消
            case R.id.newAudio_bottomGroup_cancel:
                cancelRecord();
                break;
            //发送
            case R.id.newAudio_bottomGroup_upload:
                uploadRecord();
                break;
            default:
                break;
        }
    }

    private boolean hasSdcard() {
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            return true;
        } else {
            return false;
        }
    }

    private void startRecord() {
        if (hasSdcard()) {
            sdcardPath = new File(dir);
            try {
                recordPath = File.createTempFile(prefix, ".amr", sdcardPath); //三个参数分别为前缀、后缀、目录
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(activity, "未找到存储卡，无法存储", Toast.LENGTH_SHORT).show();
            return;
        }
        initRecorder();
        try {
            recorder.prepare();
            recorder.start();
            stateView.setText("正在录音...");
            btnStartRecord.setVisibility(View.GONE);
            btnStopRecord.setVisibility(View.VISIBLE);
            btnPlayRecord.setVisibility(View.GONE);
        } catch (Exception e) {
            e.printStackTrace();
        }
        btnStartRecord.setClickable(false);
        btnPlayRecord.setClickable(false);
        btnStopRecord.setClickable(true);
    }

    private void stopRecord() {
        try {
            recorder.stop();
            recorder.release();
            recorder = null;
            stateView.setText("点击播放");
            bottomLayout.setVisibility(View.VISIBLE);
            btnPlayRecord.setVisibility(View.VISIBLE);
            btnStartRecord.setVisibility(View.GONE);
            btnStopRecord.setVisibility(View.GONE);
            btnPlayRecord.setVisibility(View.VISIBLE);
        } catch (Exception e) {
            Toast.makeText(activity, "数据保存出错,请重新录制", Toast.LENGTH_SHORT).show();
            dismiss();
        }
        btnStartRecord.setClickable(false);
        btnStopRecord.setClickable(false);
        btnPlayRecord.setClickable(true);
    }

    private void playRecord() {
        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(recordPath.getPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (isPlaying) {
            mediaPlayer.stop();
            mediaPlayer.release();
            isPlaying = false;
            stateView.setText("点击播放");
        } else {
            try {
                mediaPlayer.prepare();
                mediaPlayer.start();
                isPlaying = true;
                stateView.setText("正在播放...");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mediaPlayer.release();
                isPlaying = false;
                stateView.setText("点击播放");
            }
        });
    }

    private void cancelRecord() {
        dismiss();
    }

    private void uploadRecord() {
        addNewAudioUploadHelp.refreshActivity(recordPath.getPath());
        dismiss();
    }

    @Override
    public void dismiss() {
        super.dismiss();
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
        if (recorder != null) {
            recorder.stop();
            recorder.release();
            recorder = null;
        }
    }
}
