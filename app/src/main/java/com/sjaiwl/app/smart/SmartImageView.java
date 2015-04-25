package com.sjaiwl.app.smart;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;

import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.sjaiwl.app.function.Configuration;

public class SmartImageView extends ImageView {
    private static final int LOADING_THREADS = 4;
    private static ExecutorService threadPool = Executors.newFixedThreadPool(LOADING_THREADS);
    private SmartImageTask currentTask;
    private WebImage webImage;

    public SmartImageView(Context context) {
        super(context);
    }

    public SmartImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SmartImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    // Helpers to set image by URL
    public void setImageUrl(String url, int operate, boolean clean) {
        if (clean) {
            webImage = new WebImage(url);
            webImage.removeFromCache(url);
            setImage(new WebImage(url), null, null, null, operate);
        }
    }

    public void setImageUrl(String url, int operate) {
        setImage(new WebImage(url), null, null, null, operate);
    }

    public void setImage(final SmartImage image, final Integer fallbackResource, final Integer loadingResource, final SmartImageTask.OnCompleteListener completeListener, final int operate) {
        // Set a loading resource
        if (loadingResource != null) {
            setImageResource(loadingResource);
        }

        // Cancel any existing tasks for this image view
        if (currentTask != null) {
            currentTask.cancel();
            currentTask = null;
        }

        // Set up the new task
        currentTask = new SmartImageTask(getContext(), image);
        currentTask.setOnCompleteHandler(new SmartImageTask.OnCompleteHandler() {
            @Override
            public void onComplete(Bitmap bitmap) {
                if (bitmap != null) {
                    if (operate == 1) {
                        setImageBitmap(bitmap);
                    } else {
                        setImageBitmap(Configuration.getRoundCornerBitmap(bitmap, 30));
                    }
                } else {
                    // Set fallback resource
                    if (fallbackResource != null) {
                        setImageResource(fallbackResource);
                    }
                }

                if (completeListener != null) {
                    if (operate == 1) {
                        completeListener.onComplete(bitmap);
                    } else {
                        completeListener.onComplete(Configuration.getRoundCornerBitmap(bitmap, 30));
                    }
                }
            }
        });

        // Run the task in a threadpool
        threadPool.execute(currentTask);
    }

    public static void cancelAllTasks() {
        threadPool.shutdownNow();
        threadPool = Executors.newFixedThreadPool(LOADING_THREADS);
    }
}