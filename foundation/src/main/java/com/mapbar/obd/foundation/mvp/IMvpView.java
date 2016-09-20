package com.mapbar.obd.foundation.mvp;

import android.content.Context;


/**
 * 基础 MVP 视图。一般情况下，MVP模式下的视图需要继承自这个类
 * Created by zhangyunfei on 16/8/3.
 */
public interface IMvpView {

    /**
     * Toast提醒
     * @param msg
     */
    public void alert(String msg);

    /**
     * Toast提醒
     * @param sourceID
     */
    public void alert(int sourceID);

    Context getContext();


}
