package com.vulndroid.app.activities;

import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.vulndroid.app.FlagManager;
import com.vulndroid.app.R;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * VULN-NET-01: Cleartext traffic permitted, no certificate pinning.
 *
 * network_security_config.xml allows cleartextTrafficPermitted="true"
 * and trusts user-installed certificates, meaning all traffic from
 * this app can be intercepted with a proxy like Burp Suite without
 * any bypass needed.
 *
 * Lab: Configure your device/emulator to use Burp as HTTP proxy,
 * install Burp's CA cert, then tap "Send Request" below and
 * observe the request/response in Burp.
 */
public class NetworkLabActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_network_lab);

        TextView tvResult = findViewById(R.id.tv_net_result);
        Button btnHttp = findViewById(R.id.btn_send_http);
        Button btnHttps = findViewById(R.id.btn_send_https);

        btnHttp.setOnClickListener(v -> sendRequest("http://example.com", tvResult, false));
        btnHttps.setOnClickListener(v -> sendRequest("https://example.com", tvResult, true));
    }

    private void sendRequest(String urlStr, TextView tvResult, boolean isHttps) {
        tvResult.setText("Sending request to " + urlStr + " ...");
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... voids) {
                try {
                    URL url = new URL(urlStr);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setConnectTimeout(8000);
                    conn.setReadTimeout(8000);
                    int code = conn.getResponseCode();

                    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder sb = new StringBuilder();
                    String line;
                    int lines = 0;
                    while ((line = br.readLine()) != null && lines < 5) {
                        sb.append(line).append("\n");
                        lines++;
                    }
                    br.close();

                    FlagManager.capture(NetworkLabActivity.this, FlagManager.FLAG_NET_01);

                    return "HTTP " + code + "\n\n" + sb.toString() + "\n\nFLAG: " + FlagManager.FLAG_NET_01;
                } catch (Exception e) {
                    return "Error: " + e.getMessage();
                }
            }

            @Override
            protected void onPostExecute(String result) {
                tvResult.setText(result);
            }
        }.execute();
    }
}
