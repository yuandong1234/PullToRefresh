package com.yuong.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private Button btListView;
    private Button btScrollView;
    private Button btRecyclerView;
    private Button btView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        btListView = findViewById(R.id.bt_listView);
        btScrollView = findViewById(R.id.bt_scrollView);
        btRecyclerView = findViewById(R.id.bt_recyclerView);
        btView = findViewById(R.id.bt_View);
        btListView.setOnClickListener(this);
        btScrollView.setOnClickListener(this);
        btRecyclerView.setOnClickListener(this);
        btView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id. bt_listView:
                startActivity(new Intent(this,ListViewRefreshActivity.class));
                break;
            case R.id. bt_scrollView:
                startActivity(new Intent(this,ScrollViewActivity.class));
                break;
            case R.id. bt_recyclerView:
                startActivity(new Intent(this,RecyclerViewActivity.class));
                break;
            case R.id. bt_View:
                startActivity(new Intent(this,NormalActivity.class));
                break;

        }
    }
}
