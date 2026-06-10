package com.vulndroid.app.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * VULNERABILITY LAB — TokenReceiver
 *
 * Vulnerabilities present (intentional):
 *  [VULN-BR-01] Exported receiver with no permission — any app can send broadcasts
 *  [VULN-BR-02] Token from broadcast written to SharedPreferences without validation
 *  [VULN-BR-03] Sensitive data logged via Log.d (readable with adb logcat)
 *
 * Attack vectors:
 *   # Inject a fake auth token
 *   adb shell am broadcast -a com.vulndroid.app.SEND_TOKEN \
 *       --es "token" "ATTACKER_TOKEN" --es "user" "admin"
 *
 *   # Read logs to find intercepted tokens
 *   adb logcat | grep "VulnDroid"
 */
public class TokenReceiver extends BroadcastReceiver {

    private static final String TAG = "VulnDroid";

    @Override
    public void onReceive(Context context, Intent intent) {
        String token = intent.getStringExtra("token");
        String user  = intent.getStringExtra("user");

        // VULN-BR-03: Logging sensitive data
        Log.d(TAG, "Received token for user: " + user + " | token: " + token);

        if (token != null) {
            // VULN-BR-02: Unauthenticated token written to prefs
            context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
                .edit()
                .putString("auth_token", token)
                .putString("username", user)
                .apply();
        }
    }
}
