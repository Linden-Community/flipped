package com.lindensys.poss.sdk;

import com.lindensys.poss.sdk.listener.ProcessListener;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.concurrent.Future;

/**
 * @author Jiang Shunzhi
 * @date 2022/5/11
 */
public interface PossClient {

    /**
     * Upload file to IPFS
     * @param filePath Local file path. If path is a directory, all files in the directory will
     *                 be uploaded recursively.
     * @return The IPFS file information of all uploaded files.
     * @throws IOException When fetch files fail or network communication fail.
     */
    List<FileInfo> add(String filePath) throws IOException;

    /**
     * Upload file to IPFS
     * @param file Local File Object. If File object is a directory, all files in the directory will
     *                 be uploaded recursively.
     * @return The IPFS file information of all uploaded files.
     * @throws IOException When fetch files fail or network communication fail.
     */
    List<FileInfo> add(File file) throws IOException;

    /**
     * Upload file to IPFS
     * @param inputStream The input stream which can read data that need uploading.
     * @param fileName If you hope add a file name for data. It can be null.
     * @return The IPFS file information of uploaded data.
     * @throws IOException When fetch files fail or network communication fail.
     */
    FileInfo add(InputStream inputStream, String fileName) throws IOException;

    /**
     * Upload file to IPFS
     * @param byteArray The byte array which contain data that need uploading.
     * @param fileName If you hope add a file name for data. It can be null.
     * @return The IPFS file information of uploaded data.
     * @throws IOException When fetch files fail or network communication fail.
     */
    FileInfo add(byte[] byteArray, String fileName) throws IOException;

    /**
     * Fetch file or data from IPFS
     * @param cid The hash from the IPFS file information.
     * @return The data of file or data with byte array format.
     * @throws IOException When network communication fail.
     */
    byte[] get(String cid) throws IOException;

    /**
     * Upload file to IPFS with encryption
     * @param file Local File Object. Directories are not supported for encrypted uploads.
     * @param privateKey Private key distributed to the client or user in advance
     * @return The encryption file information of uploaded file including IPFS file
     *         information and proof cid;
     * @throws Exception When network communication fail or encrypting fail.
     */
    EncryptedFileInfo addEncrypted(File file, String privateKey) throws Exception;

    /**
     * Upload data to IPFS with encryption
     * @param byteArray The byte array which contain data that need uploading.
     * @param privateKey Private key distributed to the client or user in advance
     * @param fileName If you hope add a file name for data. It can be null.
     * @return The encryption file information of uploaded file including IPFS file
     *         information and proof cid;
     * @throws Exception When network communication fail or encrypting fail.
     */
    EncryptedFileInfo addEncrypted(byte[] byteArray, String privateKey, String fileName) throws Exception;

    /**
     * Upload data to IPFS with encryption
     * @param filePath Local file path. Directories are not supported for encrypted uploads.
     * @param privateKey Private key distributed to the client or user in advance
     * @return The encryption file information of uploaded file including IPFS file
     *         information and proof cid;
     * @throws Exception When network communication fail or encrypting fail.
     */
    EncryptedFileInfo addEncrypted(String filePath, String privateKey) throws Exception;

    /**
     * Fetch encrypted file or data from IPFS with decryption
     * @param cid The cid of proof which need to obtain from encryption upload.
     * @param privateKey Private key distributed to the client or user in advance
     * @return The encryption file information of uploaded file including IPFS file
     *         information and proof cid;
     * @throws Exception When network communication fail or encrypting fail.
     */
    byte[] getEncrypted(String cid, String privateKey) throws Exception;

    /**
     * Fetch an encrypted file's proof and grant the file to a new grantee.
     * @param proofCid Encrypted file's proof(CID).
     * @param privateKeyStr The old proof's grantee's private key. It is also
     *                      the new proof's grantor's private key.
     * @param publicKeyStr The new proof's grantee's public key.
     * @return The hash of the new proof (CID)
     * @throws Exception When network communication fail or encrypting fail.
     */
    String grant(String proofCid, String privateKeyStr, String publicKeyStr) throws Exception;

    /**
     * Generate and put a proof with a file linked info and the grantor's private key.
     * @param fileInfo The file info which need linking to the proof
     * @param privateKeyStr the private key of the grantor.
     * @param aesKey key for AES encryption.
     * @return The hash of proof (CID)
     * @throws Exception When network communication fail or encrypting fail.
     */
    String addProof(FileInfo fileInfo, String privateKeyStr,String aesKey) throws Exception;

    /**
     * Get the proof with its CID.
     * @param cid the CID of the proof.
     * @param privateKeyStr the private key of the grantor.
     * @return The proof object. the {@link Proof#getEncryptInfo()}'s message is the AES key
     *         which is used for decryption.
     * @throws Exception When network communication fail or encrypting fail.
     */
    Proof getProof(String cid, String privateKeyStr) throws Exception;

}
