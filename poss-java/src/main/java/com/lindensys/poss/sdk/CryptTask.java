package com.lindensys.poss.sdk;

import com.lindensys.poss.sdk.listener.Process;
import com.lindensys.poss.sdk.listener.ProcessListener;
import com.lindensys.poss.sdk.util.eosecc.AesUtils;
import com.lindensys.poss.sdk.util.eosecc.Constants;
import org.bouncycastle.util.Arrays;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.Security;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ForkJoinPool;

/**
 * @author Jiang Shunzhi
 * @date 2022/5/17
 */
public class CryptTask {

    private static final ExecutorService EXECUTOR =  new ForkJoinPool(10,
                    ForkJoinPool.defaultForkJoinWorkerThreadFactory,
                    null, true);

    private String aesKey;

    private final InputStream inputStream;

    private final Type type;

    private final int size;

    private transient int status = CREATE;

    private transient int location = 0;

    private static final int FAIL = -1;
    private static final int CREATE = 0;
    private static final int START = 1;
    private static final int SUCCESS = 2;

    public void setAesKey(String aesKey) {
        this.aesKey = Optional.of(aesKey).get();
    }

    public String getAesKey() {
        return aesKey;
    }

    public InputStream getInputStream() {
        return inputStream;
    }

    public Type getType() {
        return type;
    }

    public int getSize() {
        return size;
    }

    public int getLocation() {
        return location;
    }

    /**
     * Task status.
     * @return -1 for fail; 0 for create; 1 for start, 2 for success
     */
    public int getStatus() {
        return status;
    }

    public CryptTask(InputStream inputStream, Type type) throws IOException {
        this(null,inputStream,type);
    }

    public CryptTask(String aesKey, InputStream inputStream, Type type) throws IOException {
        this.aesKey = aesKey;
        this.inputStream = Optional.of(inputStream).get();
        this.size = this.inputStream.available();
        this.type = Optional.of(type).get();
    }

    /**
     * Start task with {@link CryptTask#aesKey}
     * @param listener listener The listener which can react to process events. It can be null.
     * @return Input stream which will contain encrypted or decrypted data.
     */
    public InputStream start(ProcessListener<InputStream> listener) {
        Objects.requireNonNull(aesKey,"Cannot find AES key");
        return start(aesKey,listener);
    }

    /**
     * Start task.
     * @param aesKey If {@link CryptTask#aesKey} is not null, this parameter will not work.
     * @param listener listener The listener which can react to process events. It can be null.
     * @return Input stream which will contain encrypted or decrypted data.
     */
    public InputStream start(String aesKey, ProcessListener<InputStream> listener) {
        if (status != 0) {
            throw new RuntimeException("Task has been started");
        }
        if (Objects.isNull(this.aesKey) && Objects.isNull(aesKey)) {
            throw new NullPointerException("Cannot find AES key");
        }
        if (Objects.nonNull(this.aesKey)) {
            aesKey = this.aesKey;
        }
        status = START;
        boolean handleEvent = Objects.nonNull(listener);
        Security.addProvider(Constants.BC_PROVIDER);
        if (handleEvent) {
            EXECUTOR.execute(listener::onStart);
        }
        int blockSize = type == Type.ENCRYPT? Constants.ENCRYPT_BLOCK: Constants.DECRYPT_BLOCK;
        ByteArrayOutputStream cryptDataStream = new ByteArrayOutputStream(blockSize);
        try {
            if (handleEvent) {
                EXECUTOR.execute(() -> listener.onProcess(new Process(size, location)));
            }
            while (location < size) {
                byte[] blockData = new byte[blockSize];
                int readLen = inputStream.read(blockData);
                if (readLen != blockSize) {
                    blockData = Arrays.copyOfRange(blockData,0, readLen);
                }
                byte[] cryptData = type == Type.ENCRYPT?
                        AesUtils.encrypt(blockData,aesKey)
                        : AesUtils.decrypt(blockData,aesKey);
                cryptDataStream.write(cryptData);
                location = location + readLen;
                if (handleEvent) {
                    EXECUTOR.execute(() -> listener.onProcess(new Process(size, location)));
                }
            }
        } catch (Exception e) {
            if (handleEvent) {
                EXECUTOR.execute(() -> listener.onError("Encrypt process failed",e));
                status = FAIL;
                return null;
            } else {
                throw new RuntimeException("Encrypt process failed",e);
            }
        }
        ByteArrayInputStream output = new ByteArrayInputStream(cryptDataStream.toByteArray());
        if (handleEvent) {
            EXECUTOR.execute(() -> listener.onComplete(output));
            status = SUCCESS;
        }
        return output;
    }

    public enum Type {
        /**
         * Type of task
         */
        ENCRYPT,
        DECRYPT
    }

}
