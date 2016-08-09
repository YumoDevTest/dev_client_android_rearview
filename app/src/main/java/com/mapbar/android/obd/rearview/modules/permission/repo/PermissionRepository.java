package com.mapbar.android.obd.rearview.modules.permission.repo;

import android.content.Context;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.misc.TransactionManager;
import com.j256.ormlite.support.ConnectionSource;
import com.mapbar.android.obd.rearview.modules.common.MyOrmLiteSqliteOpenHelper;
import com.mapbar.android.obd.rearview.modules.permission.model.MyPermissionInfo;
import com.mapbar.android.obd.rearview.obd.Application;
import com.mapbar.box.protobuf.bean.ObdRightBean;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * 权限信息的持久化仓库
 * Created by zhangyunfei on 16/8/9.
 */
public class PermissionRepository {
    WeakReference<Context> context;

    public PermissionRepository(Context context1) {
        this.context = new WeakReference<>(context1);
    }

    public void saveAndReplacePermission(List<ObdRightBean.ObdRight> obdRightList) throws Exception {
        if (context == null || context.get() == null)
            throw new Exception();
        final List<MyPermissionInfo> newPermissionInfoList = new ArrayList<>(8);
        MyPermissionInfo tmp;
        for (int i = 0; i < obdRightList.size(); i++) {
            ObdRightBean.ObdRight obdRight = obdRightList.get(i);
            if (obdRight == null) continue;
            tmp = new MyPermissionInfo(obdRight.getProductId(),
                    obdRight.getProducteStatus(),
                    obdRight.getDeadline());
            newPermissionInfoList.add(tmp);
        }
        final MyOrmLiteSqliteOpenHelper myOrmLiteSqliteOpenHelper = new MyOrmLiteSqliteOpenHelper(context.get());
        final ConnectionSource connectionSource = myOrmLiteSqliteOpenHelper.getConnectionSource();
        //事务操作
        try {
            final Dao<MyPermissionInfo, Integer> dao = myOrmLiteSqliteOpenHelper.getDao(MyPermissionInfo.class);

            TransactionManager.callInTransaction(connectionSource,
                    new Callable<Void>() {
                        @Override
                        public Void call() throws Exception {
                            //读旧的
                            List<MyPermissionInfo> oldList = dao.queryForAll();
                            //删旧的
                            dao.delete(oldList);
                            dao.create(newPermissionInfoList);
                            //写新的
                            return null;
                        }
                    });
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                connectionSource.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            myOrmLiteSqliteOpenHelper.close();
        }
    }

    public List<ObdRightBean.ObdRight> getPermissonList() {
        MyOrmLiteSqliteOpenHelper myOrmLiteSqliteOpenHelper = null;// = new MyOrmLiteSqliteOpenHelper(context);
        Dao<MyPermissionInfo, Integer> dao;
        try {
            myOrmLiteSqliteOpenHelper = new MyOrmLiteSqliteOpenHelper(context.get());
            dao = myOrmLiteSqliteOpenHelper.getDao(MyPermissionInfo.class);
            List<MyPermissionInfo> myPermissionInfoList = dao.queryForAll();
            List<ObdRightBean.ObdRight> lst = new ArrayList<>();
            for (int i = 0; i < myPermissionInfoList.size(); i++) {
                MyPermissionInfo item = myPermissionInfoList.get(i);
                ObdRightBean.ObdRight tmp = ObdRightBean.ObdRight.newBuilder()
                        .setProductId(item.getProductId())
                        .setProducteStatus(item.getProducteStatus())
                        .setDeadline(item.getDeadline()).build();
                lst.add(tmp);
            }
            return lst;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        } finally {
            if (myOrmLiteSqliteOpenHelper != null)
                myOrmLiteSqliteOpenHelper.close();
        }

    }
}
