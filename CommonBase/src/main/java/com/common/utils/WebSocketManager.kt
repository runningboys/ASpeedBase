package com.common.utils

import com.common.receiver.NetworkStateReceiver
import com.common.utils.glide.SslSocketClient
import com.common.utils.log.LogUtil
import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ServerHandshake
import org.json.JSONObject
import java.net.URI

class WebSocketManager private constructor() {
    private val TAG = "WebSocketManager"
    private var reconnectCount = 0
    private var maxConnectCount = 6
    private var isInit = false
    private lateinit var uri: URI
    private lateinit var onOpen: () -> Unit
    private lateinit var client: WebSocketClient
    private var onClose: ((code: Int, reason: String) -> Unit)? = null
    private var eventMap = mutableMapOf<String, Function1<String, Unit>>()


    init {
        NetworkStateReceiver.instance.addListener(object : NetworkStateReceiver.NetworkStateChangedListener {
            override fun onNetworkStateChanged(isNetAvailable: Boolean) {
                if (isNetAvailable) {
                    LogUtil.e(TAG, "网络已连接")
                    if (!isInit) return
                    reconnectCount = 0
                    toReconnect()
                } else {
                    LogUtil.e(TAG, "网络已断开")
                }
            }

        })
    }



    companion object {
        private const val DEFAULT_NAME = "default-WebSocket"
        private val instanceMap = mutableMapOf<String, WebSocketManager>()

        /**
         * 获取默认实例
         *
         * @param context
         * @return
         */
        fun defaultInstance(): WebSocketManager {
            return of(DEFAULT_NAME)
        }

        /**
         * 获取对应实例
         *
         * @param context
         * @param name
         * @return
         */
        fun of(name: String): WebSocketManager {
            var instance = instanceMap[name]
            if (instance == null) {
                instance = WebSocketManager()
                instanceMap[name] = instance
            }
            return instance
        }
    }




    // 连接WebSocket
    fun connectSocket(uri: URI, onOpen: () -> Unit, onClose: ((code: Int, reason: String) -> Unit)? = null) {
        this.uri = uri
        this.onOpen = onOpen
        this.onClose = onClose

        realConnect()
        isInit = true
    }
    
    
    private fun realConnect() {
        client = object : WebSocketClient(uri) {
            override fun onOpen(handshakedata: ServerHandshake) {
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
                UIHandler.run { onClose?.invoke(code, reason.orEmpty()) }
                LogUtil.d(TAG, "onClose --> reconnectCount:$reconnectCount")

                if (reconnectCount >= maxConnectCount) {
                    LogUtil.e(TAG, "onClose --> 超过最大重连次数")
                    return
                }

                // 进行最多6次重连，每次间隔5秒
                UIHandler.run({ toReconnect() }, 5000)
            }

            override fun onError(ex: java.lang.Exception?) {
                LogUtil.d(TAG, "onError" + ex?.message)
            }

        }

        // 设置wss
        if (uri.scheme == "wss") {
            client.setSocketFactory(SslSocketClient.getSSLSocketFactory())
        }

        // 开始connect
        client.connect()

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
            client.send(text)
        } catch (e: Exception) {
            LogUtil.e(TAG, e.message.orEmpty())
        }
    }


    fun toReconnect() {
        LogUtil.d(TAG, "reconnect --> 执行重连")
        if (!NetworkUtil.isNetAvailable()) {
            LogUtil.e(TAG, "reconnect --> 网络不可用")
            return
        }

        client.reconnect()
        reconnectCount++
        LogUtil.d(TAG, "reconnect --> count:$reconnectCount")
    }
}