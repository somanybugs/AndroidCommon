package lhg.common.utils;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

public class ToastUtil
{

	static Handler sUiHandler;
	static Toast sToast;

	private static void show(final Context context, final CharSequence text, int time) {
		if (context == null) {
			return;
		}
		if (Thread.currentThread() != Looper.getMainLooper().getThread()) {
			if (sUiHandler == null) {
				synchronized (ToastUtil.class) {
					if (sUiHandler == null) {
						sUiHandler = new Handler(Looper.getMainLooper());
					}
				}
			}
			sUiHandler.post(() -> showInMainThread(context, text));
		} else {
			showInMainThread(context, text);
		}

	}

	private static void showInMainThread(final Context context, final CharSequence text) {
		if (context == null) {
			return;
		}
		if (sToast == null)
		{
			sToast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
		}
		sToast.setText(text);
		sToast.show();
		Log.i("TOASTT", String.valueOf(text));
	}

	public static void show(final Context context, final int textId) {
		if (context == null) {
			return;
		}
		show(context, context.getString(textId), Toast.LENGTH_SHORT);
	}
	public static void showLong(final Context context, final int textId) {
		if (context == null) {
			return;
		}
		show(context, context.getString(textId), Toast.LENGTH_LONG);
	}
	public static void show(final Context context, final CharSequence text) {
		show(context, text, Toast.LENGTH_SHORT);
	}
	public static void showLong(final Context context, final CharSequence text) {
		show(context, text, Toast.LENGTH_LONG);
	}
}
