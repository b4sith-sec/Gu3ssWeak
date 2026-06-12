package com.vulndroid.app.activities;

import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.vulndroid.app.FlagManager;

public class FlagBoardActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ScrollView scroll = new ScrollView(this);
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(32, 48, 32, 48);
        layout.setBackgroundColor(0xFF0D0D0D);

        TextView title = new TextView(this);
        title.setText("CTF Scoreboard");
        title.setTextSize(24);
        title.setTextColor(0xFFFF3B30);
        title.setTypeface(null, android.graphics.Typeface.BOLD);
        layout.addView(title);

        int captured = FlagManager.count(this);
        int total = FlagManager.ALL_FLAGS.length;
        TextView score = new TextView(this);
        score.setText(captured + " / " + total + " flags captured");
        score.setTextSize(14);
        score.setTextColor(0xFF888888);
        score.setPadding(0, 8, 0, 24);
        layout.addView(score);

        android.content.SharedPreferences prefs = getSharedPreferences("ctf_flags", MODE_PRIVATE);

        String[][] flagData = {
            {FlagManager.FLAG_WV_01,    "VULN-WV-01",    "JS enabled, no origin check",         "HIGH"},
            {FlagManager.FLAG_WV_02,    "VULN-WV-02",    "JavascriptInterface bridge exposed",   "CRITICAL"},
            {FlagManager.FLAG_WV_03,    "VULN-WV-03",    "Local file read via file://",          "HIGH"},
            {FlagManager.FLAG_WV_04,    "VULN-WV-04",    "Arbitrary URL from intent",            "HIGH"},
            {FlagManager.FLAG_DL_02,    "VULN-DL-02",    "Arbitrary activity via deeplink",      "CRITICAL"},
            {FlagManager.FLAG_DL_03,    "VULN-DL-03",    "Token overwrite via deeplink",         "CRITICAL"},
            {FlagManager.FLAG_DL_CHAIN, "VULN-DL-CHAIN", "Deeplink to WebView RCE chain",        "CRITICAL"},
            {FlagManager.FLAG_AP_01,    "VULN-AP-01",    "Exported admin panel, no permission",  "CRITICAL"},
            {FlagManager.FLAG_AP_02,    "VULN-AP-02",    "Hardcoded credentials in source",      "CRITICAL"},
            {FlagManager.FLAG_BR_02,    "VULN-BR-02",    "Token injection via broadcast",        "CRITICAL"},
            {FlagManager.FLAG_BR_03,    "VULN-BR-03",    "Sensitive data in logcat",             "MEDIUM"},
            {FlagManager.FLAG_SV_02A,   "VULN-SV-02a",   "Data wipe via exported service",       "CRITICAL"},
            {FlagManager.FLAG_SV_02B,   "VULN-SV-02b",   "Data exfil SSRF via service",          "CRITICAL"},
        };

        for (String[] entry : flagData) {
            boolean owned = prefs.getBoolean(entry[0], false);

            LinearLayout row = new LinearLayout(this);
            row.setOrientation(LinearLayout.VERTICAL);
            row.setBackgroundColor(owned ? 0xFF0D2318 : 0xFF1C1C1E);
            row.setPadding(20, 16, 20, 16);
            LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
            p.setMargins(0, 0, 0, 8);
            row.setLayoutParams(p);

            TextView idView = new TextView(this);
            idView.setText((owned ? "✅ " : "🔒 ") + entry[1] + "  [" + entry[3] + "]");
            idView.setTextColor(owned ? 0xFF30D158 : 0xFF888888);
            idView.setTextSize(13);
            idView.setTypeface(null, android.graphics.Typeface.BOLD);
            row.addView(idView);

            TextView descView = new TextView(this);
            descView.setText(entry[2]);
            descView.setTextColor(owned ? 0xFF1B8A4A : 0xFF555555);
            descView.setTextSize(12);
            descView.setPadding(0, 4, 0, 0);
            row.addView(descView);

            if (owned) {
                TextView flagView = new TextView(this);
                flagView.setText(entry[0]);
                flagView.setTextColor(0xFF30D158);
                flagView.setTextSize(11);
                flagView.setTypeface(android.graphics.Typeface.MONOSPACE);
                flagView.setPadding(0, 8, 0, 0);
                row.addView(flagView);
            }

            layout.addView(row);
        }

        if (FlagManager.count(this) == FlagManager.ALL_FLAGS.length) {
            TextView master = new TextView(this);
            master.setText("MASTER FLAG\n" + FlagManager.FLAG_MASTER);
            master.setTextColor(0xFFFFD700);
            master.setTextSize(13);
            master.setTypeface(android.graphics.Typeface.MONOSPACE);
            master.setBackgroundColor(0xFF2A1F00);
            master.setPadding(20, 16, 20, 16);
            LinearLayout.LayoutParams mp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
            mp.setMargins(0, 16, 0, 0);
            master.setLayoutParams(mp);
            layout.addView(master);
        }

        // Submit Flag button
        android.widget.Button btnSubmit = new android.widget.Button(this);
        btnSubmit.setText("Submit Flag");
        btnSubmit.setBackgroundColor(0xFFFF3B30);
        btnSubmit.setTextColor(0xFFFFFFFF);
        btnSubmit.setTextSize(15);
        LinearLayout.LayoutParams bp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 140);
        bp.setMargins(0, 24, 0, 0);
        btnSubmit.setLayoutParams(bp);
        btnSubmit.setOnClickListener(v -> startActivity(new android.content.Intent(this, FlagSubmitActivity.class)));
        layout.addView(btnSubmit);
        scroll.addView(layout);
        setContentView(scroll);
    }
}
