package com.gu3sswe4k.app.activities;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.gu3sswe4k.app.FlagManager;
import com.gu3sswe4k.app.R;
import java.util.Random;

/**
 * VULN-OTP-01: No rate limiting on OTP verification
 *
 * The OTP is a 4-digit code (0000-9999). The verify endpoint has
 * NO rate limiting, NO lockout after failed attempts, and NO
 * exponential backoff. An attacker can brute-force all 10000
 * combinations in seconds via automated requests.
 *
 * The correct OTP is also logged to logcat for "debugging" -
 * compounding the issue.
 *
 * Exploit:
 *   adb logcat | grep Gu3ssWeak_OTP
 *   (then brute-force or read the logged OTP directly)
 */
public class OtpBruteForceActivity extends AppCompatActivity {

    private static final String TAG = "Gu3ssWeak_OTP";
    private String correctOtp;
    private int attemptCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp_bruteforce);

        // Generate "random" 4-digit OTP
        correctOtp = String.format("%04d", new Random().nextInt(10000));

        // VULN: OTP logged for "debugging" - no production check removes this
        Log.d(TAG, "Generated OTP for session: " + correctOtp);

        EditText etOtp = findViewById(R.id.et_otp_input);
        Button btnVerify = findViewById(R.id.btn_verify_otp);
        TextView tvResult = findViewById(R.id.tv_otp_result);
        TextView tvAttempts = findViewById(R.id.tv_attempt_count);

        btnVerify.setOnClickListener(v -> {
            attemptCount++;
            tvAttempts.setText("Attempts: " + attemptCount + " (no limit, no lockout)");

            String input = etOtp.getText().toString().trim();

            // VULN: No rate limiting, no lockout, no backoff - unlimited attempts
            if (correctOtp.equals(input)) {
                FlagManager.capture(this, FlagManager.FLAG_OTP_01);
                tvResult.setText(
                    "OTP VERIFIED!\n\n" +
                    "Correct after " + attemptCount + " attempt(s)\n" +
                    "Max possible: 10000 (0000-9999)\n\n" +
                    "FLAG: " + FlagManager.FLAG_OTP_01
                );
                tvResult.setTextColor(0xFF30D158);
                tvResult.setBackgroundColor(0xFF0D2318);
            } else {
                tvResult.setText(
                    "Invalid OTP. Try again.\n" +
                    "(no rate limit applied - keep guessing)"
                );
                tvResult.setTextColor(0xFFFF3B30);
                tvResult.setBackgroundColor(0xFF200A0A);
            }
            tvResult.setVisibility(android.view.View.VISIBLE);
        });
    }
}
