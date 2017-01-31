package com.focustech.electronicbrand.capabilities.cache;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * <sharepref基础存储功能模块>
 *
 * @author caoyinfei
 * @version [版本号, 2016/6/6]
 * @see [相关类/方法]
 * @since [V1]
 */
public class BaseSharedPreference {
    private String fileName;

    private Context context;

    public BaseSharedPreference(Context context, String fileName) {
        this.fileName = fileName;
        this.context = context;
    }

    /**
     * Retrieve the package shared preferences object.
     *
     * @return
     */
    private SharedPreferences getSharedPreferences() {
        return context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
    }

    public void saveBoolean(String key, boolean value) {
        getSharedPreferences().edit().putBoolean(key, value).commit();
    }

    public boolean getBoolean(String key, boolean defvalue) {
        return getSharedPreferences().getBoolean(key, defvalue);
    }

    /**
     * Save a string value to the shared preference.
     *
     * @param key   to mark the store value.
     * @param value to saved value.
     */
    public void saveString(String key, String value) {
        getSharedPreferences().edit().putString(key, value).commit();
    }

    /**
     * Get the specified value through the key value.
     *
     * @param key to retrieve the value.
     * @return the string value returned.
     */
    public String getString(String key, String def) {
        return getSharedPreferences().getString(key, def);
    }

    /**
     * Save a integer value to the shared preference.
     *
     * @param key   to mark the store value.
     * @param value to saved value.
     */
    public void saveInt(String key, int value) {
        getSharedPreferences().edit().putInt(key, value).commit();

    }

    /**
     * Get the specified value through the key value.
     *
     * @param key to retrieve the value.
     * @return the integer value returned.
     */
    public int getInt(String key, int def) {
        return getSharedPreferences().getInt(key, def);
    }

    /**
     * Save a Long value to the shared preference.
     *
     * @param key   to mark the store value.
     * @param value to saved value.
     */
    public void saveLong(String key, long value) {
        getSharedPreferences().edit().putLong(key, value).commit();
    }

    /**
     * Get the specified value through the key value.
     *
     * @param key to retrieve the value.
     * @return the integer value returned.
     */
    public long getLong(String key, long def) {
        return getSharedPreferences().getLong(key, def);
    }
}
