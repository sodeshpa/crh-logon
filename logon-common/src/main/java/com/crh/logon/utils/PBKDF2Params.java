package com.crh.logon.utils;

public class PBKDF2Params {

    private int iterations;
    private byte[] salt;
    private int hashBytesLength;

    public PBKDF2Params(int iterations, byte[] salt, int hashBytesLength) {
        this.iterations = iterations;
        this.salt = salt;
        this.hashBytesLength = hashBytesLength;
    }

    public int getIterations() {
        return iterations;
    }

    public void setIterations(int iterations) {
        this.iterations = iterations;
    }

    public byte[] getSalt() {
        return salt;
    }

    public void setSalt(byte[] salt) {
        this.salt = salt;
    }

    public int getHashBytesLength() {
        return hashBytesLength;
    }

    public void setHashBytesLength(int hashBytesLength) {
        this.hashBytesLength = hashBytesLength;
    }
}