package com.mapbar.android.obd.rearview.modules.permission.repo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.mapbar.android.obd.rearview.modules.common.BaseDao;
import com.mapbar.android.obd.rearview.modules.permission.model.MyPermissionInfo;
import com.mapbar.android.obd.rearview.obd.util.Cache;
import com.mapbar.android.obd.rearview.obd.util.LogUtil;
import com.mapbar.android.obd.rearview.obd.util.MemoryCache;
import com.mapbar.box.protobuf.bean.ObdRightBean;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * 权限信息的持久化仓库
 * Created by zhangyunfei on 16/8/9.
 */
public class PermissionRepository extends BaseDao {
    private static final String TAG = PermissionRepository.class.getSimpleName();
    private Cache mPermissionListCache;
    private static final String CACHE_KEY_GET_PERMISSON_LIST = "CACHE_KEY_GET_PERMISSON_LIST";

    private WeakReference<Context> context;
    private ChangedListener changedListener;

    public PermissionRepository(Context context1) {
        super(context1);
        this.context = new WeakReference<>(context1);
        mPermissionListCache = new MemoryCache();
    }

    public void saveAndReplacePermission(List<ObdRightBean.ObdRight> obdRightList) throws Exception {
        if (context == null || context.get() == null)
            throw new Exception();
        LogUtil.d(TAG, "## 准备 保存权限到本地数据库");
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
        SQLiteDatabase db = getDB();
        try {
            db.beginTransaction();

            db.execSQL("delete from MyPermissionInfo");
            for (int i = 0; i < newPermissionInfoList.size(); i++) {
                MyPermissionInfo item1 = newPermissionInfoList.get(i);
                ContentValues contentValues = new ContentValues();
                contentValues.put("productId", item1.getProductId());
                contentValues.put("producteStatus", item1.getProducteStatus());
                contentValues.put("deadline", item1.getDeadline());
                db.insert("MyPermissionInfo", null, contentValues);
            }
            db.setTransactionSuccessful();

            notifyChanged();
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (db != null) {
                db.endTransaction();
                db.close();
            }
        }
    }

    public List<ObdRightBean.ObdRight> getPermissonList() {
        if (mPermissionListCache.exist(CACHE_KEY_GET_PERMISSON_LIST)) {
//            LogUtil.d(TAG, "## 准备从内存缓存读取 getPermissonList");
            try {
                return (List<ObdRightBean.ObdRight>) mPermissionListCache.getCache(CACHE_KEY_GET_PERMISSON_LIST);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        LogUtil.d(TAG, "## 准备从数据库读取 getPermissonList");
        SQLiteDatabase db = getReadableDB();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("select * from MyPermissionInfo", null);
            List<ObdRightBean.ObdRight> lst = new ArrayList<>();
            while (cursor.moveToNext()) {
                String productId = cursor.getString(cursor.getColumnIndex("productId"));
                int producteStatus = cursor.getInt(cursor.getColumnIndex("producteStatus"));
                String deadline = cursor.getString(cursor.getColumnIndex("deadline"));

                ObdRightBean.ObdRight tmp = ObdRightBean.ObdRight.newBuilder()
                        .setProductId(productId)
                        .setProducteStatus(producteStatus)
                        .setDeadline(deadline).build();
                lst.add(tmp);
            }
            mPermissionListCache.cache(CACHE_KEY_GET_PERMISSON_LIST, lst);
            return lst;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        } finally {
            if (cursor != null)
                cursor.close();
            if (db != null)
                db.close();
        }
    }

    public void clearCache() {
        LogUtil.d(TAG, "## 清理缓存");
        mPermissionListCache.clear();
        notifyChanged();
    }

    private void notifyChanged() {
        if (changedListener != null)
            changedListener.onChanged();
    }

    /**
     * 设置一个监听，本地数据发生了变化
     * @param changedListener
     */
    public void setChangedListener(ChangedListener changedListener) {
        this.changedListener = changedListener;
    }

    /**
     * 权限存储的 变更监听器
     */
    public interface ChangedListener {
        void onChanged();
    }
}
