package com.common.utils.network

import com.common.utils.thread.ThreadUtil
import com.common.utils.thread.UIHandler
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.regex.Pattern
import kotlin.math.abs
import kotlin.math.ceil


object NetworkQualityChecker {
    private var isRunning = false

    /**
     * ping检测网络质量
     *
     * 资料：https://blog.csdn.net/u012514113/article/details/137750028
     *
     * 格式：
     * PING 141.101.90.0 (141.101.90.0) 56(84) bytes of data.
     * 64 bytes from 141.101.90.0: icmp_seq=1 ttl=53 time=195 ms
     * 64 bytes from 141.101.90.0: icmp_seq=2 ttl=53 time=214 ms
     * 64 bytes from 141.101.90.0: icmp_seq=3 ttl=53 time=196 ms
     * 64 bytes from 141.101.90.0: icmp_seq=4 ttl=53 time=216 ms
     * --- 141.101.90.0 ping statistics ---
     * 5 packets transmitted, 4 received, 20% packet loss, time 806ms
     * rtt min/avg/max/mdev = 195.370/205.911/216.828/9.809 ms
     *
     */
    fun check(duration: Long = 3000, serverIp: String, callback: (data: NetworkQualityResult) -> Unit) = ThreadUtil.request {
        isRunning = true

        // "ping -c 4 -i 0.2 -w 1"
        val count = (duration / 300).toInt()
        val process = ProcessBuilder("ping", "-c", "$count", "-i", "0.3", "-w", "${duration/1000}", serverIp).start()
        val reader = BufferedReader(InputStreamReader(process.inputStream))

        var rtt = 0.0
        var lossRate = 0.0
        val latencies = mutableListOf<Double>()

        var line: String
        while (reader.readLine().also  { line = it.orEmpty() } != null) {
            if (line.contains("icmp_seq")) {
                val value = line.substringAfter("time=").substringBefore(" ms").trim()
                latencies.add(ceil(value.toDouble()))
            }
            else if (line.contains("%")) {
                val value = line.substringAfter("received, ").substringBefore("%").trim()
                lossRate = value.toDouble() / 100.0
            }
            else if (line.contains("rtt min/avg")) {
                val values = line.substringAfter("rtt min/avg/max/mdev = ").substringBefore(" ms").split("/")
                rtt = ceil(values[1].toDouble())
            }
        }

        // 计算抖动（延迟的标准差）
        val jitter = if (latencies.size  > 1) latencies.map  { abs(it - rtt) }.average() else 0.0

        UIHandler.run { callback.invoke(NetworkQualityResult(lossRate, ceil(jitter * 10) / 10, rtt)) }
    }


    fun stop() {
        isRunning = false
    }
}

// 网络质量结果类
data class NetworkQualityResult(
    val packetLossRate: Double, // 丢包率（百分比）
    val jitter: Double,         // 抖动（ms）
    val averageLatency: Double  // 平均延迟（ms）
)