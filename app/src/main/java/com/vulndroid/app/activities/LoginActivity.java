package com.vulndroid.app.activities;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.vulndroid.app.FlagManager;
import com.vulndroid.app.R;

/**
 * VULN-SQL-01: SQL Injection in login
 * Bypass with: ' OR '1'='1
 * Or: admin' --
 */
public class LoginActivity extends AppCompatActivity {

    private SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Setup vulnerable DB
        db = openOrCreateDatabase("users.db", MODE_PRIVATE, null);
        db.execSQL("CREATE TABLE IF NOT EXISTS users (id INTEGER PRIMARY KEY, username TEXT, password TEXT, role TEXT)");
        db.execSQL("DELETE FROM users");
        db.execSQL("INSERT INTO users VALUES (1, 'admin', 'sup3r_s3cr3t', 'admin')");
        db.execSQL("INSERT INTO users VALUES (2, 'guest', 'guest123', 'user')");

        EditText etUser = findViewById(R.id.et_username);
        EditText etPass = findViewById(R.id.et_password);
        Button btnLogin = findViewById(R.id.btn_login);
        TextView tvHint = findViewById(R.id.tv_hint);
        TextView tvResult = findViewById(R.id.tv_result);

        tvHint.setText("Hint: Try SQL injection to bypass login");

        btnLogin.setOnClickListener(v -> {
            String user = etUser.getText().toString();
            String pass = etPass.getText().toString();

            // VULN-SQL-01: Direct string concatenation — injectable!
            String query = "SELECT * FROM users WHERE username='" + user + "' AND password='" + pass + "'";

            android.util.Log.d("Gu3ssWeak_SQL", "Query: " + query);

            try {
                Cursor cursor = db.rawQuery(query, null);

                if (cursor.moveToFirst()) {
                    String role = cursor.getString(cursor.getColumnIndex("role"));
                    String username = cursor.getString(cursor.getColumnIndex("username"));

                    if ("admin".equals(role)) {
                        // SQL injection succeeded — capture flag!
                        FlagManager.capture(this, FlagManager.FLAG_SQL_01);
                        tvResult.setTextColor(0xFF30D158);
                        tvResult.setBackgroundColor(0xFF0D2318);
                        tvResult.setText("ACCESS GRANTED!\nWelcome " + username + " [" + role + "]\n\nFLAG: " + FlagManager.FLAG_SQL_01);
                        tvResult.setVisibility(android.view.View.VISIBLE);

                        // Open admin panel after 2 seconds
                        new android.os.Handler().postDelayed(() ->
                            startActivity(new Intent(this, AdminPanelActivity.class)), 2000);
                    } else {
                        tvResult.setTextColor(0xFFFFFFFF);
                        tvResult.setBackgroundColor(0xFF111111);
                        tvResult.setText("Logged in as guest. Not admin!");
                        tvResult.setVisibility(android.view.View.VISIBLE);
                    }
                    cursor.close();
                } else {
                    tvResult.setTextColor(0xFFFF3B30);
                    tvResult.setBackgroundColor(0xFF200A0A);
                    tvResult.setText("Login failed. Try SQL injection!");
                    tvResult.setVisibility(android.view.View.VISIBLE);
                    cursor.close();
                }
            } catch (Exception e) {
                tvResult.setText("Error: " + e.getMessage());
                tvResult.setVisibility(android.view.View.VISIBLE);
            }
        });
    }
}
