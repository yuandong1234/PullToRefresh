package com.yuong.view;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.widget.Button;
import android.widget.LinearLayout;

import com.yuong.pulltorefresh.library.RefreshLayout;
import com.yuong.pulltorefresh.library.listener.RefreshListener;

import java.lang.ref.WeakReference;

public class ScrollViewActivity extends AppCompatActivity {
    private RefreshLayout refreshLayout;
    private LinearLayout llContainer;
    private int tag;
    private ScrollViewHandler handler;
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            handler.sendEmptyMessage(tag);
        }
    };

    public static class ScrollViewHandler extends Handler {
        private final WeakReference<ScrollViewActivity> mActivity;

        public ScrollViewHandler(ScrollViewActivity activity) {
            mActivity = new WeakReference<ScrollViewActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            ScrollViewActivity activity = mActivity.get();
            if (activity != null) {
                switch (msg.what) {
                    case 1:
                        activity.refreshLayout.onRefreshComplete();
                        break;
                    case 2:
                        activity.refreshLayout.onLoadMoreComplete();
                        break;
                }
            }
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scroll_view);
        initView();
    }

    private void initView() {
        refreshLayout = findViewById(R.id.refreshLayout);
        llContainer = findViewById(R.id.ll_container);
        for (int i = 0; i < 20; i++) {
            Button textView = new Button(this);
            LinearLayout.LayoutParams params =
                    new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 150);
            textView.setGravity(Gravity.CENTER);
            textView.setText("TEXT "+i);
            textView.setLayoutParams(params);
            llContainer.addView(textView);
        }

        handler = new ScrollViewHandler(this);
        refreshLayout.setOnRefreshListener(new RefreshListener() {
            @Override
            public void onRefresh() {
                tag=1;
                handler.removeCallbacksAndMessages(null);
                handler.postDelayed(runnable, 3000);
            }

            @Override
            public void onLoadMore() {
                tag=2;
                handler.removeCallbacksAndMessages(null);
                handler.postDelayed(runnable, 3000);
            }
        });
    }
}
