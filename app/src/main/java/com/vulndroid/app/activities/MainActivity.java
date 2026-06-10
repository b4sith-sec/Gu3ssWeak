package com.vulndroid.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import com.vulndroid.app.R;

/**
 * Hub activity — links to all vulnerable labs.
 * Not itself vulnerable; used for navigation during manual testing.
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Seed a fake token so WebView bridge demo works
        getSharedPreferences("user_prefs", MODE_PRIVATE)
            .edit()
            .putString("auth_token", "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.VICTIM_TOKEN")
            .putString("username", "victim_user")
            .apply();

        Button btnWebView   = findViewById(R.id.btn_webview);
        Button btnDeeplink  = findViewById(R.id.btn_deeplink);
        Button btnAdmin     = findViewById(R.id.btn_admin);

        btnWebView.setOnClickListener(v ->
            startActivity(new Intent(this, WebViewActivity.class)));

        btnDeeplink.setOnClickListener(v ->
            startActivity(new Intent(this, DeeplinkActivity.class)));

        btnAdmin.setOnClickListener(v ->
            startActivity(new Intent(this, AdminPanelActivity.class)));
    }
}
