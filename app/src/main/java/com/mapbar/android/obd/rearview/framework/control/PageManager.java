package com.mapbar.android.obd.rearview.framework.control;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.mapbar.android.obd.rearview.R;
import com.mapbar.android.obd.rearview.framework.activity.AppPage;
import com.mapbar.android.obd.rearview.framework.activity.BaseActivity;
import com.mapbar.android.obd.rearview.obd.MainActivity;

import java.util.ArrayList;

/**
 * 管理页面跳转
 */
public class PageManager {
    private static PageManager manager;
    private final int containerId = R.id.content_view;
    private BaseActivity mContext;
    private FragmentManager fManager;
    private ArrayList<AppPage> pages;
    private String currentPageName = "";

    private PageManager() {
        mContext = MainActivity.getInstance();
        fManager = mContext.getSupportFragmentManager();
        pages = new ArrayList<>();
    }

    public static PageManager getInstance() {
        if (manager == null) {
            manager = new PageManager();
        }
        return manager;
    }

    public AppPage createPage(Class<? extends AppPage> clazz, Bundle data) {
        try {
            final AppPage appPage = clazz.newInstance();
            appPage.setData(data);
            return appPage;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public AppPage createPage(Class<? extends AppPage> clazz) {
        return createPage(clazz, null);
    }

    /**
     * 跳转到某一Page，会打开一个全新的page
     *
     * @param clazz AppPage.class
     */
    public void goPage(Class<? extends AppPage> clazz) {
        goPage(clazz, null);
    }

    public void goPage(Class<? extends AppPage> clazz, Bundle data) {
        currentPageName = clazz.getName();
        FragmentTransaction transaction = fManager.beginTransaction();
        transaction.setCustomAnimations(R.anim.push_left_in, R.anim.push_left_out, R.anim.push_right_in, R.anim.push_right_out);
        final AppPage page = createPage(clazz, data);
        transaction.replace(containerId, page);
        transaction.commit();
        pages.add(page);
    }

    /**
     * 跳转到某一个page,会打开一个全新的page,并把当前的page销毁(相当于调用goPage+Finish)
     *
     * @param clazz
     * @param bundle
     */
    public void goPageFinish(Class<? extends AppPage> clazz, Bundle bundle) {
        goPage(clazz, bundle);
        finishPage(clazz);
    }

    /**
     * 返回某一特定的page，后退栈中存在相同的page，返回后者，不允许返回不存在的页面
     *
     * @param clazz 返回指定page的Class
     * @return true 返回上一页成功，false 返回上一页失败
     */
    public boolean goBack(Class<? extends AppPage> clazz) {
        if (pages.size() > 1) {
            //FIXME 返回不存在的页面抛出异常
            for (int i = pages.size() - 1; i >= 0; i--) {
                if (pages.get(i).getClass().equals(clazz)) {
                    FragmentTransaction transaction = fManager.beginTransaction();
                    transaction.setCustomAnimations(R.anim.push_right_in, R.anim.push_right_out);
                    transaction.replace(containerId, pages.get(i));
                    currentPageName = clazz.getName();
                    transaction.commit();
                    break;
                }
                pages.remove(i);
            }
            return true;
        } else {
            return false;
        }
    }

    /**
     * 返回上一页
     */
    public boolean goBack() {
        if (pages.size() > 1) {
            return goBack(pages.get(pages.size() - 2).getClass());
        } else {
            return false;
        }
    }

    /**
     * 返回上一页，且带有参数bundle，重写上一页的onRestart方法可以得到返回的data。
     *
     * @param data 返回上一页的参数。
     * @return true, 返回了上一页；false，没有上一页可以返回
     */
    public boolean goBack(Bundle data) {
        if (pages.size() >= 2) {
            AppPage page = pages.get(pages.size() - 2);
            page.setData(data);
            goBack();
            return true;
        } else {
            return false;
        }
    }

    /**
     * 不支持A页面跳转到A页面，也就是自己跳自己之后，销毁前一个A页面
     * 适用场景：A跳到B，A页面移除，B返回A之前的页面，或者退出程序，次方法不会导致页面返回，返回页面调用goBack，不能和goBack同时调用
     *
     * @param clazz 页面跳转到下一页面前，class。
     */
    public boolean finishPage(Class<? extends AppPage> clazz) {
        AppPage page = null;
        final int size = pages.size();
        if (size > 1) {
            if (pages.get(size - 1).getClass().equals(clazz)) {//没有跳转
                page = pages.get(size - 1);
            } else if (pages.get(size - 2).getClass().equals(clazz)) {//finish后跳转新的一页
                page = pages.get(size - 2);
            }
        } else if (size == 1) {
            if (pages.get(0).getClass().equals(clazz)) {
                page = pages.get(0);
            }
        }
        if (page != null) {
            pages.remove(page);
            return true;
        }
        return false;
    }

    /**
     * 清除所有页面
     */
    public void finishAll() {
        pages.clear();
        SDKListenerManager.getInstance().clearListener();
    }

    public Context getmContext() {
        return mContext;
    }

    public ArrayList<AppPage> getPages() {
        return pages;
    }

    public String getCurrentPageName() {
        return currentPageName;
    }

    public static class ManagerHolder {
        public static PageManager pageManager = getInstance();
    }
}
