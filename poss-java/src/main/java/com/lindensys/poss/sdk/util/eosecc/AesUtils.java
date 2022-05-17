package com.lindensys.poss.sdk.util.eosecc;

import com.lindensys.poss.sdk.CryptInfo;
import com.lindensys.poss.sdk.util.BitUtils;
import com.lindensys.poss.sdk.util.HashUtils;
import org.bouncycastle.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * AES加解密工具类
 *
 * @author Jiang Shunzhi
 * @date 2021/6/7
 */
public class AesUtils {

    public static byte[] encrypt(byte[] data, String aesKey) throws Exception {
        return encrypt(data, AesKey.getInstance(aesKey));
    }

    public static byte[] decrypt(byte[] data, String aesKey) throws Exception {
        return decrypt(data, AesKey.getInstance(aesKey));
    }

    public static CryptInfo encrypt(PrivateKey privateKey,
                                    PublicKey publicKey,
                                    byte[] data) throws Exception {
        return encrypt(privateKey, publicKey, data, null);
    }

    public static CryptInfo encrypt(PrivateKey privateKey,
                                    PublicKey publicKey,
                                    byte[] data,
                                    Long nonce) throws Exception {
        return crypt(privateKey, publicKey, nonce, data, null);
    }

    public static CryptInfo decrypt(PrivateKey privateKey,
                                    PublicKey publicKey,
                                    byte[] data,
                                    Long nonce,
                                    Long checksum) throws Exception {
        return crypt(privateKey, publicKey, nonce, data, checksum);
    }

    public static CryptInfo decrypt(PrivateKey privateKey,
                                    PublicKey publicKey,
                                    CryptInfo cryptInfo) throws Exception {
        return crypt(
                privateKey,
                publicKey,
                cryptInfo.getNonce().getNonce(),
                cryptInfo.getMessage(),
                cryptInfo.getChecksum()
        );
    }

    private static Long uniqueNonce() {
        byte[] bytes = new byte[2];
        Constants.RANDOM.nextBytes(bytes);
        int randomInt = bytes[0] << 8 | bytes[1];
        long entropy = ++randomInt % 0xFFFF;
        long seed = System.currentTimeMillis();
        return seed << 16 | entropy;
    }

    private static CryptInfo crypt(
            PrivateKey privateKey,
            PublicKey publicKey,
            Long nonce,
            byte[] data,
            Long checksum
    ) throws Exception {
        if (privateKey == null) {
            throw new RuntimeException("PrivateKey is required");
        }
        if (publicKey == null) {
            throw new RuntimeException("PublicKey is required");
        }
        if (data == null || data.length == 0) {
            throw new RuntimeException("Data is required");
        }
        if (nonce == null) {
            nonce = uniqueNonce();
        }
        byte[] sharedSecret = privateKey.getSharedSecret(publicKey);
        ByteBuffer byteBuffer = ByteBuffer.allocate(72).order(ByteOrder.LITTLE_ENDIAN);
        byteBuffer.putLong(nonce);
        byteBuffer.put(sharedSecret);
        AesKey aesKey = AesKey.getInstance(byteBuffer.array());
        byte[] check = HashUtils.sha256(aesKey.getEncryptionKey());
        check = BitUtils.paddingBytes(Arrays.reverse(Arrays.copyOf(check, 4)), 8);
        ByteBuffer checkBuffer = ByteBuffer.allocate(8).put(check);
        Long checkNum = checkBuffer.getLong(0);
        byte[] message;
        if (checksum == null) {
            message = encrypt(data, aesKey);
        } else {
            if (!checkNum.equals(checksum)) {
                throw new RuntimeException("Invalid key");
            }
            message = decrypt(data, aesKey);
        }
        return new CryptInfo(nonce, message, checkNum);
    }

    private static byte[] encrypt(byte[] data, AesKey aesKey) throws Exception {
        return encrypt(data, aesKey.getKey(), aesKey.getIv());
    }

    private static byte[] encrypt(byte[] data, byte[] key, byte[] iv) throws Exception {
        Cipher cipher = aesCipher(Cipher.ENCRYPT_MODE, key, iv);
        return cipher.doFinal(data);
    }

    private static byte[] decrypt(byte[] data, AesKey aesKey) throws Exception {
        return decrypt(data, aesKey.getKey(), aesKey.getIv());
    }

    private static byte[] decrypt(byte[] data, byte[] key, byte[] iv) throws Exception {
        Cipher cipher = aesCipher(Cipher.DECRYPT_MODE, key, iv);
        return cipher.doFinal(data);
    }

    private static Cipher aesCipher(int cipherMode, byte[] key, byte[] iv) throws Exception {
        SecretKey secretKey = new SecretKeySpec(key, "AES");
        IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding", "BC");
        cipher.init(cipherMode, secretKey, ivParameterSpec);
        return cipher;
    }

    public static String generateAesKey() {
        return KeyPair.generateNew().getPrivateKey().toString();
    }

}
