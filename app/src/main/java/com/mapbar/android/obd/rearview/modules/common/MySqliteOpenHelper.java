package com.mapbar.android.obd.rearview.modules.common;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.mapbar.android.obd.rearview.R;
import com.mapbar.android.obd.rearview.obd.util.MyFileUtil;
import com.mapbar.obd.FileUtils;

import java.io.InputStream;

/**
 * Created by zhangyunfei on 16/8/9.
 */
public class MySqliteOpenHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "obd_permission.db";
    private Context context;

    public MySqliteOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    static String SQL_CREATE_TABLE = "";

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        try {
            InputStream inputStream = context.getResources().openRawResource(R.raw.sql_create_table_1);
            String str = MyFileUtil.readAll(inputStream);
            sqLiteDatabase.execSQL(str);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
