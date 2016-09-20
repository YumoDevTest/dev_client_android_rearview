//package com.mapbar.android.obd.rearview.modules.common;
//
//import android.content.Context;
//
//import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
//import com.j256.ormlite.dao.Dao;
//import com.mapbar.android.obd.rearview.modules.permission.model.MyPermissionInfo;
//
//import java.sql.SQLException;
//
///**
// * Created by zhangyunfei on 16/8/9.
// */
//public class DaoFactory {
//
//    public static OrmLiteSqliteOpenHelper createOrmLiteSqliteOpenHelper(Context context){
//        MyOrmLiteSqliteOpenHelper myOrmLiteSqliteOpenHelper = new MyOrmLiteSqliteOpenHelper(context);
//        return myOrmLiteSqliteOpenHelper;
//    }
//
//    /**
//     * 获得 dao
//     * @param context
//     * @return
//     */
//    public static Dao<MyPermissionInfo, Integer> createPermissionInfoDao(Context context) {
//        try {
//            return createOrmLiteSqliteOpenHelper(context).getDao(MyPermissionInfo.class);
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        return null;
//    }
//}
