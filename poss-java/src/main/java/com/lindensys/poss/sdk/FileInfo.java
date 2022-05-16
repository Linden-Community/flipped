package com.lindensys.poss.sdk;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * @author Jiang Shunzhi
 * @date 2022/5/9
 */
public class FileInfo implements Serializable {

    @SerializedName("Name")
    private String name;

    @SerializedName(value = "Size", alternate = "Tsize")
    private Long size;
    @SerializedName("Hash")
    private Hash hash;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public Hash getHash() {
        return hash;
    }

    public void setHash(Hash hash) {
        this.hash = hash;
    }
}
