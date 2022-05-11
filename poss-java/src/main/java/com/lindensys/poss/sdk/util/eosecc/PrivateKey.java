package com.lindensys.poss.sdk.util.eosecc;

import com.lindensys.poss.sdk.util.BitUtils;
import com.lindensys.poss.sdk.util.HashUtils;
import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPrivateKey;
import org.bouncycastle.jce.spec.ECPrivateKeySpec;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.util.Arrays;

import java.math.BigInteger;
import java.security.interfaces.ECPrivateKey;
import java.security.spec.InvalidKeySpecException;

/**
 * 私钥对象
 *
 * @author Jiang Shunzhi
 * @date 2021/6/7
 */
public class PrivateKey {

    private final BCECPrivateKey privateKey;

    private PrivateKey(ECPrivateKey ecPrivateKey) {
        if (ecPrivateKey instanceof BCECPrivateKey) {
            this.privateKey = (BCECPrivateKey) ecPrivateKey;
        } else {
            throw new RuntimeException("Invalid ECPrivateKey Object");
        }
    }

    /**
     * 由私钥整数生成私钥对象
     *
     * @param d 私钥整数
     */
    public PrivateKey(BigInteger d) {
        ECPrivateKeySpec ecPrivateKeySpec = new ECPrivateKeySpec(d, Constants.EC_PARAMETER_SPEC);
        java.security.PrivateKey privateKey = null;
        try {
            privateKey = Constants.KEY_FACTORY.generatePrivate(ecPrivateKeySpec);
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }
        this.privateKey = (BCECPrivateKey) privateKey;
    }

    /**
     * 由JDK私钥接口对象转换为本地私钥对象
     *
     * @param ecPrivateKey JDK私钥接口对象
     * @return 本地私钥对象
     */
    public static PrivateKey fromEcPrivateKey(ECPrivateKey ecPrivateKey) {
        return new PrivateKey(ecPrivateKey);
    }

    /**
     * 计算获得公钥对象
     *
     * @return 对应的私钥对象
     */
    public PublicKey toPublic() {
        ECPoint q = privateKey.getParameters().getG().multiply(privateKey.getD());
        return new PublicKey(q);
    }

    /**
     * 获取私钥整数
     *
     * @return 私钥整数
     */
    public BigInteger getD() {
        return privateKey.getD();
    }

    /**
     * 获取WIF格式字符串
     *
     * @return WIF格式字符串
     */
    public String toWif() {
        byte[] d = getD().toByteArray();
        byte[] bytes = Arrays.concatenate(new byte[]{Constants.VERSION}, d);
        return KeyUtils.checkEncode(bytes);
    }

    public BCECPrivateKey getPrivateKey() {
        return privateKey;
    }

    /**
     * 载入公钥获取共享秘钥
     *
     * @param publicKey 公钥对象
     * @return 共享秘钥数据
     */
    public byte[] getSharedSecret(PublicKey publicKey) {
        ECPoint p = publicKey.getPublicKey().getQ().multiply(getD()).normalize();
        byte[] preSecret = BitUtils.paddingBytes(p.getAffineXCoord().toBigInteger().toByteArray(), 32);
        return HashUtils.sha512(preSecret);
    }

    @Override
    public String toString() {
        return toWif();
    }

}
