package com.example.arsojib.bulksms.Utils;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class CustomSwipeDisableViewPager extends ViewPager {

    private boolean swipeable;

    public CustomSwipeDisableViewPager(Context context) {
        super(context);
    }

    public CustomSwipeDisableViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.swipeable = true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return this.swipeable && super.onTouchEvent(event);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        return this.swipeable && super.onInterceptTouchEvent(event);
    }

    public void setSwipeable(boolean swipeable) {
        this.swipeable = swipeable;
    }

}