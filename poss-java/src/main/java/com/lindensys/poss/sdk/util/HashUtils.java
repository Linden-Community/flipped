package com.lindensys.poss.sdk.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

/**
 * Hash运算工具类
 *
 * @author Jiang Shunzhi
 * @date 2021/6/7
 */
public class HashUtils {

    /**
     * SHA-256 值计算
     *
     * @param data 字符串数据
     * @return 散列值字节数组
     */
    public static byte[] sha256(String data) {
        return sha256(data.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * SHA-256 值计算
     *
     * @param data 字节数组数据
     * @return 散列值字节数组
     */
    public static byte[] sha256(byte[] data) {
        try {
            MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
            sha256.update(data);
            return sha256.digest();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * SHA-512 值计算
     *
     * @param data 字符串数据
     * @return 散列值字节数组
     */
    public static byte[] sha512(String data) {
        return sha512(data.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * SHA-512 值计算
     *
     * @param data 字节数组数据
     * @return 散列值字节数组
     */
    public static byte[] sha512(byte[] data) {
        try {
            MessageDigest sha512 = MessageDigest.getInstance("SHA-512");
            sha512.update(data);
            return sha512.digest();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * SHA-1 值计算
     *
     * @param data 字符串数据
     * @return 散列值字节数组
     */
    public static byte[] sha1(String data) {
        return sha1(data.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * SHA-1 值计算
     *
     * @param data 字节数组数据
     * @return 散列值字节数组
     */
    public static byte[] sha1(byte[] data) {
        try {
            MessageDigest sha1 = MessageDigest.getInstance("SHA-1");
            sha1.update(data);
            return sha1.digest();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * MD5 值计算
     *
     * @param data 字符串数据
     * @return 散列值字节数组
     */
    public static byte[] md5(String data) {
        return sha1(data.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * MD5 值计算
     *
     * @param data 字节数组数据
     * @return 散列值字节数组
     */
    public static byte[] md5(byte[] data) {
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(data);
            return md5.digest();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * RIPEMD-160 值计算
     *
     * @param data 字符串数据
     * @return 散列值字节数组
     */
    public static byte[] ripemd160(String data) {
        return ripemd160(data.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * RIPEMD-160 值计算
     *
     * @param data 字节数组数据
     * @return 散列值字节数组
     */
    public static byte[] ripemd160(byte[] data) {
        try {
            MessageDigest ripemd160 = MessageDigest.getInstance("ripemd160", "BC");
            ripemd160.update(data);
            return ripemd160.digest();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
