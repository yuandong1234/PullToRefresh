package com.yuong.view;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.yuong.pulltorefresh.library.RefreshLayout;
import com.yuong.pulltorefresh.library.listener.RefreshListener;

import java.lang.ref.WeakReference;

public class NormalActivity extends AppCompatActivity {

    private RefreshLayout refreshLayout;
    private Button btUnablePullUp;
    private Button btAblePullUp;
    private Button btAblePullDown;
    private Button btUnablePullDown;
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
        btUnablePullUp = findViewById(R.id.bt_unable_pull_up);
        btAblePullUp = findViewById(R.id.bt_able_pull_up);
        btAblePullDown = findViewById(R.id.bt_able_pull_down);
        btUnablePullDown = findViewById(R.id.bt_unable_pull_down);


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

        btUnablePullUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refreshLayout.setEnableLoadMore(false);
            }
        });

        btAblePullUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refreshLayout.setEnableLoadMore(true);
            }
        });

        btAblePullDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refreshLayout.setEnableRefresh(true);
            }
        });

        btUnablePullDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refreshLayout.setEnableRefresh(false);
            }
        });
    }
}
