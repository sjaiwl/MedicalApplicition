package com.sjaiwl.app.medicalapplicition;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sjaiwl.app.function.UsedTools;
import com.sjaiwl.app.zoom.Bimp;
import com.sjaiwl.app.zoom.PhotoView;


/**
 * Created by sjaiwl on 15/4/17.
 */
public class ViewResourceActivity extends Activity implements SurfaceHolder.Callback {
    private PhotoView imageView;
    private ImageView playButton;
    private SurfaceView surfaceView;
    private MediaPlayer mediaPlayer = null;
    private SurfaceHolder surfaceHolder = null;
    private TextView cancelButton, sendButton;
    private String path = null;
    private Integer type = null;
    private boolean isPlaying = false;
    private int degree = 0;
    private RelativeLayout topLayout;
    private LinearLayout bottomLayout;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.view_resource);
        initView();
        getData();
    }

    private void initView() {
        imageView = (PhotoView) findViewById(R.id.viewResource_gallery);
        playButton = (ImageView) findViewById(R.id.view_videoResource_playButton);
        surfaceView = (SurfaceView) findViewById(R.id.view_videoResource);
        cancelButton = (TextView) findViewById(R.id.viewResource_cancelButton);
        sendButton = (TextView) findViewById(R.id.viewResource_sendButton);
        topLayout = (RelativeLayout) findViewById(R.id.viewResource_topLayout);
        bottomLayout = (LinearLayout) findViewById(R.id.viewResource_bottomLayout);
        topLayout.bringToFront();
        bottomLayout.bringToFront();
    }

    private void getData() {
        path = getIntent().getStringExtra("uploadResourcePath");
        type = (Integer) getIntent().getSerializableExtra("uploadResourceType");
        initData();
    }

    private void initData() {
        if (type == 2) {
            imageView.setVisibility(View.VISIBLE);
            //获取旋转角度
            degree = UsedTools.getBitmapDegree(path);
            if (degree != 0) {
                Bimp.bitmap = UsedTools.rotateBitmapByDegree(Bimp.bitmap,degree);
            }
            imageView.setImageBitmap(Bimp.bitmap);

        } else {
            surfaceView.setVisibility(View.VISIBLE);
            surfaceHolder = surfaceView.getHolder();
            surfaceHolder.addCallback(this);
            surfaceHolder.setFormat(PixelFormat.RGBX_8888);
        }

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (path != null) {
                    intent = new Intent();
                    intent.putExtra("degree", degree);
                    setResult(RESULT_OK, intent);
                }
                finish();
            }
        });
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        //必须在surface创建后才能初始化MediaPlayer,否则不会显示图像
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.setDisplay(surfaceHolder);
        //设置显示视频显示在SurfaceView上
        try {
            mediaPlayer.setDataSource(path);
            mediaPlayer.prepare();
            mediaPlayer.seekTo(1000);
            playButton.setVisibility(View.VISIBLE);
            startPlay();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    private void startPlay() {

        surfaceView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPlaying) {
                    playButton.setVisibility(View.VISIBLE);
                    mediaPlayer.pause();
                    isPlaying = false;
                } else {
                    playButton.setVisibility(View.GONE);
                    mediaPlayer.start();
                    isPlaying = true;
                }
            }
        });

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                isPlaying = false;
                playButton.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}
