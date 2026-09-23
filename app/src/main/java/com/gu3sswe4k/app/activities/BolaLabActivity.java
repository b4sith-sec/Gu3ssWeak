package com.gu3sswe4k.app.activities;

import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.gu3sswe4k.app.FlagManager;
import com.gu3sswe4k.app.R;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * VULN-BOLA-01: Broken Object Level Authorization.
 *
 * The app authenticates as user 5 with token "user_token_5". The mock
 * API (scripts/bola_api.py) checks that the token is VALID but never
 * checks it belongs to the SAME user id being requested. Changing the
 * id in the request returns another user's data using your own token.
 *
 * Run the server first:
 *   python3 scripts/bola_api.py
 *
 * Requests go to 10.0.2.2:8080, which is the Android emulator's alias
 * for your host machine's localhost. Route the emulator's proxy
 * through Burp to intercept/replay these requests manually.
 */
public class BolaLabActivity extends AppCompatActivity {

    private static final String MY_TOKEN = "user_token_5";
    private static final String BASE_URL = "http://10.0.2.2:8080";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bola_lab);

        EditText etUserId = findViewById(R.id.et_user_id);
        Button btnFetch = findViewById(R.id.btn_fetch_profile);
        TextView tvResult = findViewById(R.id.tv_bola_result);

        btnFetch.setOnClickListener(v -> {
            String idStr = etUserId.getText().toString().trim();
            if (idStr.isEmpty()) {
                tvResult.setText("Enter a user id.");
                return;
            }
            fetchProfile(idStr, tvResult);
        });
    }

    private void fetchProfile(String userId, TextView tvResult) {
        tvResult.setText("Requesting /api/user/" + userId + "/profile ...");
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... voids) {
                try {
                    URL url = new URL(BASE_URL + "/api/user/" + userId + "/profile");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setConnectTimeout(6000);
                    conn.setReadTimeout(6000);
                    // Always send OUR OWN token, regardless of which id we're requesting.
                    conn.setRequestProperty("Authorization", "Bearer " + MY_TOKEN);

                    int code = conn.getResponseCode();
                    InputStreamReader isr = code >= 200 && code < 300
                            ? new InputStreamReader(conn.getInputStream())
                            : new InputStreamReader(conn.getErrorStream());
                    BufferedReader br = new BufferedReader(isr);
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) sb.append(line);
                    br.close();

                    String body = sb.toString();

                    // VULN-BOLA-01 flag: we authenticated as user 5, but got
                    // data belonging to a DIFFERENT user id back.
                    if (!userId.equals("5") && code == 200 && body.contains("\"id\"")) {
                        FlagManager.capture(BolaLabActivity.this, FlagManager.FLAG_BOLA_01);
                    }

                    return "HTTP " + code + "\n\nToken sent: " + MY_TOKEN + "\nRequested id: " + userId + "\n\n" + body;
                } catch (Exception e) {
                    return "Error: " + e.getMessage()
                            + "\n\nIs scripts/bola_api.py running?\n(python3 scripts/bola_api.py on your host machine)";
                }
            }

            @Override
            protected void onPostExecute(String result) {
                tvResult.setText(result);
            }
        }.execute();
    }
}
