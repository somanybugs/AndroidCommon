package lhg.common.utils;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class ImageUtils {
	private static final String	TAG	= "ImageUtils";

	public static boolean scaleImage(String srcJPG, String destJPG, int minSideLength, int maxNumOfPixels) {
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(srcJPG, options);
		if (!thumbBitmap(srcJPG, destJPG, options, minSideLength, maxNumOfPixels)) {
			try {
				FileUtils.copy(srcJPG, destJPG);
			}
			catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		}
		return true;
	}
	public static Bitmap loadScaleBitmap(String srcJPG, int minSideLength, int maxNumOfPixels) {
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(srcJPG, options);
		int scale = ThumbnailUtils.computeSampleSize(options, minSideLength, maxNumOfPixels);
		options.inJustDecodeBounds = false;
		options.inSampleSize = Math.max(1, scale);
		return BitmapFactory.decodeFile(srcJPG, options);
	}

	public static boolean saveBitmap(Bitmap bmp, String desFile) {
		try {
			File file = new File(desFile);
			file.delete();
			FileOutputStream out = new FileOutputStream(file);
			if (bmp.compress(Bitmap.CompressFormat.JPEG, 90, out)) {
				out.flush();
				out.close();
				Log.i(TAG, "保存的照片路径 " + desFile);
				return true;
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public static boolean thumbBitmap(String srcJPG, String destJPG, BitmapFactory.Options options, int minSideLength,
			int maxNumOfPixels) {
		int scale = ThumbnailUtils.computeSampleSize(options, minSideLength, maxNumOfPixels);
		if (scale > 1) {
			options.inJustDecodeBounds = false;
			options.inSampleSize = scale;
			Bitmap bitmap = BitmapFactory.decodeFile(srcJPG, options);

			if (bitmap != null) {
				saveBitmap(bitmap, destJPG);

				ExifInterface srcExif = null, destExif = null;
				try {
					srcExif = new ExifInterface(srcJPG);
					destExif = new ExifInterface(destJPG);
					int ration =  srcExif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 0);
					destExif.setAttribute(ExifInterface.TAG_ORIENTATION, String.valueOf(ration));
					destExif.saveAttributes();
				} catch (Exception ex) {
					Log.d(TAG, "cannot read exif" + ex);
				}

				options.outHeight = bitmap.getHeight();
				options.outWidth = bitmap.getWidth();
				if (!bitmap.isRecycled()) {
					bitmap.recycle();
				}
				bitmap = null;
				return true;
			}
		} else {
//			options.inJustDecodeBounds = false;
//			options.inSampleSize = 1;
//			Bitmap bitmap = BitmapFactory.decodeFile(srcJPG, options);
//			if (bitmap != null) {
//				saveBitmap(bitmap, destJPG);
//				options.outHeight = bitmap.getHeight();
//				options.outWidth = bitmap.getWidth();
//				if (!bitmap.isRecycled()) {
//					bitmap.recycle();
//				}
//				bitmap = null;
//				return true;
//			}
		}
		return false;
	}

	/**
	 * 旋转图片，使图片保持正确的方向。
	 * @param bitmap 原始图片
	 * @param degrees 原始图片的角度
	 * @return Bitmap 旋转后的图片
	 */
	public static Bitmap rotateBitmap(Bitmap bitmap, int degrees) {
		if (degrees == 0 || null == bitmap) {
			return bitmap;
		}
		Matrix matrix = new Matrix();
		matrix.setRotate(degrees, bitmap.getWidth() / 2, bitmap.getHeight() / 2);
		Bitmap bmp = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
		if (null != bitmap) {
			bitmap.recycle();
		}
		return bmp;
	}

	public static int readPictureDegree(String path) {
		int degree = 0;
		try {
			ExifInterface exifInterface = new ExifInterface(path);
			int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION,ExifInterface.ORIENTATION_NORMAL);
			switch (orientation) {
				case ExifInterface.ORIENTATION_ROTATE_90:
					degree = 90;
					break;
				case ExifInterface.ORIENTATION_ROTATE_180:
					degree = 180;
					break;
				case ExifInterface.ORIENTATION_ROTATE_270:
					degree = 270;
					break;
			}
		} catch (IOException e) {
			e.printStackTrace();
			return degree;
		}
		return degree;
	}


	public static byte[] thumbBitmap(ContentResolver contentResolver, Uri uri, int minSideLength, int maxNumOfPixels) {
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;

		InputStream is = null;
		try {
			is = contentResolver.openInputStream(uri);
			BitmapFactory.decodeStream(is, null, options);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				is= null;
			}
		}

		int scale = ThumbnailUtils.computeSampleSize(options, minSideLength, maxNumOfPixels);
		options.inJustDecodeBounds = false;
		options.inSampleSize = scale;

		try {
			is = contentResolver.openInputStream(uri);
			Bitmap bitmap = BitmapFactory.decodeStream(is, null, options);
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			if (bitmap.compress(Bitmap.CompressFormat.JPEG, 80, out)) {
				return out.toByteArray();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}



	public static void addBitmapToAlbum(Context context, String displayName, InputStream inputStream, String mimeType) {
//		String mimeType = "image/jpeg";
		ContentValues values = new ContentValues();
		values.put(MediaStore.MediaColumns.DISPLAY_NAME, displayName);
		values.put(MediaStore.MediaColumns.MIME_TYPE, mimeType);

		if (Build.VERSION.SDK_INT >= 29 ) {
//			values.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES); TODO 这一行代码报错,不知道为什么
			values.put("relative_path", Environment.DIRECTORY_PICTURES);
		} else {
			File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), displayName);
			values.put(MediaStore.MediaColumns.DATA, file.getAbsolutePath());
		}

		ContentResolver contentResolver = context.getContentResolver();
		Uri uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

		if (uri != null) {

			OutputStream outputStream = null;
			try {
				outputStream = contentResolver.openOutputStream(uri);
				byte[] buffer = new byte[4096];
				int len = 0;
				while ((len = inputStream.read(buffer)) != -1) {
					outputStream.write(buffer, 0, len);
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if (outputStream != null) {
					try {
						outputStream.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}


//
//	private void writeFile(String imagePath, ContentValues values, ContentResolver contentResolver, Uri item) {
//		try (OutputStream rw = contentResolver.openOutputStream(item, "rw")) {
//			// Write data into the pending image.
//			Sink sink = Okio.sink(rw);
//			BufferedSource buffer = Okio.buffer(Okio.source(new File(imagePath)));
//			buffer.readAll(sink);
//			values.put(MediaStore.Video.Media.IS_PENDING, 0);
//			contentResolver.update(item, values, null, null);
//			new File(imagePath).delete();
//			if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
//				Cursor query = contentResolver.query(item, null, null, null);
//				if (query != null) {
//					int count = query.getCount();
//					Log.e("writeFile","writeFile result :" + count);
//					query.close();
//				}
//			}
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//	}
}
