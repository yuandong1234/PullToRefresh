package com.yuong.pulltorefresh.library;

import android.content.Context;
import android.support.v7.view.menu.ShowableListMenu;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            measureChild(child, widthMeasureSpec, heightMeasureSpec);
        }
    }


    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
//        Log.e(TAG, "onLayout()...");
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
    private int mLastYIntercept;

    //在这里针对不同的类型进行拦截操作
    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        boolean intercept = false;
        int currentX = (int) event.getY();
        Log.e(TAG,"onInterceptTouchEvent :currentX -------> "+currentX);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // 记录下本次系列触摸事件的起始点Y坐标
                // mLastYMoved = y;
                // 不拦截ACTION_DOWN，因为当ACTION_DOWN被拦截，后续所有触摸事件都会被拦截
                 mLastYIntercept = currentX;
                intercept = false;
                break;
            case MotionEvent.ACTION_MOVE:
                Log.e(TAG,"mLastYIntercept "+mLastYIntercept +" currentX :"+currentX);
                if (currentX >= mLastYIntercept) { // 下滑操作
                    // 获取最顶部的子视图
                    View child = getChildAt(0);
                    if (child instanceof AdapterView) {
                        intercept = avPullDownIntercept(child);
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                intercept = false;
            break;
        }
        mLastYIntercept = currentX;
        return intercept;
    }

    private boolean avPullDownIntercept(View child) {
        boolean intercept = true;
        AdapterView adapterChild = (AdapterView) child;
        // 判断AbsListView是否已经到达内容最顶部
        if (adapterChild.getFirstVisiblePosition() != 0
                || adapterChild.getChildAt(0).getTop() != 0) {
            // 如果没有达到最顶端，则仍然将事件下放
            intercept = false;
        }
        return intercept;
    }

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
