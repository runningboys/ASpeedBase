package com.common.utils.socket

import com.blankj.utilcode.util.NetworkUtils
import com.common.receiver.NetworkStateReceiver
import com.common.utils.glide.SslSocketClient
import com.common.utils.log.LogUtil
import com.common.utils.network.NetworkUtil
import com.common.utils.socket.SocketApi.Companion.instanceMap
import com.common.utils.thread.ThreadUtil
import com.common.utils.thread.UIHandler
import okhttp3.Headers.Companion.toHeaders
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okio.ByteString
import okio.ByteString.Companion.toByteString


class WebSocketImpl2(val name: String): SocketApi {
    private val TAG = "WebSocketImpl2"
    private var isInit = false
    private var isOpen = false
    private var isNetConnected = true
    private var url = ""
    private lateinit var headers: Map<String, String>
    private lateinit var onOpen: () -> Unit
    private var socket: WebSocket? = null
    private val okHttpClient = SslSocketClient.getOkHttpClient()
    private var onClose: ((code: Int, reason: String) -> Unit)? = null
    private var messageMap = mutableMapOf<String, Function1<String, Unit>>()
    private var bytesMap = mutableMapOf<String, Function1<ByteArray, Unit>>()


    init {
        NetworkStateReceiver.addListener(object : NetworkStateReceiver.NetworkStateChangedListener {
            override fun onNetworkStateChanged(isNetAvailable: Boolean) {
                isNetConnected = isNetAvailable
                if (isNetAvailable) {
                    LogUtil.e(TAG, "网络已连接")
                    if (!isInit) return
                    toReconnect(1000)
                } else {
                    LogUtil.e(TAG, "网络已断开")
                }
            }
        })
    }




    // 连接WebSocket
    override fun connectSocket(url: String, headers: Map<String, String>, onOpen: () -> Unit, onClose: ((code: Int, reason: String) -> Unit)?) {
        this.url = url
        this.headers = headers
        this.onOpen = onOpen
        this.onClose = onClose

        realConnect()
        isInit = true
    }

    override fun updateHeaders(headers: Map<String, String>) {
        this.headers = headers
    }


    private fun realConnect() {
        isNetConnected = NetworkUtil.isNetAvailable()
        val request: Request = Request.Builder().url(url).headers(headers.toHeaders()).build()
        socket = okHttpClient.newWebSocket(request, object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                isOpen = true
                LogUtil.d(TAG, "onOpen")
                UIHandler.run { onOpen() }
            }

            override fun onMessage(webSocket: WebSocket, message: String) {
                // 接收到服务端发来的消息
                LogUtil.d(TAG, "onMessage:$message")
                UIHandler.run { messageMap.values.forEach { it.invoke(message) } }
            }

            override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
                UIHandler.run { bytesMap.values.forEach { it.invoke(bytes.toByteArray()) } }
            }

            override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
                LogUtil.e(TAG, "onClosing --> name:$name  code:$code  reason:${reason}")
                isOpen = false
                if (!isInit) return
                UIHandler.run { onClose?.invoke(code, reason) }
                toReconnect(1000)
            }

            override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                LogUtil.e(TAG, "onClosed --> name:$name  code:$code  reason:${reason}")
                isOpen = false
            }

            override fun onFailure(webSocket: WebSocket, ex: Throwable, response: Response?) {
                LogUtil.e(TAG, "onError --> name:$name  Exception:", ex)
                isOpen = false
            }
        })

        LogUtil.d(TAG, "开始connect $name")
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
                socket?.send(text)
            } else {
                LogUtil.w(TAG, "$name  send failed. isNetConnected:$isNetConnected")
            }
        } catch (e: Exception) {
            LogUtil.e(TAG, name, e)
        }
    }

    override fun send(bytes: ByteArray) {
        try {
            if (isInit && isNetConnected) {
                socket?.send(bytes.toByteString())
            } else {
                LogUtil.w(TAG, "$name  send failed. isNetConnected:$isNetConnected")
            }
        } catch (e: Exception) {
            LogUtil.e(TAG, name, e)
        }
    }


    override fun close() {
        socket?.close(1000, null)
        socket = null
        isInit = false
        messageMap.clear()
        bytesMap.clear()
        instanceMap.remove(name)
        LogUtil.d(TAG, "关闭连接 $name")
    }


    override fun isConnected(): Boolean {
        return isOpen
    }


    fun toReconnect(delayMills: Long) = executeTask(delayMills) {
        if (!isInit) return@executeTask

        if (isConnected()) {
            LogUtil.e(TAG, "reconnect --> 早已重连")
            return@executeTask
        }

        if (!NetworkUtil.isNetAvailable()) {
            LogUtil.e(TAG, "reconnect --> 网络不可用")
            return@executeTask
        }

        socket?.close(1000, null)
        realConnect()
        LogUtil.d(TAG, "reconnect")
    }


    private fun executeTask(delayMills: Long, task: () -> Unit) {
        ThreadUtil.schedule({ task() }, delayMills)
    }

}