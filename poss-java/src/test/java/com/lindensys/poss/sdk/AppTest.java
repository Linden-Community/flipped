package com.lindensys.poss.sdk;

import static org.junit.Assert.assertEquals;

import com.google.gson.Gson;
import com.lindensys.poss.sdk.util.HashUtils;
import com.lindensys.poss.sdk.util.eosecc.*;
import io.ipfs.multihash.Multihash;
import org.junit.Test;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * Unit test for simple App.
 */
public class AppTest 
{

    private final Gson gson = new Gson();
    private final DefaultPossClient client = new DefaultPossClient("testnet");

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
        List<FileInfo> fileInfos = client.add("/Users/skyjourney/xoado/tool/photo.jpg");
        System.out.println("Source File CID:\t" + fileInfos.get(0).getHash().getHash());
        String privateKey = "5KQayTDGKgWPZjEehoQxQDvqVuNgiVXYkzsgAcg72P36Qr1AMzG";
        EncryptedFileInfo fileInfo = client.addEncrypted("/Users/skyjourney/xoado/tool/photo.jpg",privateKey);
        System.out.println(gson.toJson(fileInfo));
        String proofCid = fileInfo.getCid();
        System.out.println(proofCid);
//        String proofCid = "bafyreicbzr3gzpolft2wtk335lclmksrie2ds5zfyuntkdv7xk523qpuai";
        byte[] data = client.getEncrypted(proofCid,privateKey);
        FileOutputStream fos = new FileOutputStream("/Users/skyjourney/xoado/tool/test.jpg");
        fos.write(data);
        fos.flush();
        fos.close();
        fileInfos = client.add("/Users/skyjourney/xoado/tool/test.jpg");
        System.out.println("Decrypted File CID:\t" + fileInfos.get(0).getHash().getHash());
    }

    @Test
    public void keyTest() throws Exception {
        String privateKeyStr = "5KQayTDGKgWPZjEehoQxQDvqVuNgiVXYkzsgAcg72P36Qr1AMzG";
        PrivateKey privateKey = KeyUtils.parsePrivateKey(privateKeyStr);
        String publicKeyStr = "EOS5Rm1VBzzHMM7qD3xCBUFh9qGfpUi9eJcgzaLoiKHGdHBD8erqa";
        PublicKey publicKey = KeyUtils.parsePublicKey(publicKeyStr);
        CryptInfo cryptInfo = AesUtils.encrypt(privateKey,publicKey,new byte[]{1,2,3,4,5,6,7,8});
        AesUtils.decrypt(privateKey,publicKey,cryptInfo);
    }

    @Test
    public void nonceTest() {
        Long num = Constants.RANDOM.nextLong();
        Nonce nonce = new Nonce(num);
        assertEquals(num,nonce.getNonce());
    }

    @Test
    public void newKeyPair() {
        KeyPair keyPair = KeyPair.generateNew();
        System.out.println("Public Key: " + keyPair.getPublicKey());
        System.out.println("Private Key: " + keyPair.getPrivateKey());
    }

    @Test
    public void grantTest() throws Exception {
        String privateKeyNew = "KwFZwFES9omGbfLL1GbibB8ecPF7NDfn1UfiNv6Si5WfQXSXjck7";
        String publicKeyNew = "EOS6SWdRCgWQyu7Y8ujuRL57dXBomFDVYmEs2GyLnvMw6U9nvXdpJ";
        String privateKey = "5KQayTDGKgWPZjEehoQxQDvqVuNgiVXYkzsgAcg72P36Qr1AMzG";
//        EncryptedFileInfo fileInfo = client.addEncrypted("/Users/skyjourney/xoado/tool/photo.jpg",privateKey);
//        String proofCid = fileInfo.getCid();
        String proofCid = "bafyreialcffurghml4t2thgaata33rc7fx5roytqw2mkwyy2byhiugw2ti";
        System.out.println(proofCid);
        String newProof = client.grant(proofCid,privateKey,publicKeyNew);
        byte[] data = client.getEncrypted(newProof,privateKeyNew);
        FileOutputStream fos = new FileOutputStream("/Users/skyjourney/xoado/tool/test.jpg");
        fos.write(data);
        fos.flush();
        fos.close();
    }

    @Test
    public void cryptSample() throws IOException {
        InputStream inputStream = Files.newInputStream(Paths.get("/Users/skyjourney/xoado/tool/test.jpg"));
        String aesKey = AesUtils.generateAesKey();
        CryptTask task = new CryptTask(aesKey, inputStream, CryptTask.Type.ENCRYPT);
        InputStream encryptStream = task.start(null);
    }

}
