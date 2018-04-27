package com.yuong.view;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yuong.pulltorefresh.library.RefreshLayout;
import com.yuong.pulltorefresh.library.listener.RefreshListener;

import java.lang.ref.WeakReference;

public class RecyclerViewActivity extends AppCompatActivity {
    private RefreshLayout refreshLayout;
    private RecyclerView recyclerView;
    private String[] items = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N"};
    private MyAdapter adapter;
    private int tag;
    private RecyclerViewHandler handler;
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            handler.sendEmptyMessage(tag);
        }
    };

    public static class RecyclerViewHandler extends Handler {
        private final WeakReference<RecyclerViewActivity> mActivity;

        public RecyclerViewHandler(RecyclerViewActivity activity) {
            mActivity = new WeakReference<RecyclerViewActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            RecyclerViewActivity activity = mActivity.get();
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
        setContentView(R.layout.activity_recycler_view);
        initView();
    }

    private void initView() {
        refreshLayout = findViewById(R.id.refreshLayout);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MyAdapter();
        recyclerView.setAdapter(adapter);

        handler = new RecyclerViewHandler(this);

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


    private class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(RecyclerViewActivity.this).inflate(android.R.layout.simple_list_item_1, null);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.textView.setText(items[position]);
        }

        @Override
        public int getItemCount() {
            return items.length;
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView textView;

            public ViewHolder(View itemView) {
                super(itemView);
                textView = itemView.findViewById(android.R.id.text1);
                textView.setGravity(Gravity.CENTER);
            }
        }
    }
}
