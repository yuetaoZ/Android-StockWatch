package com.example.stockwatchapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
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
        super.setTitle("Stock Watch");
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recycler);
        stockAdapter = new StockAdapter(stockList, this);

        recyclerView.setAdapter(stockAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        swiper = findViewById(R.id.swiper);
        swiper.setOnRefreshListener(this::doRefresh);

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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.action_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == R.id.addItem) {
            addItem();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }

    }

    private void addItem() {
        // Single input value dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // Create an edittext and set it to be the builder's view
        final EditText et = new EditText(this);
        et.setInputType(InputType.TYPE_CLASS_TEXT);
        et.setGravity(Gravity.CENTER_HORIZONTAL);
        builder.setView(et);

        builder.setPositiveButton("OK", (dialog, id) -> {
            //tv1.setText(et.getText());
            Toast.makeText(MainActivity.this, "Symbol sent!", Toast.LENGTH_SHORT).show();
        });
        builder.setNegativeButton("CANCEL", (dialog, id) -> Toast.makeText(MainActivity.this, "You changed your mind!", Toast.LENGTH_SHORT).show());

        builder.setMessage("Please enter a Stock Symbol:");
        builder.setTitle("Stock Selection");

        AlertDialog dialog = builder.create();
        dialog.show();
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

        builder.setPositiveButton("DELETE", (dialog, which) -> {
            stockList.remove(pos);
            stockAdapter.notifyItemRemoved(pos);
            saveData();
            Toast.makeText(MainActivity.this, "Stock deleted!", Toast.LENGTH_SHORT).show();
        });

        builder.setNegativeButton("CANCEL", (dialog, which) -> Toast.makeText(MainActivity.this, "You changed your mind!", Toast.LENGTH_SHORT).show());

        AlertDialog dialog = builder.create();
        dialog.show();
        return false;
    }

    private void saveData() {
    }
}