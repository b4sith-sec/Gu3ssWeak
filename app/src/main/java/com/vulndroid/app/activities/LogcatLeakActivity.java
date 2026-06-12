package com.vulndroid.app.activities;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.vulndroid.app.FlagManager;
import com.vulndroid.app.R;

/**
 * VULN-BR-03: Sensitive data in logcat
 *
 * As the user types into the username/password fields, every
 * keystroke is logged via Log.d. Any app with READ_LOGS, or
 * `adb logcat`, can capture credentials in plaintext as they
 * are typed.
 *
 * Exploit:
 *   adb logcat | grep Gu3ssWeak_INPUT
 */
public class LogcatLeakActivity extends AppCompatActivity {

    private static final String TAG = "Gu3ssWeak_INPUT";
    private boolean flagCaptured = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logcat_leak);

        EditText etUser = findViewById(R.id.et_log_username);
        EditText etPass = findViewById(R.id.et_log_password);
        TextView tvHint = findViewById(R.id.tv_log_hint);
        Button btnCheck = findViewById(R.id.btn_check_logcat);

        TextWatcher userWatcher = new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int a, int b, int c) {}
            @Override public void onTextChanged(CharSequence s, int a, int b, int c) {
                Log.d(TAG, "username field changed: " + s.toString());
                maybeCapture();
            }
            @Override public void afterTextChanged(Editable s) {}
        };

        TextWatcher passWatcher = new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int a, int b, int c) {}
            @Override public void onTextChanged(CharSequence s, int a, int b, int c) {
                // VULN: password value logged in plaintext on every keystroke
                Log.d(TAG, "password field changed: " + s.toString());
                maybeCapture();
            }
            @Override public void afterTextChanged(Editable s) {}
        };

        etUser.addTextChangedListener(userWatcher);
        etPass.addTextChangedListener(passWatcher);

        btnCheck.setOnClickListener(v -> {
            tvHint.setText(
                "Run this in your terminal:\n\n" +
                "adb logcat | grep " + TAG + "\n\n" +
                "Every keystroke you typed above\nis visible in plaintext."
            );
            tvHint.setVisibility(android.view.View.VISIBLE);
        });
    }

    private void maybeCapture() {
        if (!flagCaptured) {
            flagCaptured = true;
            FlagManager.capture(this, FlagManager.FLAG_BR_03);
        }
    }
}
