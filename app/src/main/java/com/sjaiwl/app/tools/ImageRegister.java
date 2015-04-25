package com.sjaiwl.app.tools;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.os.Handler;
import android.widget.ImageView;

import com.sjaiwl.app.medicalapplicition.R;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.net.URL;
import java.util.HashMap;

public class ImageRegister {

	private Object lock = new Object();
	private boolean mAllowLoad = true;
	private boolean firstLoad = true;
	private int mStartLoadLimit = 0;
	private int mStopLoadLimit = 0;
	final Handler handler = new Handler();
	private HashMap<String, SoftReference<Drawable>> imageCache = new HashMap<String, SoftReference<Drawable>>();

	public void setLoadLimit(int startLoadLimit, int stopLoadLimit) {
		if (startLoadLimit > stopLoadLimit) {
			return;
		}
		mStartLoadLimit = startLoadLimit;
		mStopLoadLimit = stopLoadLimit;
	}

	public void restore() {
		mAllowLoad = true;
		firstLoad = true;
	}

	public void Lock() {
		mAllowLoad = false;
		firstLoad = false;
	}

	public void unLock() {
		mAllowLoad = true;
		synchronized (lock) {
			lock.notifyAll();
		}
	}

	public void loadImage(final Integer position, final ImageView imageView,
			final String imageUrl, final String fileName) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				if (!mAllowLoad) {
					synchronized (lock) {
						try {
							lock.wait();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}

				if (mAllowLoad && firstLoad) {
					loadImage2(position,imageUrl, imageView, fileName);
				}
				if (mAllowLoad && position<= mStopLoadLimit && position >= mStartLoadLimit) {
					loadImage2(position,imageUrl, imageView, fileName);
				}
			}
		}).start();
	}

	private void loadImage2(final Integer position,final String mImageUrl, final ImageView imageView,
			final String fileName) {
		if (imageCache.containsKey(mImageUrl)) {
			SoftReference<Drawable> softReference = imageCache.get(mImageUrl);
			final Drawable d = softReference.get();
			if (d != null) {
				handler.post(new Runnable() {
					@SuppressLint("NewApi") @Override
					public void run() {
						if (mAllowLoad) {
							if (imageView.getTag().equals(mImageUrl)) {
								//imageView.setBackground(d);
							}
						}
					}
				});
				return;
			}
		}
		try {
			final Drawable d = loadImageFromUrl(position,imageView,mImageUrl, fileName);
			if (d != null) {
				imageCache.put(mImageUrl, new SoftReference<Drawable>(d));
				handler.post(new Runnable() {
					@SuppressLint("NewApi") @Override
					public void run() {
						if (mAllowLoad) {
							if (imageView.getTag().equals(mImageUrl)) {
								imageView.setBackground(d);
							}
						}
					}
				});
			}
		} catch (IOException e) {
			handler.post(new Runnable() {
				@Override
				public void run() {
					imageView.setBackgroundResource(R.mipmap.ic_launcher);
				}
			});
			e.printStackTrace();
		}
	}

	@SuppressLint("NewApi") 
	public Drawable loadImageFromUrl(Integer position,ImageView imageView,String url, String fileName)
			throws IOException {
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			String imageName = url.substring(url.lastIndexOf("/"));
			String pathName = (Environment.getExternalStorageDirectory() + "/"
					+ fileName + imageName);
			File f = new File(pathName);
			if (f.exists()) {
				Bitmap bitmap = compressImageFromFile(pathName);
				@SuppressWarnings("deprecation")
				BitmapDrawable d = new BitmapDrawable(bitmap);
				System.out.println("加载了第:"+position);
				return d;
			}else{
			System.out.println("下载了第:"+position);
			URL m = new URL(url);
			InputStream inputStream = (InputStream) m.getContent();
			DataInputStream in = new DataInputStream(inputStream);
			FileOutputStream out = new FileOutputStream(f);
			byte[] buffer = new byte[1024];
			int byteRead = 0;
			while ((byteRead = in.read(buffer)) != -1) {
				out.write(buffer, 0, byteRead);
			}
			in.close();
			out.close();
			return loadImageFromUrl(position,imageView,url, fileName);
			}
		} else {
			URL m = new URL(url);
			InputStream inputStream = (InputStream) m.getContent();
			Drawable drawable = Drawable.createFromStream(inputStream, null);
			inputStream.close();
			return drawable;
		}
		    
	}

	private Bitmap compressImageFromFile(String srcPath) {
		BitmapFactory.Options newOpts = new BitmapFactory.Options();
		newOpts.inJustDecodeBounds = true;
		Bitmap bitmap = BitmapFactory.decodeFile(srcPath, newOpts);
		newOpts.inJustDecodeBounds = false;
		int w = newOpts.outWidth;
		int h = newOpts.outHeight;
		float hh = 100f;
		float ww = 120f;
		int be = 1;
		if (w > h && w > ww) {
			be = (int) (newOpts.outWidth / ww);
		} else if (w < h && h > hh) {
			be = (int) (newOpts.outHeight / hh);
		}
		if (be <= 0)
			be = 1;
		newOpts.inSampleSize = be;
		newOpts.inPreferredConfig = Config.ARGB_8888;
		newOpts.inPurgeable = true;
		newOpts.inInputShareable = true;
		bitmap = BitmapFactory.decodeFile(srcPath, newOpts);
		return bitmap;
	}

}
