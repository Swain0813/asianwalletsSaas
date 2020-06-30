package com.asianwallets.channels.bdt;

/**
 * @description:
 * @author: YangXu
 * @create: 2020-06-23 11:40
 **/
public class DecryptedContent<T>  {
    private int encryptionKeyId;
    private int signerId;
    private int signKeyId;
    private T content;
    private byte[] rawContent;

    public DecryptedContent() {
    }

    public int getEncryptionKeyId() {
        return this.encryptionKeyId;
    }

    public void setEncryptionKeyId(int encryptionKeyId) {
        this.encryptionKeyId = encryptionKeyId;
    }

    public int getSignerId() {
        return this.signerId;
    }

    public void setSignerId(int signerId) {
        this.signerId = signerId;
    }

    public int getSignKeyId() {
        return this.signKeyId;
    }

    public void setSignKeyId(int signKeyId) {
        this.signKeyId = signKeyId;
    }

    public T getContent() {
        return this.content;
    }

    public void setContent(T content) {
        this.content = content;
    }

    public byte[] getRawContent() {
        return this.rawContent;
    }

    public void setRawContent(byte[] rawContent) {
        this.rawContent = rawContent;
    }
}
