package com.sjaiwl.app.medicalapplicition;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.sjaiwl.app.function.ResourceInfo;
import com.sjaiwl.app.smart.SmartImageTask;
import com.sjaiwl.app.smart.SmartImageView;
import com.sjaiwl.app.smart.WebImage;
import com.sjaiwl.app.zoom.PhotoView;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by sjaiwl on 15/4/18.
 */
public class ShowResourceActivity extends Activity implements MediaPlayer.OnCompletionListener,
        MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener, MediaPlayer.OnBufferingUpdateListener,
        View.OnClickListener {
    private ResourceInfo resourceInfo;
    private Integer resourceType;
    private RelativeLayout topLayout;
    private RelativeLayout bottomLayout;
    private RelativeLayout resourceView;
    private TextView cancelButton, topText, allTime;
    private ImageView playButton;
    private ProgressBar progressBar;
    private SeekBar seekBar;
    private PhotoView imageView;
    private TextView textView;
    private SurfaceView surfaceView;
    private SurfaceHolder surfaceHolder;
    private MediaPlayer mediaPlayer;
    private AnimationDrawable draw;
    /**
     * 播放总时间
     */
    private String videoTimeString;

    private long videoTimeLong;
    /**
     * 记录当前播放的位置
     */
    private static int playPosition = -1;

    /**
     * seekBar是否自动拖动
     */
    private boolean seekBarAutoFlag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.show_resource);
        initView();
        initData();
    }

    private void initView() {
        topLayout = (RelativeLayout) findViewById(R.id.showResource_topLayout);
        bottomLayout = (RelativeLayout) findViewById(R.id.showResource_bottomLayout);
        resourceView = (RelativeLayout) findViewById(R.id.showResource_view);
        cancelButton = (TextView) findViewById(R.id.showResource_cancelButton);
        topText = (TextView) findViewById(R.id.showResource_topText);
        allTime = (TextView) findViewById(R.id.showResource_allTime);
        playButton = (ImageView) findViewById(R.id.showResource_playButton);
        seekBar = (SeekBar) findViewById(R.id.showResource_seekBar);
        imageView = (PhotoView) findViewById(R.id.showResource_imageView);
        textView = (TextView) findViewById(R.id.showResource_textView);
        surfaceView = (SurfaceView) findViewById(R.id.showResource_videoView);
        progressBar = (ProgressBar) findViewById(R.id.showResource_progressBar);
        topLayout.bringToFront();
        bottomLayout.bringToFront();
    }

    private void initData() {
        resourceInfo = (ResourceInfo) getIntent().getSerializableExtra("resource");
        resourceType = resourceInfo.getResource_type();
        selectResourceType();
        doClick();
    }

    private void selectResourceType() {
        switch (resourceType) {
            case 1:
                showText();
                break;
            case 2:
                showImage();
                break;
            case 3:
                showVideo();
                break;
            case 4:
                showAudio();
                break;
            default:
                break;
        }
    }

    private void showText() {
        topText.setText("文本");
        bottomLayout.setVisibility(View.INVISIBLE);
        textView.setVisibility(View.VISIBLE);
        textView.setText(resourceInfo.getResource_description());
        if (!TextUtils.isEmpty(textView.getText().toString().trim())) {
            progressBar.setVisibility(View.GONE);
        }
    }

    private void showImage() {
        topText.setText("图像");
        bottomLayout.setVisibility(View.INVISIBLE);
        imageView.setVisibility(View.VISIBLE);
        imageView.setImage(new WebImage(resourceInfo.getResource_url()), null, null, new SmartImageTask.OnCompleteListener() {
            @Override
            public void onComplete() {
                progressBar.setVisibility(View.GONE);
                Log.i("tag","11");
            }
        }, 1);
    }

    private void showVideo() {
        topText.setText("视频");
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(new SurfaceCallback());
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    private void showAudio() {
        topText.setText("音频");
        surfaceView.setVisibility(View.VISIBLE);
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(new SurfaceCallback());
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    private void doClick() {
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private class SurfaceCallback implements SurfaceHolder.Callback {
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

        }

        public void surfaceCreated(SurfaceHolder holder) {
            // surfaceView被创建
            // 设置播放资源
            playVideo();
        }

        public void surfaceDestroyed(SurfaceHolder holder) {
            // surfaceView销毁,同时销毁mediaPlayer
            if (null != mediaPlayer) {
                mediaPlayer.release();
                mediaPlayer = null;
            }

        }
    }

    /**
     * 播放视频
     */
    public void playVideo() {
        // 初始化MediaPlayer
        mediaPlayer = new MediaPlayer();
        // 重置mediaPaly,建议在初始滑mediaplay立即调用。
        mediaPlayer.reset();
        // 设置声音效果
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        // 设置媒体加载完成以后回调函数。
        mediaPlayer.setOnPreparedListener(this);
        // 设置播放完成监听
        mediaPlayer.setOnCompletionListener(this);
        // 错误监听回调函数
        mediaPlayer.setOnErrorListener(this);
        // 设置缓存变化监听
        mediaPlayer.setOnBufferingUpdateListener(this);
        mediaPlayer.setScreenOnWhilePlaying(true);
        try {
            mediaPlayer.setDataSource(resourceInfo.getResource_url());
            // 设置异步加载视频，包括两种方式 prepare()同步，prepareAsync()异步
            mediaPlayer.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(ShowResourceActivity.this, "加载视频错误！", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        Log.i("tag", "onPrepared");
        if(resourceType == 4){
            imageView.setVisibility(View.VISIBLE);
            imageView.bringToFront();
            imageView.setBackgroundResource(R.drawable.animation);
            draw = (AnimationDrawable)imageView.getBackground();
            draw.start();
        }
        progressBar.setVisibility(View.GONE);
        // 判断是否有保存的播放位置,防止屏幕旋转时，界面被重新构建，播放位置丢失。
        if (playPosition >= 0) {
            mediaPlayer.seekTo(playPosition);
            playPosition = -1;
        }
        seekBarAutoFlag = true;
        // 设置控制条,放在加载完成以后设置，防止获取getDuration()错误
        seekBar.setMax(mediaPlayer.getDuration());
        // 设置播放时间
        videoTimeLong = mediaPlayer.getDuration();
        videoTimeString = getShowTime(videoTimeLong);
        allTime.setText("00:00:00/" + videoTimeString);
        // 设置拖动监听事件
        seekBar.setOnSeekBarChangeListener(new SeekBarChangeListener());
        // 设置按钮监听事件
        // 暂停和播放
        playButton.setOnClickListener(this);
        playButton.setBackground(getResources().getDrawable(R.drawable.drawable_expand_close));
        // 设置显示到屏幕
        mediaPlayer.setDisplay(surfaceHolder);
        // 播放视频
        mediaPlayer.start();
        // 开启线程 刷新进度条
        new Thread(runnable).start();
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        seekBar.setProgress(Integer.parseInt(String.valueOf(videoTimeLong)));
        // 设置重播
        if (seekBarAutoFlag) {
            if(resourceType == 4){
                draw.stop();
            }
            playButton.setBackground(getResources().getDrawable(R.drawable.drawable_expand_open));
            playPosition = 0;
        }
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
        Log.i("tag", "onBufferingUpdate-->" + percent);
    }

    @Override
    public void onClick(View v) {
        if (null != mediaPlayer) {
            if (mediaPlayer.isPlaying()) {
                if(resourceType == 4){
                    draw.stop();
                }
                playButton.setBackground(getResources().getDrawable(R.drawable.drawable_expand_open));
                mediaPlayer.pause();
                playPosition = mediaPlayer.getCurrentPosition();
            } else if (playPosition >= 0) {
                if(resourceType == 4){
                    draw.start();
                }
                playButton.setBackground(getResources().getDrawable(R.drawable.drawable_expand_close));
                mediaPlayer.seekTo(playPosition);
                mediaPlayer.start();
                playPosition = -1;
            }
        }
    }

    /**
     * 滑动条变化线程
     */
    private Runnable runnable = new Runnable() {

        public void run() {
            // TODO Auto-generated method stub
            // 增加对异常的捕获，防止在判断mediaPlayer.isPlaying的时候，报IllegalStateException异常
            try {
                while (seekBarAutoFlag) {
                    /*
                     * mediaPlayer不为空且处于正在播放状态时，使进度条滚动。
                     * 通过指定类名的方式判断mediaPlayer防止状态发生不一致
                     */
                    if (null != ShowResourceActivity.this.mediaPlayer
                            && ShowResourceActivity.this.mediaPlayer.isPlaying()) {
                        seekBar.setProgress(mediaPlayer.getCurrentPosition());
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    /**
     * seekBar拖动监听类
     *
     * @author shenxiaolei
     */
    @SuppressWarnings("unused")
    private class SeekBarChangeListener implements SeekBar.OnSeekBarChangeListener {

        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            // TODO Auto-generated method stub
            if (progress >= 0) {
                // 如果是用户手动拖动控件，则设置视频跳转。
                if (fromUser) {
                    mediaPlayer.seekTo(progress);
                    playPosition = progress;
                }
                // 设置当前播放时间
                allTime.setText(getShowTime(progress) + "/" + videoTimeString);
            }
        }

        public void onStartTrackingTouch(SeekBar seekBar) {
            // TODO Auto-generated method stub

        }

        public void onStopTrackingTouch(SeekBar seekBar) {
            // TODO Auto-generated method stub
        }

    }
    /**
     * 转换播放时间
     *
     * @param milliseconds 传入毫秒值
     * @return 返回 hh:mm:ss或mm:ss格式的数据
     */
    @SuppressLint("SimpleDateFormat")
    public String getShowTime(long milliseconds) {
        // 获取日历函数
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliseconds);
        SimpleDateFormat dateFormat = null;
        // 判断是否大于60分钟，如果大于就显示小时。设置日期格式
        if (milliseconds / 60000 > 60) {
            dateFormat = new SimpleDateFormat("hh:mm:ss");
        } else {
            dateFormat = new SimpleDateFormat("mm:ss");
        }
        return dateFormat.format(calendar.getTime());
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        switch (what) {
            case MediaPlayer.MEDIA_ERROR_UNKNOWN:
                Toast.makeText(this, "MEDIA_ERROR_UNKNOWN", Toast.LENGTH_SHORT).show();
                break;
            case MediaPlayer.MEDIA_ERROR_SERVER_DIED:
                Toast.makeText(this, "MEDIA_ERROR_SERVER_DIED", Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }

        switch (extra) {
            case MediaPlayer.MEDIA_ERROR_IO:
                Toast.makeText(this, "MEDIA_ERROR_IO", Toast.LENGTH_SHORT).show();
                break;
            case MediaPlayer.MEDIA_ERROR_MALFORMED:
                Toast.makeText(this, "MEDIA_ERROR_MALFORMED", Toast.LENGTH_SHORT).show();
                break;
            case MediaPlayer.MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK:
                Toast.makeText(this, "MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK",
                        Toast.LENGTH_SHORT).show();
                break;
            case MediaPlayer.MEDIA_ERROR_TIMED_OUT:
                Toast.makeText(this, "MEDIA_ERROR_TIMED_OUT", Toast.LENGTH_SHORT).show();
                break;
            case MediaPlayer.MEDIA_ERROR_UNSUPPORTED:
                Toast.makeText(this, "MEDIA_ERROR_UNSUPPORTED", Toast.LENGTH_SHORT).show();
                break;
        }
        return false;
    }

    /**
     * 从暂停中恢复
     */
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        // 判断播放位置
        if (playPosition >= 0) {
            if (null != mediaPlayer) {
                seekBarAutoFlag = true;
                mediaPlayer.seekTo(playPosition);
                mediaPlayer.start();
            } else {
                playVideo();
            }

        }
    }

    /**
     * 页面处于暂停状态
     */
    @Override
    protected void onPause() {
        super.onPause();
        try {
            if (null != mediaPlayer && mediaPlayer.isPlaying()) {
                playPosition = mediaPlayer.getCurrentPosition();
                mediaPlayer.pause();
                seekBarAutoFlag = false;
            }
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
    }

    /**
     * 发生屏幕旋转时调用
     */
    protected void onSaveInstanceState(Bundle outState) {
        // TODO Auto-generated method stub
        super.onSaveInstanceState(outState);
        if (null != mediaPlayer) {
            // 保存播放位置
            playPosition = mediaPlayer.getCurrentPosition();
        }
    }

    /**
     * 屏幕旋转完成时调用
     */
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onRestoreInstanceState(savedInstanceState);

    }

    public void onConfigurationChanged(Configuration newConfig) {
        // TODO Auto-generated method stub
        super.onConfigurationChanged(newConfig);
    }

    /**
     * 屏幕销毁时调用
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 由于MediaPlay非常占用资源，所以建议屏幕当前activity销毁时，则直接销毁
        try {
            if (null != ShowResourceActivity.this.mediaPlayer) {
                // 提前标志为false,防止在视频停止时，线程仍在运行。
                seekBarAutoFlag = false;
                // 如果正在播放，则停止。
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.stop();
                }
                playPosition = -1;
                // 释放mediaPlayer
                ShowResourceActivity.this.mediaPlayer.release();
                ShowResourceActivity.this.mediaPlayer = null;
            }
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
    }
}
