package com.mapbar.android.obd.rearview.modules.permission.repo;

import com.mapbar.box.protobuf.bean.ObdRightBean;

import java.util.ArrayList;
import java.util.List;

/**
 * 权限信息的持久化仓库
 * Created by zhangyunfei on 16/8/9.
 */
public class PermissionRepository {
    public static List<ObdRightBean.ObdRight> mCache;


    public void savePermission(List<ObdRightBean.ObdRight> obdRightList){
        mCache = new ArrayList<>(obdRightList);
    }

    public List<ObdRightBean.ObdRight> getPermissonList(){
        return mCache;
    }
}
