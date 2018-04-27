package com.yuong.pulltorefresh.library;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Scroller;

import com.yuong.pulltorefresh.library.footer.ClassicLoadingFooter;
import com.yuong.pulltorefresh.library.header.ClassicLoadingHeader;
import com.yuong.pulltorefresh.library.header.LoadingLayout;
import com.yuong.pulltorefresh.library.intercept.ViewDispatchEvent;
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

    /**
     * footer view
     */
    private LoadingLayout footer;

    /**
     * footer height
     */
    private int hideFooterHeight;
    /**
     * current refresh state
     */
    private State currentState;

    private RefreshListener refreshListener;

    private Scroller mScroller;
    private static final int SCROLL_SPEED = 500;

    private boolean loadOnce;

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
        Log.e(TAG, "onFinishInflate()...");
        addHeader();
        addFooter();
    }

    //添加头部
    private void addHeader() {
        addView(getHeader());
    }

    private void addFooter() {
        addView(getFooter());
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        Log.e(TAG, "onMeasure()...");
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            measureChild(child, widthMeasureSpec, heightMeasureSpec);
        }
    }


    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        Log.e(TAG, "onLayout()...");
        if (changed && !loadOnce) {
            int height = 0;
            int count = getChildCount();
            View child;
            Log.e(TAG, "ChildCount : " + count);
            for (int i = 0; i < count; i++) {
                child = getChildAt(i);
                if (child == header) {
                    hideHeaderHeight = child.getMeasuredHeight();
                    Log.e(TAG, "hideHeaderHeight : " + hideHeaderHeight);
                    child.layout(0, -hideHeaderHeight, child.getMeasuredWidth(), 0);
                } else if (child == footer) {
                    hideFooterHeight = child.getMeasuredHeight();
                    Log.e(TAG, "hideFooterHeight : " + hideFooterHeight);
                    child.layout(0, height, child.getMeasuredWidth(), height + hideFooterHeight);
                } else {
                    child.layout(0, height, child.getMeasuredWidth(), height + child.getMeasuredHeight());
                    height += child.getMeasuredHeight();
                }
            }
            loadOnce = true;
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

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (currentState == State.STATUS_REFRESHING||currentState==State.STATUS_LOADING) {
            return false;
        }
        return super.dispatchTouchEvent(ev);
    }

    private int mLastMoveY;
    private int mLastYIntercept;

    //在这里针对不同的类型进行拦截操作
    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        boolean intercept = false;
        int currentY = (int) event.getY();
        Log.e(TAG, "onInterceptTouchEvent :currentX -------> " + currentY);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // 记录下本次系列触摸事件的起始点Y坐标
                mLastMoveY = currentY;
                mLastYIntercept = currentY;
                // 不拦截ACTION_DOWN，因为当ACTION_DOWN被拦截，后续所有触摸事件都会被拦截
                intercept = false;
                break;
            case MotionEvent.ACTION_MOVE:
                Log.e(TAG, "mLastYIntercept " + mLastYIntercept + " currentX :" + currentY);
                View child = getChildAt(0);
                if (currentY > mLastYIntercept) { // 下拉操作
                    // 获取最顶部的子视图
                    intercept = ViewDispatchEvent.pullDown(child);
                } else {
                    // 上拉操作
                    intercept = ViewDispatchEvent.pullUp(child,getMeasuredHeight());
                }
                break;
            case MotionEvent.ACTION_UP:
                intercept = false;
                break;
        }
        mLastYIntercept = currentY;
        return intercept;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int y = (int) event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mLastMoveY = y;
                break;
            case MotionEvent.ACTION_MOVE:
                int distance = mLastMoveY - y;
                Log.e(TAG, "distance :" + distance + "  getScrollY() :" + getScrollY());
                if (getScrollY() < 0) {
                    if (getScrollY() <= -hideHeaderHeight) {
                        //释放立即刷新
                        currentState = State.STATUS_RELEASE_TO_REFRESH;
                        header.setState(State.STATUS_RELEASE_TO_REFRESH);
                    } else {
                        //下拉可以刷新
                        currentState = State.STATUS_PULL_TO_REFRESH;
                        header.setState(State.STATUS_PULL_TO_REFRESH);
                    }
                }

                if (getScrollY() > 0) {
                    if (getScrollY() >= hideFooterHeight) {
                        currentState = State.STATUS_RELEASE_TO_LOADING;
                        footer.setState(State.STATUS_RELEASE_TO_LOADING);
                    } else {
                        currentState = State.STATUS_PULL_TO_LOADING;
                        footer.setState(State.STATUS_PULL_TO_LOADING);
                    }
                }
                scrollBy(0, distance);
                break;
            case MotionEvent.ACTION_UP:
                if (getScrollY() <= -hideHeaderHeight) {
                    //正在刷新
                    mScroller.startScroll(0, getScrollY(), 0, -(getScrollY() + hideHeaderHeight), SCROLL_SPEED);
                    header.setState(State.STATUS_REFRESHING);
                    if (currentState != State.STATUS_REFRESHING) {
                        currentState = State.STATUS_REFRESHING;
                        if (refreshListener != null) {
                            Log.e(TAG, "The view is refreshing...");
                            refreshListener.onRefresh();
                        }
                    }
                } else if (getScrollY() >= hideFooterHeight) {
                    //正在加载
                    mScroller.startScroll(0, getScrollY(), 0, -(getScrollY() - hideFooterHeight), SCROLL_SPEED);
                    footer.setState(State.STATUS_LOADING);
                    if (currentState != State.STATUS_LOADING) {
                        currentState = State.STATUS_LOADING;
                        if (refreshListener != null) {
                            Log.e(TAG, "The view is loading more...");
                            refreshListener.onLoadMore();
                        }
                    }
                } else {
                    mScroller.startScroll(0, getScrollY(), 0, -getScrollY(), SCROLL_SPEED);
                }
                break;
        }
        mLastMoveY = y;
        return true;
    }

    public void onRefreshComplete() {
        //刷新完毕
        Log.e(TAG, "The view refresh complete");
        currentState = State.STATUS_REFRESH_FINISHED;
        header.setState(State.STATUS_REFRESH_FINISHED);
        postDelayed(new Runnable() {
            @Override
            public void run() {
                mScroller.startScroll(0, getScrollY(), 0, -getScrollY(), SCROLL_SPEED);
            }
        }, 500);
    }

    public void onLoadMoreComplete() {
        //加载完毕
        Log.e(TAG, "The view load more  complete");
        currentState = State.STATUS_REFRESH_FINISHED;
        footer.setState(State.STATUS_REFRESH_FINISHED);
        postDelayed(new Runnable() {
            @Override
            public void run() {
                mScroller.startScroll(0, getScrollY(), 0, -getScrollY(), SCROLL_SPEED);
            }
        }, 500);
    }

    /**
     * set the Header
     *
     * @param header
     */
    public void setHeader(LoadingLayout header) {
        this.header = header;
    }

    /**
     * set the Footer
     *
     * @param footer
     */
    public void setFooter(LoadingLayout footer) {
        this.footer = footer;
    }

    /**
     * get the Header
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
     * get the Footer
     *
     * @return
     */
    public LoadingLayout getFooter() {
        if (footer == null) {
            footer = new ClassicLoadingFooter(getContext());
        }
        return footer;
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
