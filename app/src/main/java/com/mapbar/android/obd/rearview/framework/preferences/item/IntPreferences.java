package com.mapbar.android.obd.rearview.framework.preferences.item;

import com.mapbar.android.obd.rearview.framework.preferences.SharedPreferencesWrapper;

/**
 * @author jingzuo
 */
public class IntPreferences extends BasePreferences {

    private int defaultValue;

    public IntPreferences(SharedPreferencesWrapper sharedPreferencesWrapper, String sharedPreferencesKey, int sharedPreferencesDefaultValue) {
        super(sharedPreferencesWrapper, sharedPreferencesKey);
        defaultValue = sharedPreferencesDefaultValue;
    }

    public void set(int value) {
        getSharedPreferences().edit().putInt(getSharedPreferencesKey(), value).commit();
    }

    public int get() {
        return getSharedPreferences().getInt(getSharedPreferencesKey(), getDefaultValue());
    }

    public int getDefaultValue() {
        return defaultValue;
    }
}
