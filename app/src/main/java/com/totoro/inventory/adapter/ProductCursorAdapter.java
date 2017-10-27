package com.totoro.inventory.adapter;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.totoro.inventory.R;
import com.totoro.inventory.data.ProductContract;

/**
 * Created by totoro-fly on 2017/10/25.
 */

public class ProductCursorAdapter extends CursorAdapter {
    public ProductCursorAdapter(Context context, Cursor c) {
        super(context, c, true);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.item_list, parent, false);

    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView nameTV = (TextView) view.findViewById(R.id.tv_item_name);
        TextView amountTV = (TextView) view.findViewById(R.id.tv_item_amount);
        TextView priceTV = (TextView) view.findViewById(R.id.tv_item_price);
        TextView saleTV = (TextView) view.findViewById(R.id.tv_item_sale);
        int nameIndex = cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_PRODUCT_NAME);
        int amountIndex = cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_PRODUCT_AMOUNT);
        int priceIndex = cursor.getColumnIndex(ProductContract.ProductEntry.COLUME_PRODUCT_PRICE);
        int saleIndex = cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_PRODUCT_SALE);
        String name = cursor.getString(nameIndex);
        String amount = cursor.getString(amountIndex);
        String price = cursor.getString(priceIndex);
        String sale = cursor.getString(saleIndex);
        nameTV.setText(name+" ");
        amountTV.setText(amount+" ");
        priceTV.setText(price+" "); 
        saleTV.setText(sale+" ");
    }
}
