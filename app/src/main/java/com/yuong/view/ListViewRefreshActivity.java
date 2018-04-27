package com.yuong.view;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.yuong.pulltorefresh.library.RefreshLayout;
import com.yuong.pulltorefresh.library.listener.RefreshListener;

import java.lang.ref.WeakReference;

public class ListViewRefreshActivity extends AppCompatActivity {
    private RefreshLayout refreshLayout;
    private ListView listView;
    private ArrayAdapter<String> adapter;
    private String[] items = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N"};
    private int tag;
    private ListViewHandler handler;
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            handler.sendEmptyMessage(tag);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_view_refresh);

        initView();
    }


    private void initView() {
        refreshLayout = findViewById(R.id.refreshLayout);
        listView = (ListView) findViewById(R.id.listView);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, items);
        listView.setAdapter(adapter);

        handler = new ListViewHandler(this);

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

    public static class ListViewHandler extends Handler {
        private final WeakReference<ListViewRefreshActivity> mActivity;

        public ListViewHandler(ListViewRefreshActivity activity) {
            mActivity = new WeakReference<ListViewRefreshActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            ListViewRefreshActivity activity = mActivity.get();
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
}
