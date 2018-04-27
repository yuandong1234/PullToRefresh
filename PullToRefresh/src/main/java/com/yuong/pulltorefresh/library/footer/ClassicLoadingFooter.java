package com.yuong.pulltorefresh.library.footer;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.yuong.pulltorefresh.library.R;
import com.yuong.pulltorefresh.library.State;
import com.yuong.pulltorefresh.library.header.LoadingLayout;

/**
 * Created by yuandong on 2018/4/27.
 */

public class ClassicLoadingFooter extends LoadingLayout {
    private View footerView;
    private TextView description;

    private State currentState = State.STATUS_REFRESH_FINISHED;

    /**
     * 记录上一次的状态是什么，避免进行重复操作
     */
    private State lastState = currentState;

    public ClassicLoadingFooter(Context context) {
        super(context);
    }

    @Override
    protected void initView(Context context) {
        setOrientation(VERTICAL);
        footerView = LayoutInflater.from(context).inflate(R.layout.layout_basic_refresh_footer, null);
        description = (TextView) footerView.findViewById(R.id.describe);
        addView(footerView);
    }

    @Override
    public void setState(State status) {
        currentState = status;
        if (currentState != lastState) {
            switch (status) {
                case STATUS_PULL_TO_LOADING:
                    description.setText(getContext().getResources().getString(R.string.pull_to_loading));
                    break;
                case STATUS_RELEASE_TO_LOADING:
                    description.setText(getContext().getResources().getString(R.string.realse_to_loading));
                    break;
                case STATUS_LOADING:
                    description.setText(getContext().getResources().getString(R.string.loading));
                    break;
                case STATUS_REFRESH_FINISHED:
                    description.setText(getContext().getResources().getString(R.string.load_finished));
                    break;
            }
        }
        lastState = currentState;
    }
}
