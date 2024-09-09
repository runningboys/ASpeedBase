package com.common.utils.network

import com.blankj.utilcode.util.NetworkUtils
import com.blankj.utilcode.util.ThreadUtils
import com.common.utils.glide.SslSocketClient
import com.common.utils.log.LogUtil
import com.common.utils.thread.UIHandler
import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ServerHandshake
import org.json.JSONObject
import java.net.URI
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit

class WebSocketManager private constructor(val name: String) {
    private val TAG = "WebSocketManager"
    private var reconnectCount = 0
    private var maxConnectCount = 6
    private var isInit = false
    private var isConnected = false
    private var isNetConnected = true
    private lateinit var uri: URI
    private lateinit var onOpen: () -> Unit
    private lateinit var client: WebSocketClient
    private var onClose: ((code: Int, reason: String) -> Unit)? = null
    private var eventMap = mutableMapOf<String, Function1<String, Unit>>()


    init {
        NetworkUtils.registerNetworkStatusChangedListener(object : NetworkUtils.OnNetworkStatusChangedListener {
            override fun onDisconnected() {
                isNetConnected = false
                LogUtil.e(TAG, "网络已断开")
            }

            override fun onConnected(networkType: NetworkUtils.NetworkType?) {
                isNetConnected = true
                LogUtil.e(TAG, "网络已连接")
                if (!isInit) return
                reconnectCount = 0
                toReconnect(1000)
            }
        })
    }



    companion object {
        private const val DEFAULT_NAME = "default-WebSocket"
        private val instanceMap = ConcurrentHashMap<String, WebSocketManager>()

        /**
         * 获取默认实例
         */
        fun defaultInstance(): WebSocketManager {
            return of(DEFAULT_NAME)
        }

        /**
         * 获取对应实例
         */
        fun of(name: String): WebSocketManager {
            return instanceMap.getOrPut(name) { WebSocketManager(name) }
        }
    }




    // 连接WebSocket
    fun connectSocket(uri: URI, headers: Map<String, String>, onOpen: () -> Unit, onClose: ((code: Int, reason: String) -> Unit)? = null) {
        this.uri = uri
        this.onOpen = onOpen
        this.onClose = onClose

        realConnect(headers)
        isInit = true
    }


    private fun realConnect(headers: Map<String, String>) {
        isNetConnected = NetworkUtils.isConnected()
        client = object : WebSocketClient(uri, headers) {
            override fun onOpen(handshakedata: ServerHandshake) {
                isConnected = true
                reconnectCount = 0
                LogUtil.d(TAG, "onOpen")
                UIHandler.run { onOpen() }
            }

            override fun onMessage(message: String) {
                LogUtil.d(TAG, "onMessage:$message")
                val jsonObj = JSONObject(message)
                if (jsonObj.has("cmd") && jsonObj.optString("cmd") == "pong") return
                UIHandler.run { eventMap.values.forEach { it.invoke(message) } }
            }

            override fun onClose(code: Int, reason: String?, remote: Boolean) {
                isConnected = false
                UIHandler.run { onClose?.invoke(code, reason.orEmpty()) }
                LogUtil.d(TAG, "onClose --> count:$reconnectCount  code:$code  reason:${reason.orEmpty()}")

                /*if (reconnectCount >= maxConnectCount) {
                    LogUtil.e(TAG, "onClose --> 超过最大重连次数")
                    return
                }*/

                // 进行最多6次重连，每次间隔6秒
                toReconnect(6000)
            }

            override fun onError(ex: java.lang.Exception?) {
                LogUtil.d(TAG, "onError --> " + ex?.message)
            }

        }

        // 设置wss
        if (uri.scheme == "wss") {
            client.setSocketFactory(SslSocketClient.getSSLSocketFactory())
        }

        // 开始connect
        client.connect()
        LogUtil.d(TAG, "开始connect $name")

        heartbeat()
    }


    private fun heartbeat() {
        UIHandler.run({
            send("{\"cmd\":\"ping\"}")
            heartbeat()
        }, 30 * 1000)
    }


    fun addListener(label: String, onMessage: (message: String) -> Unit) {
        eventMap[label] = onMessage
    }


    fun removeListener(label: String) {
        eventMap.remove(label)
    }


    // 发送消息
    fun send(text: String) {
        try {
            if (isNetConnected) {
                client.send(text)
            }
        } catch (e: Exception) {
            LogUtil.e(TAG, e.message.orEmpty())
        }
    }


    private fun toReconnect(delayMills: Long) = executeTask(delayMills) {
        if (isConnected) {
            LogUtil.e(TAG, "reconnect --> 早已重连")
            return@executeTask
        }

        if (!NetworkUtils.isConnected()) {
            LogUtil.e(TAG, "reconnect --> 网络不可用")
            return@executeTask
        }

        client.reconnect()
        reconnectCount++
        LogUtil.d(TAG, "reconnect --> count:$reconnectCount")
    }


    private fun executeTask(delayMills: Long, task: () -> Unit) {
        LogUtil.i(TAG, "reconnect --> 执行重连", 4)
        ThreadUtils.executeByCachedWithDelay(object : ThreadUtils.SimpleTask<Unit>() {
            override fun doInBackground() {
                task()
            }

            override fun onSuccess(result: Unit?) {

            }
        }, delayMills, TimeUnit.MILLISECONDS)
    }

}