package com.gu3sswe4k.app.activities;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.gu3sswe4k.app.R;

/**
 * VULNERABILITY LAB — AdminPanelActivity
 *
 * Vulnerabilities present (intentional):
 *  [VULN-AP-01] Exported with no permission check — any app or adb can launch this
 *  [VULN-AP-02] Displays sensitive info (hardcoded API key, internal paths)
 *  [VULN-AP-03] No authentication check before showing admin UI
 *  [VULN-AP-04] "Fixed" auth gate trusts a caller-supplied Intent extra
 *               (is_authenticated) instead of verifying anything server-side
 *               or against a real session/token. Any caller can set it.
 *
 * Attack vector (AP-01, unauthenticated):
 *   adb shell am start -n com.gu3sswe4k.app/.activities.AdminPanelActivity
 *
 * Attack vector (AP-04, bypassing the "auth check"):
 *   adb shell am start -n com.gu3sswe4k.app/.activities.AdminPanelActivity \
 *       --ez is_authenticated true
 */
public class AdminPanelActivity extends AppCompatActivity {

    // VULN-AP-02: Hardcoded secrets in source
    private static final String ADMIN_API_KEY    = "sk-vuln-hardcoded-key-12345";
    private static final String INTERNAL_API_URL = "http://192.168.1.100:8080/admin/api";
    private static final String DB_PASSWORD      = "admin@vulndroid2024";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        com.gu3sswe4k.app.FlagManager.capture(this, com.gu3sswe4k.app.FlagManager.FLAG_AP_01);

        // VULN-AP-04: This LOOKS like an auth check, but "is_authenticated"
        // is a plain Intent extra — fully attacker-controlled. No token,
        // no signature, no server round-trip. Setting it via adb or from
        // any app on the device satisfies this "check".
        boolean claimedAuthenticated = getIntent().getBooleanExtra("is_authenticated", false);
        if (claimedAuthenticated) {
            com.gu3sswe4k.app.FlagManager.capture(this, com.gu3sswe4k.app.FlagManager.FLAG_AP_04);
        }

        // VULN-AP-03: No isAdmin() check — anyone who reaches this screen gets full access
        TextView info = findViewById(R.id.admin_info);
        info.setText(
            "=== ADMIN PANEL ===\n\n" +
            "API Key: " + ADMIN_API_KEY + "\n" +
            "Internal API: " + INTERNAL_API_URL + "\n" +
            "DB Password: " + DB_PASSWORD + "\n" +
            "App data dir: " + getApplicationInfo().dataDir + "\n\n" +
            "Auth extra claimed: " + claimedAuthenticated
        );
    }
}
