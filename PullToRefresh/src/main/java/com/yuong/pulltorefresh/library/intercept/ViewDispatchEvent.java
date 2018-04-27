package com.yuong.pulltorefresh.library.intercept;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ScrollView;

/**
 * 用于解决各种view嵌套事件滑动冲突
 * Created by yuandong on 2018/4/27.
 */

public class ViewDispatchEvent {

    public static boolean pullDown(View view) {
        if (view instanceof AdapterView) {
            return pullDownOnAdapterView(view);
        } else if (view instanceof ScrollView) {
            return pullDownOnScrollView(view);
        } else if (view instanceof RecyclerView) {
            return pullDownOnRecyclerView(view);
        } else {
            return false;
        }
    }

    public static boolean pullUp(View view, int viewGroupHeight) {
        Log.e("9999999","viewGroupHeight : "+viewGroupHeight);
        if (view instanceof AdapterView) {
            return pullUpOnAdapterView(view, viewGroupHeight);
        } else if (view instanceof ScrollView) {
            return pullUpOnScrollView(view);
        } else if (view instanceof RecyclerView) {
            return pullUpOnRecyclerView(view);
        } else {
            return false;
        }
    }

    private static boolean pullDownOnAdapterView(View child) {
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

    private static boolean pullDownOnScrollView(View child) {
        boolean intercept = false;
        if (child.getScrollY() <= 0) {
            intercept = true;
        }
        return intercept;
    }

    private static boolean pullDownOnRecyclerView(View child) {
        boolean intercept = false;

        RecyclerView recyclerChild = (RecyclerView) child;
        if (recyclerChild.computeVerticalScrollOffset() <= 0)
            intercept = true;
        return intercept;
    }

    private static boolean pullUpOnAdapterView(View child, int viewGroupHeight) {
        boolean intercept = false;
        AdapterView adapterChild = (AdapterView) child;

        // 判断AbsListView是否已经到达内容最底部
        if (adapterChild.getLastVisiblePosition() == adapterChild.getCount() - 1
                && (adapterChild.getChildAt(adapterChild.getChildCount() - 1).getBottom() == viewGroupHeight)) {
            // 如果到达底部，则拦截事件
            intercept = true;
        }
        return intercept;
    }

    private static boolean pullUpOnScrollView(View child) {
        boolean intercept = false;
        ScrollView scrollView = (ScrollView) child;
        View scrollChild = scrollView.getChildAt(0);

        if (scrollView.getScrollY() >= (scrollChild.getHeight() - scrollView.getHeight())) {
            intercept = true;
        }
        return intercept;
    }

    private static boolean pullUpOnRecyclerView(View child) {
        boolean intercept = false;
        RecyclerView recyclerChild = (RecyclerView) child;
        if (recyclerChild.computeVerticalScrollExtent() + recyclerChild.computeVerticalScrollOffset()
                >= recyclerChild.computeVerticalScrollRange())
            intercept = true;

        return intercept;
    }
}
