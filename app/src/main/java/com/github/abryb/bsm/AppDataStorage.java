package com.github.abryb.bsm;

import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InvalidClassException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;
import java.util.Arrays;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

public class AppDataStorage {

    private static final String TAG = "AppDataStorage";
    private static final String AKS_ENCRYPTION_KEY_ALIAS = "key_encryption";
    private static final String DATA_FILE = "data.enc";
    private File dataFile;

    AppDataStorage(File directory) throws AppException {

        this.dataFile = new File(directory, DATA_FILE);
        initAndroidKeyStoreKeys();
    }

    public void saveData(AppData data) throws AppException {
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream out = new ObjectOutputStream(bos);
            out.writeObject(data);
            out.flush();
            byte[] plainBytes = bos.toByteArray();
            byte[] encryptedBytes = AppCrypto.encrypt(getAndroidKeyStoreSecretKey(), plainBytes);

            FileOutputStream f = new FileOutputStream(dataFile);
            f.write(encryptedBytes);

            f.close();
            out.close();
            bos.close();
        } catch (Exception e) {
            throw new AppException(e);
        }
    }

    public AppData loadData() throws AppException {
        try {
            byte[] encryptedBytes = new byte[(int) dataFile.length()];
            FileInputStream fis = new FileInputStream(dataFile);
            fis.read(encryptedBytes); //read file into bytes[]

            byte[] plainBytes = AppCrypto.decrypt(getAndroidKeyStoreSecretKey(), encryptedBytes);

            ByteArrayInputStream bis = new ByteArrayInputStream(plainBytes);
            ObjectInputStream in = new ObjectInputStream(bis);
            AppData appData = (AppData) in.readObject();

            fis.close();
            bis.close();
            in.close();

            return appData;

        } catch (IOException | ClassNotFoundException e) {
            return new AppData();
        } catch (Exception e) {
            throw new AppException(e);
        }
    }

    private SecretKey getAndroidKeyStoreSecretKey() throws AppException {
        try {
            KeyStore ks = KeyStore.getInstance("AndroidKeyStore");
            ks.load(null);
            KeyStore.SecretKeyEntry entry = (KeyStore.SecretKeyEntry) ks.getEntry(AKS_ENCRYPTION_KEY_ALIAS, null);
            return entry.getSecretKey();
        } catch (Exception e) {
            throw new AppException(e);
        }
    }

    protected void initAndroidKeyStoreKeys() throws AppException {
        try {
            KeyStore ks = KeyStore.getInstance("AndroidKeyStore");
            ks.load(null);

            if (!ks.containsAlias(AKS_ENCRYPTION_KEY_ALIAS)) {
                KeyGenParameterSpec.Builder builder = new KeyGenParameterSpec.Builder(AKS_ENCRYPTION_KEY_ALIAS,
                        KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT);
                KeyGenParameterSpec keySpec = builder
                        .setKeySize(256)
                        .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                        .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
//                    .setRandomizedEncryptionRequired(true)
                        // TODO
//                        .setUserAuthenticationRequired(true)
//                        .setUserAuthenticationValidityDurationSeconds(5 * 60)
                        .build();
                KeyGenerator kg = KeyGenerator.getInstance("AES", "AndroidKeyStore");
                kg.init(keySpec);
                kg.generateKey();
                Log.d(TAG, "AndroidKeyStore created key " + AKS_ENCRYPTION_KEY_ALIAS);
            }
        } catch (KeyStoreException | NoSuchAlgorithmException | CertificateException | IOException | NoSuchProviderException | InvalidAlgorithmParameterException e) {
            throw new AppException(e);
        }
    }
}
