package com.sjaiwl.app.smart;

import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import com.sjaiwl.app.medicalapplicition.R;

public class WebImage implements SmartImage {
    private static final int CONNECT_TIMEOUT = 5000;
    private static final int READ_TIMEOUT = 10000;
    private static WebImageCache webImageCache;

    private String url;


    public WebImage(String url) {
        this.url = url;
    }

    public Bitmap getBitmap(Context context) {
        // Don't leak context
        if (webImageCache == null) {
            webImageCache = new WebImageCache(context);
        }

        // Try getting bitmap from cache first
        Bitmap bitmap = null;
        if (url != null) {
            bitmap = webImageCache.get(url);
            if (bitmap == null) {
                bitmap = getBitmapFromUrl(url);
                if (bitmap != null) {
                    webImageCache.put(url, bitmap);
                }
            }
        }

        return bitmap;
    }

    public Bitmap getBitmapFromUrl(String url) {
        Bitmap bitmap = null;
        try {
            URLConnection conn = new URL(url).openConnection();
            conn.setConnectTimeout(CONNECT_TIMEOUT);
            conn.setReadTimeout(READ_TIMEOUT);
            bitmap = BitmapFactory.decodeStream((InputStream) conn.getContent());
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (bitmap == null) {
            bitmap = BitmapFactory.decodeResource(Resources.getSystem(), R.mipmap.userphoto);
        }
        return bitmap;
    }

    public void removeFromCache(String url) {
        if (webImageCache != null) {
            webImageCache.remove(url);
        }
    }
}
