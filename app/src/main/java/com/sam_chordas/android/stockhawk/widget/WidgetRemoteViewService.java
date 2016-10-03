package com.sam_chordas.android.stockhawk.widget;

import android.content.Intent;
import android.database.Cursor;
import android.os.Binder;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.data.QuoteColumns;
import com.sam_chordas.android.stockhawk.data.QuoteProvider;
import com.sam_chordas.android.stockhawk.rest.Utils;

/**
 * Created by MeMyself on 9/28/2016.
 */
//Implementation of this code was taken from the widget remote service view implementation in the Sunshine weather app
//of the Advanced Android Development course
//https://github.com/udacity/Advanced_Android_Development/blob/master/app/src/main/java/com/example/android/sunshine/app/widget/DetailWidgetRemoteViewsService.java
public class WidgetRemoteViewService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new RemoteViewsFactory() {
            private Cursor data = null;

            @Override
            public void onCreate() {
                // Nothing to do
            }

            @Override
            public void onDataSetChanged() {
                if (data != null) {
                    data.close();
                }
                // This method is called by the app hosting the widget (e.g., the launcher)
                // However, our ContentProvider is not exported so it doesn't have access to the
                // data. Therefore we need to clear (and finally restore) the calling identity so
                // that calls use our process and permission
                final long identityToken = Binder.clearCallingIdentity();
                data = getContentResolver().query(QuoteProvider.Quotes.CONTENT_URI,
                        new String[]{QuoteColumns._ID, QuoteColumns.SYMBOL, QuoteColumns.BIDPRICE,
                                QuoteColumns.PERCENT_CHANGE, QuoteColumns.CHANGE, QuoteColumns.ISUP},
                        QuoteColumns.ISCURRENT + " = ?",
                        new String[]{"1"},
                        null);
                Binder.restoreCallingIdentity(identityToken);
            }

            @Override
            public void onDestroy() {
                if (data != null) {
                    data.close();
                    data = null;
                }
            }

            @Override
            public int getCount() {
                return data == null ? 0 : data.getCount();
            }

            @Override
            public RemoteViews getViewAt(int position) {
                if (position == AdapterView.INVALID_POSITION ||
                        data == null || !data.moveToPosition(position)) {
                    return null;
                }
                RemoteViews views = new RemoteViews(getPackageName(),
                        R.layout.widget_list_item);
                views.setTextViewText(R.id.widget_stock_symbol, data.getString(data.getColumnIndex("symbol")));
                views.setContentDescription(R.id.widget_stock_symbol, getString(R.string.a11y_stock_symbol, data.getString(data.getColumnIndex("symbol"))));
                views.setTextViewText(R.id.widget_bid_price, data.getString(data.getColumnIndex("bid_price")));
                views.setContentDescription(R.id.widget_bid_price, getString(R.string.a11y_bid_price, data.getString(data.getColumnIndex("bid_price"))));

                if (data.getInt(data.getColumnIndex("is_up")) == 1) {
                    views.setTextColor(R.id.widget_change, getResources().getColor(R.color.material_green_700));
                } else {
                    views.setTextColor(R.id.widget_change, getResources().getColor(R.color.material_red_700));
                }
                if (Utils.showPercent) {
                    views.setTextViewText(R.id.widget_change, data.getString(data.getColumnIndex("percent_change")));
                    views.setContentDescription(R.id.widget_change, getString(R.string.a11y_percent_change, data.getString(data.getColumnIndex("percent_change"))));
                } else {
                    views.setTextViewText(R.id.widget_change, data.getString(data.getColumnIndex("change")));
                    views.setContentDescription(R.id.widget_change, getString(R.string.a11y_percent_change, data.getString(data.getColumnIndex("change"))));
                }
                final Intent fillInIntent = new Intent();
                fillInIntent.putExtra("quoteSymbol", data.getString(data.getColumnIndex("symbol")));
                views.setOnClickFillInIntent(R.id.widget_proper_list_item, fillInIntent);
                return views;
            }

            @Override
            public RemoteViews getLoadingView() {
                return new RemoteViews(getPackageName(), R.layout.widget_list_item);
            }

            @Override
            public int getViewTypeCount() {
                return 1;
            }

            @Override
            public long getItemId(int position) {
                if (data.moveToPosition(position))
                    return data.getLong(data.getColumnIndex("_id"));
                return position;
            }

            @Override
            public boolean hasStableIds() {
                return true;
            }
        };
    }
}
