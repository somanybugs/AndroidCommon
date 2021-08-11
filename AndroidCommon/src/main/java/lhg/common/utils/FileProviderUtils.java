package lhg.common.utils;

import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;

import androidx.core.content.FileProvider;

import java.io.File;

/**
 * Company:
 * Project:
 * Author: liuhaoge
 * Date: 2020/11/24 20:37
 * Note:
 */
public class FileProviderUtils {
    public static File getExternalShareFile(Context context, String name) {
        return new File(context.getExternalFilesDir(null), "shout/" + name);
    }

    public static Uri uriFromFile(Context context, File file) {
        if (Build.VERSION.SDK_INT >= 24) {
            return FileProvider.getUriForFile(context.getApplicationContext(),
                    context.getPackageName() + ".fileprovider", file);
        } else {
            return Uri.fromFile(file);
        }
    }
}
