package com.focustech.electronicbrand.bridge.http;

import android.content.Context;

import com.focustech.electronicbrand.bridge.BridgeLifeCycleListener;
import com.focustech.electronicbrand.capabilities.http.ITRequestResult;
import com.focustech.electronicbrand.capabilities.http.OkHttpUtil;
import com.focustech.electronicbrand.capabilities.http.Param;

import java.io.File;

/**
 * <http公共解析库>
 *
 * @author caoyinfei
 * @version [版本号, 2016/6/6]
 * @see [相关类/方法]
 * @since [V1]
 */
public class OkHttpManager implements BridgeLifeCycleListener {

    @Override
    public void initOnApplicationCreate(Context context) {

    }

    /**
     * 异步Get请求 泛型返回
     *
     * @param url             请求url
     * @param iTRequestResult 请求回调
     * @param clazz           Class<T>
     * @param params          请求参数
     * @param <T>             泛型模板
     */
    public <T> void requestAsyncGet(String url, ITRequestResult<T> iTRequestResult, Class<T> clazz, Param... params) {
        OkHttpUtil.getInstance().requestAsyncGetEnqueue(url, iTRequestResult, clazz, params);
    }

    /**
     * 异步Get请求 带tag(关闭页面则取消请求)
     *
     * @param url             请求url
     * @param activityName    请求activityName
     * @param iTRequestResult 请求回调
     * @param clazz           Class<T>
     * @param params          请求参数
     * @param <T>             泛型模板
     */
    public <T> void requestAsyncGetByTag(String url, String activityName, ITRequestResult<T> iTRequestResult, Class<T> clazz, Param... params) {
        OkHttpUtil.getInstance().requestAsyncGetEnqueueByTag(url, activityName, iTRequestResult, clazz, params);
    }

    /**
     * 异步POST请求
     *
     * @param url             请求url
     * @param iTRequestResult 请求回调
     * @param clazz           Class<T>
     * @param params          请求参数
     * @param <T>             泛型模板
     */
    public <T> void requestAsyncPost(String url, ITRequestResult<T> iTRequestResult, Class<T> clazz, Param... params) {
        OkHttpUtil.getInstance().requestAsyncPost(url, iTRequestResult, clazz, params);
    }

    /**
     * 异步POST请求 带tag(关闭页面则取消请求)
     *
     * @param url             请求url
     * @param activityName    请求activityName
     * @param iTRequestResult 请求回调
     * @param clazz           Class<T>
     * @param params          请求参数
     * @param <T>             泛型模板
     */
    public <T> void requestAsyncPostByTag(String url, String activityName, ITRequestResult<T> iTRequestResult, Class<T> clazz, Param... params) {
        OkHttpUtil.getInstance().requestAsyncPostByTag(url, activityName, iTRequestResult, clazz, params);
    }


    /**
     * 异步DELETE请求
     *
     * @param url             请求url
     * @param iTRequestResult 请求回调
     * @param clazz           Class<T>
     * @param params          请求参数
     * @param <T>             泛型模板
     */
    public <T> void requestAsyncDelete(String url, ITRequestResult<T> iTRequestResult, Class<T> clazz, Param... params) {
        OkHttpUtil.getInstance().requestAsyncDelete(url, iTRequestResult, clazz, params);
    }

    /**
     * 异步POST请求 单文件上传
     *
     * @param url             请求url
     * @param file            待上传的文件
     * @param key             待上传的key
     * @param iTRequestResult 请求回调
     * @param clazz           Class<T>
     * @param params          请求参数
     */
    public <T> void requestAsyncPost(String url, File file, String key, ITRequestResult<T> iTRequestResult, Class<T> clazz, Param... params) {
        OkHttpUtil.getInstance().requestAsyncPost(url, file, key, iTRequestResult, clazz, params);
    }

    /**
     * 异步POST请求 单文件上传 带tag(关闭页面则取消请求)
     *
     * @param url             请求url
     * @param activityName    请求activityName
     * @param file            待上传的文件
     * @param key             待上传的key
     * @param iTRequestResult 请求回调
     * @param clazz           Class<T>
     * @param params          请求参数
     */
    public <T> void requestAsyncPostByTag(String url, String activityName, File file, String key, ITRequestResult<T> iTRequestResult, Class<T> clazz, Param... params) {
        OkHttpUtil.getInstance().requestAsyncPostByTag(url, activityName, file, key, iTRequestResult, clazz, params);
    }

    /**
     * 异步POST请求 多文件上传
     *
     * @param url             请求url
     * @param files           待上传的文件s
     * @param keys            待上传文件的keys
     * @param iTRequestResult 请求回调
     * @param clazz           Class<T>
     * @param params          请求参数
     */
    public <T> void requestAsyncPost(String url, File[] files, String[] keys, ITRequestResult<T> iTRequestResult, Class<T> clazz, Param... params) {
        OkHttpUtil.getInstance().requestAsyncPost(url, files, keys, iTRequestResult, clazz, params);
    }

    /**
     * 异步POST请求 多文件上传  带tag(关闭页面则取消请求)
     *
     * @param url             请求url
     * @param activityName    请求activityName
     * @param files           待上传的文件s
     * @param keys            待上传文件的keys
     * @param iTRequestResult 请求回调
     * @param clazz           Class<T>
     * @param params          请求参数
     */
    public <T> void requestAsyncPostByTag(String url, String activityName, File[] files, String[] keys, ITRequestResult<T> iTRequestResult, Class<T> clazz, Param... params) {
        OkHttpUtil.getInstance().requestAsyncPostByTag(url, activityName, files, keys, iTRequestResult, clazz, params);
    }

    /**
     * 异步POST请求 单图片上传上传
     *
     * @param url             请求url
     * @param files           待上传图片数组
     * @param fileName        待上传图片名
     * @param key             待上传的key
     * @param iTRequestResult 请求回调
     * @param clazz           Class<T>
     * @param params          请求参数
     */
    public <T> void requestAsyncPost(String url, byte[] files, String fileName, String key, ITRequestResult<T> iTRequestResult, Class<T> clazz, Param... params) {
        OkHttpUtil.getInstance().requestAsyncPost(url, files, fileName, key, iTRequestResult, clazz, params);
    }

    /**
     * 异步POST请求 单图片上传上传 带tag(关闭页面则取消请求)
     *
     * @param url             请求url
     * @param activityName    请求activityName
     * @param files           待上传图片数组
     * @param fileName        待上传图片名
     * @param key             待上传的key
     * @param iTRequestResult 请求回调
     * @param clazz           Class<T>
     * @param params          请求参数
     */
    public <T> void requestAsyncPostByTag(String url, String activityName, byte[] files, String fileName, String key, ITRequestResult<T> iTRequestResult, Class<T> clazz, Param... params) {
        OkHttpUtil.getInstance().requestAsyncPostByTag(url, activityName, files, fileName, key, iTRequestResult, clazz, params);
    }

    /**
     * 取消正在请求的url
     *
     * @param url
     */
    public void cancelRequest(String url) {
        OkHttpUtil.getInstance().cancelRequest(url);
    }

    /**
     * 取消当前页面正在请求的请求
     *
     * @param activity
     */
    public void cancelActivityRequest(String activity) {
        OkHttpUtil.getInstance().cancelActivityRequest(activity);
    }

    @Override
    public void clearOnApplicationQuit() {
        OkHttpUtil.getInstance().destory();
    }
}
