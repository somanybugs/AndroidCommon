package lhg.common.utils;

import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.interfaces.RSAPublicKey;


public class SSHRsaUtils {

    public static void genSSHRsa(String publicKeyPath, String privateKeyPath, String pukNote) {
        StringBuilder sbpuk = new StringBuilder();
        StringBuilder sbprk = new StringBuilder();
        genSSHRsa(sbpuk, sbprk, pukNote);
        FileUtils.write(publicKeyPath, sbpuk.toString());
        FileUtils.write(privateKeyPath, sbprk.toString());
    }

       public static void genSSHRsa(StringBuilder sbpuk, StringBuilder sbprk, String pukNote) {
           ByteArrayOutputStream puk = new ByteArrayOutputStream();
           ByteArrayOutputStream prk = new ByteArrayOutputStream();
           KeyPairGenerator generator = null;
           try {
               generator = KeyPairGenerator.getInstance("RSA");
               generator.initialize(2048);
               KeyPair keyPair = generator.genKeyPair();
               encodePublicKey(puk, (RSAPublicKey) keyPair.getPublic());

               sbpuk.append("ssh-rsa ");
               sbpuk.append(Base64.encodeToString(puk.toByteArray(), Base64.NO_WRAP));
               sbpuk.append(" ").append(pukNote);

               sbprk.append("-----BEGIN RSA PRIVATE KEY-----\n");
               sbprk.append(new String(keyPair.getPrivate().getEncoded())).append("\n");
               sbprk.append("-----END RSA PRIVATE KEY-----\n");
           } catch (Exception e) {
               e.printStackTrace();
           }
       }

    public static void genSSHRsa(ByteArrayOutputStream pubkey, ByteArrayOutputStream prikey) {
        KeyPairGenerator generator = null;
        try {
            generator = KeyPairGenerator.getInstance("RSA");
            generator.initialize(2048);
            KeyPair keyPair = generator.genKeyPair();
            encodePublicKey(pubkey, (RSAPublicKey) keyPair.getPublic());
            encodePrivateKey(prikey, keyPair.getPrivate());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void encodePublicKey(ByteArrayOutputStream out, RSAPublicKey key) throws IOException {
        /* encode the "ssh-rsa" string */
        byte[] sshrsa = new byte[]{0, 0, 0, 7, 's', 's', 'h', '-', 'r', 's', 'a'};
        out.write(sshrsa);
        /* Encode the public exponent */
        BigInteger e = key.getPublicExponent();
        byte[] data = e.toByteArray();
        encodeUInt32(data.length, out);
        out.write(data);
        /* Encode the modulus */
        BigInteger m = key.getModulus();
        data = m.toByteArray();
        encodeUInt32(data.length, out);
        out.write(data);
    }

    public static void encodePrivateKey(ByteArrayOutputStream out, PrivateKey key) throws IOException {
        out.write(key.getEncoded());
    }

    public static void encodeUInt32(int value, OutputStream out) throws IOException {
        byte[] tmp = new byte[4];
        tmp[0] = (byte) ((value >>> 24) & 0xff);
        tmp[1] = (byte) ((value >>> 16) & 0xff);
        tmp[2] = (byte) ((value >>> 8) & 0xff);
        tmp[3] = (byte) (value & 0xff);
        out.write(tmp);
    }

}
