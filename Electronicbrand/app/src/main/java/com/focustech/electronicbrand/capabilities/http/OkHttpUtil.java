package com.focustech.electronicbrand.capabilities.http;

import android.os.Handler;

import com.focustech.electronicbrand.bean.BaseResp;
import com.focustech.electronicbrand.capabilities.json.GsonHelper;
import com.focustech.electronicbrand.capabilities.log.EBLog;
import com.focustech.electronicbrand.util.GeneralUtils;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import java.io.File;
import java.io.IOException;
import java.net.FileNameMap;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * <http公共解析库>
 *
 * @author caoyinfei
 * @version [版本号, 2016/6/6]
 * @see [相关类/方法]
 * @since [V1]
 */
public class OkHttpUtil {

    Handler handler = new Handler() {

    };

    private final String TAG = OkHttpUtil.class.getSimpleName();

    private static OkHttpUtil manager;

    private OkHttpClient mOkHttpClient;

    public final int TIMEOUT = 20;

    public final int WRITE_TIMEOUT = 20;

    public final int READ_TIMEOUT = 20;

    /**
     * 请求url集合
     */
    private HashMap<String, Set<String>> requestMap;

    public OkHttpUtil() {
        requestMap = new HashMap<String, Set<String>>();
        mOkHttpClient = new OkHttpClient();

        mOkHttpClient.setConnectTimeout(TIMEOUT, TimeUnit.SECONDS);
        mOkHttpClient.setWriteTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS);
        mOkHttpClient.setReadTimeout(READ_TIMEOUT, TimeUnit.SECONDS);
    }

    public static OkHttpUtil getInstance() {
        if (manager == null) {
            synchronized (OkHttpUtil.class) {
                if (manager == null) {
                    return new OkHttpUtil();
                }
            }
        }
        return manager;
    }
    /*********************************************************** get请求*********************************************************/

    /**
     * 异步Get请求 具体实现
     *
     * @param url             请求url
     * @param iTRequestResult 请求回调
     * @param clazz           Class<T>
     * @param params          请求参数
     * @param <T>             泛型模板
     */
    public <T> void requestAsyncGetEnqueue(String url, ITRequestResult<T> iTRequestResult, Class<T> clazz, Param... params) {
        String constructUrl = constructUrl(url, params);
        Request request = new Request.Builder()
                .get()
                .url(constructUrl)
                .build();
        mOkHttpClient.newCall(request).enqueue(new TRequestCallBack(iTRequestResult, clazz));
    }

    /**
     * 异步Get请求 具体实现（可取消）
     *
     * @param url             请求url
     * @param activityName    请求activityName
     * @param iTRequestResult 请求回调
     * @param clazz           Class<T>
     * @param params          请求参数
     * @param <T>             泛型模板
     */
    public <T> void requestAsyncGetEnqueueByTag(String url, String activityName, ITRequestResult<T> iTRequestResult, Class<T> clazz, Param... params) {
        addRequestUrl(activityName, url);
        String constructUrl = constructUrl(url, params);
        Request request = new Request.Builder()
                .get()
                .url(constructUrl)
                .tag(url)
                .build();
        mOkHttpClient.newCall(request).enqueue(new TRequestCallBack(iTRequestResult, clazz, activityName));
    }

    /**
     * 构造get请求的url
     *
     * @param url    不带参数的url
     * @param params 参数
     * @return 带参数的url
     */
    private String constructUrl(String url, Param... params) {
        StringBuilder sb = new StringBuilder();
        sb.append(url);
        if (params.length != 0) {
            sb.append("?");
        } else {
            return sb.toString();
        }

        for (Param param :
                params) {
            sb.append(param.key + "=" + param.value + "&");
        }
        return sb.toString().substring(0, sb.length() - 1);
    }

    /*********************************************************** post请求*********************************************************/
    /**
     * 异步POST请求  具体实现
     *
     * @param url             请求url
     * @param iTRequestResult 请求回调
     * @param clazz           Class<T>
     * @param params          请求参数
     * @param <T>             泛型模板
     */
    public <T> void requestAsyncPost(String url, ITRequestResult<T> iTRequestResult, Class<T> clazz, Param... params) {
        FormEncodingBuilder builder = new FormEncodingBuilder();
        for (Param param :
                params) {
            builder.add(param.key, param.value);
        }
        RequestBody body = builder.build();
        Request request = new Request.Builder().post(body).url(url).build();
        mOkHttpClient.newCall(request).enqueue(new TRequestCallBack(iTRequestResult, clazz));
    }

    /**
     * 异步POST请求  具体实现（可取消）
     *
     * @param url             请求url
     * @param activityName    请求activityName
     * @param iTRequestResult 请求回调
     * @param clazz           Class<T>
     * @param params          请求参数
     * @param <T>             泛型模板
     */
    public <T> void requestAsyncPostByTag(String url, String activityName, ITRequestResult<T> iTRequestResult, Class<T> clazz, Param... params) {
        addRequestUrl(activityName, url);
        FormEncodingBuilder builder = new FormEncodingBuilder();
        for (Param param :
                params) {
            builder.add(param.key, param.value);
        }
        RequestBody body = builder.build();
        Request request = new Request.Builder().post(body).url(url).tag(url).build();
        mOkHttpClient.newCall(request).enqueue(new TRequestCallBack(iTRequestResult, clazz, activityName));
    }


    /**
     * 异步DELETE请求  具体实现
     *
     * @param url             请求url
     * @param iTRequestResult 请求回调
     * @param clazz           Class<T>
     * @param params          请求参数
     * @param <T>             泛型模板
     */
    public <T> void requestAsyncDelete(String url, ITRequestResult<T> iTRequestResult, Class<T> clazz, Param... params) {
        String finalUrl = constructUrl(url, params);
        Request request = new Request.Builder()
                .delete()
                .url(finalUrl)
                .build();
        mOkHttpClient.newCall(request).enqueue(new TRequestCallBack(iTRequestResult, clazz));
    }
    /*********************************************************** 文件请求*********************************************************/
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
        MultipartBuilder builder = new MultipartBuilder().type(MultipartBuilder.FORM);
        for (Param param :
                params) {
            builder.addFormDataPart(param.key, param.value);
        }
        builder = constructMultipartBuilder(builder, file, key);
        RequestBody body = builder.build();
        Request request = new Request.Builder().post(body).url(url).build();
        mOkHttpClient.newCall(request).enqueue(new TRequestCallBack(iTRequestResult, clazz));
    }

    /**
     * 异步POST请求 单文件上传（可取消）
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
        addRequestUrl(activityName, url);
        MultipartBuilder builder = new MultipartBuilder().type(MultipartBuilder.FORM);
        for (Param param :
                params) {
            builder.addFormDataPart(param.key, param.value);
        }
        builder = constructMultipartBuilder(builder, file, key);
        RequestBody body = builder.build();
        Request request = new Request.Builder().post(body).url(url).tag(url).build();
        mOkHttpClient.newCall(request).enqueue(new TRequestCallBack(iTRequestResult, clazz, activityName));
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
        MultipartBuilder builder = new MultipartBuilder().type(MultipartBuilder.FORM);
        for (Param param :
                params) {
            builder.addFormDataPart(param.key, param.value);
        }
        for (int i = 0; i < files.length; i++) {
            builder = constructMultipartBuilder(builder, files[i], keys[i]);
        }
        RequestBody body = builder.build();
        Request request = new Request.Builder().post(body).url(url).build();
        mOkHttpClient.newCall(request).enqueue(new TRequestCallBack(iTRequestResult, clazz));
    }

    /**
     * 异步POST请求 多文件上传（可取消）
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
        addRequestUrl(activityName, url);
        MultipartBuilder builder = new MultipartBuilder().type(MultipartBuilder.FORM);
        for (Param param :
                params) {
            builder.addFormDataPart(param.key, param.value);
        }
        for (int i = 0; i < files.length; i++) {
            builder = constructMultipartBuilder(builder, files[i], keys[i]);
        }
        RequestBody body = builder.build();
        Request request = new Request.Builder().post(body).url(url).tag(url).build();
        mOkHttpClient.newCall(request).enqueue(new TRequestCallBack(iTRequestResult, clazz, activityName));
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
        MultipartBuilder builder = new MultipartBuilder().type(MultipartBuilder.FORM);
        for (Param param :
                params) {
            builder.addFormDataPart(param.key, param.value);
        }
        RequestBody requestBody = RequestBody.create(MediaType.parse("image/*"), files);
        builder.addFormDataPart(key, fileName, requestBody);
        RequestBody body = builder.build();
        Request request = new Request.Builder().post(body).url(url).build();
        mOkHttpClient.newCall(request).enqueue(new TRequestCallBack(iTRequestResult, clazz));
    }

    /**
     * 异步POST请求 单图片上传上传（可取消）
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
        addRequestUrl(activityName, url);
        MultipartBuilder builder = new MultipartBuilder().type(MultipartBuilder.FORM);
        for (Param param :
                params) {
            builder.addFormDataPart(param.key, param.value);
        }
        RequestBody requestBody = RequestBody.create(MediaType.parse("image/*"), files);
        builder.addFormDataPart(key, fileName, requestBody);
        RequestBody body = builder.build();
        Request request = new Request.Builder().post(body).url(url).tag(url).build();
        mOkHttpClient.newCall(request).enqueue(new TRequestCallBack(iTRequestResult, clazz, activityName));
    }

    /**
     * 构造多部件builer
     *
     * @param builder 当前实例化MultipartBuilder
     * @param file    待上传文件
     * @param key     对应的参数名
     * @return 构造后的MultipartBuilder
     */
    private MultipartBuilder constructMultipartBuilder(MultipartBuilder builder, File file, String key) {
        String name = file.getName();
        RequestBody requestBody = RequestBody.create(MediaType.parse(guessMimeType(name)), file);
        builder.addFormDataPart(key, name, requestBody);
        return builder;
    }

    /**
     * 获取文件类型
     *
     * @param path
     * @return
     */
    private String guessMimeType(String path) {
        FileNameMap fileNameMap = URLConnection.getFileNameMap();
        String contentTypeFor = fileNameMap.getContentTypeFor(path);
        if (contentTypeFor == null) {
            contentTypeFor = "application/octet-stream";
        }
        return contentTypeFor;
    }

    /**
     * 增加请求标志
     *
     * @param activityName
     * @param url
     */
    private void addRequestUrl(String activityName, String url) {
        if (requestMap.containsKey(activityName)) {
            requestMap.get(activityName).add(url);
        } else {
            Set<String> urlSet = new HashSet<String>();
            urlSet.add(url);
            requestMap.put(activityName, urlSet);
        }
    }

    /**
     * 取消正在请求的url
     *
     * @param url 请求url
     */
    public void cancelRequest(String url) {
        try {
            mOkHttpClient.getDispatcher().cancel(url);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * 取消当前页面正在请求的请求
     *
     * @param activityName
     */
    public void cancelActivityRequest(String activityName) {
        try {
            if (requestMap.containsKey(activityName)) {
                Set<String> urlSet = requestMap.get(activityName);
                for (String url : urlSet) {
                    mOkHttpClient.getDispatcher().cancel(url);
                }
                requestMap.remove(activityName);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /*************************************************************
     * 回调方法
     *********************************************************/
    class TRequestCallBack<T> implements Callback {

        private ITRequestResult<T> mITRequestResult;

        private Class<T> clazz;

        private String notifyMsg = "";

        private String activityName;

        public TRequestCallBack(ITRequestResult<T> mITRequestResult, Class<T> clazz) {
            this.mITRequestResult = mITRequestResult;
            this.clazz = clazz;
        }

        public TRequestCallBack(ITRequestResult<T> mITRequestResult, Class<T> clazz, String activityName) {
            this.mITRequestResult = mITRequestResult;
            this.clazz = clazz;
            this.activityName = activityName;
        }

        @Override
        public void onFailure(Request request, IOException e) {
            EBLog.e(TAG, request.toString() + e.toString());
            if (!isHaveActivtyName(activityName)) return;
            notifyMsg = NETWORK_ERROR;
            postErrorMsg();
        }

        @Override
        public void onResponse(Response response) throws IOException {
            if (!isHaveActivtyName(activityName)) return;
            if (response.isSuccessful()) {
                String result = response.body().string(); //方法只能调用一次
                EBLog.i(TAG, result);
                final T res = GsonHelper.toType(result, clazz);
                int code = -1;
                if (res != null && res instanceof BaseResp) {
                    code = ((BaseResp) res).getRetcode();
                    switch (code) {
                        case 000000:
                            postSucessMsg(res);
                            break;
                        case 10005:
                        case 10011:
                            //自动登录
                        default:
                            notifyMsg = ((BaseResp) res).getRetinfo();
                            postErrorMsg();
                            break;
                    }
                } else {
                    notifyMsg = SERVER_ERROR;
                    postErrorMsg();
                }
            } else {
                notifyMsg = NETWORK_ERROR;
                postErrorMsg();
            }
        }

        /**
         * 主线程发送错误消息
         */
        private void postErrorMsg() {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    mITRequestResult.onCompleted();
                    mITRequestResult.onFailure(notifyMsg);
                }
            });
        }

        /**
         * 主线程发送正确消息
         */
        private void postSucessMsg(final T res) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    mITRequestResult.onCompleted();
                    mITRequestResult.onSuccessful(res);
                }
            });
        }

        /**
         * 当前activity是否存在
         *
         * @param activityName
         */
        private boolean isHaveActivtyName(String activityName) {
            if (GeneralUtils.isNotNullOrZeroLenght(activityName)) {
                return requestMap.containsKey(activityName);
            } else {
                return true;
            }
        }
    }

    public static String SERVER_ERROR = "请求失败，请稍后再试";

    public static String NETWORK_ERROR = "您的网络状况不佳，请检查网络连接";

    public void destory() {
        manager = null;
    }
}
