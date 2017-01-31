package com.focustech.electronicbrand.bridge.security;

import android.content.Context;

import com.focustech.electronicbrand.bridge.BridgeLifeCycleListener;
import com.focustech.electronicbrand.capabilities.security.SecurityUtils;

/**
 * <加解密管理类>
 *
 * @author caoyinfei
 * @version [版本号, 2016/6/12]
 * @see [相关类/方法]
 * @since [V1]
 */
public class SecurityManager  implements BridgeLifeCycleListener {
    @Override
    public void initOnApplicationCreate(Context context) {

    }

    @Override
    public void clearOnApplicationQuit() {

    }

    /**
     * md5 加密
     * @param str
     * @return
     */
    public String get32MD5Str(String str){
        return SecurityUtils.get32MD5Str(str);
    }

}
