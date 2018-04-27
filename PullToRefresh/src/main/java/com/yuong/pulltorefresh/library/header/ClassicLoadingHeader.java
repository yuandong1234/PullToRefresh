package com.yuong.pulltorefresh.library.header;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.yuong.pulltorefresh.library.R;
import com.yuong.pulltorefresh.library.State;
import com.yuong.pulltorefresh.library.internal.RotateImageView;

/**
 * 经典下拉头部
 * Created by yuandong on 2018/4/23.
 */

public class ClassicLoadingHeader extends LoadingLayout {

    private View headerView;
    private ImageView arrow;
    private TextView description;
    private RotateImageView progressBar;

    private State currentState = State.STATUS_REFRESH_FINISHED;

    /**
     * 记录上一次的状态是什么，避免进行重复操作
     */
    private State lastState = currentState;


    public ClassicLoadingHeader(Context context) {
        super(context);
    }


    @Override
    protected void initView(Context context) {
        setOrientation(VERTICAL);
        headerView = LayoutInflater.from(context).inflate(R.layout.layout_basic_refresh_header, null);
        arrow = (ImageView) headerView.findViewById(R.id.arrow);
        description = (TextView) headerView.findViewById(R.id.description);
        progressBar = (RotateImageView) headerView.findViewById(R.id.progress_bar);
        addView(headerView);
    }

    @Override
    public void setState(State status) {
        currentState = status;
        if (lastState != currentState) {
            switch (status) {
                case STATUS_PULL_TO_REFRESH:
                    arrow.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.INVISIBLE);
                    rotateArrow();
                    description.setText(getContext().getResources().getString(R.string.pull_to_refresh));
                    break;
                case STATUS_RELEASE_TO_REFRESH:
                    arrow.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.INVISIBLE);
                    rotateArrow();
                    description.setText(getContext().getResources().getString(R.string.release_to_refresh));
                    break;
                case STATUS_REFRESHING:
                    arrow.clearAnimation();
                    arrow.setVisibility(View.GONE);
                    progressBar.setVisibility(View.VISIBLE);
                    description.setText(getContext().getResources().getString(R.string.refreshing));
                    break;
                case STATUS_REFRESH_FINISHED:
                    arrow.setVisibility(View.INVISIBLE);
                    progressBar.setVisibility(View.INVISIBLE);
                    description.setText(getContext().getResources().getString(R.string.refresh_finished));
                    break;
            }
        }
        lastState = currentState;
    }

    /**
     * 根据当前的状态来旋转箭头。
     */
    private void rotateArrow() {
        float pivotX = arrow.getWidth() / 2f;
        float pivotY = arrow.getHeight() / 2f;
        float fromDegrees = 0f;
        float toDegrees = 0f;
        if (currentState == State.STATUS_PULL_TO_REFRESH) {
            fromDegrees = 180f;
            toDegrees = 360f;
        } else if (currentState == State.STATUS_RELEASE_TO_REFRESH) {
            fromDegrees = 0f;
            toDegrees = 180f;
        }

        RotateAnimation animation = new RotateAnimation(fromDegrees, toDegrees, pivotX, pivotY);
        animation.setDuration(100);
        animation.setFillAfter(true);
        arrow.startAnimation(animation);
    }
}
