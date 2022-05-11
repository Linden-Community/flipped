package com.lindensys.poss.sdk;

import com.lindensys.poss.sdk.util.eosecc.CryptInfo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Jiang Shunzhi
 * @date 2022/5/9
 */
public class Proof implements Serializable {

    private String data = "djE=";

    private List<FileInfo> links = new ArrayList<>();

    private CryptInfo encryptInfo;

    private String grantee;

    private String grantor;

    public void setData(String data) {
        this.data = data;
    }

    public String getData() {
        return data;
    }

    public List<FileInfo> getLinks() {
        return links;
    }

    public void setLinks(List<FileInfo> links) {
        this.links = links;
    }

    public CryptInfo getEncryptInfo() {
        return encryptInfo;
    }

    public void setEncryptInfo(CryptInfo encryptInfo) {
        this.encryptInfo = encryptInfo;
    }

    public String getGrantee() {
        return grantee;
    }

    public void setGrantee(String grantee) {
        this.grantee = grantee;
    }

    public String getGrantor() {
        return grantor;
    }

    public void setGrantor(String grantor) {
        this.grantor = grantor;
    }
}
