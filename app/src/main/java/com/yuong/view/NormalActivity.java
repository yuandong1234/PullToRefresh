package com.yuong.view;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;

import com.yuong.pulltorefresh.library.RefreshLayout;
import com.yuong.pulltorefresh.library.listener.RefreshListener;

import java.lang.ref.WeakReference;

public class NormalActivity extends AppCompatActivity {

    private RefreshLayout refreshLayout;
    private NormalHandler handler;
    private int tag;
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            handler.sendEmptyMessage(tag);
        }
    };

    public static class NormalHandler extends Handler {
        private final WeakReference<NormalActivity> mActivity;

        public NormalHandler(NormalActivity activity) {
            mActivity = new WeakReference<NormalActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            NormalActivity activity = mActivity.get();
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
        setContentView(R.layout.activity_normal);
        initView();
    }

    private void initView() {
        refreshLayout = findViewById(R.id.refreshLayout);

        handler = new NormalHandler(this);
        refreshLayout.setOnRefreshListener(new RefreshListener() {
            @Override
            public void onRefresh() {
                tag = 1;
                handler.removeCallbacksAndMessages(null);
                handler.postDelayed(runnable, 3000);
            }

            @Override
            public void onLoadMore() {
                tag = 2;
                handler.removeCallbacksAndMessages(null);
                handler.postDelayed(runnable, 3000);
            }
        });
    }
}
