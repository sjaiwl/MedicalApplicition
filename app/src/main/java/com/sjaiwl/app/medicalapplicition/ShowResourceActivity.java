package com.sjaiwl.app.medicalapplicition;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
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

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.HttpHandler;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.sjaiwl.app.function.AppConfiguration;
import com.sjaiwl.app.function.NetworkUtils;
import com.sjaiwl.app.function.ResourceInfo;
import com.sjaiwl.app.function.UserInfo;
import com.sjaiwl.app.zoom.PhotoView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

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
    private SurfaceHolder surfaceHolder = null;
    private MediaPlayer mediaPlayer = null;
    private AnimationDrawable draw;
    private final String PREFERENCE_NAME = "userSetting" + UserInfo.user.getDoctor_id();
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
    //缓存目录
    private static String dir = Environment.getExternalStorageDirectory()
            .getAbsolutePath() + "/MedicalApplication/videoCache/";
    //缓存文件
    private File file, videoCacheFile;
    //缓存文件前缀
    private String videoCachePrefix = "VideoCacheFile"; //要保存的视频缓存文件的前缀
    private SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
    private HttpHandler handler;
    //播放路径
    private String videoPath;
    //缓存提示
    private ProgressDialog progressDialog = null;
    //是否需要缓存
    private static boolean isNeedToLoad;
    //缓存路径保存
    private final String VIDEO_CACHE_PREFERENCE_NAME = "videoCachePath";

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
        progressBar.bringToFront();
        topLayout.bringToFront();
        bottomLayout.bringToFront();
    }

    private void initData() {
        resourceInfo = (ResourceInfo) getIntent().getSerializableExtra("resource");
        resourceType = resourceInfo.getResource_type();
        doClick();
    }

    private void doViewActivity() {
        if (checkNetWorkState()) {
            selectResourceType();
        }
    }

    //检查网络状态
    private boolean checkNetWorkState() {
        SharedPreferences preferences = getSharedPreferences(PREFERENCE_NAME, Activity.MODE_PRIVATE);
        boolean viewSettingState = preferences.getBoolean("viewSettingState", true);
        if (NetworkUtils.isConnectInternet(this)) {
            if (NetworkUtils.isConnectWifi(this)) {
                return true;
            } else {
                if (viewSettingState) {
                    return true;
                } else {
                    Toast.makeText(this, "当前接入的是移动网络，请在“设置”中修改后查看", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    return false;
                }
            }
        } else {
            Toast.makeText(this, "请接入网络后查看", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
            return false;
        }
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
        // DisplayImageOptions是用于设置图片显示的类
        DisplayImageOptions options;
        // 使用DisplayImageOptions.Builder()创建DisplayImageOptions
        options = new DisplayImageOptions.Builder()
                .cacheInMemory(true)                        // 设置下载的图片是否缓存在内存中
                .build();                                   // 创建配置过得DisplayImageOption对象
        //加载图片
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this).build();
        ImageLoader imageLoader = ImageLoader.getInstance();
        imageLoader.init(config);
        imageLoader.displayImage(resourceInfo.getResource_url(), imageView, options, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {

            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {

            }
        });
    }

    private void showVideo() {
        isNeedToLoad = true;
        topText.setText("视频");
        surfaceView.setVisibility(View.VISIBLE);
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        surfaceHolder.addCallback(new SurfaceCallback());
    }

    private void showAudio() {
        topText.setText("音频");
        surfaceView.setVisibility(View.VISIBLE);
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        surfaceHolder.addCallback(new SurfaceCallback());
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
            videoTypeSelect();
        }

        private void videoTypeSelect() {
            //如果是视频需要缓存到本地
            if (resourceType == 3) {
                SharedPreferences preferences = getSharedPreferences(VIDEO_CACHE_PREFERENCE_NAME, Activity.MODE_PRIVATE);
                String tempString = preferences.getString(resourceInfo.getResource_url(), null);
                if (tempString != null) {
                    videoPath = preferences.getString(resourceInfo.getResource_url(), null);
                    File tempFile = new File(videoPath);
                    if (tempFile.length() != 0) {
                        isNeedToLoad = false;
                        //播放视频
                        playVideo();
                    } else {
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.remove(resourceInfo.getResource_url());
                        editor.commit();
                        tempFile.delete();
                        isNeedToLoad = true;
                    }
                }

                if (isNeedToLoad && isHasSdcard()) {
                    videoCacheFile = new File(file, videoCachePrefix + format.format(new Date()) + ".3gp");
                    HttpUtils http = new HttpUtils();
                    handler = http.download(resourceInfo.getResource_url(), videoCacheFile.getPath(), true, false,
                            new RequestCallBack<File>() {
                                @Override
                                public void onStart() {
                                    //显示提示框
                                    showProgressDialog();
                                }

                                @Override
                                public void onLoading(long total, long current, boolean isUploading) {
                                    //正在下载
                                    super.onLoading(total, current, isUploading);
                                }

                                @Override
                                public void onSuccess(ResponseInfo<File> responseInfo) {
                                    //关闭提示框
                                    dismissProgressDialog();
                                    //保存文件路径
                                    videoPath = videoCacheFile.getPath();
                                    //加入到缓存目录
                                    SharedPreferences sharedPreferences = getSharedPreferences(VIDEO_CACHE_PREFERENCE_NAME, Activity.MODE_PRIVATE);
                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    editor.putString(resourceInfo.getResource_url(), videoPath);
                                    editor.commit();
                                    //播放视频
                                    playVideo();
                                }

                                @Override
                                public void onFailure(HttpException error, String msg) {
                                    //关闭提示框
                                    dismissProgressDialog();
                                    Toast.makeText(ShowResourceActivity.this, "视频缓存出错，请重试", Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            } else {
                videoPath = resourceInfo.getResource_url();
                // 设置播放资源,音频无需缓存，直接在线播放
                playVideo();
            }
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
            mediaPlayer.setDataSource(videoPath);
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
        if (resourceType == 4) {
            surfaceView.setBackgroundColor(getResources().getColor(R.color.showResource_BackgroundColor));
            textView.setVisibility(View.VISIBLE);
            textView.bringToFront();
            textView.setBackgroundResource(R.drawable.animation);
            draw = (AnimationDrawable) textView.getBackground();
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
        //设置button
        playButton.setBackground(getResources().getDrawable(R.mipmap.stop_button));
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
        if (resourceType == 4) {
            draw.stop();
        }
        playButton.setBackground(getResources().getDrawable(R.mipmap.play_button));
        playPosition = 0;
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
        Log.i("tag", "onBufferingUpdate-->" + percent);
    }

    @Override
    public void onClick(View v) {
        if (null != mediaPlayer) {
            if (mediaPlayer.isPlaying()) {
                if (resourceType == 4) {
                    draw.stop();
                }
                playButton.setBackground(getResources().getDrawable(R.mipmap.play_button));
                mediaPlayer.pause();
                playPosition = mediaPlayer.getCurrentPosition();
            } else if (playPosition >= 0) {
                if (resourceType == 4) {
                    draw.start();
                }
                playButton.setBackground(getResources().getDrawable(R.mipmap.stop_button));
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
                Log.e("MediaPlayer Error", "MEDIA_ERROR_UNKNOWN");
                break;
            case MediaPlayer.MEDIA_ERROR_SERVER_DIED:
                Log.e("MediaPlayer Error", "MEDIA_ERROR_SERVER_DIED");
                break;
            default:
                break;
        }

        switch (extra) {
            case MediaPlayer.MEDIA_ERROR_IO:
                Log.e("MediaPlayer Error", "MEDIA_ERROR_IO");
                break;
            case MediaPlayer.MEDIA_ERROR_MALFORMED:
                Log.e("MediaPlayer Error", "MEDIA_ERROR_MALFORMED");
                break;
            case MediaPlayer.MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK:
                Log.e("MediaPlayer Error", "MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK");
                break;
            case MediaPlayer.MEDIA_ERROR_TIMED_OUT:
                Log.e("MediaPlayer Error", "MEDIA_ERROR_TIMED_OUT");
                break;
            case MediaPlayer.MEDIA_ERROR_UNSUPPORTED:
                Log.e("MediaPlayer Error", "MEDIA_ERROR_UNSUPPORTED");
                break;
        }
        return false;
    }

    /**
     * 从暂停中恢复
     */
    protected void onResume() {
        // TODO Auto-generated method stub
        doViewActivity();
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
            Toast.makeText(ShowResourceActivity.this, "未找到存储卡，无法存储", Toast.LENGTH_SHORT).show();
            return false;
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

    private void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = ProgressDialog.show(ShowResourceActivity.this,
                    "视频缓存", "正在努力加载中 ...", true, false);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setCancelable(true);
        }
    }

    private void dismissProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }
}

