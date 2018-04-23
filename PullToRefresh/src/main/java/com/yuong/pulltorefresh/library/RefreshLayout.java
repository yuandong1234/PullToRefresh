package com.yuong.pulltorefresh.library;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Scroller;

import com.yuong.pulltorefresh.library.header.ClassicLoadingHeader;
import com.yuong.pulltorefresh.library.header.LoadingLayout;
import com.yuong.pulltorefresh.library.listener.RefreshListener;

/**
 * Created by yuandong on 2018/4/21.
 */

public class RefreshLayout extends ViewGroup {

    private static String TAG = RefreshLayout.class.getSimpleName();
    /**
     * header view
     */
    private LoadingLayout header;
    /**
     * header height
     */
    private int hideHeaderHeight;


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
        addView(getHeader());
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//        Log.e(TAG, "onMeasure()...");
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
//        Log.e(TAG, "onLayout()...");
        int height = 0;
        int count = getChildCount();
        View child;
        Log.e(TAG, "ChildCount : "+ count);
        for (int i = 0; i < count; i++) {
            child = getChildAt(i);
            if (child == header) {
                hideHeaderHeight = child.getMeasuredHeight();
                Log.e(TAG, "hideHeaderHeight : " + hideHeaderHeight);
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
//                Log.e(TAG, "distance :" + distance);
//                Log.e(TAG, "getScrollY() : " + getScrollY());

                if (distance > 0 && getScrollY() == 0) return false;

                if (getScrollY() <= -hideHeaderHeight) {
                    //释放立即刷新
                    header.setState(State.STATUS_RELEASE_TO_REFRESH);
                } else {
                    //下拉可以刷新
                    header.setState(State.STATUS_PULL_TO_REFRESH);
                }
                scrollBy(0, distance);
                break;
            case MotionEvent.ACTION_UP:
                if (getScrollY() <= -hideHeaderHeight) {
                    //正在刷新
                    mScroller.startScroll(0, getScrollY(), 0, -(getScrollY() + hideHeaderHeight));
                    header.setState(State.STATUS_REFRESHING);
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
        //刷新完毕
        header.setState(State.STATUS_REFRESH_FINISHED);
        postDelayed(new Runnable() {
            @Override
            public void run() {
                mScroller.startScroll(0, getScrollY(), 0, -getScrollY());
            }
        }, 500);
    }

    /**
     * 设置头部
     *
     * @param header
     */
    public void setHeader(LoadingLayout header) {
        this.header = header;
    }

    /**
     * 获得头部
     *
     * @return
     */
    public LoadingLayout getHeader() {
        if (header == null) {
            header = new ClassicLoadingHeader(getContext());
        }
        return header;
    }

    /**
     * 设置监听
     *
     * @param refreshListener
     */
    public void setOnRefreshListener(RefreshListener refreshListener) {
        this.refreshListener = refreshListener;
    }

}
