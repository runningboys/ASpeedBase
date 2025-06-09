package com.common.utils.socket

import com.blankj.utilcode.util.NetworkUtils
import com.common.utils.glide.SslSocketClient
import com.common.utils.log.LogUtil
import com.common.utils.network.NetworkUtil
import com.common.utils.socket.SocketApi.Companion.instanceMap
import com.common.utils.thread.ThreadUtil
import com.common.utils.thread.UIHandler
import org.java_websocket.WebSocket
import org.java_websocket.client.WebSocketClient
import org.java_websocket.framing.CloseFrame
import org.java_websocket.framing.Framedata
import org.java_websocket.framing.PingFrame
import org.java_websocket.handshake.ServerHandshake
import java.net.URI
import java.nio.ByteBuffer

class WebSocketImpl(val name: String): SocketApi {
    private val TAG = "WebSocketImpl"
    private var isInit = false
    private var isConnecting = false
    private var isNetConnected = true
    private var reconnectCount = 0
    private lateinit var uri: URI
    private lateinit var headers: Map<String, String>
    private lateinit var onOpen: () -> Unit
    private var client: WebSocketClient? = null
    private var onClose: ((code: Int, reason: String) -> Unit)? = null
    private var messageMap = mutableMapOf<String, Function1<String, Unit>>()
    private var bytesMap = mutableMapOf<String, Function1<ByteArray, Unit>>()


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
                toReconnect(0, 1000)
            }
        })
    }


    // 连接WebSocket
    override fun connectSocket(url: String, headers: Map<String, String>, onOpen: () -> Unit, onClose: ((code: Int, reason: String) -> Unit)?) {
        this.uri = URI(url)
        this.headers = headers
        this.onOpen = onOpen
        this.onClose = onClose

        realConnect()
        isInit = true
        isConnecting = true
    }

    override fun updateHeaders(headers: Map<String, String>) {
        this.headers = headers
        headers.forEach { client?.addHeader(it.key, it.value) }
    }


    private fun realConnect() {
        isNetConnected = NetworkUtil.isNetAvailable()
        client = object : WebSocketClient(uri, headers) {
            override fun onOpen(handshakedata: ServerHandshake) {
                LogUtil.i(TAG, "连接成功")
                if (!isInit || this != client) return
                reconnectCount = 0
                isConnecting = false
                sendPing()
                UIHandler.run { onOpen() }
            }

            override fun onMessage(message: String) {
                LogUtil.d(TAG, "onMessage:$message")
                UIHandler.run { messageMap.values.forEach { it.invoke(message) } }
            }

            override fun onMessage(bytes: ByteBuffer) {
                UIHandler.run { bytesMap.values.forEach { it.invoke(bytes.array()) } }
            }

            override fun onPreparePing(conn: WebSocket?): PingFrame {
                LogUtil.i(TAG, "ping --> name:$name")
                return super.onPreparePing(conn)
            }

            override fun onWebsocketPong(conn: WebSocket?, f: Framedata?) {
                super.onWebsocketPong(conn, f)
                LogUtil.i(TAG, "pong --> name:$name")
            }

            override fun onClose(code: Int, reason: String?, remote: Boolean) {
                LogUtil.e(TAG, "onClose --> name:$name  code:$code  reason:${reason.orEmpty()}")
                if (!isInit || this != client) return
                isConnecting = false
                UIHandler.run { onClose?.invoke(code, reason.orEmpty()) }
                val delayMills = reconnectCount.coerceAtMost(5) * 1000L
                toReconnect(code, delayMills)
            }

            override fun onError(ex: java.lang.Exception?) {
                LogUtil.e(TAG, "onError --> name:$name  Exception:" + ex?.message)
            }
        }

        // 设置wss
        if (uri.scheme == "wss") {
            client?.setSocketFactory(SslSocketClient.getSSLSocketFactory())
        }

        client?.connectionLostTimeout = 30

        // 开始connect
        client?.connect()
        LogUtil.i(TAG, "开始connect $name")
    }


    override fun addListener(label: String, onMessage: (message: String) -> Unit, onBytes: (bytes: ByteArray) -> Unit) {
        messageMap[label] = onMessage
        bytesMap[label] = onBytes
    }


    override fun removeListener(label: String) {
        messageMap.remove(label)
        bytesMap.remove(label)
    }


    // 发送消息
    override fun send(text: String) {
        try {
            if (isInit && isNetConnected) {
                client?.send(text)
            } else {
                LogUtil.w(TAG, "$name  send failed. isNetConnected:$isNetConnected")
            }
        } catch (e: Exception) {
            LogUtil.e(TAG, name, e)
        }
    }

    override fun send(bytes: ByteArray) {
        try {
            if (isInit && isNetConnected && isConnected()) {
                client?.send(bytes)
            } else {
                LogUtil.w(TAG, "$name  send failed  isNetConnected:$isNetConnected  isConnected:${isConnected()}")
            }
        } catch (e: Exception) {
            LogUtil.e(TAG, name, e)
        }
    }


    override fun close() {
        client?.connectionLostTimeout = 0
        client?.close()
        client = null
        isInit = false
        isConnecting = false
        messageMap.clear()
        bytesMap.clear()
        instanceMap.remove(name)
        LogUtil.i(TAG, "关闭连接 $name")
    }


    override fun isConnected(): Boolean {
        return client?.isOpen ?: false
    }


    fun toReconnect(code: Int, delayMills: Long) = executeTask(delayMills) {
        if (!isInit) return@executeTask

        if (isConnected()) {
            LogUtil.w(TAG, "reconnect --> 早已重连")
            return@executeTask
        }

        if (!NetworkUtil.isNetAvailable()) {
            LogUtil.w(TAG, "reconnect --> 网络不可用")
            return@executeTask
        }

        if (isConnecting) {
            LogUtil.w(TAG, "reconnect --> 正在连接中")
            return@executeTask
        }

        isConnecting = true
        reconnectCount++
        headers.forEach { client?.addHeader(it.key, it.value) }
        LogUtil.i(TAG, "toReconnect --> count:$reconnectCount")
        if (code == CloseFrame.NEVER_CONNECTED) {
            client?.connectionLostTimeout = 0
            client?.close()
            client = null
            realConnect()
        } else {
            client?.reconnect()
        }
    }


    private fun executeTask(delayMills: Long, task: () -> Unit) {
        ThreadUtil.schedule({ task() }, delayMills)
    }

}