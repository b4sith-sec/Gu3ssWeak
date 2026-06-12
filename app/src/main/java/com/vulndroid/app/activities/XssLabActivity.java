package com.vulndroid.app.activities;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import com.vulndroid.app.FlagManager;
import com.vulndroid.app.R;

/**
 * VULN-XSS-01: Reflected XSS with naive blocklist filter
 *
 * User input is reflected directly into HTML loaded in a WebView.
 * A naive filter strips the literal string "<script>" (case-sensitive,
 * single pass) but does not account for:
 *   - case variation: <ScRiPt>
 *   - non-script vectors: <img onerror=...>, <svg onload=...>
 *   - HTML entity / URL encoding
 *
 * Exploit payloads that bypass the filter:
 *   <img src=x onerror=alert(document.title)>
 *   <svg onload=alert(1)>
 *   <ScRiPt>alert(1)</ScRiPt>
 */
public class XssLabActivity extends AppCompatActivity {

    @SuppressLint({"SetJavaScriptEnabled", "AddJavascriptInterface"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_xss_lab);

        EditText etInput = findViewById(R.id.et_xss_input);
        Button btnRender = findViewById(R.id.btn_xss_render);
        WebView webView = findViewById(R.id.webview_xss);

        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient());

        // Flag-checking bridge - called by the payload if it executes
        webView.addJavascriptInterface(new Object() {
            @android.webkit.JavascriptInterface
            public void onXssTriggered() {
                FlagManager.capture(XssLabActivity.this, FlagManager.FLAG_XSS_01);
            }
        }, "XssCheck");

        btnRender.setOnClickListener(v -> {
            String input = etInput.getText().toString();

            // VULN-XSS-01: naive, case-sensitive, single-pass blocklist
            String filtered = input.replace("<script>", "").replace("</script>", "");

            String html = "<html><body>"
                + "<h3>Search results for:</h3>"
                + "<div id='out'>" + filtered + "</div>"
                + "<script>"
                + "  var out = document.getElementById('out');"
                + "  out.addEventListener('error', function(){ XssCheck.onXssTriggered(); }, true);"
                + "</script>"
                + "</body></html>";

            webView.loadDataWithBaseURL(null, html, "text/html", "UTF-8", null);
        });
    }
}
