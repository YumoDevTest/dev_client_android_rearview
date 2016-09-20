package com.mapbar.android.obd.rearview.lib.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by zhangyunfei on 16/8/9.
 */
public class BaseDao {
    MySqliteOpenHelper sqliteOpenHelper;

    public BaseDao(Context context) {
        this.sqliteOpenHelper = new MySqliteOpenHelper(context);
    }

    public SQLiteDatabase getDB() {
        return sqliteOpenHelper.getWritableDatabase();
    }


    public SQLiteDatabase getReadableDB() {
        return sqliteOpenHelper.getReadableDatabase();
    }
}
