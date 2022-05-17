package com.lindensys.poss.sdk;

import com.google.gson.Gson;
import com.lindensys.poss.sdk.listener.Process;
import com.lindensys.poss.sdk.listener.ProcessListener;
import com.lindensys.poss.sdk.util.eosecc.*;
import io.ipfs.api.IPFS;
import io.ipfs.api.MerkleNode;
import io.ipfs.api.NamedStreamable;
import io.ipfs.cid.Cid;
import io.ipfs.multibase.Multibase;
import io.ipfs.multihash.Multihash;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.Arrays;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.Security;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

/**
 * @author Jiang Shunzhi
 * @date 2021/5/26
 */
public class DefaultPossClient implements PossClient {

    private final IPFS client;

    private static final String ENCRYPTED_POSTFIX = ".encrypted";

    private static final String DEFAULT_HOST = "csg.lindensys.cn";

    private static final int DEFAULT_PORT = 80;

    private static final int DECRYPT_BLOCK = 262160;

    private static final int ENCRYPT_BLOCK = 262144;

    private static final ExecutorService EXECUTOR = Executors.newWorkStealingPool(10);

    public DefaultPossClient(String clientId) {
        this.client = new IPFS(DEFAULT_HOST,DEFAULT_PORT,"/poss/v1/"+clientId + "/",false);
    }

    public DefaultPossClient(String host, Integer port) {
        this.client = new IPFS(Optional.ofNullable(host).orElse(DEFAULT_HOST),
                Optional.ofNullable(port).orElse(DEFAULT_PORT));
    }

    public DefaultPossClient() {
        this(DEFAULT_HOST,null);
    }

    @Override
    public List<FileInfo> add(String filePath) throws IOException {
        return add(new NamedStreamable.FileWrapper(new File(filePath)));
    }

    @Override
    public List<FileInfo> add(File file) throws IOException {
        return add(new NamedStreamable.FileWrapper(file));
    }

    @Override
    public FileInfo add(InputStream inputStream, String fileName) throws IOException {
        return add(new NamedStreamable.InputStreamWrapper(Optional.ofNullable(fileName), inputStream)).get(0);
    }

    @Override
    public FileInfo add(byte[] byteArray, String fileName) throws IOException {
        return add(new NamedStreamable.ByteArrayWrapper(Optional.ofNullable(fileName), byteArray)).get(0);
    }

    private List<FileInfo> add(NamedStreamable wrapper) throws IOException {
        List<MerkleNode> nodes = client.add(wrapper);
        return nodes.stream().map(node -> {
            FileInfo fileInfo = new FileInfo();
            fileInfo.setName(node.name.orElse(""));
            fileInfo.setHash(Hash.fromString(node.hash.toBase58()));
            fileInfo.setSize(node.largeSize.map(Long::parseLong).orElse(null));
            return fileInfo;
        }).collect(Collectors.toList());
    }

    @Override
    public byte[] get(String cid) throws IOException {
        return client.cat(Multihash.fromBase58(cid));
    }

    @Override
    public EncryptedFileInfo addEncrypted(File file, String privateKey) throws Exception {
        if (!file.exists() || !file.isFile()) {
            throw new RuntimeException("File is not exist or is a directory.");
        }
        byte[] data = Files.readAllBytes(file.toPath());
        return addEncryptedExecute(data,privateKey,file.getName().concat(ENCRYPTED_POSTFIX));
    }

    @Override
    public EncryptedFileInfo addEncrypted(byte[] byteArray, String privateKey, String fileName) throws Exception {
        return addEncryptedExecute(byteArray,privateKey,fileName.concat(ENCRYPTED_POSTFIX));
    }

    @Override
    public EncryptedFileInfo addEncrypted(String filePath, String privateKey) throws Exception {
        return addEncrypted(new File(filePath),privateKey);
    }

    private EncryptedFileInfo addEncryptedExecute(byte[] data, String privateKeyStr, String fileName) throws Exception {
        String aesKey = KeyPair.generateNew().getPrivateKey().toString();
        byte[] encryptedData = encrypt(data,aesKey);
        FileInfo fileInfo = add(encryptedData,fileName);
        PrivateKey privateKey = KeyUtils.parsePrivateKey(privateKeyStr);
        PublicKey publicKey = privateKey.toPublic();
        Proof proof = new Proof();
        proof.setGrantor(publicKey.toString());
        proof.setGrantee(publicKey.toString());
        CryptInfo cryptInfo = AesUtils.encrypt(privateKey,publicKey,aesKey.getBytes(StandardCharsets.UTF_8));
        proof.setEncryptInfo(cryptInfo);
        proof.getLinks().add(fileInfo);
        MerkleNode node = client.dag.put("cbor",proof.toCborRaw());
        EncryptedFileInfo encryptedFileInfo = new EncryptedFileInfo();
        encryptedFileInfo.setCid(Multibase.encode(Multibase.Base.Base32,node.hash.toBytes()));
        encryptedFileInfo.setResource(fileInfo);
        return encryptedFileInfo;
    }

    @Override
    public byte[] getEncrypted(String cid, String privateKey) throws Exception {
        byte[] dag = client.dag.get(Cid.decode(cid));
        String json = new String(dag,StandardCharsets.UTF_8);
        Proof proof = new Gson().fromJson(json,Proof.class);
        PrivateKey privateKeyO = KeyUtils.parsePrivateKey(privateKey);
        PublicKey publicKey = KeyUtils.parsePublicKey(proof.getGrantor());
        CryptInfo cryptInfo = AesUtils.decrypt(privateKeyO,publicKey,proof.getEncryptInfo());
        String aesKey = new String(cryptInfo.getMessage(),StandardCharsets.UTF_8);
        byte[] data = get(proof.getLinks().get(0).getHash().getHash());
        return decrypt(data, aesKey);
    }
    
    private byte[] encrypt(byte[] data, String aesKey) throws Exception {
        byte[] encryptedData = new byte[0];
        int location = 0;
        while (location <= data.length) {
            int end = Math.min(data.length,location + ENCRYPT_BLOCK);
            byte[] block = Arrays.copyOfRange(data,location, end);
            encryptedData = Arrays.concatenate(encryptedData,AesUtils.encrypt(block,aesKey));
            location = location + ENCRYPT_BLOCK;
        }
        return encryptedData;
    }

    private byte[] decrypt(byte[] data, String aesKey) throws Exception {
        byte[] decryptedData = new byte[0];
        int location = 0;
        while (location <= data.length) {
            int end = Math.min(data.length,location + DECRYPT_BLOCK);
            byte[] block = Arrays.copyOfRange(data,location, end);
            decryptedData = Arrays.concatenate(decryptedData,AesUtils.decrypt(block,aesKey));
            location = location + DECRYPT_BLOCK;
        }
        return decryptedData;
    }

    @Override
    public String addProof(FileInfo fileInfo, String privateKeyStr, String aesKey) throws Exception {
        PrivateKey privateKey = KeyUtils.parsePrivateKey(privateKeyStr);
        PublicKey publicKey = privateKey.toPublic();
        Proof proof = new Proof();
        proof.setGrantor(publicKey.toString());
        proof.setGrantee(publicKey.toString());
        CryptInfo cryptInfo = AesUtils.encrypt(privateKey,publicKey,aesKey.getBytes(StandardCharsets.UTF_8));
        proof.setEncryptInfo(cryptInfo);
        proof.getLinks().add(fileInfo);
        MerkleNode node = client.dag.put("cbor",proof.toCborRaw());
        return Multibase.encode(Multibase.Base.Base32,node.hash.toBytes());
    }

    @Override
    public Proof getProof(String cid, String privateKeyStr) throws Exception {
        byte[] dag = client.dag.get(Cid.decode(cid));
        String json = new String(dag, StandardCharsets.UTF_8);
        Proof proof = new Gson().fromJson(json, Proof.class);
        PrivateKey privateKey = KeyUtils.parsePrivateKey(privateKeyStr);
        PublicKey publicKey = KeyUtils.parsePublicKey(proof.getGrantor());
        CryptInfo cryptInfo = AesUtils.decrypt(privateKey, publicKey, proof.getEncryptInfo());
        proof.setEncryptInfo(cryptInfo);
        return proof;
    }

    @Override
    public String grant(String proofCid, String privateKeyStr, String publicKeyStr) throws Exception {
        byte[] dag = client.dag.get(Cid.decode(proofCid));
        String json = new String(dag, StandardCharsets.UTF_8);
        Proof oldProof = new Gson().fromJson(json, Proof.class);
        PrivateKey privateKey = KeyUtils.parsePrivateKey(privateKeyStr);
        byte[] aesKey = AesUtils.decrypt(privateKey,
                KeyUtils.parsePublicKey(oldProof.getGrantor()),
                oldProof.getEncryptInfo()).getMessage();
        PublicKey publicKey = KeyUtils.parsePublicKey(publicKeyStr);
        Proof newProof = new Proof();
        newProof.setGrantor(privateKey.toPublic().toString());
        newProof.setGrantee(publicKeyStr);
        CryptInfo cryptInfo = AesUtils.encrypt(privateKey,publicKey,aesKey);
        newProof.setEncryptInfo(cryptInfo);
        newProof.setLinks(oldProof.getLinks());
        MerkleNode node = client.dag.put("cbor",newProof.toCborRaw());
        return Multibase.encode(Multibase.Base.Base32,node.hash.toBytes());
    }

}
