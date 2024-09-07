package com.common.utils.network

import com.common.utils.log.LogUtil
import io.socket.client.Ack
import io.socket.client.IO
import io.socket.client.Manager
import io.socket.client.Socket
import io.socket.engineio.client.transports.WebSocket
import io.socket.thread.EventThread
import org.json.JSONArray


/**
 * SocketIO内部封装WebSocket的长连接工具
 */
object SocketIoClient {
    private const val TAG = "SocketIoClient"
    private lateinit var host: String
    private var mSocket: Socket? = null
    private var isReconnect = false
    private var state = ConnectionState.CLOSED


    fun init(host: String) {
        SocketIoClient.host = host
    }


    fun connect(onConnected:() -> Unit, onReconnected:() -> Unit) {
        LogUtil.d(TAG, "connect --> $host")
        if (state === ConnectionState.CONNECTING || state === ConnectionState.CONNECTED) {
            LogUtil.w(TAG, "socket.io: $state")
            return
        }

        state = ConnectionState.NEW
        val opts = IO.Options()
        opts.forceNew = true
        opts.reconnection = true
        opts.reconnectionDelay = 2000
        opts.reconnectionDelayMax = 3000
        opts.reconnectionAttempts = 6
        opts.secure = host.startsWith("https://")
        opts.transports = arrayOf(WebSocket.NAME)
        
        try {
            mSocket = IO.socket(host, opts)
            subscribeOn(Socket.EVENT_CONNECT) {
                LogUtil.i(TAG, "Socket connected")
                isReconnect = false
                onConnected()
            }

            subscribeOn(Manager.EVENT_RECONNECT) {
                LogUtil.i(TAG, "Socket reconnected")
                isReconnect = false
                state = ConnectionState.CONNECTED
                onReconnected()
            }

            subscribeOn(Socket.EVENT_DISCONNECT) {
                state = ConnectionState.CONNECTING
                LogUtil.i(TAG, "Socket disconnect")
            }
            subscribeOn(Socket.EVENT_CONNECT_ERROR) {
                LogUtil.i(TAG, "Socket connect error")
            }

            subscribeOn(Manager.EVENT_RECONNECT_ATTEMPT) {
                isReconnect = true
                state = ConnectionState.CONNECTING
            }

            subscribeOn(Manager.EVENT_RECONNECT_FAILED) {
                onFailed("socket.io 重连达最大次数，重连失败结束")
            }

            mSocket?.connect()
            LogUtil.d(TAG, "go to Connect")
        } catch (e: Exception) {
            LogUtil.e(TAG, e)
        }
    }


    fun close() {
        EventThread.exec {
            LogUtil.i(TAG, "socket close")
            if (mSocket != null) {
                mSocket?.close()
                mSocket = null
            }
            isReconnect = false
            state = ConnectionState.CLOSED
        }
    }

    /**
     * 订阅
     *
     * @param event
     * @param listener
     */
    fun subscribeOn(event: String, onCall: (data: JSONArray) -> Unit) {
        LogUtil.d(TAG, "subscribeOn --> event: $event")
        mSocket?.on(event) { args ->
            val data = transformJsonArray(*args)
            LogUtil.d(TAG, "subscribeOn --> event: " + event + " \n接收数据: " + data.toString(2))
            onCall(data)
        }
    }

    /**
     * 发布
     *
     * @param event
     * @param params
     * @param ack
     */
    fun publishOn(event: String, vararg params: Any, onAck: ((data: JSONArray) -> Unit)? = null) {
        LogUtil.d(TAG, "publishOn --> event: " + event + " \n发送数据: " + transformJsonArray(params).toString(2))

        mSocket?.emit(event, params, Ack { args ->
            val data = transformJsonArray(*args)
            LogUtil.d(TAG, "event: " + event + " \n接收数据: " + data.toString(2))
            onAck?.invoke(data)
        })
    }

    /**
     * 收到参数转换为jsonArray
     *
     * @param args
     * @return
     */
    private fun transformJsonArray(vararg args: Any): JSONArray {
        val jsonArray = JSONArray()
        if (args == null) {
            return jsonArray
        }
        for (arg in args) {
            jsonArray.put(arg)
        }
        return jsonArray
    }


    /**
     * 异常失败退出
     *
     * @param des
     */
    private fun onFailed(des: String) {
        state = ConnectionState.FAILED
        LogUtil.e(TAG, des)
    }


    enum class ConnectionState {
        NEW, CONNECTING, CONNECTED, CLOSED, FAILED
    }
}