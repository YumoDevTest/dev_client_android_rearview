package com.mapbar.obd.foundation.mvp;

import android.os.Bundle;

/**
 * 基础 MVP presenter。一般情况下，MVP模式下的presenter需要继承自这个类
 * Created by zhangyunfei on 16/8/3.
 */
public abstract class BasePresenter<T extends IMvpView> {
    private T view;
    private Bundle bundle;

    public BasePresenter(T view) {
        this.view = view;
    }

    public BasePresenter(T view, Bundle bundle) {
        this.view = view;
        this.bundle = bundle;
    }

    public Bundle getBundle() {
        return bundle;
    }

    /**
     * 设置视图
     *
     * @param view
     */
    public void setView(T view) {
        this.view = view;
    }

    public T getView() {
        return view;
    }

    public abstract void clear();



}
