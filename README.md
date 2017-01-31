1. 前言
-----

安卓属于小团队开发，架构的重要性在很多公司其实不是那么的明显，加上现在的开源框架层出不穷，更好的帮助我们上手android项目的开发。我前两年也在公司主导过项目开发，搭建过不少项目，以前主要的倾向是MVC，导致了activity/fragment过大，而且很多公共功能杂乱在项目中，后期维护起来不方便，最近刚好有时间，重新搭建了一个新的框架。（ps：有建议或者更好想法的可以留言。）

2. 用到的知识点：
----------

UI----面向对象
数据交互----MVP模式   
数据库------GreenDao 
网络图片加载-----picasso
json解析-----gson
http请求----OKHttp   
事件总线----eventbus


----------


以上知识点不熟悉的，可以先熟悉下基本知识，如果已经了解过，可以直接跳过下面的链接，直接看下文的使用。
MVP模式   讲解地址：http://blog.csdn.net/dfskhgalshgkajghljgh/article/details/51317956
GreenDao 讲解地址：http://blog.csdn.net/dfskhgalshgkajghljgh/article/details/51304390 
picasso    讲解地址：http://blog.csdn.net/dfskhgalshgkajghljgh/article/details/51684693
OKHttp   讲解地址：鸿神的博客讲解地址：
http://blog.csdn.net/lmj623565791/article/details/47911083
eventbus  讲解地址：http://blog.csdn.net/dfskhgalshgkajghljgh/article/details/51681705

----------


3. 框架整体结构：
----------
![这里写图片描述](http://img.blog.csdn.net/20160612124551431)

4.项目目录结构：
-------
![这里写图片描述](http://img.blog.csdn.net/20160612124751434)

bean---------------------------------------------------存放java model对象
biz-----------------------------------------------------业务模块，根据不能业务建立子模块
bridge-------------------------------------------------底层功能实现跟UI层的衔接层
capabilities--------------------------------------------底层功能具体实现（后期项目迭代到一定程度稳定后会考虑以jar形式导入）
constant-----------------------------------------------常量
ui------------------------------------------------------界面，根据不同业务建立子模块
util-----------------------------------------------------业务层公共方法
view---------------------------------------------------自定义view实现

5.具体解析
------
还是按照大家的习惯思维，从界面--->数据---->网络----->交互，这样的层次讲解。

1）UI层

UI层其实比较简单，主要就是用到面向对象的封装,BaseActivity为基类，同时BaseActivity实现三个接口，分别为CreateInit, PublishActivityCallBack, PresentationLayerFunc，这三个接口的作用依次是：界面初始化，页面跳转封装，页面交互封装。PresentationLayerFunc的具体实现是在PresentationLayerFuncHelper里面，BaseActivity类会初始化该类，把复杂的功能实现抽象出去，轻量化基类。

![这里写图片描述](http://img.blog.csdn.net/20160612131858162)

BaseActivity代码如下所示：

```
/**
 * <基础activity>
 *
 * @author caoyinfei
 * @version [版本号, 2014-3-24]
 * @see [相关类/方法]
 * @since [V1]
 */
public abstract class BaseActivity extends Activity implements CreateInit, PublishActivityCallBack, PresentationLayerFunc, IMvpView, OnClickListener {

    private PresentationLayerFuncHelper presentationLayerFuncHelper;

    /**
     * 返回按钮
     */
    private LinearLayout back;

    /**
     * 标题，右边字符
     */
    protected TextView title, right;

    public BasePresenter presenter;

    public final String TAG = this.getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        presentationLayerFuncHelper = new PresentationLayerFuncHelper(this);

        initViews();
        initListeners();
        initData();
        setHeader();
        EBApplication.ebApplication.addActivity(this);
        EventBus.getDefault().register(this);
    }

    @Override
    public void setHeader() {
        back = (LinearLayout) findViewById(R.id.ll_back);
        title = (TextView) findViewById(R.id.tv_title);
        right = (TextView) findViewById(R.id.tv_right);
        back.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_back:
                finish();
                break;
        }
    }

    public void onEventMainThread(Event event) {

    }

    @Override
    protected void onResume() {
        EBApplication.ebApplication.currentActivityName = this.getClass().getName();
        super.onResume();
    }

    @Override
    public void startActivity(Class<?> openClass, Bundle bundle) {
        Intent intent = new Intent(this, openClass);
        if (null != bundle)
            intent.putExtras(bundle);
        startActivity(intent);
    }

    @Override
    public void openActivityForResult(Class<?> openClass, int requestCode, Bundle bundle) {
        Intent intent = new Intent(this, openClass);
        if (null != bundle)
            intent.putExtras(bundle);
        startActivityForResult(intent, requestCode);
    }

    @Override
    public void setResultOk(Bundle bundle) {
        Intent intent = new Intent();
        if (bundle != null) ;
        intent.putExtras(bundle);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void showToast(String msg) {
        presentationLayerFuncHelper.showToast(msg);
    }

    @Override
    public void showProgressDialog() {
        presentationLayerFuncHelper.showProgressDialog();
    }

    @Override
    public void hideProgressDialog() {
        presentationLayerFuncHelper.hideProgressDialog();
    }

    @Override
    public void showSoftKeyboard(View focusView) {
        presentationLayerFuncHelper.showSoftKeyboard(focusView);
    }

    @Override
    public void hideSoftKeyboard() {
        presentationLayerFuncHelper.hideSoftKeyboard();
    }

    @Override
    protected void onDestroy() {
        EBApplication.ebApplication.deleteActivity(this);
        EventBus.getDefault().unregister(this);
        if (presenter != null) {
            presenter.detachView(this);
        }
        OkHttpManager httpManager = BridgeFactory.getBridge(Bridges.HTTP);
        httpManager.cancelActivityRequest(TAG);
        super.onDestroy();
    }

}
```
PresentationLayerFuncHelper代码如下所示：

```
/**
 * <页面基础公共功能实现>
 *
 * @author caoyinfei
 * @version [版本号, 2016/6/6]
 * @see [相关类/方法]
 * @since [V1]
 */
public class PresentationLayerFuncHelper implements PresentationLayerFunc {

    private Context context;

    public PresentationLayerFuncHelper(Context context) {
        this.context = context;
    }

    @Override
    public void showToast(String msg) {
        ToastUtil.makeText(context, msg);
    }

    @Override
    public void showProgressDialog() {

    }

    @Override
    public void hideProgressDialog() {

    }

    @Override
    public void showSoftKeyboard(View focusView) {

    }

    @Override
    public void hideSoftKeyboard() {

    }
}

```

三个接口，分别为CreateInit, PublishActivityCallBack, PresentationLayerFunc代码如下所示：

```

/**
 * <公共方法抽象>
 *
 * @author caoyinfei
 * @version [版本号, 2014-3-24]
 * @see [相关类/方法]
 * @since [V1]
 */
public interface CreateInit {
    /**
     * 初始化布局组件
     */
    public void initViews();

    /**
     * 增加按钮点击事件
     */
    void initListeners();

    /**
     * 初始化数据
     */
    public void initData();

    /**
     * 初始化公共头部
     */
    public void setHeader();
}

```

```
/**
 * <页面跳转封装>
 *
 * @author caoyinfei
 * @version [版本号, 2016/6/6]
 * @see [相关类/方法]
 * @since [V1]
 */
public interface PublishActivityCallBack {
    /**
     * 打开新界面
     *
     * @param openClass 新开页面
     * @param bundle    参数
     */
    public void startActivity(Class<?> openClass, Bundle bundle);

    /**
     * 打开新界面，期待返回
     *
     * @param openClass 新界面
     * @param requestCode 请求码
     * @param bundle 参数
     */
    public void openActivityForResult(Class<?> openClass, int requestCode, Bundle bundle);

    /**
     * 返回到上个页面
     *
     * @param bundle 参数
     */
    public void setResultOk(Bundle bundle);
}

```

```
/**
 * <页面基础公共功能抽象>
 *
 * @author caoyinfei
 * @version [版本号, 2016/6/6]
 * @see [相关类/方法]
 * @since [V1]
 */
public interface PresentationLayerFunc {
    /**
     * 弹出消息
     *
     * @param msg
     */
    public void showToast(String msg);

    /**
     * 网络请求加载框
     */
    public void showProgressDialog();

    /**
     * 隐藏网络请求加载框
     */
    public void hideProgressDialog();

    /**
     * 显示软键盘
     *
     * @param focusView
     */
    public void showSoftKeyboard(View focusView);

    /**
     * 隐藏软键盘
     */
    public void hideSoftKeyboard();
}

```
对于上层开发而言，工作就比较简单了，比如登录界面（LoginActivity），只要继承BaseActivity则可以了，然后用IDE工具，自动导入必要的override方法。
代码如下：

```
public class LoginActivity extends BaseActivity implements IUserLoginView {

    /**
     * 用户名
     */
    private EditText userName;

    /**
     * 用户密码
     */
    private EditText password;

    /**
     * 登录
     */
    private Button login;

    private LoginPresenter mUserLoginPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_main);
        super.onCreate(savedInstanceState);

        presenter = mUserLoginPresenter = new LoginPresenter();
        mUserLoginPresenter.attachView(this);
    }

    @Override
    public void initViews() {
        userName = (EditText) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.passowrd);
        login = (Button) findViewById(R.id.login);
    }

    @Override
    public void initListeners() {
        login.setOnClickListener(this);
    }

    @Override
    public void initData() {

    }

    @Override
    public void setHeader() {
        super.setHeader();
        title.setText("登录");
    }

    @Override
    public void onEventMainThread(Event event) {
        super.onEventMainThread(event);
        switch (event){
            case IMAGE_LOADER_SUCCESS:
                clearEditContent();
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login:
                //13914786934   123456  可以登录
                mUserLoginPresenter.login(userName.getText().toString(), password.getText().toString());
                break;
        }
        super.onClick(v);
    }

    @Override
    public void clearEditContent() {
        userName.setText("");
        password.setText("");
    }

    @Override
    public void onError(String errorMsg, String code) {
        showToast(errorMsg);
    }

    @Override
    public void onSuccess() {
        startActivity(HomeActivity.class,null);
    }

    @Override
    public void showLoading() {

    }

    @Override
    public void hideLoading() {

    }
}

```
大家应该看得出，acitivty里面全是接口，开发gg只要把想应实现填到对应的接口中即可，这样实现的好处有几个：
1.每个页面都是这种统一的格式，后期人员流动后维护方便。
2.公共处理，比如title栏，每个页面都有，各个页面去单独实现，代码冗余，这边抽到BaseActivity 里面setHeader（）方法去统一处理，当时各个子类也可以自定义特殊格式，比如title栏上面的titleName的不同。
3.公共方法抽象，避免每个activity重复大量代码。

2）数据交互层
可能有人会看到上面的代码中有MVP的代码，会看不太懂？别急，接下来讲解MVP的作用。
之前activity层既做界面，又做业务逻辑，代码量特别大，动不动几百上千行，之前项目上线的时候，领导让我混淆一下，我当时说，这种代码，过几个月我们自己都看不懂了，还需要混淆吗？哈哈~~当然是开玩笑。
言归正传，我们这边用MVP代替了MVC，从上面activity可以看出，activity只做两件事：1、view的创建。2、用户交互。那业务逻辑我们放在哪里呢？这里我们引入Presenter层，用来专门处理业务逻辑，并通过IMvpView接口实现跟activity的交互（mvp具体讲解，前面已经很详细的介绍过，地址：http://blog.csdn.net/dfskhgalshgkajghljgh/article/details/51317956）

代码如下：
上面我们说过，Presenter与View交互是通过接口。所以我们这里需要定义一个IUserLoginView ，难点就在于应该有哪些方法，我们这个是登录页面，其实有哪些功能，就应该有哪些方法，比如登录成功，失败，弹出加载框这些都要通知ui（Activity）去更新。所以定义了如下方法：
```
/**
 * <功能详细描述>
 *
 * @author caoyinfei
 * @version [版本号, 2016/5/4]
 * @see [相关类/方法]
 * @since [V1]
 */
public interface IMvpView {
    void onError(String errorMsg, String code);

    void onSuccess();

    void showLoading();

    void hideLoading();
}
```

```
/**
 * <功能详细描述>
 *
 * @author caoyinfei
 * @version [版本号, 2016/5/4]
 * @see [相关类/方法]
 * @since [产品/模块版本]
 */
public interface IUserLoginView extends IMvpView {
    void clearEditContent();
}
```
LoginPresenter 为登录的业务实现类，他需要做两件事：1、业务处理。2.通知页面数据刷新。业务处理很简单，这边不做介绍了。Presenter与页面交互是通过接口实现的，这边通过继承基类BasePresenter，从而实现接口attachView（V view），这边的view是个泛型，在这里，他其实是IUserLoginView，LoginActivity会实现这个接口，在初始化LoginPresenter 的时候，会把自身传过来mUserLoginPresenter.attachView(this);-----这段代码是在LoginActivity的onCreate中，这样 Presenter通知页面刷新就只要通过接口就可以了。

```
/**
 * <基础业务类>
 *
 * @author caoyinfei
 * @version [版本号, 2016/6/6]
 * @see [相关类/方法]
 * @since [V1]
 */
public interface Presenter<V> {
    void attachView(V view);

    void detachView(V view);
}

```

```
/**
 * <基础业务类>
 *
 * @author caoyinfei
 * @version [版本号, 2016/6/6]
 * @see [相关类/方法]
 * @since [V1]
 */
public abstract class BasePresenter<V extends IMvpView> implements Presenter<V> {
    protected V mvpView;

    public void attachView(V view) {
        mvpView = view;
    }

    @Override
    public void detachView(V view) {
        mvpView = null;
    }
}

```

```
/**
 * <功能详细描述>
 *
 * @author caoyinfei
 * @version [版本号, 2016/5/4]
 * @see [相关类/方法]
 * @since [产品/模块版本]
 */
public class LoginPresenter extends BasePresenter<IUserLoginView> {

    public LoginPresenter() {

    }

    public void login(String useName, String password) {
        //网络层
        mvpView.showLoading();
        SecurityManager securityManager = BridgeFactory.getBridge(Bridges.SECURITY);
        OkHttpManager httpManager = BridgeFactory.getBridge(Bridges.HTTP);

        httpManager.requestAsyncPostByTag(URLUtil.USER_LOGIN, getName(), new ITRequestResult<LoginResp>() {
                    @Override
                    public void onCompleted() {
                        mvpView.hideLoading();
                    }

                    @Override
                    public void onSuccessful(LoginResp entity) {
                        mvpView.onSuccess();
                        EBSharedPrefManager manager = BridgeFactory.getBridge(Bridges.SHARED_PREFERENCE);
                        manager.getKDPreferenceUserInfo().saveString(EBSharedPrefUser.USER_NAME, "abc");
                    }

                    @Override
                    public void onFailure(String errorMsg) {
                        mvpView.onError(errorMsg, "");
                    }

                }, LoginResp.class, new Param("username", useName),
                new Param("pas", securityManager.get32MD5Str(password)));
    }
}
```

```
public class LoginActivity extends BaseActivity implements IUserLoginView {

    /**
     * 用户名
     */
    private EditText userName;

    /**
     * 用户密码
     */
    private EditText password;

    /**
     * 登录
     */
    private Button login;

    private LoginPresenter mUserLoginPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_main);
        super.onCreate(savedInstanceState);

        presenter = mUserLoginPresenter = new LoginPresenter();
        mUserLoginPresenter.attachView(this);
    }

    @Override
    public void initViews() {
        userName = (EditText) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.passowrd);
        login = (Button) findViewById(R.id.login);
    }

    @Override
    public void initListeners() {
        login.setOnClickListener(this);
    }

    @Override
    public void initData() {

    }

    @Override
    public void setHeader() {
        super.setHeader();
        title.setText("登录");
    }

    @Override
    public void onEventMainThread(Event event) {
        super.onEventMainThread(event);
        switch (event){
            case IMAGE_LOADER_SUCCESS:
                clearEditContent();
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login:
                //13914786934   123456  可以登录
                mUserLoginPresenter.login(userName.getText().toString(), password.getText().toString());
                break;
        }
        super.onClick(v);
    }

    @Override
    public void clearEditContent() {
        userName.setText("");
        password.setText("");
    }

    @Override
    public void onError(String errorMsg, String code) {
        showToast(errorMsg);
    }

    @Override
    public void onSuccess() {
        startActivity(HomeActivity.class,null);
    }

    @Override
    public void showLoading() {

    }

    @Override
    public void hideLoading() {

    }
}
```

3）网络层
网络由于google在6.0后不再使用httpclient，之前项目中通过httpclient实现了网络通信，现在跟随google，换成OKHttp框架。这个框架的讲解不再介绍了，比较简单，我贴一个鸿神的博客讲解地址：
http://blog.csdn.net/lmj623565791/article/details/47911083有兴趣的可以去看看。
我这边做的事情是，对OKHttp再做了一层封装，更方便我们使用。

```
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
     * 取消当前页面正在的请求
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

```

```
/**
 * <功能详细描述>
 *
 * @author caoyinfei
 * @version [版本号, 2016/6/8]
 * @see [相关类/方法]
 * @since [产品/模块版本]
 */
public interface ITRequestResult<T> {

    public void onSuccessful(T entity);

    public void onFailure(String errorMsg);

}

```

```
/**
 * <参数类>
 *
 * @author caoyinfei
 * @version [版本号, 2016/6/8]
 * @see [相关类/方法]
 * @since [V1]
 */
public class Param {
    public Param() {
    }

    public Param(String key, String value) {
        this.key = key;
        this.value = value != null ? value : "";
    }

    public Param(String key, int value) {
        this.key = key;
        this.value = value + "";
    }

    String key;

    String value;
}
```
应该已经很清楚了，我的做的事情有三个：
1.定义ITRequestResult接口，用于处理网络请求后的回调，并且此接口中的回调在主线程中（OKHttp返回接口Callback是在子线程中 ）。
2.TRequestCallBack接口实现。
集中统一处理网络层异常码然后返回到UI层。
集中统一处理网络层正常情况，通过json库，把网络返回解析成java model返回给UI层。
3.get，post，cancel方法封装，方便调用。

4）Bridge层抽象

每个项目中的重复代码特别多，很多项目喜欢抽象公共方法类，但是项目的时间一久，可能你自己都不清楚，这个方法是否定义过，写在哪里，勤快的人会全局搜一遍，有些同学可能会嫌麻烦，自己新建一个util类，写上自己的名字，顿时感觉自己萌萌的。

这边，我们引入了BridgeFactory，用来统一管理基础功能,类似本地服务的实现原理。
BridgeFactory里面实现了文件，网络，数据库，安全等等管理类的实现，并保存了各类管理类的引用。业务层或者上层调用底层实现时，一律通过BridgeFactory去访问，而不是直接的调用。

```
/**
 * <中间连接层>
 *
 * @author caoyinfei
 * @version [版本号, 2016/6/6]
 * @see [相关类/方法]
 * @since [V1]
 */
public class BridgeFactory {

    private static BridgeFactory model;

    private HashMap<String, Object> mBridges;

    private BridgeFactory() {
        mBridges = new HashMap<String, Object>();
    }

    public static void init(Context context) {
        model = new BridgeFactory();
        model.iniLocalFileStorageManager();
        model.initPreferenceManager();
        model.initSecurityManager();
        model.initUserSession();
        model.initCoreServiceManager(context);
        model.initOkHttpManager();
    }

    public static void destroy() {
        model.mBridges = null;
        model = null;
    }

    /**
     * 初始化本地存储路径管理类
     */
    private void iniLocalFileStorageManager() {
        LocalFileStorageManager localFileStorageManager = new LocalFileStorageManager();
        model.mBridges.put(Bridges.LOCAL_FILE_STORAGE, localFileStorageManager);
        BridgeLifeCycleSetKeeper.getInstance().trustBridgeLifeCycle(localFileStorageManager);
    }

    /**
     * 初始化SharedPreference管理类
     */
    private void initPreferenceManager() {
        EBSharedPrefManager ebSharedPrefManager = new EBSharedPrefManager();
        model.mBridges.put(Bridges.SHARED_PREFERENCE, ebSharedPrefManager);
        BridgeLifeCycleSetKeeper.getInstance().trustBridgeLifeCycle(ebSharedPrefManager);
    }

    /**
     * 网络请求管理类
     */
    private void initOkHttpManager() {
        OkHttpManager mOkHttpManager = new OkHttpManager();
        model.mBridges.put(Bridges.HTTP, mOkHttpManager);
        BridgeLifeCycleSetKeeper.getInstance().trustBridgeLifeCycle(mOkHttpManager);
    }

    /**
     * 初始化安全模块
     */
    private void initSecurityManager() {
        SecurityManager securityManager = new SecurityManager();
        model.mBridges.put(Bridges.SECURITY, securityManager);
        BridgeLifeCycleSetKeeper.getInstance().trustBridgeLifeCycle(securityManager);
    }

    /**
     * 初始化用户信息模块
     */
    private void initUserSession() {
    }

    /**
     * 初始化Tcp服务
     *
     * @param context
     */
    private void initCoreServiceManager(Context context) {
    }


    private void initDBManager() {
    }

    /**
     * 通过bridgeKey {@link Bridges}来获取对应的Bridge模块
     *
     * @param bridgeKey {@link Bridges}
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <V extends Object> V getBridge(String bridgeKey) {
        final Object bridge = model.mBridges.get(bridgeKey);
        if (bridge == null) {
            throw new NullPointerException("-no defined bridge-");
        }
        return (V) bridge;
    }
}

```
并且，通过BridgeLifeCycleListener 接口，实现各个底层功能管理类的统一初始化跟销毁工作，保持跟app的生命周期一致。代码如下：

```
/**
 * 如果Bridge层的生命周期和App的生命周期相关（在Application
 * onCreate的时候初始化，在用户双击back键退出），则实现此接口，届时统一初始化和销毁
 */
public interface BridgeLifeCycleListener {
    public void initOnApplicationCreate(Context context);

    public void clearOnApplicationQuit();
}

```
Manager类代码如下：
```
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

```

5）多页面交互
可能会有多个页面存在逻辑关系，比如HomeActivity加载图片成功后，要通知LoginActivity上面的EditText内容清除，当然这个需求是我瞎扯的，然而真正开发中的需求何尝不是这样呢。我擦，无意间流露出对产品经理的喜爱~~~~。
可能会有人用广播，用观察者，应该还有人会定义静态方法，去直接调用，不管怎么，我不评价，因为我之前也都用过。。。。
我们这边改成eventbus去做页面之间的交互，eventbus的好处，相信大家都清楚。
EventBus是一款针对Android优化的发布/订阅事件总线。主要功能是替代Intent,Handler,BroadCast在Fragment，Activity，Service，线程之间传递消息.优点是开销小，代码更优雅。以及将发送者和接收者解耦。

使用的代码如下：

HomeActivity.java类
```
 @Override
    public void initData() {
        Picasso.with(this).load("http://i.imgur.com/DvpvklR.png").resize(DensityUtil.dip2px(this,200), DensityUtil.dip2px(this,200)).centerCrop().into(image);
        EventBus.getDefault().post(Event.IMAGE_LOADER_SUCCESS);//发送刷新通知
    }
```
LoginActivity.java类

```
 @Override
    public void onEventMainThread(Event event) {
        super.onEventMainThread(event);
        switch (event){
            //接受通知
            case IMAGE_LOADER_SUCCESS:
                clearEditContent();
                break;
        }
    }
```

```
 /**
 * <事件类型>
 *
 * @author caoyinfei
 * @version [版本号, 2016/6/6]
 * @see [相关类/方法]
 * @since [V1]
 */
public enum Event {
    /**
     * 图片成功
     */
    IMAGE_LOADER_SUCCESS,

}
```
6）其他公共类的封装
当然还有很多类的封装，框架中都有涉及，这边由于时间问题不一一介绍了，大家可以自行研究。

 - Gson的封装使用
 - Log的封装，方便上线，调整优先级，关闭日志
 - FileUtil常用文件操作的封装
 - LocalFileStorageManager  本地文件缓存目录封装
 - SecurityUtils 加解密的封装(前面有文章介绍过原理，代码中不宜出现加密过程，暂时删除了)
 - 数据库封装 （前面有文章介绍过GreenDao，并有例子，这边不介绍了）
 http://blog.csdn.net/dfskhgalshgkajghljgh/article/details/51304390
 ......
 
 



6.其他思考
----

1.Android依赖注入的框架：Dagger、RoboGuice和ButterKnife，依赖注入的框架，见仁见智，有些人很推崇，但是我个人不怎么喜欢，首先影响了代码结构，代码交接成本高，个人小项目可以尝试使用，大的公司项目还是在考虑。
2.rxjava先在个人项目中使用，熟练后再在框架中会引入。

由于框架暂时还没有在项目中试用一段时间，细节方面，好的或者不足的还希望大家勿喷，提出来我会持续改进。


----------


欢迎一起交流讨论
群号：469890293
