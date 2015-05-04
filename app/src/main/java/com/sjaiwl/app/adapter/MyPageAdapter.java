package com.sjaiwl.app.adapter;

import android.support.v4.view.PagerAdapter;
import android.view.View;

import com.sjaiwl.app.zoom.ViewPagerFixed;

import java.util.ArrayList;

/**
 * Created by sjaiwl on 15/4/23.
 */
public class MyPageAdapter extends PagerAdapter {

    private ArrayList<View> listViews;

    private int size;

    public MyPageAdapter(ArrayList<View> listViews) {
        this.listViews = listViews;
        size = listViews == null ? 0 : listViews.size();
    }

    public void setListViews(ArrayList<View> listViews) {
        this.listViews = listViews;
        size = listViews == null ? 0 : listViews.size();
    }

    public int getCount() {
        return size;
    }

    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    public void destroyItem(View arg0, int arg1, Object arg2) {
        ((ViewPagerFixed) arg0).removeView(listViews.get(arg1 % size));
    }

    public void finishUpdate(View arg0) {
    }

    public Object instantiateItem(View arg0, int arg1) {
        try {
            ((ViewPagerFixed) arg0).addView(listViews.get(arg1 % size), 0);

        } catch (Exception e) {
        }
        return listViews.get(arg1 % size);
    }

    public boolean isViewFromObject(View arg0, Object arg1) {
        return arg0 == arg1;
    }

}
