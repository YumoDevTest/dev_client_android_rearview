package com.mapbar.android.obd.rearview.lib.mvp;

/**
 * 基础 MVP presenter。一般情况下，MVP模式下的presenter需要继承自这个类
 * Created by zhangyunfei on 16/8/3.
 */
public abstract class BasePresenter<T extends IMvpView> {
    private T view;



    public BasePresenter(T view) {
        this.view = view;
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
