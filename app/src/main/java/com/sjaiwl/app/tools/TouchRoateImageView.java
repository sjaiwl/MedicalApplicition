package com.sjaiwl.app.tools;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ImageView;

/**
 * 随触摸旋转ImageView
 *
 * @author liupoolo
 * @since 2013-02-22
 * @version 1.00
 */
public class TouchRoateImageView extends ImageView {

    private final static float MIN_DEGREE = 0f;
    private final static float MAX_DEGREE = 360f;

    private Matrix m;

    private float saveX; // 当前保存的x
    private float saveY; // 当前保存的y
    private float curTouchX; // 当前触屏的x
    private float curTouchY; // 当前触摸的y
    private float centerX; // 中心点x
    private float centerY; // 中心点y
    private float curDegree; // 当前角度
    private float changeDegree;

    public TouchRoateImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setScaleType(ScaleType.MATRIX);// 重点
        m = new Matrix();

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        centerX = this.getWidth() / 2;
        centerY = this.getHeight() / 2;
    }

    public boolean onTouchEvent(MotionEvent event) {
        handleTouch(event);
        return true;
    }

    private void handleTouch(MotionEvent event) {
        curTouchX = event.getX();
        curTouchY = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                saveTouchPoint();
                break;
            case MotionEvent.ACTION_MOVE:
                handleTouchMove();
                break;
            case MotionEvent.ACTION_UP:
                // 可以使用访问者模式这里让访问者获得当前角度
                break;
        }
    }

    private void handleTouchMove() {
        changeDegree = (float) getActionDegrees(centerX, centerY, saveX, saveY,
                curTouchX, curTouchY);
        float tempDegree = (float) curDegree + changeDegree;
        if (tempDegree >= MIN_DEGREE && tempDegree <= MAX_DEGREE) {
            optimize(tempDegree);//优化变动
            m.setRotate(curDegree, centerX, centerY);
            setImageMatrix(m);// 此方法会 调用invalidate() 从而重绘界面
        }

        saveTouchPoint();
    }

    private void optimize(float tempDegree){
        if(tempDegree>MAX_DEGREE-1){
            curDegree=MAX_DEGREE;
        }else if(tempDegree<MIN_DEGREE+1){
            curDegree=MIN_DEGREE;
        }else{
            this.curDegree = tempDegree;
        }


    }

    private void saveTouchPoint() {
        saveX = curTouchX;
        saveY = curTouchY;

    }

    /**
     * 获取两点到第三点的夹角。
     *
     * @param x
     * @param y
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     * @return
     */
    private double getActionDegrees(float x, float y, float x1, float y1,
                                    float x2, float y2) {

        double a = Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
        double b = Math.sqrt((x - x2) * (x - x2) + (y - y2) * (y - y2));
        double c = Math.sqrt((x1 - x) * (x1 - x) + (y1 - y) * (y1 - y));
        // 余弦定理
        double cosA = (b * b + c * c - a * a) / (2 * b * c);
        // 返回余弦值为指定数字的角度，Math函数为我们提供的方法
        double arcA = Math.acos(cosA);
        double degree = arcA * 180 / Math.PI;

        // 接下来我们要讨论正负值的关系了，也就是求出是顺时针还是逆时针。
        // 第1、2象限
        if (y1 < y && y2 < y) {
            if (x1 < x && x2 > x) {// 由2象限向1象限滑动
                return degree;
            }
            // 由1象限向2象限滑动
            else if (x1 >= x && x2 <= x) {
                return -degree;
            }
        }
        // 第3、4象限
        if (y1 > y && y2 > y) {
            // 由3象限向4象限滑动
            if (x1 < x && x2 > x) {
                return -degree;
            }
            // 由4象限向3象限滑动
            else if (x1 > x && x2 < x) {
                return degree;
            }

        }
        // 第2、3象限
        if (x1 < x && x2 < x) {
            // 由2象限向3象限滑动
            if (y1 < y && y2 > y) {
                return -degree;
            }
            // 由3象限向2象限滑动
            else if (y1 > y && y2 < y) {
                return degree;
            }
        }
        // 第1、4象限
        if (x1 > x && x2 > x) {
            // 由4向1滑动
            if (y1 > y && y2 < y) {
                return -degree;
            }
            // 由1向4滑动
            else if (y1 < y && y2 > y) {
                return degree;
            }
        }

        // 在特定的象限内
        float tanB = (y1 - y) / (x1 - x);
        float tanC = (y2 - y) / (x2 - x);
        if ((x1 > x && y1 > y && x2 > x && y2 > y && tanB > tanC)// 第一象限
                || (x1 > x && y1 < y && x2 > x && y2 < y && tanB > tanC)// 第四象限
                || (x1 < x && y1 < y && x2 < x && y2 < y && tanB > tanC)// 第三象限
                || (x1 < x && y1 > y && x2 < x && y2 > y && tanB > tanC))// 第二象限
            return -degree;
        return degree;
    }

    public float getCurDegree() {
        return curDegree;
    }

    public void setCurDegree(float curDegree) {
        if (curDegree >= MIN_DEGREE && curDegree <= MAX_DEGREE) {
            this.curDegree = curDegree;
            m.setRotate(curDegree, centerX, centerY);
            setImageMatrix(m);
        }

    }

}
