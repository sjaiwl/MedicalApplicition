package com.sjaiwl.app.function;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;

public class AppConfiguration {
    public static final String server = "http://115.28.181.5:3000/";
    public static final String loginUrl = server + "login.json";
    public static final String registerUrl = server + "register.json";
    public static final String updateUserUrl = server + "update_user.json";
    public static final String updateUserPictureUrl = server + "update_userPictureUrl.json";
    public static final String settingPasswordUrl = server + "setting_password.json";
    public static final String queryUserUrl = server + "query_user.json";
    public static final String get_allPatientUrl = server + "get_allPatient.json";
    public static final String get_searchPatientUrl = server + "get_searchPatient.json";
    public static final String get_allResourceUrl = server + "get_allResource.json";
    public static final String get_patientResourceUrl = server + "get_patientResource.json";
    public static final String newResourceUrl = server + "new_resource.json";
    public static final String deleteResourceUrl = server + "delete_resource.json";
    public static final String appKey = "66d1672bf728";
    public static final String appSecret = "ba3db467dda8d3d4efa27cbec85ba1aa";
    public static final float thumbnailMinSize = 200f;

    public static String classifyFromUTC(String UTCTime) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        //转换utc time
        String utcTime = getLocalTimeFromUTC(UTCTime, 1);
        //获取当前日期
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        String month = UsedTools.formatTime(calendar.get(Calendar.MONTH) + 1);
        String day = UsedTools.formatTime(calendar.get(Calendar.DAY_OF_MONTH));
        String nowTime = year + "-" + month + "-" + day;
        //转换日期
        Date utcDate = null;
        Date nowDate = null;
        int dayTime = 0;
        try {
            utcDate = df.parse(utcTime);
            nowDate = df.parse(nowTime);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (utcTime.equals(nowTime)) {
            return "今天";
        } else {
            dayTime = Math.abs((int) (utcDate.getTime() - nowDate.getTime()))
                    / (1000 * 60 * 60 * 24);
            if (dayTime <= 7) {
                return "一周内";
            }
            if (dayTime <= 30) {
                return "一月内";
            } else {
                return "以前";
            }
        }
    }

    public static String getLocalTimeFromUTC(String UTCTime, Integer type) {
        if (UTCTime == "") {
            return "时间获取失败";
        }
        SimpleDateFormat sdf = new SimpleDateFormat(
                "yyyy-MM-dd'T'HH:mm:ss.SSS Z");
        UTCTime = UTCTime.replace("Z", " UTC");
        Date dt = null;
        try {
            dt = sdf.parse(UTCTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long millis = dt.getTime();
        Date dat = new Date(millis);
        GregorianCalendar gc = new GregorianCalendar();
        gc.setTime(dat);
        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat format2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        if (type == 1) {
            return format1.format(gc.getTime());
        } else {
            return format2.format(gc.getTime());
        }
    }

    public static Bitmap getRoundCornerBitmap(Bitmap bitmap, float roundPX) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        Bitmap bitmap2 = Bitmap.createBitmap(width, height, Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap2);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, width, height);
        final RectF rectF = new RectF(rect);

        paint.setColor(color);
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        canvas.drawRoundRect(rectF, roundPX, roundPX, paint);

        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return bitmap2;
    }
}
