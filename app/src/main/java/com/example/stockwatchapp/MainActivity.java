package com.example.stockwatchapp;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
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

import com.example.stockwatchapp.apis.StockDownloader;
import com.example.stockwatchapp.apis.StockSymbolNameDownloader;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, View.OnLongClickListener {

    private static final String TAG = "MainActivity";
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

        // Load the initial data
        StockSymbolNameDownloader rd = new StockSymbolNameDownloader();
        new Thread(rd).start();

        readJSONData();

    }

    private void doRefresh() {
        final List<String> updateList = new ArrayList<>();

        for (Stock stock: stockList) {
            String sym = stock.getSymbol() + "-" + stock.getCompanyName();
            updateList.add(sym);
        }

        stockList.clear();

        for (String sym: updateList) {
            doSelection(sym);
        }

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
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        final EditText et = new EditText(this);
        et.setInputType(InputType.TYPE_CLASS_TEXT);
        et.setGravity(Gravity.CENTER_HORIZONTAL);
        builder.setView(et);

        builder.setPositiveButton("OK", (dialog, id) -> {
            String stockSymbol = et.getText().toString().trim();

            final ArrayList<String> stockSearchList = StockSymbolNameDownloader.findMatches(stockSymbol);

            listSelectionDialog(stockSearchList, stockSymbol);

            Toast.makeText(MainActivity.this, "Symbol sent!", Toast.LENGTH_SHORT).show();
        });
        builder.setNegativeButton("CANCEL", (dialog, id) -> Toast.makeText(MainActivity.this, "You changed your mind!", Toast.LENGTH_SHORT).show());

        builder.setMessage("Please enter a Stock Symbol:");
        builder.setTitle("Stock Selection");

        AlertDialog dialog = builder.create();
        dialog.show();
    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void listSelectionDialog(List<String> stockSearchList, String stockSymbol) {
        int searchListSize = stockSearchList.size();

        if (searchListSize == 0) {
            reportSymbolNotFound(stockSymbol);
        } else if (searchListSize == 1) {
            doSelection(stockSearchList.get(0));
        } else {
            showSearchListDialog(stockSearchList);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void showSearchListDialog(List<String> stockSearchList) {
        int searchListSize = stockSearchList.size();

        final CharSequence[] sArray = new CharSequence[searchListSize];
        for (int i = 0; i < searchListSize; i++)
            sArray[i] = stockSearchList.get(i);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Make a selection");

        builder.setItems(sArray, (dialog, which) -> {
            String symbol = stockSearchList.get(which);
            doSelection(symbol);
        });

        builder.setNegativeButton("Nevermind", (dialog, id) -> Toast.makeText(MainActivity.this, "You changed your mind!", Toast.LENGTH_SHORT).show());
        AlertDialog dialog = builder.create();

        dialog.show();
    }

    private void doSelection(String sym) {
        String[] data = sym.split("-");
        StockDownloader countryDownloader = new StockDownloader(this, data[0].trim());
        new Thread(countryDownloader).start();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void addPickedStock(Stock stock, String sym) {
        if (stock == null) {
            reportSymbolNotFound(sym);
            return;
        }

        if(stockDuplicate(stock)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setIcon(R.drawable.baseline_report_problem_24);
            builder.setTitle("Duplicate Stock");
            builder.setMessage("Stock symbol " + sym +" is already displayed");
            AlertDialog dialog = builder.create();
            dialog.show();
            return;
        }

        stockList.add(stock);
        stockAdapter.notifyDataSetChanged();
        writeJSONData();
    }

    private void reportSymbolNotFound(String stockSymbol) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Symbol not Found: " + stockSymbol);
        builder.setMessage("Data for stock symbol");

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private boolean stockDuplicate(Stock newStock) {
        for (Stock stock: stockList) {
            if (newStock.getSymbol().contentEquals(stock.getSymbol())) {
                return true;
            }
        }
        return false;
    }


    @Override
    public void onClick(View v) {
        int pos = recyclerView.getChildLayoutPosition(v);
        Stock s = stockList.get(pos);

        String url = "https://www.marketwatch.com/investing/stock/" + s.getSymbol();
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);

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

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onPause() {
        super.onPause();
        writeJSONData();
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
            Log.d(TAG, "writeJSONData: " + e.getMessage());
        }
    }

    private void readJSONData() {
        try {
            FileInputStream fis = getApplicationContext().
                    openFileInput(getString(R.string.notes_file));

            // Read string content from file
            byte[] data = new byte[(int) fis.available()]; // this technique is good for small files
            int loaded = fis.read(data);
            Log.d(TAG, "readJSONData: Loaded " + loaded + " bytes");
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
            Log.d(TAG, "readJSONData: " + stockList);

        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "readJSONData: " + e.getMessage());
        }
    }

}