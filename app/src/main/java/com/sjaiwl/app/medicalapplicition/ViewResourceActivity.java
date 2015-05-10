package com.sjaiwl.app.medicalapplicition;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
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
    /**
     * 记录当前播放的位置
     */
    private static int playPosition = -1;

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
            //获取旋转角度
            degree = UsedTools.getBitmapDegree(path);
            //加载图片
            ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this).build();
            ImageLoader imageLoader = ImageLoader.getInstance();
            imageLoader.init(config);
            imageLoader.displayImage("file:///" + path, imageView, new ImageLoadingListener() {
                @Override
                public void onLoadingStarted(String imageUri, View view) {

                }

                @Override
                public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

                }

                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    if (degree != 0) {
                        loadedImage = UsedTools.rotateBitmapByDegree(loadedImage, degree);
                        imageView.setImageBitmap(loadedImage);
                    }
                }

                @Override
                public void onLoadingCancelled(String imageUri, View view) {

                }
            });
            imageView.setVisibility(View.VISIBLE);
        } else {
            surfaceView.setVisibility(View.VISIBLE);
            surfaceHolder = surfaceView.getHolder();
            surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
            surfaceHolder.addCallback(this);
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
                intent = new Intent();
                intent.putExtra("degree", degree);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        playVideo();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        // surfaceView销毁,同时销毁mediaPlayer
        if (null != mediaPlayer) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    private void playVideo() {
        //必须在surface创建后才能初始化MediaPlayer,否则不会显示图像
        mediaPlayer = new MediaPlayer();
        mediaPlayer.reset();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.setDisplay(surfaceHolder);
        //设置显示视频显示在SurfaceView上
        try {
            mediaPlayer.setDataSource(path);
            mediaPlayer.prepare();
            mediaPlayer.seekTo(1);
            playButton.setVisibility(View.VISIBLE);
            startPlay();
        } catch (Exception e) {
            e.printStackTrace();
        }

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                isPlaying = false;
                playButton.setVisibility(View.VISIBLE);
            }
        });
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
