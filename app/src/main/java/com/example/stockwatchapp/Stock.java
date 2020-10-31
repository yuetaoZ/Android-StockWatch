package com.example.stockwatchapp;

public class Stock {

    private String symbol;
    private String companyName;
    private Double latestPrice = 0.0;
    private Double change = 0.0;
    private Double changePercent = 0.0;

    public Stock(String symbol, String companyName) {
        this.symbol = symbol;
        this.companyName = companyName;
    }

    public Stock(String symbol, String companyName, Double latestPrice, Double change, Double changePercent) {
        this.symbol = symbol;
        this.companyName = companyName;
        this.latestPrice = latestPrice;
        this.change = change;
        this.changePercent = changePercent;
    }

    public Stock() {

    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public void setLatestPrice(Double latestPrice) {
        this.latestPrice = latestPrice;
    }

    public void setChange(Double change) {
        this.change = change;
    }

    public void setChangePercent(Double changePercent) {
        this.changePercent = changePercent;
    }

    public String getSymbol() { return symbol; }

    public String getCompanyName() { return companyName; }

    public Double getLatestPrice() { return latestPrice; }

    public Double getChange() { return change; }

    public Double getChangePercent() { return changePercent; }
}
