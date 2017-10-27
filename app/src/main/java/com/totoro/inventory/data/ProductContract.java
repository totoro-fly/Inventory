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
    public static final String PATH_PETS = "product";

    public final static class ProductEntry implements BaseColumns {
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_PETS);
        public static final String CENTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PETS;
        public static final String CENTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PETS;

        public final static String TABLE_NAME = "product";
        public final static String _ID = BaseColumns._ID;
        public final static String COLUMN_PRODUCT_NAME = "name";
        public final static String COLUMN_PRODUCT_AMOUNT = "amount";
        public final static String COLUME_PRODUCT_PRICE = "price";
        public final static String COLUMN_PRODUCT_SALE = "sale";

//        public final static int GENDER_UNKNOEWN = 0;
//        public final static int GENDER_MALE = 1;
//        public final static int GENDER_FEMALE = 2;
//        public static boolean isValidGender(int gender) {
//            if (gender == GENDER_UNKNOEWN || gender == GENDER_MALE || gender == GENDER_FEMALE) {
//                return true;
//            }
//            return false;
//        }
    }

}
