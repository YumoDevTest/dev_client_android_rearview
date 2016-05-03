package com.mapbar.android.obd.rearview.framework.preferences.item;

import com.mapbar.android.obd.rearview.framework.preferences.SharedPreferencesWrapper;

/**
 * @author jingzuo
 */
public class FloatPreferences extends BasePreferences {

    private float defaultValue;

    public FloatPreferences(SharedPreferencesWrapper sharedPreferencesWrapper, String sharedPreferencesKey, float sharedPreferencesDefaultValue) {
        super(sharedPreferencesWrapper, sharedPreferencesKey);
        defaultValue = sharedPreferencesDefaultValue;
    }

    public void set(float value) {
        getSharedPreferences().edit().putFloat(getSharedPreferencesKey(), value).commit();
    }

    public float get() {
        return getSharedPreferences().getFloat(getSharedPreferencesKey(), getDefaultValue());
    }

    public float getDefaultValue() {
        return defaultValue;
    }
}
