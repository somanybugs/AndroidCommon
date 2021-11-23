package lhg.common.utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.database.Cursor;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.telephony.TelephonyManager;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.ColorUtils;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import lhg.common.R;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.URLDecoder;
import java.security.MessageDigest;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

/**
 * Created by lhg on 2017/6/20.
 */

public class Utils {


    public static <T extends View> T findFirstView(Activity activity, Class<T> clazz) {
       return findFirstView(activity.getWindow().getDecorView(), clazz);
    }

    public static <T extends View> T findFirstView(View view, Class<T> clazz) {
        if (view == null) {
            return null;
        }
        if (clazz.isAssignableFrom(view.getClass())) {
            return (T) view;
        }
        if (view instanceof ViewGroup) {
            ViewGroup group = (ViewGroup) view;
            for (int i = 0; i < group.getChildCount(); i++) {
                View ret = findFirstView(group.getChildAt(i), clazz);
                if (ret != null) {
                    return (T) ret;
                }
            }
        }
        return null;
    }

    public static String[] toStringArray(int[] vals) {
        String[] items = new String[vals.length];
        for (int i = 0;i<items.length;i++) {
            items[i] = String.valueOf(vals[i]);
        }
        return items;
    }
    //将相同名字的属性从src赋值到dest
    public static void cloneFields(Object src, Object dest) {
        try {
            Field[] tmp = dest.getClass().getDeclaredFields();
            Map<String, Field> destFields = new HashMap<>();
            for (Field f : tmp) {
                destFields.put(f.getName(), f);
            }

            Field[] srcFields = src.getClass().getDeclaredFields();
            for (Field sf : srcFields) {
                Field df = destFields.get(sf.getName());
                if (df != null) {
                    sf.setAccessible(true);
                    df.setAccessible(true);
                    df.set(dest, sf.get(src));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    //将相同名字的属性从src赋值到dest
    public static <T> T cloneObject(T src) {
        try {
            T dest = (T) src.getClass().newInstance();
            Field[] srcFields = src.getClass().getDeclaredFields();
            for (Field sf : srcFields) {
                sf.setAccessible(true);
                sf.set(dest, sf.get(src));
            }
            return dest;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public static String exceptionToString(Exception e) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        String rt = sw.toString();
        pw.close();
        return rt;
    }

    public static byte[] int_to_buf4_lh(int inword) {
        byte[] outbuf = new byte[4];
        int_to_buf4_lh(inword, outbuf, 0);
        return outbuf;
    }
    public static void int_to_buf4_lh(int inword, byte[] outbuf, int offset) {
        //{*((UBYTE *)(outbuf))=(UBYTE)*((char *)&indword+LONG_HIGH0);*((UBYTE*)(outbuf)+1)=(UBYTE)*((char *)&indword+LONG_HIGH1);*((UBYTE *)(outbuf)+2)=*((char *)&indword+LONG_HIGH2);*((UBYTE *)(outbuf)+3)=*((char *)&indword+LONG_HIGH3);}
        outbuf[offset++] = (byte) ((inword >> 0) & 0xff);
        outbuf[offset++] = (byte) ((inword >> 8) & 0xff);
        outbuf[offset++] = (byte) ((inword >> 16) & 0xff);
        outbuf[offset++] = (byte) ((inword >> 24) & 0xff);
    }
    	public static void int_to_buf4_hl(long inword, byte[] outbuf, int offset){
		// {*((UBYTE *)(outbuf))=(UBYTE)*((char *)&indword+LONG_HIGH3);*((UBYTE*)(outbuf)+1)=(UBYTE)*((char *)&indword+LONG_HIGH2);*((UBYTE *)(outbuf)+2)=*((char *)&indword+LONG_HIGH1);*((UBYTE *)(outbuf)+3)=*((char *)&indword+LONG_HIGH0);}
		outbuf[offset++] = (byte) ((inword >> 24) & 0xff);
		outbuf[offset++] = (byte) ((inword >> 16) & 0xff);
		outbuf[offset++] = (byte) ((inword >> 8) & 0xff);
		outbuf[offset++] = (byte) ((inword >> 0) & 0xff);

	}
    public static int buf4_to_int_lh(byte[] inbuf, int offset){
        // {*((char *)&outdword+LONG_HIGH0)=*((UBYTE *)inbuf+0);*((char *)&outdword+LONG_HIGH1)=*((UBYTE *)(inbuf)+1);*((char *)&outdword+LONG_HIGH2)=*((UBYTE *)(inbuf)+2);*((char *)&outdword+LONG_HIGH3)=*((UBYTE *)(inbuf)+3);}
        return ((0xff & inbuf[offset++]) << 0) +
                ((0xff & inbuf[offset++]) << 8) +
                ((0xff & inbuf[offset++]) << 16) +
                ((0xff & inbuf[offset++]) << 24)
                ;
    }
    public static int buf4_to_int_lh(byte[] inbuf){
        // {*((char *)&outdword+LONG_HIGH0)=*((UBYTE *)inbuf+0);*((char *)&outdword+LONG_HIGH1)=*((UBYTE *)(inbuf)+1);*((char *)&outdword+LONG_HIGH2)=*((UBYTE *)(inbuf)+2);*((char *)&outdword+LONG_HIGH3)=*((UBYTE *)(inbuf)+3);}
        return buf4_to_int_lh(inbuf, 0);
    }

	public static int buf4_to_int_hl(byte[] inbuf, int offset){
		// {*((char *)&outdword+LONG_HIGH3)=*((UBYTE *)inbuf+0);*((char *)&outdword+LONG_HIGH2)=*((UBYTE *)(inbuf)+1);*((char *)&outdword+LONG_HIGH1)=*((UBYTE *)(inbuf)+2);*((char *)&outdword+LONG_HIGH0)=*((UBYTE *)(inbuf)+3);}
		return ((0xff & inbuf[offset++]) << 24) +
				((0xff & inbuf[offset++]) << 16) +
				((0xff & inbuf[offset++]) << 8) +
				((0xff & inbuf[offset++]) << 0)
				;
	}

    public static int Int(Number i) {
        return i == null ? 0 : i.intValue();
    }

    public static boolean isListEmpty(Collection<?> list) {
        return list == null || list.isEmpty();
    }

    public static void shareText(Context context, String text) {
        Intent intent = new Intent(Intent.ACTION_SEND); // 启动分享发送的属性
        intent.setType("text/plain"); // 分享发送的数据类型
        intent.putExtra(Intent.EXTRA_TEXT, text); // 分享的内容
        context.startActivity(Intent.createChooser(intent, "分享到"));// 目标应用选择对话框的标题
    }

    public static void shareImage(Context context, String file) {
        Uri uri = FileProviderUtils.uriFromFile(context, new File(file));
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
        shareIntent.setType("image/jpeg");
        context.startActivity(Intent.createChooser(shareIntent, "分享图片到"));// 目标应用选择对话框的标题
    }

    public static void shareFile(Context context, File file) {
        Uri uri = FileProviderUtils.uriFromFile(context, file);
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
        shareIntent.setType("*/*");
        context.startActivity(Intent.createChooser(shareIntent, "发送文件到"));// 目标应用选择对话框的标题
    }


    public static void copy2Clipboard(Context context, String text) {
        ClipboardManager cmb = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        cmb.setPrimaryClip(ClipData.newPlainText(null, text));
    }

    public static void clearClipboard(Context context) {
        ClipboardManager cmb = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        cmb.setPrimaryClip(ClipData.newPlainText(null, ""));
    }

    public static void hideKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static void showKeyboard(View focus) {
        InputMethodManager inputMethodManager = (InputMethodManager) focus.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.showSoftInput(focus, InputMethodManager.SHOW_FORCED);
    }

    public static int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    public static String uniqueId(Context context) {
        try {
            final TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            final String tmDevice, androidId;
            tmDevice = tm.getDeviceId();
            androidId = android.provider.Settings.Secure.getString(context.getContentResolver(),
                    android.provider.Settings.Secure.ANDROID_ID);
            if (TextUtils.isEmpty(tmDevice) && TextUtils.isEmpty(androidId)) {
                return null;
            }
            MessageDigest md = MessageDigest.getInstance("MD5");
            if (!TextUtils.isEmpty(tmDevice)) {
                md.update(tmDevice.getBytes());
            }
            if (!TextUtils.isEmpty(androidId)) {
                md.update(androidId.getBytes());
            }
            StringBuffer hexString = new StringBuffer();
            byte[] messageDigest = md.digest();
            for (int i = 0; i < messageDigest.length; i++)
                hexString.append(Integer.toHexString(0xFF & messageDigest[i]));
            return hexString.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String uniqueId2(Context context) {
        String uid = uniqueId(context);
        if (TextUtils.isEmpty(uid)) {
            uid = UUID.randomUUID().toString();
        }
        return uid;
    }

    public static void openDefaultBrowser(Context context, String url) {
        Intent intent = new Intent();
        intent.setAction("android.intent.action.VIEW");
        Uri content_url = Uri.parse(url);
        intent.setData(content_url);
        context.startActivity(intent);
    }

    public static String parseFilePath(Context context, Uri uri) {
        String picturePath = null;
        try {
            if (uri.getScheme().startsWith("file")) {
                picturePath = uri.toString().substring("file://".length());
                picturePath = URLDecoder.decode(picturePath, "utf-8");
            } else {
                String[] filePathColumn = {MediaStore.Images.Media.DATA};
                Cursor cursor = context.getContentResolver().query(uri, filePathColumn, null, null, null);
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                picturePath = cursor.getString(columnIndex);
                cursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return picturePath;
    }



    public static void showConfirmDialog(Context context, CharSequence title, CharSequence message, final Runnable okRun) {
        new AlertDialog.Builder(context).setTitle(title).setMessage(message)
                .setPositiveButton(context.getString(R.string.common_ok), new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (okRun != null) {
                            okRun.run();
                        }
                    }
                }).setNegativeButton(context.getString(R.string.common_cancel), null).show();
    }

    public static <Key> void removeFromMapThatKeyNotExistInList(Map<Key, ?> map, List<Key> list) {
        Object[] keys = map.keySet().toArray();
        for (Object key : keys) {
            if (!list.contains(key)) {
                map.remove(key);
            }
        }
    }

    public static String nil(String text) {
        return text == null ? "" : text;
    }

    public static int getAppVersionCode(Context context) {
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
            return pi.versionCode;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static Drawable getAppIcon(Context app) {
        ApplicationInfo info = app.getApplicationInfo();
        return info.loadIcon(app.getPackageManager());
    }


    static DecimalFormat twofractNf = null;

    public static DecimalFormat twofractNf() {
        if (twofractNf == null) {
            synchronized (Utils.class) {
                if (twofractNf == null) {
                    twofractNf = new DecimalFormat();
                    twofractNf.setMaximumFractionDigits(2);
                }
            }
        }
        return twofractNf;
    }

    public static String max2fract(float number) {
        String text = twofractNf().format(number);
        return text.replaceAll(",", "");
    }

    public static String max2fract(double number) {
        String text = twofractNf().format(number);
        return text.replaceAll(",", "");
    }

    public static Date getZeroClock(long time) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }


    public static String[] split(String text, String sep) {
        if (TextUtils.isEmpty(text)) {
            return null;
        }
        List<String> list = new ArrayList<>();
        for (int i = 0, size = text.length(); i < size; ) {
            int f = text.indexOf(sep, i);
            if (f < 0) {
                list.add(text.substring(i));
                break;
            }
            if (f == i) {
                list.add("");
            } else {
                list.add(text.substring(i, f));
            }
            i = f + sep.length();
        }
        return list.toArray(new String[]{});
    }

    public static <T> String join(List<T> list, String sep) {
        if (list == null) {
            return null;
        }
        StringBuilder result = new StringBuilder();
        for (int i = 0, size = list.size(); i < size; i++) {
            T obj = list.get(i);
            result.append(obj.toString());
            if (i < size - 1) {
                result.append(sep);
            }
        }
        return result.toString();
    }

    public static void dial(String number, Context context) {
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + number));
        context.startActivity(intent);
    }

    public static void sms(String number, Context context, String message) {
        Uri uri = Uri.parse("smsto:" + number);
        Intent sendIntent = new Intent(Intent.ACTION_VIEW, uri);
        sendIntent.putExtra("sms_body", message);
        context.startActivity(sendIntent);
    }


    public static SpannableStringBuilder append(SpannableStringBuilder builder, CharSequence text, Object... whats) {
        int start = builder.length();
        builder.append(text);
        if (whats != null) {
            for (Object what : whats) {
                builder.setSpan(what, start, builder.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
            }
        }

        return builder;
    }


    public static String replaceFirst(String str, String oldStr, String newStr) {
        int i = str.indexOf(oldStr);
        if (i == -1) return str;
        str = str.substring(0, i) + newStr + str.substring(i + oldStr.length());
        return str;
    }

    public static Point screenSizeInPixel(Context context) {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        int widthPixels = dm.widthPixels;
        int heightPixels = dm.heightPixels;
        return new Point(Math.min(widthPixels, heightPixels), Math.max(widthPixels, heightPixels));
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    public static int vrtualBtnHeight(Context ctx) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            WindowManager wm = (WindowManager) ctx.getSystemService(Context.WINDOW_SERVICE);
            Display display = wm.getDefaultDisplay();
            Point realSize = new Point(), size = new Point();
            display.getRealSize(realSize);
            display.getSize(size);
            return realSize.y - size.y;
        }
        return 0;
    }


    public static String formatDistance(double meters) {
        if (meters < 1000) {
            return ((int) meters) + "m";
        }
        return ((int) (meters / 1000)) + "km";
    }


    static String numbers = "0123456789.";
    //格式化输入金额字符串
    public static String formatInputAmountText(String text) {
        if (TextUtils.isEmpty(text)) {
            return "";
        }

        StringBuilder sb = new StringBuilder();

        //删除所有非数字字符
        for (char ch : text.toCharArray()) {
            if (numbers.contains(String.valueOf(ch))) {
                sb.append(ch);
            }
        }

        int dot1Index = sb.indexOf(".");
        if (dot1Index >= 0) {
            int dot2Index = sb.indexOf(".", dot1Index + 1);
            //不能超过两个dot
            if (dot2Index >= 0) {
                sb.delete(dot2Index, sb.length());
            }

            //dot后不能超过两位小数
            if (sb.length() - (dot1Index + 1) > 2) {
                sb.delete(dot1Index + 3, sb.length());
            }
        }

        //删除前面的所有0
        while (sb.length() > 0 && sb.charAt(0) == '0') {
            sb.deleteCharAt(0);
        }

        //如果最开始是dot或者长度是0,则添加0
        if (sb.length() == 0 || sb.charAt(0) == '.') {
            sb.insert(0, '0');
        }

        return sb.toString();
    }

    public static Drawable createColorBackground(int color) {
        ColorDrawable normal = new ColorDrawable(color);
        ColorDrawable select = new ColorDrawable(ColorUtils.blendARGB(color, 0x77999999, 0.3f));
        ColorDrawable disable = new ColorDrawable(ColorUtils.blendARGB(color, 0x77999999, 0.6f));
        StateListDrawable drawable = new StateListDrawable();
        drawable.addState(new int[]{-android.R.attr.state_enabled}, disable);//  状态  , 设置按下的图片
        drawable.addState(new int[]{android.R.attr.state_pressed}, select);//  状态  , 设置按下的图片
        drawable.addState(new int[]{}, normal);//默认状态,默认状态下的图片
        return drawable;
    }


    public static void beepPromt() {
    }

    public static Drawable getAppLauncherIcon(Context context) {
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
            return pi.applicationInfo.loadIcon(pm);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static int getAppLauncherIconId(Context context) {
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
            return pi.applicationInfo.icon;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static String getAppVersionName(Context context) {
        String versionName = "";
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
            versionName = pi.versionName;
            if (TextUtils.isEmpty(versionName)) {
                return "";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return versionName;
    }

    public static String getApplicationName(Context app) {
        PackageManager packageManager = null;
        ApplicationInfo applicationInfo = app.getApplicationInfo();
        return app.getString(applicationInfo.labelRes);
    }




//    public static void initFromProperties(Context context) {
//        File file = new File(context.getFilesDir(), "config.ini");
//        boolean fileExists = file.exists();
//        if (!fileExists) {
//            return;
//        }
//        App.paramters().initFromProperties(file);
//        file.delete();
//    }


    public static <T extends Fragment> T addActionFragment(FragmentManager fragmentManager, Class<T> clazz) {
        String tag = clazz.getName();
        T fragment = (T) fragmentManager.findFragmentByTag(tag);
        if (fragment == null) {
            try {
                fragment = clazz.newInstance();
                fragmentManager.beginTransaction().add(fragment, tag).commit();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return fragment;
    }

    public static boolean isAlipay(String code) {
        return code.startsWith("28");
    }

    //去除空格
    public static byte[] sHexToBytes(String hex) {
        hex  = hex.replaceAll("\\s*", "");
        return hexToBytes(hex);
    }

    public static byte[] hexToBytes(String hex) {
        byte[] raw = new byte[hex.length() / 2];
        for (int src = 0, dst = 0; dst < raw.length; ++dst) {
            int hi = Character.digit(hex.charAt(src++), 16);
            int lo = Character.digit(hex.charAt(src++), 16);
            if ((hi < 0) || (lo < 0))
                throw new IllegalArgumentException();
            raw[dst] = (byte) (hi << 4 | lo);
        }
        return raw;
    }

    public static String bytesToHEX(byte[] b, int offset, int len) {
        if (b == null)
            return "";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < len; i++) {
            byteToHex(sb, b[i+offset]);
        }
        return sb.toString();
    }

    public static String bytesToHEX(byte[] b) {
        if (b == null)
            return "";
        return bytesToHEX(b, 0, b.length);
    }

    private static void byteToHex(StringBuilder sb, byte b) {
        int n = b;
        if (n < 0)
            n = 256 + n;
        int d1 = n / 16;
        int d2 = n % 16;
        sb.append(hexDigits.charAt(d1)).append(hexDigits.charAt(d2));
    }

    public static String byteToHex(byte b) {
        StringBuilder sb = new StringBuilder();
        byteToHex(sb, b);
        return sb.toString();
    }

    private final static String hexDigits = "0123456789ABCDEF";

    public static String fen2yuan(long a) {
        String prefix = "";
        if (a < 0) {
            prefix = "-";
            a = -a;
        }
        return prefix + String.format("%d.%02d", a/100, a%100);
    }



    @SuppressWarnings("unchecked")
    public static Class<?> getSuperClassGenricType(Class clazz, final int index) {

        Type genType = null;
        while (true) {
            if (clazz.getName().equals(Object.class.getName())) {
                return null;
            }

            //返回表示此 Class 所表示的实体（类、接口、基本类型或 void）的直接超类的 Type。
            genType = clazz.getGenericSuperclass();
            if (!(genType instanceof ParameterizedType)) {
                clazz = clazz.getSuperclass();
            } else {
                break;
            }
        }


        //返回表示此类型实际类型参数的 Type 对象的数组。
        Type[] params = ((ParameterizedType) genType).getActualTypeArguments();

        if (index >= params.length || index < 0) {
            return Object.class;
        }
        if (!(params[index] instanceof Class)) {
            return Object.class;
        }

        return (Class) params[index];
    }



    //获取所有成员变量 包括父类
    public static Field[] getAllFields(Class clazz, boolean setAccess) {
        List<Field> list = new ArrayList<>();
        getAllFields(list, clazz);
        if (setAccess) {
            for (Field f : list) {
                f.setAccessible(true);
            }
        }
        return list.toArray(new Field[]{});
    }

    public static void setFieldValue(Object entity, Field f, String value) throws IllegalAccessException {
        Class type = f.getType();
        if (type == String.class) {
            f.set(entity, value);
        } else if (type == java.util.Date.class || type == java.sql.Date.class) {
//                f.set(entity, rs.getTimestamp(index));
        } else if (type == int.class || type == Integer.class) {
            f.setInt(entity, Integer.parseInt(value));
        } else if (type == double.class || type == Double.class) {
            f.setDouble(entity, Double.parseDouble(value));
        } else if (type == long.class || type == Long.class) {
            f.setLong(entity, Long.parseLong(value));
        } else if (type == boolean.class || type == Boolean.class) {
            f.setBoolean(entity, Boolean.parseBoolean(value));
        } else {
            throw new RuntimeException("不支持这种格式 " + type.getName());
        }
    }

    private static void getAllFields(List<Field> list, Class clazz) {
        for (Field field : clazz.getDeclaredFields()) {
            if (!Modifier.isStatic(field.getModifiers())) {
                list.add(field);
            }
        }
        if (clazz.getSuperclass() != Object.class) {
            getAllFields(list, clazz.getSuperclass());
        }
    }

    public static long yuan2fen(String trans_amount) {
        if (TextUtils.isEmpty(trans_amount)) {
            return 0;
        }
        int dotIndex = trans_amount.indexOf('.');
        if (dotIndex == -1) {//没有点
            return Long.parseLong(trans_amount)*100l;
        }
        StringBuilder sb = new StringBuilder(trans_amount);
        int len = dotIndex + 3;
        while (sb.length() < len) {
            sb.append('0');
        }
        sb.delete(len, sb.length());
        sb.deleteCharAt(dotIndex);
        return Long.parseLong(sb.toString());
    }

    public static int listSize(List list) {
        return list == null ? 0 : list.size();
    }

    public static byte[] randomBytes(int len) {
        byte[] buf = new byte[len];
        for (int i=0;i<buf.length;i++) {
            buf[i] = (byte) (Math.random()*0xFF);
        }
        return buf;
    }

    static final String num = "1234567890";
    static final String alfnum = "1234567890abcdefghigklmnopqrstuvwxyzABCDEFGHIGKLMNOPQRSTUVWXYZ";
    public static String randomStr(int len) {
        Random random = new Random();
        StringBuffer buffer = new StringBuffer();
        int alfnumLen = alfnum.length();
        for (int i = 0; i < len; i++) {
            buffer.append(alfnum.charAt(random.nextInt(alfnumLen)));
        }
        return buffer.toString();
    }

    public static String randomNum(int len) {
        Random random = new Random();
        StringBuffer buffer = new StringBuffer();
        int alfnumLen = num.length();
        for (int i = 0; i < len; i++) {
            buffer.append(num.charAt(random.nextInt(alfnumLen)));
        }
        return buffer.toString();
    }

    public static Activity getActivityFromView(View view) {
        if (null != view) {
            Context context = view.getContext();
            while (context instanceof ContextWrapper) {
                if (context instanceof Activity) {
                    return (Activity) context;
                }
                context = ((ContextWrapper) context).getBaseContext();
            }
        }
        return null;
    }


    public static Activity getActivityFromContext(Context context) {
        while (context instanceof ContextWrapper) {
            if (context instanceof Activity) {
                return (Activity) context;
            }
            context = ((ContextWrapper) context).getBaseContext();
        }
        return null;
    }

    public static String getSignature(Context context)
    {
        try {
            /** 通过包管理器获得指定包名包含签名的包信息 **/
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_SIGNATURES);
            /******* 通过返回的包信息获得签名数组 *******/
            Signature[] signatures = packageInfo.signatures;
            /******* 循环遍历签名数组拼接应用签名 *******/
            return signatures[0].toCharsString();
            /************** 得到应用签名 **************/
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean isNumber(String o) {
        if (TextUtils.isEmpty(o)) {
            return false;
        }
        for (char c : o.toCharArray()) {
            if (c >= '0' && c <= '9') {
                continue;
            } else {
                return false;
            }
        }
        return true;
    }

    public static float sum(float[] arr) {
        float s = 0;
        for(int i=0;i<arr.length; i++) {
            s += arr[i];
        }
        return s;
    }
}
