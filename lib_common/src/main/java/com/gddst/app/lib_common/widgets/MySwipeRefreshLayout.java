package com.gddst.app.lib_common.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewConfiguration;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class MySwipeRefreshLayout extends SwipeRefreshLayout {
    private float startY;
    private float startX;
    // 判断viewPager是否在正在拖拽
    private boolean isviewPagerDragger;
    private  int mTouchSlop;
    public MySwipeRefreshLayout(@NonNull Context context) {
        super(context);
    }

    public MySwipeRefreshLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }
    private void init(Context context){
        //滑动最小距离
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        int action = ev.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:

                startY = ev.getRawY();
                startX = ev.getRawX();
                // 初始化标记
                isviewPagerDragger = false;
                break;
            case MotionEvent.ACTION_MOVE:
                // 如果正在拖拽，直接返回flase，让viewPager处理
                if(isviewPagerDragger) {
                    return false;
                }

                float gapX = Math.abs(ev.getRawX() - startX);
                float gapY = Math.abs(ev.getRawY() - startY);
                // 如果是滑动并且是横向滑动，返回flase让viewPager处理
                if(gapX > mTouchSlop && gapX > gapY) {
                    isviewPagerDragger = true;
                    return false;
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                // 标记复位
                isviewPagerDragger = false;
                break;
        }
        return super.onInterceptTouchEvent(ev);
    }
}
