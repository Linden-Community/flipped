package com.lindensys.poss.sdk.util.eosecc;

import org.bouncycastle.jce.ECNamedCurveTable;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jce.spec.ECParameterSpec;

import java.security.*;
import java.security.spec.ECGenParameterSpec;
import java.util.Random;

/**
 * EOS ECC工具包的常量类
 *
 * @author Jiang Shunzhi
 * @date 2021/6/6
 */
public class Constants {

    /**
     * 秘钥的字符串编码时的VERSION字符
     */
    public static final byte VERSION = (byte) 0x80;

    public static final byte ODD = (byte) 0x03;

    public static final byte EVEN = (byte) 0x02;

    public static final int DECRYPT_BLOCK = 262160;

    public static final int ENCRYPT_BLOCK = 262144;

    /**
     * ECDH的Key工厂，根据参数获取公私钥时使用
     */
    public static final KeyFactory KEY_FACTORY;

    /**
     * 使用SECP256K1曲线的曲线参数
     */
    public static final ECParameterSpec EC_PARAMETER_SPEC;

    /**
     * 使用SECP256K1曲线的秘钥生成器
     */
    public static final KeyPairGenerator KEY_PAIR_GENERATOR;

    /**
     * 用于生成随机数的对象，使用安全随机数
     */
    public static final Random RANDOM = new SecureRandom();

    public static final Provider BC_PROVIDER = new BouncyCastleProvider();

    static {
        Security.addProvider(BC_PROVIDER);
        ECGenParameterSpec ecGenParameterSpec = new ECGenParameterSpec("secp256k1");
        EC_PARAMETER_SPEC = ECNamedCurveTable.getParameterSpec("secp256k1");
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("ECDH", "BC");
            keyPairGenerator.initialize(ecGenParameterSpec);
            KEY_PAIR_GENERATOR = keyPairGenerator;
            KEY_FACTORY = KeyFactory.getInstance("ECDH", "BC");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
