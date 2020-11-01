package com.example.stockwatchapp.apis;

import android.net.Uri;
import android.os.Build;
import android.util.JsonReader;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.example.stockwatchapp.MainActivity;
import com.example.stockwatchapp.Stock;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;

public class StockDownloader implements Runnable {
    private static final String TAG = "StockDownloader";
    private static final String REGION_URL = "https://cloud.iexapis.com/stable/stock/";
    private MainActivity mainActivity;
    private String searchTarget;
    private String token = "/quote?token=pk_a3b4a450d5b04ef1b783ad96b4ca36ec";

    public StockDownloader(MainActivity mainActivity, String searchTarget) {
        this.mainActivity = mainActivity;
        this.searchTarget = searchTarget;
    }

    @Override
    public void run() {
        Uri.Builder uriBuilder = Uri.parse(REGION_URL + searchTarget + token).buildUpon();
        String urlToUse = uriBuilder.toString();

        Log.d(TAG, "run: " + urlToUse);

        StringBuilder sb = new StringBuilder();
        try {
            URL url = new URL(urlToUse);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.connect();

            if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                Log.d(TAG, "run: HTTP ResponseCode NOT OK: " + conn.getResponseCode());
                return;
            }

            InputStream is = conn.getInputStream();
            BufferedReader reader = new BufferedReader((new InputStreamReader(is)));

            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
            }

            Log.d(TAG, "run: " + sb.toString());

        } catch (Exception e) {
            Log.e(TAG, "run: ", e);
            return;
        }

        process(sb.toString());
        Log.d(TAG, "run: ");

    }

    private void process(String s) {
        try {
            JSONObject jStock = new JSONObject(s);

            String symbol = jStock.getString("symbol");
            String companyName = jStock.getString("companyName");

            String latestPriceString = jStock.getString("latestPrice");
            Double latestPrice = 0.0;
            if(!latestPriceString.trim().isEmpty())
                latestPrice = Double.parseDouble(latestPriceString);

            String changeString = jStock.getString("change");
            Double change = 0.0;
            if(!changeString.trim().isEmpty())
                change = Double.parseDouble(changeString);

            String changePercentString = jStock.getString("changePercent");
            Double roundChangePercent = 0.0;
            if(!changePercentString.trim().isEmpty()) {
                roundChangePercent = Math.round(Double.parseDouble(changePercentString) * 10000.0) / 100.0;
            }

            final Stock stock = new Stock(symbol, companyName,
                    latestPrice, change, roundChangePercent);

            mainActivity.runOnUiThread(new Runnable() {
                @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                @Override
                public void run() {
                    mainActivity.addPickedStock(stock, searchTarget);
                }
            });

        } catch (Exception e) {
            Log.d(TAG, "parseJSON: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
