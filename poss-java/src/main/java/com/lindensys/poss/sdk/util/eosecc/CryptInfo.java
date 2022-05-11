package com.lindensys.poss.sdk.util.eosecc;

import java.util.Base64;

/**
 * 加密信息类
 *
 * @author Jiang Shunzhi
 * @date 2021/6/7
 */
public class CryptInfo {

    /**
     * 单次加解密时的唯一随机数
     */
    private final Nonce nonce;

    /**
     * 加密后数据或解密后数据
     */
    private final String message;

    /**
     * 验证解密使用的散列计算整数值
     */
    private final Long checksum;

    public CryptInfo(Long nonce, byte[] message, Long checksum) {
        this.nonce = new Nonce(nonce);
        this.message = Base64.getEncoder().encodeToString(message);
        this.checksum = checksum;
    }

    public Nonce getNonce() {
        return nonce;
    }

    public String getMessage() {
        return message;
    }

    public Long getChecksum() {
        return checksum;
    }

}
