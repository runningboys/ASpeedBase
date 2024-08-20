package com.data.network.manager.interceptor

import android.text.TextUtils
import okhttp3.FormBody
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import java.io.IOException
import java.security.InvalidKeyException
import java.security.NoSuchAlgorithmException
import java.util.Arrays
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

/**
 * 添加公共参数的拦截器
 */
class GeneralParamInterceptor : Interceptor {
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        var oldRequest: Request = chain.request()
        val body = oldRequest.body
        if (body is FormBody) {
            val builder: FormBody.Builder = addSign(oldRequest.body as FormBody?)
            oldRequest = oldRequest.newBuilder().post(builder.build()).build()
        }
        return chain.proceed(oldRequest)
    }

    /**
     * 给所有的post请求添加sign
     *
     * 如果请求参数中不包含sign，那么在本方法中统一添加sign
     * 如果请求参数中包含sign，那么使用sign的值作为ticket给sign加密
     *
     * @param formBody 请求体
     * @return 重新生成的请求体
     */
    private fun addSign(formBody: FormBody?): FormBody.Builder {
//      1. 用于记录给sign签名的ticket
        var ticket = ""

//      2. 把请求中所有的参数放到HashMap中，如果参数中包含sign，则不添加到HashMap中，并记录sign的参数值
        val params = HashMap<String, String>()
        for (i in 0 until formBody!!.size) {
            val value = formBody.encodedValue(i)
            val name = formBody.encodedName(i)
            if (name == "sign") {
                ticket = value
            } else {
                params[formBody.encodedName(i)] = value
            }
        }

//      3. 判断参数中是否有sign
        val builder = FormBody.Builder()
        for ((key, value) in params) {
            builder.add(key, value)
        }
        if (!TextUtils.isEmpty(ticket)) {
            //请求中包含sign，使用sign的值作为ticket生成sign
            val sign = getSign(params, ticket)
            builder.add("sign", sign)
        } else {
            //请求中没有sign，添加sign(这里需要改成当前用户的ticket)
            val ticket1 = ""
            //            String ticket1 = UserSp.getTicket();
            builder.add("sign", getSign(params, ticket1))
        }
        return builder
    }

    companion object {
        /**
         * 获取加密串
         *
         * @param map
         *
         * @return
         */
        fun getSign(map: Map<String, String>, ticket: String): String {
            val joinStr = paramsJoint(map)
            return getSHA256(joinStr, ticket)
        }

        fun getSHA256(message: String, ticket: String): String {
            var encdeStr = ""
            try {
                val sha256_HMAC = Mac.getInstance("HmacSHA256")
                val secret_key = SecretKeySpec(ticket.toByteArray(), "HmacSHA256")
                sha256_HMAC.init(secret_key)
                val bytes = sha256_HMAC.doFinal(message.toByteArray())
                encdeStr = encodeHex(bytes)
            } catch (e: NoSuchAlgorithmException) {
                e.printStackTrace()
            } catch (e: InvalidKeyException) {
                e.printStackTrace()
            } catch (e: NullPointerException) {
                e.printStackTrace()
            }
            return encdeStr
        }

        /**
         * Used building output as Hex
         */
        private val DIGITS = charArrayOf(
                '0', '1', '2', '3', '4', '5', '6', '7',
                '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'
        )

        /**
         * Converts an array of bytes into an array of characters representing the hexadecimal values of each byte in order.
         * The returned array will be double the length of the passed array, as it takes two characters to represent any
         * given byte.
         *
         * @param data
         * a byte[] to convert to Hex characters
         * @return A String containing lower-case hexadecimal characters
         */
        fun encodeHex(data: ByteArray): String {
            val l = data.size
            val out = CharArray(l shl 1)

            // two characters form the hex value.
            var i = 0
            var j = 0
            while (i < l) {
                out[j++] = DIGITS[0xF0 and data[i].toInt() ushr 4]
                out[j++] = DIGITS[0x0F and data[i].toInt()]
                i++
            }
            return String(out)
        }

        /**
         * 拼接请求参数
         *
         * @param params 请求参数 uuid token 不需要加密所以过滤掉了
         *
         * @return 拼接后
         */
        private fun paramsJoint(params: Map<String, String>): String {
            val list: MutableList<String> = ArrayList()
            for (`object` in params.entries) {
                val (key1, value1) = `object`
                val key = key1 as String
                val value = value1 as String
                list.add("$key=$value")
            }
            val strings = list.toTypedArray()
            Arrays.sort(strings)
            val stringBuilder = StringBuilder()
            for (string in strings) {
                stringBuilder.append(string)
                stringBuilder.append("&")
            }
            return stringBuilder.substring(0, stringBuilder.length - 1)
        }
    }
}