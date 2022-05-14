package com.lindensys.poss.sdk;

import com.google.gson.annotations.SerializedName;

/**
 * @author Jiang Shunzhi
 * @date 2022/5/13
 */
public class Hash {

    @SerializedName("/")
    private String hash;

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public static Hash fromString(String hash) {
        Hash hash1 = new Hash();
        hash1.setHash(hash);
        return hash1;
    }

}
