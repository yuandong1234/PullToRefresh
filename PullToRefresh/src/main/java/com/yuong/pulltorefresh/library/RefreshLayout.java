package com.yuong.pulltorefresh.library;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Scroller;
import android.widget.TextView;

import com.yuong.pulltorefresh.library.header.BasicRefreshHeader;
import com.yuong.pulltorefresh.library.header.IRefreshHeader;

/**
 * Created by yuandong on 2018/4/21.
 */

public class RefreshLayout extends ViewGroup {
    /**
     * 下拉状态
     */
    public static final int STATUS_PULL_TO_REFRESH = 0;

    /**
     * 释放立即刷新状态
     */
    public static final int STATUS_RELEASE_TO_REFRESH = 1;

    /**
     * 正在刷新状态
     */
    public static final int STATUS_REFRESHING = 2;

    /**
     * 刷新完成或未刷新状态
     */
    public static final int STATUS_REFRESH_FINISHED = 3;
    /**
     * 当前处理什么状态，可选值有STATUS_PULL_TO_REFRESH, STATUS_RELEASE_TO_REFRESH,
     * STATUS_REFRESHING 和 STATUS_REFRESH_FINISHED
     */
    private int currentStatus = STATUS_REFRESH_FINISHED;
    /**
     * 下拉头的高度
     */
    private int hideHeaderHeight;
    private IRefreshHeader refreshHeader;

    private View headerView;
    private TextView tvStatus;

    private RefreshListener refreshListener;

    private Scroller mScroller;

    public RefreshLayout(Context context) {
        this(context, null);
    }

    public RefreshLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RefreshLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mScroller = new Scroller(context);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        addHeader();
    }

    //添加头部
    private void addHeader() {
        headerView = LayoutInflater.from(getContext()).inflate(R.layout.layout_pull_to_refresh_header, null);
        tvStatus = (TextView) headerView.findViewById(R.id.tv_status);
        addView(headerView);
        //addView(refreshHeader);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        Log.e("RefreshLayout", "onMeasure()...");
        //测量子view的尺寸
//        measureChildren(widthMeasureSpec, heightMeasureSpec);
//        int with = MeasureSpec.getSize(widthMeasureSpec);
//        int height = MeasureSpec.getSize(heightMeasureSpec);
//        int withMode = MeasureSpec.getMode(widthMeasureSpec);
//        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
//        if (getChildCount() == 0) {//如果没有子View,当前ViewGroup没有存在的意义，不用占用空间
//            setMeasuredDimension(0, 0);
//            return;
//        }
//
//        if (withMode == MeasureSpec.AT_MOST && heightMode == MeasureSpec.AT_MOST) {
//            //高度累加，宽度取最大
//            setMeasuredDimension(getMaxChildWidth(), getTotleHeight());
//        } else if (heightMode == MeasureSpec.AT_MOST) {
//            setMeasuredDimension(with, getTotleHeight());
//        } else if (withMode == MeasureSpec.AT_MOST) {
//            setMeasuredDimension(getMaxChildWidth(), height);
//        }

        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            measureChild(child, widthMeasureSpec, heightMeasureSpec);
        }
    }

//    /***
//     * 获取子View中宽度最大的值
//     */
//    private int getMaxChildWidth() {
//        int childCount = getChildCount();
//        int maxWidth = 0;
//        for (int i = 0; i < childCount; i++) {
//            View childView = getChildAt(i);
//            if (childView.getMeasuredWidth() > maxWidth) {
//                maxWidth = childView.getMeasuredWidth();
//            }
//        }
//        return maxWidth;
//    }

//    /***
//     * 将所有子View的高度相加
//     **/
//    private int getTotleHeight() {
//        int childCount = getChildCount();
//        int height = 0;
//        for (int i = 0; i < childCount; i++) {
//
//            View childView = getChildAt(i);
//            if (childView == headerView) continue;
//            height += childView.getMeasuredHeight();
//        }
//        return height;
//    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        Log.e("RefreshLayout", "onLayout()...");
        int height = 0;
        int count = getChildCount();
        View child;
        Log.e("ri", count + "");
        for (int i = 0; i < count; i++) {
            child = getChildAt(i);
            if (child == headerView) {
                hideHeaderHeight = child.getMeasuredHeight();
                child.layout(0, -hideHeaderHeight, child.getMeasuredWidth(), 0);
            } else {
                child.layout(0, height, child.getMeasuredWidth(), height + child.getMeasuredHeight());
                height += child.getMeasuredHeight();
            }
        }
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        if (mScroller.computeScrollOffset()) {
            scrollTo(0, mScroller.getCurrY());
        }
        postInvalidate();
    }

    private int mLastMoveY;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int y = (int) event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mLastMoveY = y;
                break;
            case MotionEvent.ACTION_MOVE:
                int distance = mLastMoveY - y;
                Log.e("RefreshLayout", "distance :" + distance);
                Log.e("RefreshLayout", "getScrollY() : " + getScrollY());

                if (distance > 0 && getScrollY() == 0) return false;

                if (getScrollY() <= -hideHeaderHeight) {
                    //下拉刷新
                    currentStatus = STATUS_RELEASE_TO_REFRESH;
                    tvStatus.setText("释放立即刷新");
                } else {
                    currentStatus = STATUS_PULL_TO_REFRESH;
                    tvStatus.setText("下拉可以刷新");
                }
                scrollBy(0, distance);
                break;
            case MotionEvent.ACTION_UP:
                //mScroller.startScroll(0, getScrollY(), 0, -getScrollY());
                if (getScrollY() <= -hideHeaderHeight) {
                    //正在刷新
                    mScroller.startScroll(0, getScrollY(), 0, -(getScrollY() + hideHeaderHeight));
                    currentStatus = STATUS_REFRESHING;
                    tvStatus.setText("正在刷新…");
                    if (refreshListener != null) {
                        refreshListener.onRefresh();
                    }
                } else {
                    mScroller.startScroll(0, getScrollY(), 0, -getScrollY());
                }
                break;
        }
        mLastMoveY = y;
        return true;
    }

    public void onRefreshComplete() {
        currentStatus = STATUS_REFRESH_FINISHED;
        tvStatus.setText("刷新完毕");
        postDelayed(new Runnable() {
            @Override
            public void run() {
                mScroller.startScroll(0, getScrollY(), 0, -getScrollY());
            }
        },500);
    }

    public void setRefreshHeader(IRefreshHeader refreshHeader) {
        if(refreshHeader!=null){
            this.refreshHeader = refreshHeader;
            return;
        }
        this.refreshHeader=new BasicRefreshHeader(getContext());
    }

    public void setOnRefreshListener(RefreshListener refreshListener) {
        this.refreshListener = refreshListener;
    }

}
