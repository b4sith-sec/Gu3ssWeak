package com.vulndroid.app.activities;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.vulndroid.app.R;

/**
 * VULNERABILITY LAB — AdminPanelActivity
 *
 * Vulnerabilities present (intentional):
 *  [VULN-AP-01] Exported with no permission check — any app or adb can launch this
 *  [VULN-AP-02] Displays sensitive info (hardcoded API key, internal paths)
 *  [VULN-AP-03] No authentication check before showing admin UI
 *
 * Attack vector:
 *   adb shell am start -n com.vulndroid.app/.activities.AdminPanelActivity
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

        // VULN-AP-03: No isAdmin() check — anyone who reaches this screen gets full access
        TextView info = findViewById(R.id.admin_info);
        info.setText(
            "=== ADMIN PANEL ===\n\n" +
            "API Key: " + ADMIN_API_KEY + "\n" +
            "Internal API: " + INTERNAL_API_URL + "\n" +
            "DB Password: " + DB_PASSWORD + "\n" +
            "App data dir: " + getApplicationInfo().dataDir
        );
    }
}
