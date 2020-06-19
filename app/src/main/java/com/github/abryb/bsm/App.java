package com.github.abryb.bsm;


import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.util.Log;

import java.io.IOException;
import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.spec.KeySpec;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import javax.security.auth.x500.X500Principal;

public class App extends android.app.Application {
    private static final String TAG = "Application";
    private static final String AKS_ENCRYPTION_KEY_ALIAS = "key_encryption";
    private static final String AKS_SIGN_KEY_ALIAS = "key_sign";

    private AppDataStorage appDataStorage;
    private AppData appData;

    private byte[] currentPasswordHash;

    @Override
    public void onCreate() {
        super.onCreate();
        try {
            initAndroidKeyStoreKeys();
            appDataStorage = new AppDataStorage(getFilesDir());
            appData = appDataStorage.loadData();
            if (passwordExists() && appData.getNote() == null) {
                saveNote("");
            }
        } catch (AppException e) {
            e.printStackTrace();
        }
    }

    public boolean passwordExists() {
        return appData.getPasswordSignature() != null;
    }

    public void setPassword(String password) throws AppException {
        SecureRandom secureRandom = new SecureRandom();

        byte[] passwordSalt = new byte[16];
        secureRandom.nextBytes(passwordSalt);

        try {
            byte[] passwordHash   = createPasswordHash(password, passwordSalt);
            PrivateKey privateKey = getAndroidKeyStoreSignKey().getPrivateKey();
            Signature signature = Signature.getInstance("SHA256withRSA");
            signature.initSign(privateKey);
            signature.update(passwordHash);

            byte[] passwordSignature = signature.sign();

            appData.setPasswordSalt(passwordSalt);
            appData.setPasswordSignature(passwordSignature);
            appDataStorage.saveData(appData);

            currentPasswordHash = passwordHash;

        } catch (Exception e) {
            throw new AppException(e);
        }
    }

    public boolean verifyPassword(String password) throws AppException {
        try {
            byte[] passwordHash = createPasswordHash(password, appData.getPasswordSalt());
            Certificate certificate = getAndroidKeyStoreSignKey().getCertificate();
            Signature signature = Signature.getInstance("SHA256withRSA");
            signature.initVerify(certificate);
            signature.update(passwordHash);
            boolean verified = signature.verify(appData.getPasswordSignature());
            if (verified) {
                currentPasswordHash = passwordHash;
                return true;
            }
            return false;

        } catch (Exception e) {
            throw new AppException(e);
        }
    }

    public void changePassword(String newPassword) throws AppException {
        try {
            String note = getNote();
            setPassword(newPassword);
            saveNote(note);
        } catch (Exception e) {
            throw new AppException(e);
        }
    }

    public void saveNote(String note) throws AppException {
        try {
            byte[] encrypted = encryptWithCurrentPasswordHash(note.getBytes());
            appData.setNote(encrypted);
            appDataStorage.saveData(appData);
        } catch (Exception e) {
            e.printStackTrace();
            throw new AppException(e);
        }
    }

    public String getNote() throws AppException {
        try {
            if (appData.getNote() == null) {
                return "";
            }
            byte[] decrypted = decryptWithCurrentPasswordHash(appData.getNote());
            return new String(decrypted);

        } catch (Exception e) {
            throw new AppException(e);
        }
    }

    private KeyStore.PrivateKeyEntry getAndroidKeyStoreSignKey() throws Exception {
        KeyStore ks = KeyStore.getInstance("AndroidKeyStore");
        ks.load(null);

        return (KeyStore.PrivateKeyEntry) ks.getEntry(AKS_SIGN_KEY_ALIAS, null);
    }

    private byte[] encryptWithCurrentPasswordHash(byte[] note) throws Exception {

        SecretKey secret = new SecretKeySpec(currentPasswordHash, "AES");

        return AppCrypto.encrypt(secret, note);
    }

    private byte[] decryptWithCurrentPasswordHash(byte[] input) throws Exception {

        SecretKey secret = new SecretKeySpec(currentPasswordHash, "AES");

        return AppCrypto.decrypt(secret, input);
    }

    private byte[] createPasswordHash(String password, byte[] salt) throws Exception {
        SecretKeyFactory factory = SecretKeyFactory
                .getInstance("PBKDF2WithHmacSHA1");
        KeySpec keySpec = new PBEKeySpec(password.toCharArray(), salt, 65536,
                256);
        return factory.generateSecret(keySpec).getEncoded();
    }

    protected void initAndroidKeyStoreKeys() throws AppException {
        try {
            KeyStore ks = KeyStore.getInstance("AndroidKeyStore");
            ks.load(null);

            if (!ks.containsAlias(AKS_SIGN_KEY_ALIAS)) {
                KeyGenParameterSpec.Builder builder = new KeyGenParameterSpec.Builder(
                        AKS_SIGN_KEY_ALIAS,
                        KeyProperties.PURPOSE_SIGN);
                KeyGenParameterSpec spec = builder
                        .setCertificateSubject(new X500Principal("CN=$ALIAS_VERIFY_SIGNATURE"))
                        .setDigests(KeyProperties.DIGEST_SHA256)
                        .setSignaturePaddings(KeyProperties.SIGNATURE_PADDING_RSA_PKCS1)
                        .setCertificateSerialNumber(new BigInteger("1337"))
                        .setCertificateNotBefore(new java.util.Date())
                        .build();

                KeyPairGenerator kpGenerator = KeyPairGenerator
                        .getInstance("RSA", "AndroidKeyStore");

                kpGenerator.initialize(spec);
                kpGenerator.generateKeyPair();

                Log.d(TAG, "AndroidKeyStore created key " + AKS_SIGN_KEY_ALIAS);
            }

        } catch (KeyStoreException | NoSuchAlgorithmException | CertificateException | IOException | NoSuchProviderException | InvalidAlgorithmParameterException e) {
            e.printStackTrace();
            throw new AppException(e);
        }
    }
}

