package com.example.stockwatchapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, View.OnLongClickListener {

    private final List<Stock> stockList = new ArrayList<>();

    private RecyclerView recyclerView;
    private SwipeRefreshLayout swiper;

    private StockAdapter stockAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recycler);
        stockAdapter = new StockAdapter(stockList, this);

        recyclerView.setAdapter(stockAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        swiper = findViewById(R.id.swiper);
        swiper.setOnRefreshListener(() -> { doRefresh(); });

        // Make some data - just used to fill list
        for (int i = 0; i < 20; i++) {
            stockList.add(new Stock());
        }
    }

    private void doRefresh() {
        stockAdapter.notifyDataSetChanged();
        swiper.setRefreshing(false);
        Toast.makeText(this, "List content updated.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View v) {
        int pos = recyclerView.getChildLayoutPosition(v);
        Stock s = stockList.get(pos);

        Toast.makeText(v.getContext(), "SHORT " + s.getSymbol(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onLongClick(View v) {
        int pos = recyclerView.getChildLayoutPosition(v);
        Stock s = stockList.get(pos);
        Toast.makeText(v.getContext(), "LONG " + s.getSymbol(), Toast.LENGTH_SHORT).show();

        return false;
    }
}