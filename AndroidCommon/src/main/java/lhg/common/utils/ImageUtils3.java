package lhg.common.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapRegionDecoder;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.media.ExifInterface;
import android.util.Log;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 像微信一样的压缩尺寸
 */
public class ImageUtils3 {

    static final float divideRatio = 2f;
    private static final String TAG = "ImageUtils3";

    private static boolean regionRect(Rect rect, int srcWidth, int srcHeight, int index) {
        if (srcHeight > srcWidth) {
            int divsize = (int) (srcWidth *divideRatio);
            if (index * divsize >= srcHeight) {
                return false;
            }
            rect.set(0, index * divsize,
                    srcWidth, Math.min((index + 1) * divsize, srcHeight)
            );
            return true;
        } else {
            int divsize = (int) (srcHeight *divideRatio);
            if (index * divsize >= srcWidth) {
                return false;
            }
            rect.set(index * divsize, 0,
                    Math.min((index + 1) * divsize, srcWidth), srcHeight
                    );
            return true;
        }
    }

    public static int[] compress(String srcFile, String tagFile) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(srcFile, options);
        try {
            return compress(options.outWidth, options.outHeight, srcFile, tagFile);
        } catch (Exception e) {
            e.printStackTrace();
            int[] wh = new int[]{options.outWidth, options.outHeight};
            try {
                ExifInterface srcExif = new ExifInterface(srcFile);
                swapWhIfRatate(srcExif, wh);
            } catch (Exception ex) {
                Log.d(TAG, "cannot read exif" + ex);
            }
            return wh;
        }
    }

    public static List<String> compressOrDivide(String srcFile, String tagFile) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(srcFile, options);

        int longSide = Math.max(options.outWidth, options.outHeight);
        int shortSide = Math.min(options.outWidth, options.outHeight);
        if (shortSide * longSide > 1600 * 1600 && longSide / shortSide > 2) {
            //拆分图片
            return divide(options.outWidth, options.outHeight, srcFile, tagFile);
        } else {
            compress(options.outWidth, options.outHeight, srcFile, tagFile);
            return Arrays.asList(tagFile);
        }
    }

    private static List<String> divide(int srcWidth, int srcHeight, String srcFile, String tagFile) {
        int shortSide = Math.min(srcWidth, srcHeight);
        List<String> list = new ArrayList<>();
        BitmapRegionDecoder regionDecoder = null;
        try {
            Rect rect = new Rect();
            regionDecoder = BitmapRegionDecoder.newInstance(srcFile, true);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = computeSize(shortSide, (int) (shortSide * divideRatio));
            int divcount = 0;
            while (regionRect(rect, srcWidth, srcHeight, divcount++)) {
                Bitmap bitmap = regionDecoder.decodeRegion(rect, options);
                String file = tagFile + "_" + divcount;
                saveFile(bitmap, file);
                list.add(file);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (regionDecoder != null) {
                regionDecoder.recycle();
            }
        }
        return list;
    }

    public static int[] compress(int srcWidth, int srcHeight, String srcFile, String tagFile) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = computeSize(srcWidth, srcHeight);

        Bitmap tagBitmap = BitmapFactory.decodeFile(srcFile, options);
//        tagBitmap = rotatingImage(tagBitmap, srcExif);
        saveFile(tagBitmap, tagFile);
        int[] wh = new int[]{tagBitmap.getWidth(), tagBitmap.getHeight()};
        tagBitmap.recycle();

        ExifInterface srcExif = null, destExif = null;
        try {
            srcExif = new ExifInterface(srcFile);
            destExif = new ExifInterface(tagFile);
            int ration =  srcExif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 0);
            destExif.setAttribute(ExifInterface.TAG_ORIENTATION, String.valueOf(ration));
            destExif.saveAttributes();
            swapWhIfRatate(srcExif, wh);
        } catch (Exception ex) {
            Log.d(TAG, "cannot read exif" + ex);
        }
        return wh;
    }

    private static void swapWhIfRatate(ExifInterface exif, int[] wh) {
        int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
            case ExifInterface.ORIENTATION_ROTATE_270:
                int tmp = wh[0];//高宽交换
                wh[0] = wh[1];
                wh[1] = tmp;
                break;
        }
    }

    private static void saveFile(Bitmap bitmap, String file) {
        FileOutputStream stream = null;
        try {
            stream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 60, stream);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static int computeSize(int srcWidth, int srcHeight) {
        srcWidth = srcWidth % 2 == 1 ? srcWidth + 1 : srcWidth;
        srcHeight = srcHeight % 2 == 1 ? srcHeight + 1 : srcHeight;

        int longSide = Math.max(srcWidth, srcHeight);
        int shortSide = Math.min(srcWidth, srcHeight);

        float scale = shortSide * 1.f / longSide;
        if (scale <= 1 && scale > 0.5625) {
            if (longSide < 1664) {
                return 1;
            } else if (longSide >= 1664 && longSide < 4990) {
                return 2;
            } else if (longSide > 4990 && longSide < 10240) {
                return 4;
            } else {
                return longSide / 1280 == 0 ? 1 : longSide / 1280;
            }
        } else if (scale <= 0.5625 && scale > 0.5) {
            return longSide / 1280 == 0 ? 1 : longSide / 1280;
        } else {
            return (int) Math.ceil(shortSide / 900);
        }
    }

    private static Bitmap rotatingImage(Bitmap bitmap, ExifInterface srcExif) {
        if (srcExif == null) return bitmap;

        Matrix matrix = new Matrix();
        int angle = 0;
        int orientation = srcExif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                angle = 90;
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                angle = 180;
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                angle = 270;
                break;
        }

        matrix.postRotate(angle);

        if (angle != 0) {
            Bitmap newbmp = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            if (newbmp != bitmap) {
                bitmap.recycle();
            }
            return newbmp;
        }
        return bitmap;
    }
}