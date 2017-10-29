package com.totoro.inventory.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.totoro.inventory.R;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

/**
 * Created by totoro-fly on 2017/10/25.
 */

public class ProductProvider extends ContentProvider {

    private ProductDbHelper productDbHelper;
    private static final int PRODUCT = 1;
    private static final int PRODUCT_ID = 2;
    private static final UriMatcher URIMATCHER = new UriMatcher(android.content.UriMatcher.NO_MATCH);

    static {
        URIMATCHER.addURI(ProductContract.CONTENT_AUTHORITY, ProductContract.PATH_PRODUTC, PRODUCT);
        URIMATCHER.addURI(ProductContract.CONTENT_AUTHORITY, ProductContract.PATH_PRODUTC + "/#", PRODUCT_ID);
    }

    @Override
    public boolean onCreate() {
        productDbHelper = new ProductDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        SQLiteDatabase db = productDbHelper.getReadableDatabase();
        Cursor cursor;
        int match = URIMATCHER.match(uri);
        switch (match) {
            case PRODUCT:
                cursor = db.query(ProductContract.ProductEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case PRODUCT_ID:
                selection = ProductContract.ProductEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = db.query(ProductContract.ProductEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException(getContext().getString(R.string.null_query) + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        int match = URIMATCHER.match(uri);
        switch (match) {
            case PRODUCT:
                return ProductContract.ProductEntry.CENTENT_LIST_TYPE;
            case PRODUCT_ID:
                return ProductContract.ProductEntry.CENTENT_ITEM_TYPE;
            default:
                throw new IllegalArgumentException("gettype");
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        int match = URIMATCHER.match(uri);
        switch (match) {
            case PRODUCT:
                if (isValue(values)) {
                    return insertProduct(uri, values);
                }
                return null;
            default:
                throw new IllegalArgumentException(getContext().getString(R.string.null_insert) + uri);
        }
    }

    private Uri insertProduct(Uri uri, ContentValues values) {
        SQLiteDatabase db = productDbHelper.getWritableDatabase();
        long id = db.insert(ProductContract.ProductEntry.TABLE_NAME, null, values);
        if (id == -1) {
            return null;
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return ContentUris.withAppendedId(uri, id);
    }

    private boolean isValue(ContentValues values) {
        String name = values.getAsString(ProductContract.ProductEntry.COLUMN_PRODUCT_NAME);
        if (name.isEmpty()) {
            Toast.makeText(getContext(), R.string.write_name, Toast.LENGTH_LONG).show();
            return false;
        }
        if (values.containsKey(ProductContract.ProductEntry.COLUMN_PRODUCT_AMOUNT)) {
            String amount = values.getAsString(ProductContract.ProductEntry.COLUMN_PRODUCT_AMOUNT);
            if (amount.isEmpty()) {
                values.put(ProductContract.ProductEntry.COLUMN_PRODUCT_AMOUNT, 0);
            }
        }
        if (values.containsKey(ProductContract.ProductEntry.COLUME_PRODUCT_PRICE)) {
            String price = values.getAsString(ProductContract.ProductEntry.COLUME_PRODUCT_PRICE);
            if (price.isEmpty()) {
                values.put(ProductContract.ProductEntry.COLUME_PRODUCT_PRICE, 0);
            }
        }
        if (values.containsKey(ProductContract.ProductEntry.COLUMN_PRODUCT_SALE)) {
            String sale = values.getAsString(ProductContract.ProductEntry.COLUMN_PRODUCT_SALE);
            if (sale.isEmpty()) {
                values.put(ProductContract.ProductEntry.COLUMN_PRODUCT_SALE, 0);
            }
        }
        if (values.containsKey(ProductContract.ProductEntry.COLUMN_PRODUCT_IMAGE)) {
            String sale = values.getAsString(ProductContract.ProductEntry.COLUMN_PRODUCT_IMAGE);
            if (sale.isEmpty()) {
                values.put(ProductContract.ProductEntry.COLUMN_PRODUCT_IMAGE, picTobyte(R.drawable.ic_add_shopping_cart_black_48dp));
            }
        }
        return true;
    }

    private byte[] picTobyte(int resourceID) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        InputStream is = getContext().getResources().openRawResource(resourceID);
        Bitmap bitmap = BitmapFactory.decodeStream(is);
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }


    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase db = productDbHelper.getWritableDatabase();
        int rowsDeleted;
        final int match = URIMATCHER.match(uri);
        switch (match) {
            case PRODUCT:
                rowsDeleted = db.delete(ProductContract.ProductEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case PRODUCT_ID:
                selection = ProductContract.ProductEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = db.delete(ProductContract.ProductEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException(getContext().getString(R.string.null_delete) + uri);
        }
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        int match = URIMATCHER.match(uri);
        switch (match) {
            case PRODUCT:
                return updatePet(uri, values, selection, selectionArgs);
            case PRODUCT_ID:
                selection = ProductContract.ProductEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updatePet(uri, values, selection, selectionArgs);
            default:
                throw new IllegalArgumentException(getContext().getString(R.string.null_update) + uri);
        }
    }

    private int updatePet(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        if (values.containsKey(ProductContract.ProductEntry.COLUMN_PRODUCT_NAME)) {
            String name = values.getAsString(ProductContract.ProductEntry.COLUMN_PRODUCT_NAME);
            if (name.isEmpty()) {
                Toast.makeText(getContext(), R.string.write_name, Toast.LENGTH_LONG).show();
                throw new IllegalArgumentException(getContext().getString(R.string.write_name));
            }
        }
        if (values.containsKey(ProductContract.ProductEntry.COLUMN_PRODUCT_AMOUNT)) {
            String amount = values.getAsString(ProductContract.ProductEntry.COLUMN_PRODUCT_AMOUNT);
            if (amount.isEmpty()) {
                Toast.makeText(getContext(), R.string.write_amount, Toast.LENGTH_LONG).show();
                throw new IllegalArgumentException(getContext().getString(R.string.write_amount));

            }
        }
        if (values.containsKey(ProductContract.ProductEntry.COLUME_PRODUCT_PRICE)) {
            String price = values.getAsString(ProductContract.ProductEntry.COLUME_PRODUCT_PRICE);
            if (price.isEmpty()) {
                Toast.makeText(getContext(), R.string.write_price, Toast.LENGTH_LONG).show();
                throw new IllegalArgumentException(getContext().getString(R.string.write_price));

            }
        }
        if (values.containsKey(ProductContract.ProductEntry.COLUMN_PRODUCT_SALE)) {
            String sale = values.getAsString(ProductContract.ProductEntry.COLUMN_PRODUCT_SALE);
            if (sale.isEmpty()) {
                Toast.makeText(getContext(), R.string.write_sale, Toast.LENGTH_LONG).show();
                throw new IllegalArgumentException(getContext().getString(R.string.write_sale));

            }
        }


        if (values.size() == 0) {
            return 0;
        }
        SQLiteDatabase db = productDbHelper.getWritableDatabase();

        int rowsUpdata = db.update(ProductContract.ProductEntry.TABLE_NAME, values, selection, selectionArgs);
        if (rowsUpdata != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdata;
    }

}
