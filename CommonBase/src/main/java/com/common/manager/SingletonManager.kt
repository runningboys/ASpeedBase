package com.common.manager

/**
 * 单例容器管理
 *
 * @author LiuFeng
 * @date 2017-11-01
 */
object SingletonManager {
    private val instanceMap: MutableMap<String, Any> = HashMap()
    fun registerInstance(key: String, instance: Any) {
        instanceMap[key] = instance
    }

    fun getInstance(key: String): Any? {
        return instanceMap[key]
    }

    fun getInstanceMap(): Map<String, Any> {
        return HashMap(instanceMap)
    }

    fun containsKey(key: String): Boolean {
        return instanceMap.containsKey(key)
    }

    fun containsValue(instance: Any): Boolean {
        return instanceMap.containsValue(instance)
    }

    val isEmpty: Boolean
        get() = instanceMap.isEmpty()

    fun size(): Int {
        return instanceMap.size
    }

    fun removeInstance(key: String) {
        if (instanceMap.containsKey(key)) {
            instanceMap.remove(key)
        }
    }

    fun clearAll() {
        instanceMap.clear()
    }
}