package com.example.stockwatchapp;

public class Stock {

    private String symbol;
    private String name;
    private String companyName;
    private Double latestPrice = 0.0;
    private Double change = 0.0;
    private Double changePercent = 0.0;

    public String getSymbol() { return symbol; }

    public String getName() { return name; }

    public String getCompanyName() { return companyName; }

    public Double getLatestPrice() { return latestPrice; }

    public Double getChange() { return change; }

    public Double getChangePercent() { return changePercent; }
}
