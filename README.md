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


# 存储
### 偏好存储
#### SharedPreferences
SP的操作封装类SpManager：
- 获取默认实例：`SpManager.defaultInstance(context)`
- 获取指定实例：`SpManager.of(context, name)`
- 获取：`sp.getXX(key, defValue)`
- 存入：`sp.putXX(key, value)`

建议对SpManager进行业务封装，举例如下：
```
object UserSp {
    private lateinit var sp: SpManager
    private const val _userId = "userId"


    fun init(userId: String) {
        sp = SpManager.of(CommonUtil.getContext(), userId)
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
MMKV是腾讯开源的键值对存储库，拥有非常高的性能以及支持跨进程，它的操作封装类MMKVUtil：
- 初始化：`MMKVUtil.init(Context context)`
- 获取：`MMKVUtil.get(key, defValue)`
- 存入：`MMKVUtil.put(key, value)`
- 移除：`MMKVUtil.remove(key)`

建议对MMKVUtil进行业务封装，举例如下：
```
object DataHelper {
    private const val PLAY_VOICE = "PlayVoice"
    

    fun setPlayVoice(playVoice: PlayVoice) {
        MMKVUtil.put(PLAY_VOICE, GsonUtil.toJson(playVoice))
    }

    fun getPlayVoice(): PlayVoice {
        val json = MMKVUtil.get(PLAY_VOICE, "")
        return GsonUtil.toBean(json, PlayVoice::class.java) ?: PlayVoice("", "")
    }

    fun clearPlayVoice() {
        MMKVUtil.remove(PLAY_VOICE)
    }
}    
```
```
// 使用方式
val playVoice = DataHelper.getPlayVoice()
```



### 数据库存储
#### Room
为了便于数据库的使用，封装了独立模块database：
- 数据库表：在model目录，按需扩展表。
- 访问接口：在dao目录，按需扩展访问接口。
- 数据库的管理：在db目录，管理创建、更新、迁移等。

使用方式如下：
```
// 1.初始化参数（一般用户登录后）
DBHelper.init(this, userId)

// 2.存入和访问数据
DBFactory.getUserDao().saveOrUpdate(user)
val users = DBFactory.getUserDao().queryAll()

// 3.关闭数据库（一般用户退出登录后）
DBHelper.closeDB()
```