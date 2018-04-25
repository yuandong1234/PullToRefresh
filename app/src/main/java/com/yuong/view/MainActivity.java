package com.yuong.view;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.yuong.pulltorefresh.library.RefreshLayout;
import com.yuong.pulltorefresh.library.listener.RefreshListener;

public class MainActivity extends AppCompatActivity {
    private RefreshLayout refreshLayout;
    private ListView listView;
    private ArrayAdapter<String> adapter;
    private String[] items = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        refreshLayout = findViewById(R.id.refreshLayout);

        listView = (ListView) findViewById(R.id.listView);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, items);
        listView.setAdapter(adapter);

        refreshLayout.setOnRefreshListener(new RefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        refreshLayout.onRefreshComplete();
                    }
                }, 3000);
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.e("MainActivity","************************");
            }
        });
    }
}
