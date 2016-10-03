package com.sam_chordas.android.stockhawk.ui;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.WindowManager;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.data.QuoteColumns;
import com.sam_chordas.android.stockhawk.data.QuoteProvider;

import java.util.ArrayList;

public class LineGraphActivity extends Activity implements LoaderManager.LoaderCallbacks<Cursor>{
    private LineChart mChart;
    private float[] prices;
    private String symbol;
    private static final int CURSOR_LOADER_ID = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_line_graph);

        mChart = (LineChart) findViewById(R.id.linechart);

        Intent intent = getIntent();
        if (intent.hasExtra("quoteSymbol")){
            symbol = intent.getStringExtra("quoteSymbol");
        }

        getLoaderManager().initLoader(CURSOR_LOADER_ID, null, this);

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args){
        // This narrows the return to only the stocks that are most current.
        return new CursorLoader(this, QuoteProvider.Quotes.CONTENT_URI,
                new String[]{QuoteColumns.BIDPRICE}, QuoteColumns.SYMBOL + "= ?",
                new String[]{symbol}, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor.getCount() != 0) {
            prices = new float[cursor.getCount()];
            cursor.moveToFirst();
            for (int i = 0; i < cursor.getCount(); i++) {
                prices[i] = Float.parseFloat(cursor.getString(cursor.getColumnIndex(QuoteColumns.BIDPRICE)));
                cursor.moveToNext();
            }
            ArrayList<String> xVals = new ArrayList<>();
            ArrayList<Entry> yVals = new ArrayList<>();

            for (int i = 0; i < prices.length; i++){
                xVals.add(Integer.toString(i + 1));
                yVals.add(new Entry(prices[i], i));
            }

            LineDataSet set1 = new LineDataSet(yVals, getString(R.string.y_axis_label, symbol));
            set1.setFillAlpha(110);
            set1.setColor(Color.WHITE);
            set1.setCircleColor(Color.WHITE);
            set1.setLineWidth(1f);
            set1.setCircleRadius(3f);
            set1.setDrawCircleHole(false);
            set1.setValueTextSize(9f);
            set1.setDrawFilled(true);

            ArrayList<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
            dataSets.add(set1);

            // create a data object with the datasets
            LineData data = new LineData(xVals, dataSets);

            mChart.setData(data);
            mChart.setDescriptionTextSize(14f);
            Legend l = mChart.getLegend();
            l.setPosition(Legend.LegendPosition.RIGHT_OF_CHART_INSIDE);
            l.setForm(Legend.LegendForm.LINE);

            mChart.setDescription(getString(R.string.line_chart_name, symbol));
            mChart.setNoDataTextDescription(getString(R.string.empty_line_chart_message));

            mChart.invalidate();
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
