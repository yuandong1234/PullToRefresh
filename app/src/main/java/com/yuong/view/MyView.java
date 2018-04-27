package com.yuong.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

/**
 * 简单的自定义ViewGroup的使用
 * Created by yuandong on 2018/4/23.
 */

public class MyView extends ViewGroup {

    public MyView(Context context) {
        super(context);
    }

    public MyView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //测量子view的尺寸
        measureChildren(widthMeasureSpec, heightMeasureSpec);
        int with = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        int withMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        if (getChildCount() == 0) {//如果没有子View,当前ViewGroup没有存在的意义，不用占用空间
            setMeasuredDimension(0, 0);
            return;
        }

        if (withMode == MeasureSpec.AT_MOST && heightMode == MeasureSpec.AT_MOST) {
            //高度累加，宽度取最大
            setMeasuredDimension(getMaxChildWidth(), getTotleHeight());
        } else if (heightMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(with, getTotleHeight());
        } else if (withMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(getMaxChildWidth(), height);
        }
    }

    /***
     * 获取子View中宽度最大的值
     */
    private int getMaxChildWidth() {
        int childCount = getChildCount();
        int maxWidth = 0;
        for (int i = 0; i < childCount; i++) {
            View childView = getChildAt(i);
            if (childView.getMeasuredWidth() > maxWidth) {
                maxWidth = childView.getMeasuredWidth();
            }
        }
        return maxWidth;
    }

    /***
     * 将所有子View的高度相加
     **/
    private int getTotleHeight() {
        int childCount = getChildCount();
        int height = 0;
        for (int i = 0; i < childCount; i++) {

            View childView = getChildAt(i);
            height += childView.getMeasuredHeight();
        }
        return height;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int count = getChildCount();
        int currentHeigth = 0;
        //将子View逐个摆放
        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            int childHeigth = child.getMeasuredHeight();
            child.layout(0, currentHeigth, child.getMeasuredWidth(), currentHeigth + childHeigth);
            currentHeigth += childHeigth;
        }
    }
}
