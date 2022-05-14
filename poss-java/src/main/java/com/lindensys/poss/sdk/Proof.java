package com.lindensys.poss.sdk;

import com.google.gson.annotations.SerializedName;
import io.ipfs.api.cbor.CborObject;
import io.ipfs.multihash.Multihash;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Jiang Shunzhi
 * @date 2022/5/9
 */
public class Proof implements Serializable {

    private String data = "djE=";

    @SerializedName("Links")
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

    public byte[] toCborRaw() {
        Map<String,CborObject> proofMap = new HashMap<>();
        proofMap.put("Data", new CborObject.CborString(data));
        proofMap.put("grantee", new CborObject.CborString(grantee));
        proofMap.put("grantor", new CborObject.CborString(grantor));
        List<CborObject> linkList = new ArrayList<>();
        Map<String,CborObject> link = new HashMap<>();
        link.put("Hash", new CborObject.CborMerkleLink(Multihash.fromBase58(links.get(0).getHash().getHash())));
        link.put("Tsize", new CborObject.CborLong(links.get(0).getSize()));
        link.put("Name", new CborObject.CborString(links.get(0).getName()));
        linkList.add(CborObject.CborMap.build(link));
        proofMap.put("Links",new CborObject.CborList(linkList));
        Map<String,CborObject> cryptInfoMap = new HashMap<>();
        cryptInfoMap.put("checksum", new CborObject.CborLong(encryptInfo.getChecksum()));
        cryptInfoMap.put("message", new CborObject.CborByteArray(encryptInfo.getMessage()));
        Map<String,CborObject> nonceMap = new HashMap<>();
        nonceMap.put("high",new CborObject.CborLong(encryptInfo.getNonce().getHigh()));
        nonceMap.put("low",new CborObject.CborLong(encryptInfo.getNonce().getLow()));
        nonceMap.put("unsigned",new CborObject.CborBoolean(false));
        cryptInfoMap.put("nonce", CborObject.CborMap.build(nonceMap));
        proofMap.put("encryptInfo", CborObject.CborMap.build(cryptInfoMap));
        CborObject.CborMap proof = CborObject.CborMap.build(proofMap);
        return proof.toByteArray();
    }

}
