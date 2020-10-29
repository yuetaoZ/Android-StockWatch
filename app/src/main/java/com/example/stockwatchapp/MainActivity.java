package com.example.stockwatchapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.DialogInterface;
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

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete Stock");
        builder.setIcon(R.drawable.ic_dialog_delete);
        builder.setMessage("Delete Stock Symbol " + s.getSymbol() + "?");

        builder.setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                stockList.remove(pos);
                stockAdapter.notifyItemRemoved(pos);
                saveData();
                Toast.makeText(MainActivity.this, "Stock deleted!", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(MainActivity.this, "You changed your mind!", Toast.LENGTH_SHORT).show();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
        return false;
    }

    private void saveData() {
    }
}