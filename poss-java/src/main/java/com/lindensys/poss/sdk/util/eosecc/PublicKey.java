package com.lindensys.poss.sdk.util.eosecc;

import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPublicKey;
import org.bouncycastle.jce.spec.ECPublicKeySpec;
import org.bouncycastle.math.ec.ECPoint;

import java.security.interfaces.ECPublicKey;
import java.security.spec.InvalidKeySpecException;

/**
 * 公钥对象
 *
 * @author Jiang Shunzhi
 * @date 2021/6/6
 */
public class PublicKey {

    public static final String EOS = "EOS";

    private final BCECPublicKey publicKey;

    private PublicKey(ECPublicKey ecPublicKey) {
        if (ecPublicKey instanceof BCECPublicKey) {
            this.publicKey = (BCECPublicKey) ecPublicKey;
        } else {
            throw new RuntimeException("Invalid ECPublicKey Object");
        }
    }

    public PublicKey(ECPoint q) {
        ECPublicKeySpec ecPublicKeySpec = new ECPublicKeySpec(q, Constants.EC_PARAMETER_SPEC);
        java.security.PublicKey publicKey = null;
        try {
            publicKey = Constants.KEY_FACTORY.generatePublic(ecPublicKeySpec);
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }
        this.publicKey = (BCECPublicKey) publicKey;
    }

    /**
     * 由JDK公钥接口对象转换为本地公钥对象
     *
     * @param ecPublicKey JDK公钥接口对象
     * @return 本地公钥对象
     */
    public static PublicKey fromEcPublicKey(ECPublicKey ecPublicKey) {
        return new PublicKey(ecPublicKey);
    }

    public BCECPublicKey getPublicKey() {
        return publicKey;
    }

    /**
     * 获取私钥点Q的压缩编码字节数据
     *
     * @return 编码后的字节数据
     */
    public byte[] getEncodedQ() {
        return getEncodedQ(true);
    }

    /**
     * 获取私钥点Q的编码字节数据
     *
     * @param compressed 是否为压缩格式
     * @return 编码后的字节数据
     */
    public byte[] getEncodedQ(boolean compressed) {
        return publicKey.getQ().getEncoded(compressed);
    }

    @Override
    public String toString() {
        return EOS + KeyUtils.checkEncode(getEncodedQ(), null);
    }

}
