package lhg.common.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.text.TextUtils;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import lhg.common.R;


public class WebViewActivity extends BaseActivity {

    public static final String IntentKey_Title = "IntentKey_Title";
    public static final String IntentKey_URL = "IntentKey_URL";
    public static final String IntentKey_Content = "IntentKey_Content";
    WebView webView;
    Toolbar toolbar;
    protected String url;

    public static void startActivity(String title, String url, Context context) {
        Intent intent = new Intent(context, WebViewActivity.class);
        intent.putExtra(IntentKey_Title, title);
        intent.putExtra(IntentKey_URL, url);
        context.startActivity(intent);
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pcm_activity_webview);
        setSupportActionBar(toolbar = findViewById(R.id.toolbar));
        showPrevArrowOnActionBar();

        webView = (WebView) findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDefaultFontSize(15);
        webView.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url) { //  重写此方法表明点击网页里面的链接还是在当前的webview里跳转，不跳到浏览器那边
                view.loadUrl(url);
                return true;
            }
        });


        Intent intent = getIntent();
        if (intent == null) {
            finish();
            return;
        }
        onNewIntent(intent);
    }

    @Override
    public void onNewIntent(Intent intent) {
        String title = intent.getStringExtra(IntentKey_Title);
        setTitle(title);
        if (toolbar != null) {
            toolbar.setTitle(title);
        }
        url = intent.getStringExtra(IntentKey_URL);
        String content = intent.getStringExtra(IntentKey_Content);
        if (!TextUtils.isEmpty(url)) {
            webView.loadUrl(url);
        } else if (!TextUtils.isEmpty(content)) {
            webView.loadData(content, "text/plaint", "utf-8");
        } else {
            webView.loadData("Empty", null, null);
        }
    }

    /**
     * Called when the fragment is no longer resumed. Pauses the WebView.
     */
    @Override
    public void onResume() {
        webView.onResume();
        super.onResume();
    }

    /**
     * Called when the fragment is no longer in use. Destroys the internal state of the WebView.
     */
    @Override
    public void onDestroy() {
        if (webView != null) {
            webView.destroy();
            webView = null;
        }
        super.onDestroy();
    }

}