package com.lindensys.poss.sdk.util.eosecc;

import com.lindensys.poss.sdk.util.HashUtils;
import org.bouncycastle.util.Arrays;

import java.nio.charset.StandardCharsets;

/**
 * 用于计算加解密数据的AesKey对象，包含原Key的Hash数据、AES的秘钥和偏移向量
 *
 * @author Jiang Shunzhi
 * @date 2021/6/7
 */
public class AesKey {

    private final byte[] encryptionKey;

    private final byte[] key;

    private final byte[] iv;

    private AesKey(byte[] encryptionKey, byte[] key, byte[] iv) {
        this.encryptionKey = encryptionKey;
        this.key = key;
        this.iv = iv;
    }

    /**
     * 从原Key中生成对象
     *
     * @param aesKey 字符串记录的原Key数据
     * @return 处理后的对象
     */
    public static AesKey getInstance(String aesKey) {
        return getInstance(aesKey.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 从原Key中生成对象
     *
     * @param aesKey 字节数组记录的原Key数据
     * @return 处理后的对象
     */
    public static AesKey getInstance(byte[] aesKey) {
        byte[] encryptionKey = HashUtils.sha512(aesKey);
        byte[] key = Arrays.copyOf(encryptionKey, 32);
        byte[] iv = Arrays.copyOfRange(encryptionKey, 32, 48);
        return new AesKey(encryptionKey, key, iv);
    }

    public byte[] getEncryptionKey() {
        return encryptionKey;
    }

    public byte[] getKey() {
        return key;
    }

    public byte[] getIv() {
        return iv;
    }

}
