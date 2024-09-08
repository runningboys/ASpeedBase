一个Android快速集成基础框架依赖的库

Gradle依赖方式：
1. Project下的build.gradle
```
maven { url 'https://jitpack.io' }
```

2. app下的build.gradle
```
implementation 'com.github.runningboys:ASpeedBase:1.0'
```

# 必要依赖配置
#### BaseApp
继承并配置清单
```
class App : BaseApp() {

    override fun onCreate() {
        super.onCreate()
        AppManager.init(this)
        
        // 用户已登录
        val userId = AppSp.getLoginUserId()
        if (userId.isNotBlank()) {
            AppSp.login(userId)
            
            // 初始DB的用户
            DBHelper.init(userId)
        }
    }
}
```
```
<application
        android:name=".App"
        android:allowBackup="true"
        android:icon="@mipmap/ic_launcher"
        android:label="@string/app_name"
        android:roundIcon="@mipmap/ic_launcher_round"
        android:supportsRtl="true">
</application>        
```




# 架构（MVP、MVVM）
### 基类
#### BaseActivity
BaseActivity继承自AppCompatActivity的基类：
- 封装EventBus
- 封装网络状态
- 封装View、Listener、Data初始化方法
- 封装IBaseView常用showLoading、showMessage、onError等方法

使用方式如下：
```
class MainActivity : BaseActivity() {
    override fun getLayoutId(): Int {
        return R.layout.activity_main
    }
    
    override fun initView() {

    }

    override fun initListener() {

    }

    override fun initData() {

    }

    override fun showLoading() {
        
    }

    override fun hideLoading() {
        
    }

    override fun showMessage(message: String) {
        super.showMessage(message)
    }

    override fun onError(code: Int, message: String?) {
        super.onError(code, message)
    }

    override fun onNetworkStateChanged(isNetAvailable: Boolean) {
        super.onNetworkStateChanged(isNetAvailable)
    }
}
```

简化使用EventBus，如下：
```
// 1.覆写onMessageEvent方法接收事件：
override fun <T> onMessageEvent(eventName: String, data: T) {
    when (eventName) {
        EventName.update_nickname -> {
            val nickname = data as String
            // todo
        }
        EventName.update_avatar -> {
            val avatar = data as String
            // todo
        }
    }
}


// 2.发送事件
EventBusUtil.post(EventName.update_nickname, "张三")
```



#### BindingActivity
BindingActivity继承自BaseActivity的封装ViewBinding的基类，使用方式如下：
```
class MainActivity : BindingActivity<ActivityMainBinding>() {
    
    override fun initView() {
        binding.titleTv.text = "hello"
    }

    override fun initListener() {
        
    }

    override fun initData() {

    }
}
```


### MVP
#### MvpBindingActivity
MvpBindingActivity继承自BindingActivity的基类，使用方式如下：
```
// 1. 创建契约接口
/**
 * 契约接口，用于分离View和Presenter
 */
interface TwoContract {

    /**
     * View层接口
     */
    interface View : IBaseView {
        fun onLogin()
    }

    
    /**
     * Presenter层接口
     */
    abstract class Presenter(context: Context, view: View) : BasePresenter<View>(context, view) {
        abstract fun login(phone: String, password: String)
    }
}


// 2.创建Presenter实现类
class TwoPresenter(context: Context, view: TwoContract.View) : TwoContract.Presenter(context, view) {
    
    override fun login(phone: String, password: String) = launch {
        UserRepository.login(phone, password).collect {
            mView?.onLogin()
        }
    }

}


// 3.继承MvpBindingActivity并实现契约
class TwoActivity : MvpBindingActivity<ActivityTwoBinding, TwoContract.Presenter>(), TwoContract.View {

    override fun createPresenter(): TwoContract.Presenter {
        return TwoPresenter(this, this)
    }

    override fun initView() {
    }

    override fun initListener() {
        binding.btnLogin.setOnClickListener {
            mPresenter.login("xx", "x")
        }
    }

    override fun initData() {
    }

    override fun onLogin() {
        ToastUtil.showToast("登录成功")
    }
}
```



### MVVM
#### MvvmBindingActivity
MvvmBindingActivity继承自BindingActivity的基类，使用方式如下：
```
// 1. 创建继承自BaseViewModel的ViewModel
class OneViewModel: BaseViewModel() {

    fun login(phone: String, password: String): LiveData<NetResult<UserBean>> {
        return UserRepository.login(phone, password).asLiveData()
    }
}


// 2. 继承MvvmBindingActivity
class OneActivity : MvvmBindingActivity<ActivityOneBinding, OneViewModel>() {
    override val viewModel: OneViewModel by viewModels()


    override fun initView() {
    }

    override fun initListener() {
        binding.btnLogin.setOnClickListener { toLogin() }
    }

    override fun initData() {
    }
    
    private fun toLogin() {
        viewModel.login("xx", "xx").observe(this, object : UIObserver<UserBean>(this) {
            override fun onSucceed(result: UserBean) {
                ToastUtil.showToast("登录成功")
            }
        })
    }
}
```



# 数据（偏好存储、数据库、网络数据、缓存）
### 偏好存储
#### SharedPreferences
SP的操作类SpManager：
- 获取默认实例：`SpManager.defaultInstance()`
- 获取指定实例：`SpManager.of(name)`
- 获取：`getXX(key, defValue)`
- 存入：`putXX(key, value)`

对SpManager进行业务封装，如下：
```
object UserSp {
    private lateinit var sp: SpManager
    private const val _userId = "userId"


    /**
     * 初始用户SP
     */
    fun init(userId: String) {
        sp = SpManager.of(userId)
    }


   fun putUserId(userId: String) {
        return sp.putString(_userId, userId);
    }

    fun getUserId(): String {
        return sp.getString(_userId, "")
    }
}    
```
```
// 使用方式
val userId = UserSp.getUserId()
```


#### MMKV
MMKV是腾讯开源的键值对存储库，拥有非常高的性能以及支持跨进程。
MMKV的操作类MMKVManager：
- 获取默认实例：`MMKVManager.defaultInstance()`
- 获取指定实例：`MMKVManager.of(name)`
- 获取：`getXX(key, defValue)`
- 存入：`putXX(key, value)`
- 迁移SharedPreferences到MMKV：`transferSpToMMKV(sp)`

对MMKVManager进行业务封装，直接替换SpManager为MMKVManager，如下：
```
object UserSp {
    private lateinit var sp: MMKVManager
    private const val _userId = "userId"


    /**
     * 初始用户SP
     */
    fun init(userId: String) {
        sp = MMKVManager.of(userId)
    }


   fun putUserId(userId: String) {
        return sp.putString(_userId, userId);
    }

    fun getUserId(): String {
        return sp.getString(_userId, "")
    }
}    
```
```
// 使用方式
val userId = UserSp.getUserId()
```



### 数据库存储
#### Room
数据库封装在data模块的database中：
- 数据库表：在model目录，按需扩展表。
- 访问接口：在dao目录，通过DBFactory访问各接口类。
- 数据库的管理：在db目录，管理创建、更新、迁移等。

使用方式如下：
```
// 1.初始化参数（一般用户登录后）
DBHelper.init(userId)

// 2.存入和访问数据
DBFactory.getUserDao().saveOrUpdate(user)
val users = DBFactory.getUserDao().queryAll()

// 3.关闭数据库（一般用户退出登录后）
DBHelper.closeDB()
```

数据库升级迁移：
```
// 1.在DBMigration类中数据迁移，举例如下：
override fun migrate(database: SupportSQLiteDatabase) {
        when (endVersion) {
            2 -> {
                // 举例
                database.execSQL("ALTER TABLE Meta ADD COLUMN busItemId TEXT");
            }
            3 -> {
                database.execSQL("···");
            }
        }
    }


// 2.在AppDataBase类注解的entities中添加新表：
@Database(
    entities = [UserDB::class],
    version = DBConfig.DB_VERSION,
    exportSchema = false
)
@TypeConverters(value = [Converters::class])
abstract class AppDataBase : RoomDatabase()


// 3.在DBConfig类中升级DB_VERSION的版本号：
object DBConfig {

    /**
     * 数据库版本号
     * 注意：数据库表结构变化后，需要手动修改数据库版本号
     */
    const val DB_VERSION = 1


    /**
     * 数据库名称
     */
    @JvmStatic
    fun getDBName(userId: String): String {
        return "userId_${userId}_db"
    }
}
```


### 网络数据
#### OKHttp和Retrofit
网络数据封装在data模块的network中：
- 数据模型：在model目录存放数据模型。
- 访问接口：在api目录，通过ApiFactory访问各接口类。。
- 请求管理：在manager目录，管理OKHttp、Retrofit和接口实例等。


使用方式如下：
```
// 1.创建UserApi：
/**
 * 用户api接口
 */
interface UserApi {

    /**
     * 登录
     */
    @POST("/user/login")
    suspend fun login(@Body params: Map<String, String>): NetResult<UserBean>
}


// 2.创建UserService：
/**
 * 用户请求服务
 * 实现并封装api接口服务
 */
object UserService: BaseService() {
    private val mApiService by lazy { ApiManager.defaultInstance().getApiService(UserApi::class.java) }


    /**
     * 登录
     */
    suspend fun login(phone: String, password: String): NetResult<UserBean> {
        val params = mutableMapOf<String, String>()
        params["phone"] = phone
        params["password"] = password
        return handleResult { mApiService.login(params) }
    }

}

// 3.访问接口
val result = ApiFactory.getUserService().login(phone, password)
if (result.isSuccess()) {
    // 成功处理
} else {
    // 失败处理
}

```


### 缓存
缓存封装在data模块的cache中：
- 访问接口：通过CacheFactory访问各接口类。
- 缓存管理：在DataCacheManager类管理缓存的构建和清理等。


使用方式如下：
```
// 1.按业务创建UserCache：
class UserCache {
    private val cache = ConcurrentHashMap<String, UserBean>()

    /**
     * 获取用户
     */
    fun getUser(userId: String): UserBean? {
        return cache[userId]
    }


    /**
     * 保存用户
     */
    fun save(user: UserBean) {
        cache[user.id] = user
    }

    /**
     * 移除数据
     */
    fun remove(userId: String) {
        cache.remove(userId)
    }
}


// 2.按需求构建缓存
CacheManager.buildCache()


// 3.访问缓存
val user = CacheFactory.getUserCache().getUser(userId)
```



### 数据仓库
数据仓库用于封装组合数据库、网络请求和缓存等接口，实现数据对外的提供。
数据仓库封装在data模块的repository中：

- 数据访问：在repository目录下存放各业务数据仓库，按需访问。


使用方式如下：
```
// 1.按业务创建UserRepository：
/**
 * 用户数据仓库
 */
object UserRepository {

    /**
     * 登录
     */
    fun login(phone: String, password: String) = flow {
        // 发起登录请求
        val result = UserService.login(phone, password)

        // 登录成功
        if (result.isSuccess()) {
            val user = result.data!!
            // 记录登录并保存到SP
            AppSp.login(user.id)
            UserSp.putUser(user)

            // 保存到缓存和数据库
            CacheFactory.getUserCache().save(user )
            DBFactory.getUserDao().saveOrUpdate(user.toUserDB())
        }

        // 发送结果
        emit(result)
    }
}


// 2.访问数据仓库
方式一：
UserRepository.login(phone, password).collect {
    mView?.onLogin()
}


方式二：
1）封装
fun login(phone: String, password: String): LiveData<NetResult<UserBean>> {
    return UserRepository.login(phone, password).asLiveData()
}

2）调用
viewModel.login("xx", "xx").observe(this, object : UIObserver<UserBean>(this) {
    override fun onSucceed(result: UserBean) {
        // todo
    }
})
```



# 工具
### 日志
#### LogUtil
日志工具支持控制台打印、日志文件打印、调用栈打印等。
使用方式：
```
// 1. 初始化日志配置
private fun initLogger(context: Context) {
    if (isDebug) {
        val logDir = File(context.getExternalFilesDir(""), "log")
        LogUtil.addDiskLogHandle(context, logDir.absolutePath)
        LogUtil.addCommonLogHandle()
        LogUtil.setLogTag("TestDemo")
        LogUtil.isLoggable = true
    } else {
        LogUtil.removeAllHandles()
        LogUtil.isLoggable = false
    }
}


// 2.日志打印
LogUtil.i("普通打印")
LogUtil.i("tag", "普通打印")
LogUtil.i("调用栈打印", 5)
LogUtil.e("tag", "异常打印", throwable)
LogUtil.json("格式化json打印", "{\"name\":\"张\",\"age\":18}")
```


### 耗时统计
#### TimingRecorder
任意位置添加记录，并最终打印所有位置的顺序耗时和总耗时。
使用方式：
```
// 1.任意位置添加记录
TimingRecorder.addRecord(label, "work");
// ... do some work A ...
TimingRecorder.addRecord(label, "work A");
// ... do some work B ...
TimingRecorder.addRecord(label, "work B");
// ... do some work C ...
TimingRecorder.addRecord(label, "work C");

// 2.执行耗时打印
TimingRecorder.logTime(label);


// 耗时打印的结构
 * 标签: label <开始>
 * 0 ms  message: 开始
 * 1501 ms  message: 执行A
 * 302 ms  message: 执行B
 * 2002 ms  message: 执行完成~
 * <结束>   总耗时: 3805 ms   记录次数: 4 次
```


### 崩溃处理
#### AppCrashHandler
捕获App的崩溃信息，并根据策略进行应用退出、应用重启或应用恢复。
使用方式：
```
// 初始化
val crashDir = File(context.getExternalFilesDir(""), "crash")
AppCrashHandler.init(crashDir.absolutePath, CrashStrategy.ExitsApp)

// 策略描述
应用退出：崩溃后退出应用。
应用重启：崩溃后杀掉应用进程再重启，如果继续崩溃可能一直重启或异常。
应用恢复：捕获主线程崩溃并恢复MainLooper，但可能保持为异常状态。
```



### 事件总线
#### EventBusUtil
封装EventBus工具，以字符串eventName为事件名，用Event做数据载体，避免原EventBus每个事件创建不同对象的繁琐。
使用方式：
```
// 1.创建事件名称
object EventName {

    // 更新用户昵称
    const val update_nickname = "update_nickname"

    // 更新用户头像
    const val update_avatar = "update_avatar"
}


// 2.1 在Activity中时，直接覆写onMessageEvent方法接收事件：
override fun <T> onMessageEvent(eventName: String, data: T) {
    when (eventName) {
        EventName.update_nickname -> {
            val nickname = data as String
            // todo
        }
        EventName.update_avatar -> {
            val avatar = data as String
            // todo
        }
    }
}


// 2.2 在其他类中则需要注册和销毁
// 1）注册事件通知
EventBusUtil.register(this) 

// 2）接收事件
@Subscribe(threadMode = ThreadMode.MAIN)
fun <T> onEventBusCome(event: Event<T>) { 
}

// 3）销毁事件通知
EventBusUtil.unregister(this) 



// 3.发送事件
EventBusUtil.post(EventName.update_nickname, "张三")
```



### 吐司
#### ToastUtil


### 图片加载
#### GlideUtil


### UI线程
#### UIHandler


### 线程池
#### ThreadUtil