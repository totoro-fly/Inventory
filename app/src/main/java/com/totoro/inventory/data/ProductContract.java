package com.totoro.inventory.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by totoro-fly on 2017/10/25.
 */

public final class ProductContract {
    public static final String CONTENT_AUTHORITY = "com.totoro.inventory";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_PRODUTC = "product";

    public final static class ProductEntry implements BaseColumns {
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_PRODUTC);
        public static final String CENTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PRODUTC;
        public static final String CENTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PRODUTC;

        public final static String TABLE_NAME = "product";
        public final static String _ID = BaseColumns._ID;
        public final static String COLUMN_PRODUCT_NAME = "name";
        public final static String COLUMN_PRODUCT_AMOUNT = "amount";
        public final static String COLUME_PRODUCT_PRICE = "price";
        public final static String COLUMN_PRODUCT_SALE = "sale";
        public final static String COLUMN_PRODUCT_IMAGE = "image";
    }

}
