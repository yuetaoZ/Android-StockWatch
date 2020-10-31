package com.example.stockwatchapp;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.util.JsonWriter;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
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

        readJSONData();

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

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == R.id.addItem) {
            addItem();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void addItem() {
        // Single input value dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // Create an edittext and set it to be the builder's view
        final EditText et = new EditText(this);
        et.setInputType(InputType.TYPE_CLASS_TEXT);
        et.setGravity(Gravity.CENTER_HORIZONTAL);
        builder.setView(et);

        builder.setPositiveButton("OK", (dialog, id) -> {
            List<Stock> stockSearchList;
            String stockSymbol = et.getText().toString();
            stockSearchList = getStockSearchList(stockSymbol);

            listSelectionDialog(stockSearchList, stockSymbol);

            Toast.makeText(MainActivity.this, "Symbol sent!", Toast.LENGTH_SHORT).show();
        });
        builder.setNegativeButton("CANCEL", (dialog, id) -> Toast.makeText(MainActivity.this, "You changed your mind!", Toast.LENGTH_SHORT).show());

        builder.setMessage("Please enter a Stock Symbol:");
        builder.setTitle("Stock Selection");

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private List<Stock> getStockSearchList(String text) {
        List<Stock> stockSearchList = new ArrayList<>();

        // Make some dummy data for now - just used to fill list
        for (int i = 0; i < 20; i++) {
            Stock dummyStock = new Stock();
            dummyStock.setSymbol(String.valueOf(i));
            dummyStock.setCompanyName("Company " + i);
            stockSearchList.add(dummyStock);
        }
        return stockSearchList;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void listSelectionDialog(List<Stock> stockSearchList, String stockSymbol) {
        int searchListSize = stockSearchList.size();

        if (searchListSize == 0) {
            reportSymbolNotFound(stockSymbol);
        } else if (searchListSize == 1) {
            addPickedStock(stockSearchList.get(0).getSymbol());
        } else {
            showSearchListDialog(stockSearchList);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void showSearchListDialog(List<Stock> stockSearchList) {
        // List selection dialog
        int searchListSize = stockSearchList.size();

        //ake an array of strings
        final CharSequence[] sArray = new CharSequence[searchListSize];
        for (int i = 0; i < searchListSize; i++)
            sArray[i] = stockSearchList.get(i).getSymbol() + "-" + stockSearchList.get(i).getCompanyName();

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Make a selection");

        // Set the builder to display the string array as a selectable
        // list, and add the "onClick" for when a selection is made
        builder.setItems(sArray, (dialog, which) -> addPickedStock(sArray[which]));

        builder.setNegativeButton("Nevermind", (dialog, id) -> Toast.makeText(MainActivity.this, "You changed your mind!", Toast.LENGTH_SHORT).show());
        AlertDialog dialog = builder.create();

        dialog.show();
    }

    private void reportSymbolNotFound(String stockSymbol) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Symbol not Found: " + stockSymbol);
        builder.setMessage("Data for stock symbol");
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void addPickedStock(CharSequence charSequence) {
        Stock stock = getStockFromSource(charSequence);

        if(stockDuplicate(stock)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setIcon(R.drawable.baseline_report_problem_24);
            builder.setTitle("Duplicate Stock");
            builder.setMessage("Stock symbol " + charSequence +" is already displayed");
        } else {
            stockList.add(stock);
            stockAdapter.notifyDataSetChanged();
            writeJSONData();
            Toast.makeText(MainActivity.this, "Stock picked: " + charSequence, Toast.LENGTH_SHORT).show();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void writeJSONData() {
        try {
            FileOutputStream fos = getApplicationContext().
                    openFileOutput(getString(R.string.notes_file), Context.MODE_PRIVATE);

            JsonWriter writer = new JsonWriter(new OutputStreamWriter(fos, StandardCharsets.UTF_8));
            writer.setIndent("  ");
            writer.beginArray();
            for (Stock s: stockList) {
                writer.beginObject();
                writer.name("symbol").value(s.getSymbol());
                writer.name("companyName").value(s.getCompanyName());
                writer.name("latestPrice").value(s.getLatestPrice());
                writer.name("change").value(s.getChange());
                writer.name("changePercent").value(s.getChangePercent());
                writer.endObject();
            }
            writer.endArray();
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("MainActivity", "writeJSONData: " + e.getMessage());
        }
    }

    private void readJSONData() {
        try {
            FileInputStream fis = getApplicationContext().
                    openFileInput(getString(R.string.notes_file));

            // Read string content from file
            byte[] data = new byte[(int) fis.available()]; // this technique is good for small files
            int loaded = fis.read(data);
            Log.d("MainActivity", "readJSONData: Loaded " + loaded + " bytes");
            fis.close();
            String json = new String(data);

            // Create JSON Array from string file content
            JSONArray noteArr = new JSONArray(json);
            for (int i = 0; i < noteArr.length(); i++) {
                JSONObject nObj = noteArr.getJSONObject(i);

                // Access note data fields
                String symbol = nObj.getString("symbol");
                String companyName = nObj.getString("companyName");
                Double latestPrice = nObj.getDouble("latestPrice");
                Double change = nObj.getDouble("change");
                Double changePercent = nObj.getDouble("changePercent");


                // Create Note and add to ArrayList
                Stock s = new Stock(symbol, companyName, latestPrice, change, changePercent);
                stockList.add(s);
            }
            Log.d("MainActivity", "readJSONData: " + stockList);

        } catch (Exception e) {
            e.printStackTrace();
            Log.d("MainActivity", "readJSONData: " + e.getMessage());
        }
    }

    private boolean stockDuplicate(Stock newStock) {
        for (Stock stock: stockList) {
            if (newStock.getSymbol().contentEquals(stock.getSymbol())) {
                return true;
            }
        }
        return false;
    }

    private Stock getStockFromSource(CharSequence charSequence) {
        Stock stock = new Stock();
        return stock;
    }

    @Override
    public void onClick(View v) {
        int pos = recyclerView.getChildLayoutPosition(v);
        Stock s = stockList.get(pos);

        Toast.makeText(v.getContext(), "SHORT " + s.getSymbol(), Toast.LENGTH_SHORT).show();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
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
            writeJSONData();
            Toast.makeText(MainActivity.this, "Stock deleted!", Toast.LENGTH_SHORT).show();
        });

        builder.setNegativeButton("CANCEL", (dialog, which) -> Toast.makeText(MainActivity.this, "You changed your mind!", Toast.LENGTH_SHORT).show());

        AlertDialog dialog = builder.create();
        dialog.show();
        return false;
    }

}