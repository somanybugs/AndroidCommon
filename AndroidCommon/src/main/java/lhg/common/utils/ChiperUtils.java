package lhg.common.utils;

import android.content.Context;
import android.text.TextUtils;
import android.util.Base64;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class ChiperUtils {
    static {
        System.loadLibrary("cipher");
    }
    public static byte[] hashmac1(Context context, byte[] key, byte[] text) {
        return sign1(context, 1, key, text);
    }
    public static byte[] hashmac256(Context context, byte[] key, byte[] text) {
        return sign1(context, 2, key, text);
    }
    private static native byte[] sign1(Context context, int x, byte[] key, byte[] text);

//    public static byte[] hashmac1(Context context, byte[] key, byte[] text) {
//        key = "uJCyczmjj6y3A0BGGDelcvo1Ozi7AU".getBytes();
//        if (isEmpty(text) || isEmpty(key)) {
//            return null;
//        }
//        String type = "HmacSHA1";
//        SecretKeySpec secret = new SecretKeySpec(key, type);
//        Mac mac = null;
//        try {
//            mac = Mac.getInstance(type);
//            mac.init(secret);
//            byte[] digest = mac.doFinal(text);
//            return digest;
//        } catch (NoSuchAlgorithmException e) {
//            e.printStackTrace();
//        } catch (InvalidKeyException e) {
//            e.printStackTrace();
//        }
//        return null;
//    }
//
    private static boolean isEmpty(byte[] key) {
        return key == null || key.length == 0;
    }
//
    public static byte[] hashmac256_java(Context context, byte[] key, byte[] text) {
//        key = "uJCyczmjj6y3A0BGGDelcvo1Ozi7AU".getBytes();
        if (isEmpty(text) || isEmpty(key)) {
            return null;
        }
        String type = "HmacSHA256";
        SecretKeySpec secret = new SecretKeySpec(key, type);
        Mac mac = null;
        try {
            mac = Mac.getInstance(type);
            mac.init(secret);
            byte[] digest = mac.doFinal(text);
            return digest;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }
        return null;
    }
//
}
