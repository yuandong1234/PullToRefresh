package com.yuong.pulltorefresh.library.header;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.yuong.pulltorefresh.library.R;
import com.yuong.pulltorefresh.library.State;


/**
 * Created by yuandong on 2018/4/22.
 */

public abstract class LoadingLayout extends LinearLayout implements ILoadingLayout {

    public LoadingLayout(Context context) {
        this(context, null);
    }

    public LoadingLayout(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LoadingLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    /**
     * 初始化
     *
     * @param context
     */
    protected abstract void initView(Context context);


    /**
     * 设置头部状态
     *
     * @param status
     */
    @Override
    public void setState(State status) {}
}
