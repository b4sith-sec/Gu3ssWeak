package com.gu3sswe4k.app.activities;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.gu3sswe4k.app.FlagManager;
import com.gu3sswe4k.app.R;

/**
 * VULN-BR-03: Sensitive data in logcat (payment form)
 *
 * Card number, expiry, and CVV are logged in plaintext via Log.d
 * as the user types, and again on "Pay Now". Any app with READ_LOGS,
 * or `adb logcat`, can capture full card details.
 *
 * Exploit:
 *   adb logcat | grep Gu3ssWeak_PAYMENT
 */
public class LogcatLeakActivity extends AppCompatActivity {

    private static final String TAG = "Gu3ssWeak_PAYMENT";
    private boolean flagCaptured = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logcat_leak);

        EditText etCard = findViewById(R.id.et_card_number);
        EditText etExpiry = findViewById(R.id.et_card_expiry);
        EditText etCvv = findViewById(R.id.et_card_cvv);
        Button btnPay = findViewById(R.id.btn_pay);
        TextView tvHint = findViewById(R.id.tv_log_hint);

        TextWatcher leakWatcher = new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int a, int b, int c) {}
            @Override public void onTextChanged(CharSequence s, int a, int b, int c) {
                // VULN: card data logged in plaintext on every keystroke
                Log.d(TAG, "Card number field: " + etCard.getText().toString());
                Log.d(TAG, "Expiry field: " + etExpiry.getText().toString());
                Log.d(TAG, "CVV field: " + etCvv.getText().toString());
                maybeCapture();
            }
            @Override public void afterTextChanged(Editable s) {}
        };

        etCard.addTextChangedListener(leakWatcher);
        etExpiry.addTextChangedListener(leakWatcher);
        etCvv.addTextChangedListener(leakWatcher);

        btnPay.setOnClickListener(v -> {
            // VULN: full payment payload logged on submit
            Log.d(TAG, "=== PAYMENT SUBMITTED ===");
            Log.d(TAG, "Card: " + etCard.getText().toString());
            Log.d(TAG, "Expiry: " + etExpiry.getText().toString());
            Log.d(TAG, "CVV: " + etCvv.getText().toString());
            Log.d(TAG, "=========================");

            maybeCapture();

            tvHint.setText(
                "Payment submitted!\n\n" +
                "Now run this in your terminal:\n\n" +
                "adb logcat | grep " + TAG + "\n\n" +
                "Your full card number, expiry, and CVV\n" +
                "are visible in plaintext logcat output."
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
