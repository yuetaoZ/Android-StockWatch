package com.example.stockwatchapp;

import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class StockAdapter extends RecyclerView.Adapter<StockViewHolder> {

    private static final String TAG = "StocksAdapter";
    private final List<Stock> stockList;
    private final MainActivity mainAct;

    StockAdapter(List<Stock> stockList, MainActivity ma) {
        this.stockList = stockList;
        mainAct = ma;
    }

    @NonNull
    @Override
    public StockViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder: MAKING NEW");
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.stock_list_row, parent, false);

        itemView.setOnClickListener(mainAct);
        itemView.setOnLongClickListener(mainAct);

        return new StockViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull StockViewHolder holder, int position) {
        Stock stock = stockList.get(position);
        if(stock.getChange() < 0) {
            holder.symbol.setTextColor(Color.RED);
            holder.companyName.setTextColor(Color.RED);
            holder.latestTradePrice.setTextColor(Color.RED);
            holder.priceChangeDirection.setTextColor(Color.RED);
            holder.priceChangeAmount.setTextColor(Color.RED);
            holder.priceChangePercentage.setTextColor(Color.RED);
        }
        holder.symbol.setText(stock.getSymbol());
        holder.companyName.setText(stock.getCompanyName());
        holder.latestTradePrice.setText(String.format("%s", stock.getLatestPrice()));
        holder.priceChangeDirection.setText(stock.getChange() >= 0 ? "▲" : "▼");
        holder.priceChangeAmount.setText(String.format("%s", stock.getChange()));
        holder.priceChangePercentage.setText("(" + String.format("%s", stock.getChangePercent()) + "%)");
    }

    @Override
    public int getItemCount() {
        return stockList.size();
    }
}
