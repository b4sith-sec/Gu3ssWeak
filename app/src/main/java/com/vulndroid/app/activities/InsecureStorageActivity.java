package com.vulndroid.app.activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.vulndroid.app.FlagManager;
import com.vulndroid.app.R;

/**
 * VULN-STORE-01: Sensitive data stored in plaintext SharedPreferences
 *
 * "Remember me" credentials are written directly to SharedPreferences
 * in plaintext, with no encryption (EncryptedSharedPreferences /
 * Android Keystore not used). Any app with root, or anyone with
 * adb backup/run-as access, can read:
 *
 *   /data/data/com.vulndroid.app/shared_prefs/login_prefs.xml
 *
 * Exploit:
 *   adb shell run-as com.vulndroid.app cat \
 *     /data/data/com.vulndroid.app/shared_prefs/login_prefs.xml
 */
public class InsecureStorageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insecure_storage);

        EditText etUser = findViewById(R.id.et_store_username);
        EditText etPass = findViewById(R.id.et_store_password);
        Button btnSave = findViewById(R.id.btn_store_save);
        TextView tvResult = findViewById(R.id.tv_store_result);

        // Show what's currently stored, if anything
        android.content.SharedPreferences prefs = getSharedPreferences("login_prefs", MODE_PRIVATE);
        String savedUser = prefs.getString("saved_username", null);
        String savedPass = prefs.getString("saved_password", null);
        if (savedUser != null) {
            tvResult.setText(
                "Currently stored (plaintext):\n\n" +
                "username = " + savedUser + "\n" +
                "password = " + savedPass + "\n\n" +
                "File: /data/data/com.vulndroid.app/shared_prefs/login_prefs.xml"
            );
            tvResult.setVisibility(android.view.View.VISIBLE);
        }

        btnSave.setOnClickListener(v -> {
            String user = etUser.getText().toString();
            String pass = etPass.getText().toString();

            // VULN-STORE-01: plaintext storage, no EncryptedSharedPreferences
            prefs.edit()
                .putString("saved_username", user)
                .putBoolean("remember_me", true)
                .putString("saved_password", pass)
                .apply();

            FlagManager.capture(this, FlagManager.FLAG_STORE_01);

            tvResult.setText(
                "Saved!\n\n" +
                "Stored in plaintext at:\n" +
                "/data/data/com.vulndroid.app/shared_prefs/login_prefs.xml\n\n" +
                "username = " + user + "\n" +
                "password = " + pass + "\n\n" +
                "FLAG: " + FlagManager.FLAG_STORE_01
            );
            tvResult.setTextColor(0xFF30D158);
            tvResult.setBackgroundColor(0xFF0D2318);
            tvResult.setVisibility(android.view.View.VISIBLE);
        });
    }
}
