package com.sjaiwl.app.tools;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Rect;
import android.widget.ImageView;

import com.sjaiwl.app.medicalapplicition.R;
import com.sjaiwl.app.zoom.PhotoView;


public class RotateImageView extends PhotoView {

    /**旋转角度**/
    private float _degree;
    /**旋转中心**/
    private float _x;
    private float _y;
    /** 背景和前景资源 */
    private Bitmap _bgRes, _fgRes;
    /** 数字图片资源 */
    private Bitmap _nums;

    private Bitmap _buffer;
    private Canvas _canvas;
    private int start = 0;
    private int unitx = 29;

    public RotateImageView(Context context) {
        super(context);
        _degree = 0f;
        _x = 0;
        _y = 0;
        _nums = BitmapFactory.decodeResource(getResources(), R.mipmap.userphoto);
    }

    public void setResIds(int bgResId, int fgResId) {
        _bgRes = BitmapFactory.decodeResource(getResources(), bgResId);
        _fgRes = BitmapFactory.decodeResource(getResources(), fgResId);
    }

    public void setLevel(int level) {
        String _level = String.valueOf(level);
        _buffer = Bitmap.createBitmap( _level.length()*(_nums.getWidth()/10), _nums.getHeight(), Config.ARGB_4444);
        unitx = _nums.getWidth()/10;
        _canvas = new Canvas(_buffer);
        for(int i = 0; i <= _level.length()-1; i++){
            Rect src = new Rect((_level.charAt(i) - '0')*unitx, 0, (_level.charAt(i) - '0' + 1)*unitx, _nums.getHeight());
            Rect dst = new Rect( i*unitx, 0, (i + 1)*unitx, _nums.getHeight());
            _canvas.drawBitmap(_nums, src, dst, null);
        }
    }

    public void setLevel(int level, int Resid) {
        _nums = BitmapFactory.decodeResource(getResources(), Resid);
        String _level = String.valueOf(level);
        _buffer = Bitmap.createBitmap( _level.length()*(_nums.getWidth()/10), _nums.getHeight(), Config.ARGB_4444);
        unitx = _nums.getWidth()/10;
        _canvas = new Canvas(_buffer);
        for(int i = 0; i <= _level.length()-1; i++){
            Rect src = new Rect((_level.charAt(i) - '0')*unitx, 0, (_level.charAt(i) - '0' + 1)*unitx, _nums.getHeight());
            Rect dst = new Rect( i*unitx, 0, (i + 1)*unitx, _nums.getHeight());
            _canvas.drawBitmap(_nums, src, dst, null);
        }
    }

    public void setDegree(float degree) {
        _degree = degree;
    }

    public void setDegree(float degree, float x, float y) {
        _degree = degree;
        _x = x;
        _y = y;
    }

    public float getDegree() {
        return _degree;
    }

    public float getRotateX() {
        return _x;
    }

    public float getRotateY() {
        return _y;
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.save();
        canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG|Paint.FILTER_BITMAP_FLAG));
        canvas.rotate(_degree, _x, _y);
        canvas.drawBitmap(_bgRes, 0, 0, null);
        canvas.drawBitmap(_fgRes, 25, 58, null);
        canvas.drawBitmap(_buffer, (_bgRes.getWidth() - _buffer.getWidth())/2f, 25, null);
        canvas.restore();
    }

}