package com.focustech.electronicbrand.bridge.cache.localstorage;

import android.content.Context;

import com.focustech.electronicbrand.bridge.BridgeLifeCycleListener;
import com.focustech.electronicbrand.capabilities.cache.FileUtil;

import java.io.File;

/**
 * <管理本地文件目录>
 * 对本地存储路径做管理，本地存储分为外部存储（SD卡）和内部存储，项目以外部存储优先，如果没有外部存储，则使用内部存储\
 *
 * @author caoyinfei
 * @version [版本号, 2016/6/6]
 * @see [相关类/方法]
 * @since [V1]
 */
public class LocalFileStorageManager implements BridgeLifeCycleListener {
    private static String FILE_ROOT_NAME = "Electronicbrand";

    /**
     * 列表页面图片缓存目录
     */
    private static String FOLDER_NAME_IMAGE = "image";

    /**
     * 用户头像缓存目录
     */
    private static String FOLDER_NAME_HEAD = "head";

    /**
     * 版本更新的目录
     */
    private static String FOLDER_NAME_VERSION_UPDATE = "update";

    @Override
    public void initOnApplicationCreate(Context context) {
        createFilePaths(context);
    }

    /**
     * 新建文件目录
     *
     * @param context
     */
    private void createFilePaths(Context context) {
        getCacheImgFilePath(context);
        getVersionUpdatePath(context);

    }

    /**
     * <根目录缓存目录>
     * <功能详细描述>
     *
     * @param context
     * @return
     * @see [类、类#方法、类#成员]
     */
    private static String getCacheFilePath(Context context) {
        return FileUtil.getSDPath(context) + File.separator + FILE_ROOT_NAME + File.separator;
    }

    /**
     * <列表页面图片缓存目录>
     * <功能详细描述>
     *
     * @param context
     * @return
     */
    public static String getCacheImgFilePath(Context context) {
        return FileUtil.createNewFile(getCacheFilePath(context) + FOLDER_NAME_IMAGE + File.separator);
    }

    /**
     * <用户头像地址>
     * <功能详细描述>
     *
     * @param context
     * @param userId
     * @return
     */
    public static String getUserHeadPath(Context context, String userId) {
        return FileUtil.createNewFile(getCacheFilePath(context) + FOLDER_NAME_IMAGE + File.separator + userId + File.separator);
    }


    /**
     * <版本更新目录>
     * <功能详细描述>
     *
     * @param context
     * @return
     */
    public static String getVersionUpdatePath(Context context) {
        return FileUtil.createNewFile(getCacheFilePath(context) + FOLDER_NAME_VERSION_UPDATE + File.separator);
    }

    @Override
    public void clearOnApplicationQuit() {

    }
}