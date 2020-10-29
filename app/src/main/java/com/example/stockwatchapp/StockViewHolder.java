package com.example.stockwatchapp;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class StockViewHolder extends RecyclerView.ViewHolder {

    public TextView symbol;
    public TextView companyName;
    public TextView latestTradePrice;
    public TextView priceChangeDirection;
    public TextView priceChangeAmount;
    public TextView priceChangePercentage;

    public StockViewHolder(@NonNull View itemView) {
        super(itemView);
        symbol = itemView.findViewById(R.id.symbol);
        companyName = itemView.findViewById(R.id.companyName);
        latestTradePrice = itemView.findViewById(R.id.latestTradePrice);
        priceChangeDirection = itemView.findViewById(R.id.priceChangeDirection);
        priceChangeAmount = itemView.findViewById(R.id.priceChangeAmount);
        priceChangePercentage = itemView.findViewById(R.id.priceChangePercentage);
    }
}
