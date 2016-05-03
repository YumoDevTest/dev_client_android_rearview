package com.mapbar.android.obd.rearview.fram;

import android.view.View;

/**
 * Created by yun on 16/1/7.
 */
public abstract class BaseController<V extends View, M> {

    abstract public void bind(V view, M model);

    public void unBind() {
    }
}
