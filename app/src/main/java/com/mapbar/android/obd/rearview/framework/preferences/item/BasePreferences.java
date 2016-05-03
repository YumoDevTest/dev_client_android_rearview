package com.mapbar.android.obd.rearview.framework.preferences.item; /**
 *
 */


import com.mapbar.android.obd.rearview.framework.preferences.SharedPreferencesWrapper;

/**
 *
 */
public class BasePreferences {

    private SharedPreferencesWrapper sharedPreferencesWrapper;
    private String sharedPreferencesKey;

    BasePreferences(SharedPreferencesWrapper sharedPreferencesWrapper, String sharedPreferencesKey) {
        this.sharedPreferencesWrapper = sharedPreferencesWrapper;
        this.sharedPreferencesKey = sharedPreferencesKey;
    }

    public void remove() {
        getSharedPreferences().edit().remove(sharedPreferencesKey).commit();
    }

    public boolean contains() {
        return getSharedPreferences().contains(sharedPreferencesKey);
    }

    /**
     * @return the {@link #sharedPreferencesKey}
     */
    String getSharedPreferencesKey() {
        return sharedPreferencesKey;
    }

    /**
     * @return
     */
    SharedPreferencesWrapper getSharedPreferences() {
        return sharedPreferencesWrapper;
    }


}
