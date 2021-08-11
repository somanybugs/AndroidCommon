package lhg.common.utils;

import android.os.Environment;
import android.os.StatFs;
import android.util.Log;

import java.io.File;

@SuppressWarnings("deprecation")
public class SDCardUtil {
	private static final String	TAG		= "SDCardUtil";
	private static final int	ERROR	= -1;

	public static String getSDPath() {
		String result = null;
		File sdDir = null;
		boolean sdCardExist = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED); // 判断sd卡是否存在
		if (sdCardExist) {
			sdDir = Environment.getExternalStorageDirectory();// 获取根目录
			result = sdDir.getPath();
		}
		return result;
	}

	public static int getBlockSize() {
		int result = -1;
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			StatFs statfs = new StatFs(Environment.getExternalStorageDirectory().getPath());
			result = statfs.getBlockSize();
		}
		return result;
	}

	// return KB
	public static float getTotalSize() {
		float result = -1f;
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			StatFs statfs = new StatFs(Environment.getExternalStorageDirectory().getPath());
			result = (statfs.getBlockCount() * statfs.getBlockSize()) / 1024f;
		}
		return result;
	}

	// return KB
	public static float getFreeSize() {
		float result = -1;
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			StatFs statfs = new StatFs(Environment.getExternalStorageDirectory().getPath());
			result = (statfs.getAvailableBlocks() * statfs.getBlockSize()) / 1024f;
		}
		return result;
	}

	/**
	 * SDCARD是否存在
	 */
	public static boolean externalMemoryAvailable() {
		return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
	}

	/**
	 * 获取手机内部剩余存储空间
	 * 
	 * @return
	 */
	public static long getAvailableInternalMemorySize() {
		File path = Environment.getDataDirectory();
		StatFs stat = new StatFs(path.getPath());

		long blockSize = stat.getBlockSize();
		long availableBlocks = stat.getAvailableBlocks();
		return availableBlocks * blockSize;
	}

	/**
	 * 获取手机内部总的存储空间
	 * 
	 * @return
	 */
	public static long getTotalInternalMemorySize() {
		File path = Environment.getDataDirectory();
		StatFs stat = new StatFs(path.getPath());
		long blockSize = stat.getBlockSize();
		long totalBlocks = stat.getBlockCount();
		return totalBlocks * blockSize;
	}

	/**
	 * 获取SDCARD剩余存储空间
	 * 
	 * @return
	 */
	public static long getAvailableExternalMemorySize() {
		if (externalMemoryAvailable()) {
			File path = Environment.getExternalStorageDirectory();
			StatFs stat = new StatFs(path.getPath());
			long blockSize = stat.getBlockSize();
			long availableBlocks = stat.getAvailableBlocks();
			return availableBlocks * blockSize;
		} else {
			return ERROR;
		}
	}

	/**
	 * 获取SDCARD总的存储空间
	 * 
	 * @return
	 */
	public static long getTotalExternalMemorySize() {
		if (externalMemoryAvailable()) {
			File path = Environment.getExternalStorageDirectory();
			StatFs stat = new StatFs(path.getPath());
			long blockSize = stat.getBlockSize();
			long totalBlocks = stat.getBlockCount();
			return totalBlocks * blockSize;
		} else {
			return ERROR;
		}
	}

	/**
	 * 获取文件路径空间大小
	 * 
	 * @param path
	 * @return
	 */
	public static long getUsableSpace(String path) {
		try {
			final StatFs stats = new StatFs(path);
			return (long) stats.getBlockSize() * (long) stats.getAvailableBlocks();
		}
		catch (Exception e) {
			Log.e(TAG, "获取 sdcard 缓存大小 出错，请查看AndroidManifest.xml 是否添加了sdcard的访问权限");
			e.printStackTrace();
			return -1;
		}

	}
}
