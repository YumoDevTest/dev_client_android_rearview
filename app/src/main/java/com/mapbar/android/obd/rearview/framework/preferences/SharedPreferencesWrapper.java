package com.mapbar.android.obd.rearview.framework.preferences; /**
 *
 */


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 */
public class SharedPreferencesWrapper implements SharedPreferences {

    /**
     *
     */
    private SharedPreferences sharedPreferences;
    /**
     *
     */
    private SharedPreferenceChangeListener sharedPreferenceChangeListener;

    /**
     * 暂时放开权限为public 后续再考虑优化    FIXME
     */
    public SharedPreferencesWrapper(Context context, String name, int mode) {
        sharedPreferences = context.getSharedPreferences(name, mode);
    }

    /**
     * @return
     * @see SharedPreferences#getAll()
     */
    @Override
    public Map<String, ?> getAll() {
        return sharedPreferences.getAll();
    }

    /**
     * @param key
     * @param defValue
     * @return
     * @see SharedPreferences#getString(String, String)
     */
    @Override
    public String getString(String key, String defValue) {
        return sharedPreferences.getString(key, defValue);
    }

    /**
     * API level=11
     *
     * @param key
     * @param defValues
     * @return
     * @see SharedPreferences#getStringSet(String, Set)
     */
    @SuppressLint("NewApi")
    @Override
    public Set<String> getStringSet(String key, Set<String> defValues) {
        return sharedPreferences.getStringSet(key, defValues);
    }

    /**
     * @param key
     * @param defValue
     * @return
     * @see SharedPreferences#getInt(String, int)
     */
    @Override
    public int getInt(String key, int defValue) {
        return sharedPreferences.getInt(key, defValue);
    }

    /**
     * @param key
     * @param defValue
     * @return
     * @see SharedPreferences#getLong(String, long)
     */
    @Override
    public long getLong(String key, long defValue) {
        return sharedPreferences.getLong(key, defValue);
    }

    /**
     * @param key
     * @param defValue
     * @return
     * @see SharedPreferences#getFloat(String, float)
     */
    @Override
    public float getFloat(String key, float defValue) {
        return sharedPreferences.getFloat(key, defValue);
    }

    /**
     * @param key
     * @param defValue
     * @return
     * @see SharedPreferences#getBoolean(String, boolean)
     */
    @Override
    public boolean getBoolean(String key, boolean defValue) {
        return sharedPreferences.getBoolean(key, defValue);
    }

    /**
     * @param key
     * @return
     * @see SharedPreferences#contains(String)
     */
    @Override
    public boolean contains(String key) {
        return sharedPreferences.contains(key);
    }

    /**
     * @return
     * @see SharedPreferences#edit()
     */
    @Override
    public Editor edit() {
        return sharedPreferences.edit();
    }

    /**
     * @param listener
     * @see SharedPreferences#registerOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener)
     */
    @Override
    public void registerOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener listener) {
        if (null == sharedPreferenceChangeListener) {
            sharedPreferenceChangeListener = new SharedPreferenceChangeListener(sharedPreferences);
        }
        sharedPreferenceChangeListener.add(listener);
    }

    /**
     * @param listener
     * @see SharedPreferences#unregisterOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener)
     */
    @Override
    public void unregisterOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener listener) {
        // 弱引用不需要注销
    }

    /**
     *
     */
    private static final class SharedPreferenceChangeListener implements OnSharedPreferenceChangeListener {

        private List<WeakReference<OnSharedPreferenceChangeListener>> listeners = new ArrayList<WeakReference<OnSharedPreferenceChangeListener>>();

        /**
         *
         */
        private SharedPreferenceChangeListener(SharedPreferences sharedPreferences) {
            sharedPreferences.registerOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            for (Iterator<WeakReference<OnSharedPreferenceChangeListener>> iterator = listeners.iterator(); iterator.hasNext(); ) {
                WeakReference<OnSharedPreferenceChangeListener> r = iterator.next();
                OnSharedPreferenceChangeListener l = r.get();
                if (null != l) {
                    l.onSharedPreferenceChanged(sharedPreferences, key);
                } else {
                    iterator.remove();
                }
            }
        }

        public void add(OnSharedPreferenceChangeListener listener) {
            listeners.add(new WeakReference<OnSharedPreferenceChangeListener>(listener));
        }

    }

}
