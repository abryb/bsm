package com.github.abryb.bsm;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

public class AppCrypto {

    public static byte[] encrypt(SecretKey secret, byte[] input) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
        cipher.init(Cipher.ENCRYPT_MODE, secret);

        byte[] output = cipher.doFinal(input);
        byte[] IV = cipher.getIV();

        byte[] result = new byte[output.length + IV.length];
        System.arraycopy(IV, 0, result, 0, IV.length);
        System.arraycopy(output, 0, result, IV.length, output.length);

        return result;
    }

    public static byte[] decrypt(SecretKey secret, byte[] input) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");

        int blockSize = cipher.getBlockSize();

        byte[] IV = new byte[blockSize];
        byte[] note = new byte[input.length - blockSize];

        System.arraycopy(input, 0, IV, 0, IV.length);
        System.arraycopy(input, blockSize, note, 0, note.length);

        cipher.init(Cipher.DECRYPT_MODE, secret, new IvParameterSpec(IV));

        return cipher.doFinal(note);
    }
}
