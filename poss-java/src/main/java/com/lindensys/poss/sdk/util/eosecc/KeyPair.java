package com.lindensys.poss.sdk.util.eosecc;


import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;

/**
 * 秘钥对类
 *
 * @author Jiang Shunzhi
 * @date 2021/6/7
 */
public class KeyPair {

    private final PrivateKey privateKey;

    private final PublicKey publicKey;

    private KeyPair(java.security.KeyPair keyPair) {
        ECPublicKey publicKey = (ECPublicKey) keyPair.getPublic();
        ECPrivateKey privateKey = (ECPrivateKey) keyPair.getPrivate();
        this.privateKey = PrivateKey.fromEcPrivateKey(privateKey);
        this.publicKey = PublicKey.fromEcPublicKey(publicKey);
    }

    /**
     * 生成新的秘钥对
     *
     * @return 秘钥对对象
     */
    public static KeyPair generateNew() {
        java.security.KeyPair keyPair = Constants.KEY_PAIR_GENERATOR.generateKeyPair();
        return new KeyPair(keyPair);
    }

    /**
     * 转换KeyPair类型
     *
     * @param keyPair JDK接口类型的对象
     * @return 转换为本地KeyPair对象
     */
    public static KeyPair convertKeyPair(java.security.KeyPair keyPair) {
        return new KeyPair(keyPair);
    }

    public PublicKey getPublicKey() {
        return publicKey;
    }

    public PrivateKey getPrivateKey() {
        return privateKey;
    }
}
