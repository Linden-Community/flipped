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

    @SerializedName("Size")
    private Long size;
    @SerializedName("Hash")
    private String hash;

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

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }
}
