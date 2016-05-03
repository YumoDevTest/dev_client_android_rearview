package com.mapbar.android.obd.rearview.framework.preferences.item;

import com.mapbar.android.obd.rearview.framework.preferences.SharedPreferencesWrapper;

/**
 * @author jingzuo
 */
public class LongPreferences extends BasePreferences {

    private long defaultValue;

    public LongPreferences(SharedPreferencesWrapper sharedPreferencesWrapper, String sharedPreferencesKey, long sharedPreferencesDefaultValue) {
        super(sharedPreferencesWrapper, sharedPreferencesKey);
        defaultValue = sharedPreferencesDefaultValue;
    }

    public void set(long value) {
        getSharedPreferences().edit().putLong(getSharedPreferencesKey(), value).commit();
    }

    public long get() {
        return getSharedPreferences().getLong(getSharedPreferencesKey(), getDefaultValue());
    }

    public long getDefaultValue() {
        return defaultValue;
    }
}
