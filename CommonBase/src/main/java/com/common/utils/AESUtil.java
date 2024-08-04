package com.common.utils;

import android.util.Base64;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class AESUtil {

    /**
     * 初始化向量(类似给明文加盐，增加混淆度)
     */
    private static final String IV_STRING = "@123456--java";

    /**
     * 默认编码
     */
    private static final String charset = "UTF-8";

    /**
     * 算法/加密模式/数据填充方式
     */
    private static final String CIPHER_ALGORITHM = "AES/CBC/PKCS5Padding";

    /**
     * AES加密
     *
     * @param content 明文
     * @param key     秘钥
     * @return
     */
    public static String aesEncryptString(String content, String key) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException {
        byte[] contentBytes = content.getBytes(charset);
        byte[] keyBytes = key.getBytes(charset);
        byte[] encryptedBytes = aesEncryptBytes(contentBytes, keyBytes);

        return Base64.encodeToString(encryptedBytes, Base64.NO_WRAP);
    }

    /**
     * AES解密
     *
     * @param content 密文
     * @param key     秘钥
     * @return
     */
    public static String aesDecryptString(String content, String key) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException {
        byte[] encryptedBytes = Base64.decode(content, Base64.DEFAULT);
        byte[] keyBytes = key.getBytes(charset);
        byte[] decryptedBytes = aesDecryptBytes(encryptedBytes, keyBytes);
        return new String(decryptedBytes, charset);
    }

    public static byte[] aesEncryptBytes(byte[] contentBytes, byte[] keyBytes) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException {
        return cipherOperation(contentBytes, keyBytes, Cipher.ENCRYPT_MODE);
    }

    public static byte[] aesDecryptBytes(byte[] contentBytes, byte[] keyBytes) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException {
        return cipherOperation(contentBytes, keyBytes, Cipher.DECRYPT_MODE);
    }

    private static byte[] cipherOperation(byte[] contentBytes, byte[] keyBytes, int mode) throws UnsupportedEncodingException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
        // 构造AES秘钥
        SecretKeySpec secretKey = new SecretKeySpec(keyBytes, "AES");

        // 构造向量参数
        byte[] initParam = IV_STRING.getBytes(charset);
        IvParameterSpec ivParameterSpec = new IvParameterSpec(initParam);

        // 创建算法实例
        Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
        cipher.init(mode, secretKey, ivParameterSpec);

        // 执行加密或解密
        return cipher.doFinal(contentBytes);
    }

}
