package com.vulndroid.app.activities;

import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.vulndroid.app.FlagManager;
import com.vulndroid.app.R;

public class FlagSubmitActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flag_submit);

        EditText etFlag   = findViewById(R.id.et_flag_input);
        Button btnSubmit  = findViewById(R.id.btn_submit_flag);
        TextView tvResult = findViewById(R.id.tv_flag_result);

        String autoFlag = getIntent().getStringExtra("flag");
        if (autoFlag != null) etFlag.setText(autoFlag);

        btnSubmit.setOnClickListener(v -> {
            String input = etFlag.getText().toString().trim();
            boolean valid = false;

            for (String flag : FlagManager.ALL_FLAGS) {
                if (flag.equals(input)) {
                    boolean alreadyCaptured = FlagManager.isCaptured(this, flag);
                    FlagManager.capture(this, flag);
                    valid = true;

                    int captured = FlagManager.count(this);
                    int total = FlagManager.ALL_FLAGS.length;

                    if (captured == total) {
                        // ALL FLAGS — MASTER CONGRATS!
                        tvResult.setText(
                            "🎉🥂 CONGRATULATIONS! 🥂🎉\n\n" +
                            "YOU OWNED THE WHOLE APP!\n\n" +
                            "ALL " + total + "/" + total + " FLAGS CAPTURED!\n\n" +
                            "MASTER FLAG:\n" + FlagManager.FLAG_MASTER + "\n\n" +
                            "You are a true Android security researcher! 🕷️"
                        );
                        tvResult.setTextColor(0xFFFFD700);
                        tvResult.setBackgroundColor(0xFF2A1F00);
                    } else if (alreadyCaptured) {
                        tvResult.setText("Already captured this flag!\n\n" + captured + "/" + total + " flags owned.");
                        tvResult.setTextColor(0xFFFFFFFF);
                        tvResult.setBackgroundColor(0xFF111111);
                    } else {
                        tvResult.setText(
                            "🎉 FLAG ACCEPTED! 🎉\n\n" +
                            flag + "\n\n" +
                            captured + " / " + total + " flags captured!\n\n" +
                            (captured == total - 1 ? "One more to go! 🔥" : "Keep hacking! 🕷️")
                        );
                        tvResult.setTextColor(0xFF30D158);
                        tvResult.setBackgroundColor(0xFF0D2318);
                    }
                    break;
                }
            }

            if (!valid) {
                tvResult.setText("Invalid flag. Keep hacking! 🕷️\n\nMake sure you copy the full flag\nincluding Gu3ssWeak{...}");
                tvResult.setTextColor(0xFFFF3B30);
                tvResult.setBackgroundColor(0xFF200A0A);
            }

            tvResult.setPadding(24, 16, 24, 16);
            tvResult.setVisibility(android.view.View.VISIBLE);
        });

        findViewById(R.id.btn_view_board).setOnClickListener(v ->
            startActivity(new android.content.Intent(this, FlagBoardActivity.class)));
    }
}
