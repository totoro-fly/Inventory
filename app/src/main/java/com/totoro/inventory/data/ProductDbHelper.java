package com.totoro.inventory.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by totoro-fly on 2017/10/25.
 */

public class ProductDbHelper extends SQLiteOpenHelper {
    private final static String DATABASE_NAME = "product.db";
    private final static int DATABASE_VERSION = 1;

    public ProductDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    

    @Override
    public void onCreate(SQLiteDatabase db) {
        String SQL_CREATE_PET_TABLE = "CREATE TABLE " + ProductContract.ProductEntry.TABLE_NAME + "("
                + ProductContract.ProductEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + ProductContract.ProductEntry.COLUMN_PRODUCT_NAME + " TEXT NOT NULL,"
                + ProductContract.ProductEntry.COLUMN_PRODUCT_AMOUNT + " INTEGER NOT NULL DEFAULT 0,"
                + ProductContract.ProductEntry.COLUME_PRODUCT_PRICE + " DOUBLE NOT NULL DEFAULT 0,"
                + ProductContract.ProductEntry.COLUMN_PRODUCT_SALE + " INTEGER NOT NULL DEFAULT 0);";
        db.execSQL(SQL_CREATE_PET_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
