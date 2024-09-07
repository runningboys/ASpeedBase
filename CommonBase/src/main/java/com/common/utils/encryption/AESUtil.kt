package com.common.utils.encryption

import android.util.Base64
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

object AESUtil {
    /**
     * 初始化向量(类似给明文加盐，增加混淆度)
     */
    private const val IV_STRING = "12345@abc"

    /**
     * 默认编码
     */
    private const val charset = "UTF-8"

    /**
     * 算法/加密模式/数据填充方式
     */
    private const val CIPHER_ALGORITHM = "AES/CBC/PKCS5Padding"


    /**
     * AES加密
     *
     * @param content 明文
     * @param key     秘钥
     * @return
     */
    fun encrypt(content: String, key: String): String {
        val contentBytes = content.toByteArray(charset(charset))
        val keyBytes = key.toByteArray(charset(charset))
        val encryptedBytes = encrypt(contentBytes, keyBytes)
        return Base64.encodeToString(encryptedBytes, Base64.NO_WRAP)
    }


    /**
     * AES解密
     *
     * @param content 密文
     * @param key     秘钥
     * @return
     */
    fun decrypt(content: String, key: String): String {
        val encryptedBytes = Base64.decode(content, Base64.DEFAULT)
        val keyBytes = key.toByteArray(charset(charset))
        val decryptedBytes = decrypt(encryptedBytes, keyBytes)
        return String(decryptedBytes, charset(charset))
    }


    /**
     * AES加密
     *
     * @param contentBytes 明文
     * @param keyBytes     秘钥
     * @return
     */
    fun encrypt(contentBytes: ByteArray, keyBytes: ByteArray): ByteArray {
        return cipherOperation(contentBytes, keyBytes, Cipher.ENCRYPT_MODE)
    }


    /**
     * AES解密
     *
     * @param contentBytes 密文
     * @param keyBytes     秘钥
     * @return
     */
    fun decrypt(contentBytes: ByteArray, keyBytes: ByteArray): ByteArray {
        return cipherOperation(contentBytes, keyBytes, Cipher.DECRYPT_MODE)
    }

    private fun cipherOperation(contentBytes: ByteArray, keyBytes: ByteArray, mode: Int): ByteArray {
        // 构造AES秘钥
        val secretKey = SecretKeySpec(keyBytes, "AES")

        // 构造向量参数
        val initParam = IV_STRING.toByteArray(charset(charset))
        val ivParameterSpec = IvParameterSpec(initParam)

        // 创建算法实例
        val cipher = Cipher.getInstance(CIPHER_ALGORITHM)
        cipher.init(mode, secretKey, ivParameterSpec)

        // 执行加密或解密
        return cipher.doFinal(contentBytes)
    }
}