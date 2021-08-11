package lhg.common.utils;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InvalidClassException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;

public class FileUtils {

	public static String getSuffix(String name) {
		if (TextUtils.isEmpty(name)) {
			return "";
		}
		int i = name.lastIndexOf('.');
		if (i == -1 || i == 0) {
			return "";
		}
		return name.substring(i + 1);
	}

	public static String removeLastSeparator(String path) {
		if (TextUtils.isEmpty(path)) {
			return path;
		}
		if (path.endsWith("\\") || path.endsWith("/")) {
			path = path.substring(0, path.length() - 1);
		}
		return path;
	}

	public static byte[] readBuff(AssetManager assetManager, String file) {
		InputStream inputStream = null;
		try {
			inputStream = assetManager.open(file);
			return readBuff(inputStream);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}

	public static byte[] readBuff(InputStream is) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		InputStream reader = null;
		try {
			byte[] buffer = new byte[2048];
			int byteread = 0;
			while ((byteread = is.read(buffer)) > 0) {
				baos.write(buffer, 0, byteread);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {

		}
		return baos.toByteArray();
	}

	public static byte[] readFileBuff(File file) {
		byte[] buff = new byte[(int) file.length()];
		InputStream reader = null;
		try {
			reader = new FileInputStream(file);
			reader.read(buff);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				reader.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return buff;
	}


	public static void copyAssets(Context context, String name, String destPath) {
		InputStream is = null;
		OutputStream os = null;
		try {
			int byteread = 0;
			is = context.getAssets().open(name);
			os = new FileOutputStream(destPath);
			byte[] buffer = new byte[2048];
			while ((byteread = is.read(buffer)) > 0) {
				os.write(buffer, 0, byteread);
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (is != null)
					is.close();
				if (os != null)
					os.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static void copy(String srcfile, String destPath) {
		copy(new File(srcfile), new File(destPath));
	}

	public static void copy(File srcfile, File destPath) {
		InputStream is = null;
		OutputStream os = null;
		try {
			int byteread = 0;
			is = new FileInputStream(srcfile);
			os = new FileOutputStream(destPath);
			byte[] buffer = new byte[2048];
			while ((byteread = is.read(buffer)) > 0) {
				os.write(buffer, 0, byteread);
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (is != null)
					is.close();
				if (os != null)
					os.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}



	public static String formatFileSize(long fileS) {
		DecimalFormat df = new DecimalFormat();
		df.setMaximumFractionDigits(2);
		String fileSizeString = "";
		if (fileS == 0) {
			return "0B";
		}
		if (fileS < 1024) {
			fileSizeString = fileS + "B";
		} else if (fileS < 1024 * 1024) {
			fileSizeString = df.format((double) fileS / 1024) + "K";
		} else if (fileS < 1024 * 1024 * 1024) {
			fileSizeString = df.format((double) fileS / (1024 * 1024)) + "M";
		} else {
			fileSizeString = df.format((double) fileS / (1024 * 1024 * 1024)) + "GB";
		}
		return fileSizeString;
	}


	public static void copyAssetsToPath(Context context , String assetsName, String destPath) {
		InputStream is= null;
		FileOutputStream fos = null;
		try {
			is = context.getAssets().open(assetsName);
			fos = new FileOutputStream(destPath);
			int size = 0;
			byte[] buffer = new byte[4096];
			while ((size = is.read(buffer)) > 0) {
				fos.write(buffer, 0, size);
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (is != null) {
					is.close();
				}
				if (fos != null) {
					fos.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static String	SDPATH	= Environment.getExternalStorageDirectory() + "/building/temp/";

	public static File saveBitmap(Bitmap bm, String picName) {
		Log.e("", "保存图片");
		try {
			if (!isFileExist("")) {
				File tempf = createSDDir("");
			}
			File f = new File(SDPATH, picName + ".JPEG");
			if (f.exists()) {
				f.delete();
			}
			FileOutputStream out = new FileOutputStream(f);
			bm.compress(Bitmap.CompressFormat.JPEG, 90, out);
			out.flush();
			out.close();
			Log.e("", "已经保存");
			return f;
		}
		catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static File createSDDir(String dirName) throws IOException {
		File dir = new File(SDPATH + dirName);
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {

			System.out.println("createSDDir:" + dir.getAbsolutePath());
			System.out.println("createSDDir:" + dir.mkdirs());
		}
		return dir;
	}

	public static boolean isFileExist(String fileName) {
		File file = new File(SDPATH + fileName);
		file.isFile();
		return file.exists();
	}

	public static void delFile(String fileName) {
		File file = new File(SDPATH + fileName);
		if (file.isFile()) {
			file.delete();
		}
		file.exists();
	}

	public static void deleteDir() {
		File dir = new File(SDPATH);
		deleteDir(dir);
	}

	public static boolean fileIsExists(String path) {
		try {
			File f = new File(path);
			if (!f.exists()) {
				return false;
			}
		}
		catch (Exception e) {

			return false;
		}
		return true;
	}

	
	/**
	 * 保存对象
	 * 
	 * @param object
	 * @throws IOException
	 */
	public static <T extends Serializable> boolean saveObject(Context context, T object, String fileName) {
		FileOutputStream fos = null;
		ObjectOutputStream oos = null;
		try {
			fos = context.openFileOutput(fileName, Context.MODE_PRIVATE);
			oos = new ObjectOutputStream(fos);
			oos.writeObject(object);
			oos.flush();
			return true;
		}
		catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		finally {
			try {
				oos.close();
			}
			catch (Exception e) {}
			try {
				fos.close();
			}
			catch (Exception e) {}
		}
	}

	/**
	 * 读取对象
	 * 
	 * @return
	 * @throws IOException
	 */
	@SuppressWarnings("unchecked")
	public static <T extends Serializable> T readObject(Context context, String fileName) {
		File file = context.getFileStreamPath(fileName);
		if (file.isDirectory() || !file.exists())
			return null;
		FileInputStream fis = null;
		ObjectInputStream ois = null;
		try {
			fis = new FileInputStream(file);
			ois = new ObjectInputStream(fis);
			return (T) ois.readObject();
		}
		catch (Exception e) {
			e.printStackTrace();
			//反序列化失败 - 删除缓存文件
			if (e instanceof InvalidClassException) {
				file.delete();
			}
		}
		finally {
			try {
				ois.close();
			}
			catch (Exception e) {}
			try {
				fis.close();
			}
			catch (Exception e) {}
		}
		return null;
	}

	public static boolean isDotDotFile(String name) {
		return name.equals(".") || name.equals("..");
	}

	public static void delete(File file) {
		if (file == null || !file.exists()) {
			return;
		}
		if (isDotDotFile(file.getName())) {
			return;
		}

		if (file.isDirectory()) {
			File[] files = file.listFiles();
			if (files != null && files.length > 0) {
				for (File f : files) {
					delete(f);
				}
			}
		}
		file.delete();
	}

	public static void deleteDir(File dir) {
		delete(dir);
	}

	public static void write(String filePath, String text) {
		write(filePath, text.getBytes());
	}

	public static void write(String filePath, byte[] buf) {
		write(new File(filePath), buf);
	}

	public static void write(File file, byte[] buf) {
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(file);
			fos.write(buf);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public static byte[] read(File file) {
		FileInputStream fis = null;
		try {
			byte[] data = new byte[(int) file.length()];
			fis = new FileInputStream(file);
			int offset = 0;
			while (offset < data.length) {
				int readLen = fis.read(data, offset, Math.min(1024, data.length - offset));
				if (readLen <= 0) {
					break;
				}
				offset += readLen;
			}
			return data;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (fis != null) {
				try {
					fis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}

	public static byte[] read(InputStream is) {
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			byte[] data = new byte[1024];
			int size = 0;
			while ((size = is.read(data)) > 0) {
				baos.write(data, 0, size);
			}
			return baos.toByteArray();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}



	public static String readFile(File file, String charset) {
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(file);
			return inputstream2text(fis, charset);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			if (fis != null) {
				try {
					fis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}


	public static String inputstream2text(InputStream is) {
		return inputstream2text(is, "utf-8");
	}

	public static String inputstream2text(InputStream is, String charset) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			byte[] chs = new byte[2048];
			int count = 0;
			while ((count = is.read(chs)) > 0) {
				baos.write(chs, 0, count);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {

		}
		try {
			return new String(baos.toByteArray(), charset);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}


	public static void writeFile(File file, InputStream is) throws IOException {
		byte[] buf = new byte[2048];
		int len = 0;
		FileOutputStream fos = null;
		// 储存下载文件的目录
		try {
			fos = new FileOutputStream(file);
			while ((len = is.read(buf)) != -1) {
				fos.write(buf, 0, len);
			}
			fos.flush();
		} finally {
			try {
				if (fos != null)
					fos.close();
			} catch (IOException e) {
			}
		}
	}
}
