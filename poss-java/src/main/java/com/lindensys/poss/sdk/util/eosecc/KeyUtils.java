package com.lindensys.poss.sdk.util.eosecc;

import com.lindensys.poss.sdk.util.HashUtils;
import com.lindensys.poss.sdk.util.MathUtils;
import io.ipfs.multibase.Base58;
import org.bouncycastle.math.ec.ECConstants;
import org.bouncycastle.math.ec.ECFieldElement;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.math.ec.custom.sec.SecP256K1Curve;
import org.bouncycastle.math.ec.custom.sec.SecP256K1Field;
import org.bouncycastle.math.ec.custom.sec.SecP256K1FieldElement;
import org.bouncycastle.math.ec.custom.sec.SecP256K1Point;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.encoders.Hex;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;

/**
 * 秘钥工具类
 *
 * @author Jiang Shunzhi
 * @date 2021/6/6
 */
public class KeyUtils {

    public static final String SHA256_2 = "sha256x2";

    private static final BigInteger SEVEN = BigInteger.valueOf(7);

    /**
     * 编码秘钥为Base58字符串
     *
     * @param keyBytes 秘钥数据
     * @param keyType  编码类型
     * @return Base58的秘钥字符串
     */
    public static String checkEncode(byte[] keyBytes, String keyType) {
        byte[] checksum;
        if (SHA256_2.equalsIgnoreCase(keyType)) {
            checksum = HashUtils.sha256(HashUtils.sha256(keyBytes));
        } else {
            byte[] toHash;
            if (keyType != null) {
                toHash = Arrays.concatenate(keyBytes, keyType.getBytes(StandardCharsets.UTF_8));
            } else {
                toHash = keyBytes;
            }
            checksum = HashUtils.ripemd160(toHash);
        }
        checksum = Arrays.copyOf(checksum, 4);
        return Base58.encode(Arrays.concatenate(keyBytes, checksum));
    }

    /**
     * 编码秘钥为Base58字符串，使用SHA-256x2编码类型
     *
     * @param privateKeyBytes 秘钥数据
     * @return Base58的秘钥字符串
     */
    public static String checkEncode(byte[] privateKeyBytes) {
        return checkEncode(privateKeyBytes, SHA256_2);
    }

    /**
     * 从Base58的秘钥字符串中解码为秘钥的二进制数据
     *
     * @param keyString Base58的秘钥字符串
     * @param keyType   编码类型
     * @return 二进制秘钥
     */
    public static byte[] checkDecode(String keyString, String keyType) {
        byte[] bytes = Base58.decode(keyString);
        byte[] checksum = Arrays.copyOfRange(bytes, bytes.length - 4, bytes.length);
        byte[] key = Arrays.copyOf(bytes, bytes.length - 4);
        byte[] check;
        byte[] newCheck;
        if (SHA256_2.equalsIgnoreCase(keyType)) {
            check = HashUtils.sha256(HashUtils.sha256(key));
        } else {
            if (keyType != null) {
                byte[] toHash = Arrays.concatenate(key, keyType.getBytes(StandardCharsets.UTF_8));
                check = HashUtils.ripemd160(toHash);
            } else {
                check = HashUtils.ripemd160(key);
            }
        }
        newCheck = Arrays.copyOf(check, 4);
        if (!Arrays.areEqual(checksum, newCheck)) {
            throw new RuntimeException("Invalid checksum");
        }
        return key;
    }

    /**
     * 从Base58的秘钥字符串中解码为秘钥的二进制数据，使用SHA-256x2编码类型
     *
     * @param keyString Base58的秘钥字符串
     * @return 二进制秘钥
     */
    public static byte[] checkDecode(String keyString) {
        return checkDecode(keyString, SHA256_2);
    }

    /**
     * 从Base58的私钥字符串中解码为私钥对象
     *
     * @param privateKeyStr Base58的秘钥字符串
     * @return 私钥对象
     */
    public static PrivateKey parsePrivateKey(String privateKeyStr) {
        byte[] versionKey = checkDecode(privateKeyStr);
        byte version = versionKey[0];
        if (version != Constants.VERSION) {
            throw new RuntimeException("Expected version 0x80, instead got " + Hex.toHexString(versionKey, 0, 1));
        }
        byte[] privateKey = Arrays.copyOfRange(versionKey, 1, versionKey.length);
        return new PrivateKey(new BigInteger(1,privateKey));
    }
    /**
     * 从Base58的私钥字符串中解码为私钥对象
     *
     * @param publicKeyStr Base58的秘钥字符串
     * @return 私钥对象
     */
    public static PublicKey parsePublicKey(String publicKeyStr) {
        if (!publicKeyStr.startsWith(PublicKey.EOS)) {
            throw new RuntimeException("Invalid publicKey format.");
        }
        byte[] flaggedKey = checkDecode(publicKeyStr.substring(3),null);
        byte flag = flaggedKey[0];
        byte[] publicKey = Arrays.copyOfRange(flaggedKey,1,flaggedKey.length);
        BigInteger x = new BigInteger(1,publicKey);
        BigInteger y = MathUtils.modSqrt(x.pow(3).add(SEVEN), SecP256K1FieldElement.Q);
        if (y.testBit(0)) {
            if (flag == Constants.EVEN) {
                y = SecP256K1FieldElement.Q.subtract(y);
            }
        } else {
            if (flag == Constants.ODD) {
                y = SecP256K1FieldElement.Q.subtract(y);
            }
        }
        ECPoint q = Constants.EC_PARAMETER_SPEC.getCurve().createPoint(x,y);
        return new PublicKey(q);
    }

    /**
     * 从Base58的私钥字符串转换为对应的公钥对象
     *
     * @param privateKeyStr Base58的秘钥字符串
     * @return 公钥对象
     */
    public static PublicKey privateToPublic(String privateKeyStr) {
        return parsePrivateKey(privateKeyStr).toPublic();
    }

}
