package com.example.pc.mylauncher.data;

import android.provider.BaseColumns;

/**
 * Created by PC on 25.04.2017.
 */

public class AppsListContract {

    public static final class AppsListEntry implements BaseColumns {
        public static final String TABLE_NAME = "appslist";
        public static final String COLUMN_APP_PACKAGENAME = "packagename";
        public static final String COLUMN_APP_CLICKS = "clicks";
        public static final String COLUMN_APP_IS_FAVOURITE = "isFavourite";
    }

    public static final class URLEntry implements BaseColumns {
        public static final String TABLE_NAME = "URLlist";
        public static final String COLUMN_URL = "URL";
        public static final String COLUMN_TIMESTAMP = "timestamp";
    }
}
