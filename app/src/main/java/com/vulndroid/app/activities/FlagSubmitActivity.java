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
                    FlagManager.capture(this, flag);
                    valid = true;
                    tvResult.setText("FLAG ACCEPTED!\n\n" + flag);
                    tvResult.setTextColor(0xFF30D158);
                    tvResult.setBackgroundColor(0xFF0D2318);
                    break;
                }
            }
            if (!valid) {
                tvResult.setText("Invalid flag. Keep hacking!");
                tvResult.setTextColor(0xFFFF3B30);
                tvResult.setBackgroundColor(0xFF200A0A);
            }
            tvResult.setPadding(24, 16, 24, 16);
            tvResult.setVisibility(android.view.View.VISIBLE);
            if (FlagManager.count(this) == FlagManager.ALL_FLAGS.length) {
                tvResult.setText("ALL FLAGS CAPTURED!\n\nMASTER FLAG:\n" + FlagManager.FLAG_MASTER);
                tvResult.setTextColor(0xFFFFD700);
                tvResult.setBackgroundColor(0xFF2A1F00);
            }
        });

        findViewById(R.id.btn_view_board).setOnClickListener(v ->
            startActivity(new android.content.Intent(this, com.vulndroid.app.activities.FlagBoardActivity.class)));
    }
}
