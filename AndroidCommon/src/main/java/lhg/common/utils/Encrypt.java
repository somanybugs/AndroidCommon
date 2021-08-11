package lhg.common.utils;

import android.util.Base64;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.UnsupportedEncodingException;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class Encrypt {
	private static String	TAG	= "Encrypt";

	public static String byte2HexString(byte abyte0[]) {
		StringBuffer stringbuffer = new StringBuffer();
		int i = abyte0.length;
		int j = 0;
		do {
			if (j >= i)
				return stringbuffer.toString();
			stringbuffer.append(Integer.toHexString(0x100 | 0xff & abyte0[j]).substring(1));
			j++;
		} while (true);
	}

	public static String MD5(File file) {
		FileInputStream is = null;
		MessageDigest md5 = null;
		String result = null;
		try {
			is = new FileInputStream(file);
			md5 = MessageDigest.getInstance("MD5");
			int n = 0;
			byte[] buffer = new byte[1024];
			do {
				n = is.read(buffer);
				if (n > 0) {
					md5.update(buffer, 0, n);
				}
			} while (n != -1);
			is.skip(0);
			byte[] encodedValue = md5.digest();
			result = byte2HexString(encodedValue);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				is.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return result;
	}

	public static String MD5(String s) {
		try {
			return byte2HexString(MessageDigest.getInstance("MD5").digest(s.getBytes("UTF-8")));
		}
		catch (Exception ex) {
			Log.e(TAG, ex.getMessage(), ex);
			return null;
		}
	}

	public static String MD5(byte[] s) {
		try {
			return byte2HexString(MessageDigest.getInstance("MD5").digest(s));
		}
		catch (Exception ex) {
			Log.e(TAG, ex.getMessage(), ex);
			return null;
		}
	}

	public static void setDes3Key(String str) {
		try {
			byte[] abyte1 = str.getBytes("UTF-8");
			byte[] abyte0 = new byte[24];
			System.arraycopy(abyte1, 0, abyte0, 0, Math.min(abyte1.length, abyte0.length));
			new DESedeKeySpec(abyte0);
		}
		catch (Exception ex) {
			Log.e(TAG, ex.getMessage(), ex);
		}
	}

	/**
	 * CBC解密
	 * 
	 * @param key
	 *            密钥
	 * @param keyiv
	 *            IV
	 * @param data
	 *            Base64编码的密文
	 * @return 明文
	 * @throws Exception
	 */
	public static String des3DecodeCBC(byte[] key, byte[] keyiv, String data) throws Exception {

		Key deskey = null;
		DESedeKeySpec spec = new DESedeKeySpec(key);
		byte[] decode_Data = Base64.decode(data, Base64.NO_WRAP);

		SecretKeyFactory keyfactory = SecretKeyFactory.getInstance("desede");

		deskey = keyfactory.generateSecret(spec);

		Cipher cipher = Cipher.getInstance("desede" + "/CBC/PKCS5Padding");
		IvParameterSpec ips = new IvParameterSpec(keyiv);

		cipher.init(Cipher.DECRYPT_MODE, deskey, ips);
		byte[] bOut = cipher.doFinal(decode_Data);

		return new String(bOut, "UTF-8");

	}

	public static String des3EncodeCBC(byte[] key, byte[] keyiv, String data) throws Exception {

		Key deskey = null;
		DESedeKeySpec spec = new DESedeKeySpec(key);

		SecretKeyFactory keyfactory = SecretKeyFactory.getInstance("desede");
		deskey = keyfactory.generateSecret(spec);

		Cipher cipher = Cipher.getInstance("desede" + "/CBC/PKCS5Padding");
		IvParameterSpec ips = new IvParameterSpec(keyiv);
		cipher.init(Cipher.ENCRYPT_MODE, deskey, ips);
		byte[] bOut = cipher.doFinal(data.getBytes("UTF-8"));

		//return new BASE64Encoder().encode(bOut);
		return Base64.encodeToString(bOut, Base64.NO_WRAP);
	}

	public static String sha256(String str){
		MessageDigest messageDigest;
		String encodestr = "";
		try {
			messageDigest = MessageDigest.getInstance("SHA-256");
			messageDigest.update(str.getBytes("UTF-8"));
			encodestr = Utils.bytesToHEX(messageDigest.digest());
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return encodestr;
	}

	public static String sha1(String data) throws NoSuchAlgorithmException {
		MessageDigest md = MessageDigest.getInstance("SHA1");
		md.update(data.getBytes());
		StringBuffer buf = new StringBuffer();
		byte[] bits = md.digest();
		return Utils.bytesToHEX(bits);
	}

	public static String hmac_sha1(String key, String datas) {
		String reString = "";
		try {
			byte[] data = key.getBytes("UTF-8");
			//根据给定的字节数组构造一个密钥,第二参数指定一个密钥算法的名称
			SecretKey secretKey = new SecretKeySpec(data, "HmacSHA1");
			//生成一个指定 Mac 算法 的 Mac 对象
			Mac mac = Mac.getInstance("HmacSHA1");
			//用给定密钥初始化 Mac 对象
			mac.init(secretKey);

			byte[] text = datas.getBytes("UTF-8");
			//完成 Mac 操作
			byte[] text1 = mac.doFinal(text);

			reString = Base64.encodeToString(text1, Base64.DEFAULT);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return reString;
	}


	public static byte[] hmac_sha256(String key, String datas) {
		String reString = "";
		try {
			byte[] data = key.getBytes("UTF-8");
			//根据给定的字节数组构造一个密钥,第二参数指定一个密钥算法的名称
			SecretKey secretKey = new SecretKeySpec(data, "HmacSHA256");
			//生成一个指定 Mac 算法 的 Mac 对象
			Mac mac = Mac.getInstance("HmacSHA256");
			//用给定密钥初始化 Mac 对象
			mac.init(secretKey);
			byte[] text = datas.getBytes("UTF-8");
			//完成 Mac 操作
			byte[] text1 = mac.doFinal(text);
			return text1;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
