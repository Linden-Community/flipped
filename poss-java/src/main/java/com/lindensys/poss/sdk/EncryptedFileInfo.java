package com.lindensys.poss.sdk;

/**
 * @author Jiang Shunzhi
 * @date 2022/5/9
 */
public class EncryptedFileInfo {

    private String cid;

    private FileInfo resource;

    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

    public FileInfo getResource() {
        return resource;
    }

    public void setResource(FileInfo resource) {
        this.resource = resource;
    }
}
