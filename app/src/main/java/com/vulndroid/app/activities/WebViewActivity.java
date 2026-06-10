package com.vulndroid.app.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import androidx.appcompat.app.AppCompatActivity;
import com.vulndroid.app.R;

/**
 * VULNERABILITY LAB — WebViewActivity
 *
 * Vulnerabilities present (intentional):
 *  [VULN-WV-01] JavaScript enabled with no origin validation
 *  [VULN-WV-02] addJavascriptInterface exposes sensitive Java bridge
 *  [VULN-WV-03] Universal file access enabled (loadFileAccessFromFileURLs)
 *  [VULN-WV-04] URL loaded directly from Intent extra — no allowlist
 *  [VULN-WV-05] Arbitrary deeplink can load file:// or javascript: URLs
 *
 * Attack vectors:
 *   adb shell am start -n com.vulndroid.app/.activities.WebViewActivity \
 *       --es "url" "javascript:VulnBridge.stealToken()"
 *
 *   adb shell am start -n com.vulndroid.app/.activities.WebViewActivity \
 *       --es "url" "file:///data/data/com.vulndroid.app/shared_prefs/user.xml"
 */
public class WebViewActivity extends AppCompatActivity {

    @SuppressLint({"SetJavaScriptEnabled", "AddJavascriptInterface"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);

        WebView webView = findViewById(R.id.webview);
        WebSettings settings = webView.getSettings();

        // VULN-WV-01: JavaScript enabled globally
        settings.setJavaScriptEnabled(true);

        // VULN-WV-03: File access from file:// URLs allowed
        settings.setAllowFileAccessFromFileURLs(true);
        settings.setAllowUniversalAccessFromFileURLs(true);

        // VULN-WV-02: Exposes Java object to JavaScript — attackers can call stealToken()
        webView.addJavascriptInterface(new JavaScriptBridge(this), "VulnBridge");

        webView.setWebViewClient(new WebViewClient());

        // VULN-WV-04: URL taken directly from intent with no validation
        String url = getUrlFromIntent();
        if (url != null) {
            webView.loadUrl(url);
        } else {
            webView.loadUrl("https://example.com");
        }
    }

    private String getUrlFromIntent() {
        Intent intent = getIntent();
        // From explicit intent extra
        if (intent.hasExtra("url")) {
            return intent.getStringExtra("url");
        }
        // VULN-WV-05: Deeplink URI passed directly — vulndroid://open?url=file:///...
        Uri data = intent.getData();
        if (data != null) {
            return data.getQueryParameter("url");
        }
        return null;
    }

    /** JavaScript bridge — exposes sensitive operations to any loaded page */
    public static class JavaScriptBridge {
        private final WebViewActivity activity;

        JavaScriptBridge(WebViewActivity activity) {
            this.activity = activity;
        }

        // VULN: Any JS on the loaded page can call this
        @android.webkit.JavascriptInterface
        public String stealToken() {
            android.content.SharedPreferences prefs =
                activity.getSharedPreferences("user_prefs", android.content.Context.MODE_PRIVATE);
            return prefs.getString("auth_token", "no_token_found");
        }

        @android.webkit.JavascriptInterface
        public String getDeviceInfo() {
            return android.os.Build.MODEL + " | " + android.os.Build.VERSION.RELEASE;
        }
    }
}
