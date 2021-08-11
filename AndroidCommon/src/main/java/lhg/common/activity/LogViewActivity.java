package lhg.common.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;

import java.net.MalformedURLException;
import java.net.URL;


public class LogViewActivity extends WebViewActivity {

    Uploader uploader;
    public static void startActivity(String title, String url, String UploaderClass, Context context) {
        Intent intent = new Intent(context, LogViewActivity.class);
        intent.putExtra(IntentKey_Title, title);
        intent.putExtra(IntentKey_URL, url);
        intent.putExtra("UploaderClass", UploaderClass);
        context.startActivity(intent);
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        String UploaderClass = intent.getStringExtra("UploaderClass");
        if (!TextUtils.isEmpty(UploaderClass)) {
            try {
                uploader = (Uploader) Class.forName(UploaderClass).newInstance();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (uploader != null) {
            menu.add("上传日志").setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getTitle().equals("上传日志")) {
            try {
                onUploadLog(new URL(url).getFile());
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void onUploadLog(String file) {
        uploader.update(this, file);
    }

    public interface Uploader {
        void update(Activity activity, String file);
    }
}