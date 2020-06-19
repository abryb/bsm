package com.github.abryb.bsm;

import java.io.Serializable;

public class AppData implements Serializable {
    private byte[] passwordSalt;
    private byte[] passwordSignature;
    private byte[] note;

    public byte[] getPasswordSalt() {
        return passwordSalt;
    }

    public void setPasswordSalt(byte[] passwordSalt) {
        this.passwordSalt = passwordSalt;
    }

    public byte[] getPasswordSignature() {
        return passwordSignature;
    }

    public void setPasswordSignature(byte[] passwordSignature) {
        this.passwordSignature = passwordSignature;
    }

    public byte[] getNote() {
        return note;
    }

    public void setNote(byte[] note) {
        this.note = note;
    }
}
