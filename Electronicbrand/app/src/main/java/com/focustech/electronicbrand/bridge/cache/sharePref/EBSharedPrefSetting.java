package com.focustech.electronicbrand.bridge.cache.sharePref;

import android.content.Context;

import com.focustech.electronicbrand.capabilities.cache.BaseSharedPreference;

/**
 * <设置信息缓存>
 *
 * @author caoyinfei
 * @version [版本号, 2016/6/6]
 * @see [相关类/方法]
 * @since [产品/模块版本]
 */
public class EBSharedPrefSetting extends BaseSharedPreference {
    /**
     * 声音提醒 默认已开启
     */
    public static final String SOUND_REMINDER = "sound_reminder";

    /**
     * 震动提醒 默认已开启
     */
    public static final String VIBRATION_REMINDER = "vibration_reminder";

    public EBSharedPrefSetting(Context context, String fileName) {
        super(context, fileName);
    }
}
