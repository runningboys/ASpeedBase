package com.common.utils.socket


interface SocketApi {

    companion object {
        private const val DEFAULT_NAME = "default-WebSocket"
        val instanceMap = mutableMapOf<String, SocketApi>()

        /**
         * 获取默认实例
         */
        fun defaultInstance(): SocketApi {
            return of(DEFAULT_NAME)
        }

        /**
         * 获取对应实例
         */
        fun of(name: String): SocketApi {
            return instanceMap[name] ?: WebSocketImpl(name).apply { instanceMap[name] = this }
        }


        /**
         * 关闭全部
         */
        fun closeAll() {
            instanceMap.values.forEach { it.close() }
        }
    }


    fun connectSocket(url: String, headers: Map<String, String>, onOpen: () -> Unit, onClose: ((code: Int, reason: String) -> Unit)? = null)

    fun updateHeaders(headers: Map<String, String>)

    fun addListener(label: String, onMessage: (message: String) -> Unit, onBytes: (bytes: ByteArray) -> Unit)

    fun removeListener(label: String)

    fun send(text: String)

    fun send(bytes: ByteArray)

    fun isConnected(): Boolean

    fun close()
}