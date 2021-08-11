package lhg.common.utils;

import android.content.Context;
import android.security.KeyPairGeneratorSpec;
import android.security.keystore.KeyProperties;

import java.io.IOException;
import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.util.Calendar;

import javax.crypto.Cipher;
import javax.security.auth.x500.X500Principal;

/**
 * Company:
 * Project:
 * Author: liuhaoge
 * Date: 2020/12/13 19:11
 * Note:
 */
public class KeyStoreUtils {

    private static final String KEYSTORE_PROVIDER = "AndroidKeyStore";
    private static final String RSA_MODE = "RSA/ECB/PKCS1Padding";
    static KeyStore keyStore = null;

    private static KeyStore initKeyStore() throws KeyStoreException, CertificateException, NoSuchAlgorithmException, IOException {
        if (keyStore == null) {
            synchronized (KeyStoreUtils.class) {
                if (keyStore == null) {
                    keyStore = KeyStore.getInstance(KEYSTORE_PROVIDER);
                    keyStore.load(null);
                }
            }
        }
        return keyStore;
    }

    public static void generateRSAKey(Context context, String alias) throws NoSuchAlgorithmException, NoSuchProviderException, InvalidAlgorithmParameterException, KeyStoreException, CertificateException, IOException, UnrecoverableKeyException {
        KeyStore keyStore = initKeyStore();
        Certificate certificate = keyStore.getCertificate(alias);
        PrivateKey privateKey = (PrivateKey) keyStore.getKey(alias, null);
        if (certificate != null && privateKey != null) {
            return;
        }

        Calendar start = Calendar.getInstance();
        Calendar end = Calendar.getInstance();
        end.add(Calendar.YEAR, 30);

        KeyPairGeneratorSpec spec = new KeyPairGeneratorSpec.Builder(context)
                .setAlias(alias)
                .setSubject(new X500Principal("CN=" + alias))
                .setSerialNumber(BigInteger.TEN)
                .setStartDate(start.getTime())
                .setEndDate(end.getTime())
                .build();

        KeyPairGenerator keyPairGenerator = KeyPairGenerator
                .getInstance(KeyProperties.KEY_ALGORITHM_RSA, KEYSTORE_PROVIDER);

        keyPairGenerator.initialize(spec);
        keyPairGenerator.generateKeyPair();
    }

    public static byte[] encryptRSA(String alias, byte[] plainText) throws Exception {
        KeyStore keyStore = initKeyStore();
        PublicKey publicKey = keyStore.getCertificate(alias).getPublicKey();

        Cipher cipher = Cipher.getInstance(RSA_MODE);
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);

        byte[] encryptedByte = cipher.doFinal(plainText);
        return encryptedByte;
    }

    public static byte[] decryptRSA(String alias, byte[] encryptedText) throws Exception {
        KeyStore keyStore = initKeyStore();
        PrivateKey privateKey = (PrivateKey) keyStore.getKey(alias, null);
        Cipher cipher = Cipher.getInstance(RSA_MODE);
        cipher.init(Cipher.DECRYPT_MODE, privateKey);

        byte[] decryptedBytes = cipher.doFinal(encryptedText);
        return decryptedBytes;
    }

}
