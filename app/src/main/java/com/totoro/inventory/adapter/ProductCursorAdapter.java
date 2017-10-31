package com.totoro.inventory.adapter;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

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
    public void bindView(View view, final Context context, Cursor cursor) {
        TextView nameTV = (TextView) view.findViewById(R.id.tv_item_name);
        final TextView amountTV = (TextView) view.findViewById(R.id.tv_item_amount);
        TextView priceTV = (TextView) view.findViewById(R.id.tv_item_price);
        TextView saleTV = (TextView) view.findViewById(R.id.tv_item_sale);
        Button itemBT = (Button) view.findViewById(R.id.bt_item);
        
        int idIndex = cursor.getColumnIndex(ProductContract.ProductEntry._ID);
        int nameIndex = cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_PRODUCT_NAME);
        int amountIndex = cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_PRODUCT_AMOUNT);
        int priceIndex = cursor.getColumnIndex(ProductContract.ProductEntry.COLUME_PRODUCT_PRICE);
        int saleIndex = cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_PRODUCT_SALE);
        
        String id = cursor.getString(idIndex);
        String name = cursor.getString(nameIndex);
        final String amount = cursor.getString(amountIndex);
        String price = cursor.getString(priceIndex);
        final String sale = cursor.getString(saleIndex);
        final long idLong = Long.parseLong(id);
        
        nameTV.setText(name + " ");
        amountTV.setText(amount + " ");
        priceTV.setText(price + " ");
        saleTV.setText(sale + " ");
        
        itemBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int amountInt = Integer.parseInt(amount);
                amountInt--;
                if (amountInt < 0) {
                    Toast.makeText(context, R.string.amount_zero, Toast.LENGTH_LONG).show();
                    return;
                }
                amountTV.setText(amountInt + " ");
                ContentValues values = new ContentValues();
                values.put(ProductContract.ProductEntry.COLUMN_PRODUCT_AMOUNT, amountInt);
                context.getContentResolver().update(ContentUris.withAppendedId(ProductContract.ProductEntry.CONTENT_URI, idLong), values, null, null);

            }
        });
    }
}
