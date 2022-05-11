package com.lindensys.poss.sdk;

import static org.junit.Assert.assertEquals;

import com.google.gson.Gson;
import com.lindensys.poss.sdk.util.eosecc.KeyPair;
import com.lindensys.poss.sdk.util.eosecc.KeyUtils;
import com.lindensys.poss.sdk.util.eosecc.PrivateKey;
import com.lindensys.poss.sdk.util.eosecc.PublicKey;
import org.junit.Test;

import java.io.FileOutputStream;

/**
 * Unit test for simple App.
 */
public class AppTest 
{
    /**
     * Rigorous Test :-)
     */
    @Test
    public void keyDecodeTest()
    {
        KeyPair keyPair = KeyPair.generateNew();
        PrivateKey privateKey = keyPair.getPrivateKey();
        String pkStr = privateKey.toString();
        PrivateKey pk = KeyUtils.parsePrivateKey(pkStr);
        assertEquals("PrivateKey Check", pkStr, pk.toString());
        PublicKey publicKey = keyPair.getPublicKey();
        String eosStr = publicKey.toString();
        PublicKey check = KeyUtils.parsePublicKey(eosStr);
        assertEquals("PublicKey Check", eosStr, check.toString());
    }

    @Test
    public void mainTest() throws Exception {
        Gson gson = new Gson();
        DefaultPossClient client = new DefaultPossClient("testnet");
        String privateKey = "5J9WSgPbpePVXH8ZAoDLCxdR7YJJoo3oKFmxfS1pvNMi8F12jZ5";
        EncryptedFileInfo fileInfo = client.addEncrypted("/Users/skyjourney/xoado/tool/2.doc",privateKey);
        System.out.println(gson.toJson(fileInfo));
        String proofCid = fileInfo.getCid();
        byte[] data = client.getEncrypted(proofCid,privateKey);
        FileOutputStream fos = new FileOutputStream("/Users/skyjourney/xoado/tool/2-2.doc");
        fos.write(data);
        fos.flush();
        fos.close();
    }
}
