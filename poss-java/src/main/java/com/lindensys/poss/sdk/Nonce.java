package com.lindensys.poss.sdk;

import com.google.gson.annotations.Expose;

import java.io.Serializable;

/**
 * @author Jiang Shunzhi
 * @date 2022/5/9
 */
public class Nonce implements Serializable {

    private Integer high;

    private Integer low;

    private boolean unsigned;

    public Nonce(Long nonce) {
        this.high = (int) (nonce >> 32);
        this.low = nonce.intValue();
        unsigned = false;
    }

    public Long getNonce() {
        return (high.longValue() << 32 | 0x00000000ffffffffL) & (low.longValue() | 0xffffffff00000000L);
    }

    public Integer getHigh() {
        return high;
    }

    public void setHigh(Integer high) {
        this.high = high;
    }

    public Integer getLow() {
        return low;
    }

    public void setLow(Integer low) {
        this.low = low;
    }

    public boolean isUnsigned() {
        return unsigned;
    }

    public void setUnsigned(boolean unsigned) {
        this.unsigned = unsigned;
    }
}
