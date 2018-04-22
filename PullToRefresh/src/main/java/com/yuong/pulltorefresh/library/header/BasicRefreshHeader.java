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
import com.yuong.pulltorefresh.library.RefreshStatus;


/**
 * Created by yuandong on 2018/4/22.
 */

public class BasicRefreshHeader extends LinearLayout implements IRefreshHeader {
    private Context mContext;

    private View headerView;
    private ImageView arrow;
    private TextView description;
    private ProgressBar progressBar;
    private RefreshStatus currentStatus = RefreshStatus.STATUS_REFRESH_FINISHED;


    public BasicRefreshHeader(Context context) {
        this(context, null);
    }

    public BasicRefreshHeader(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BasicRefreshHeader(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    /**
     * 初始化
     *
     * @param mContext
     */
    private void initView(Context mContext) {
        headerView = LayoutInflater.from(mContext).inflate(R.layout.layout_basic_refresh_header, null);
        arrow = (ImageView) headerView.findViewById(R.id.arrow);
        description = (TextView) headerView.findViewById(R.id.description);
        progressBar = (ProgressBar) headerView.findViewById(R.id.progress_bar);
        setOrientation(VERTICAL);
        addView(headerView);
    }

    /**
     * 设置头部状态
     *
     * @param status
     */
    @Override
    public void setHeaderStatus(RefreshStatus status) {
        switch (status) {
            case STATUS_PULL_TO_REFRESH:
                break;
            case STATUS_RELEASE_TO_REFRESH:
                break;
            case STATUS_REFRESHING:
                break;
            case STATUS_REFRESH_FINISHED:
                break;
        }
    }
}
