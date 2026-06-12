package com.vulndroid.app.activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.vulndroid.app.FlagManager;
import com.vulndroid.app.R;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

/**
 * VULN-FP-01: Path traversal via internal file read
 *
 * This screen simulates a "download attachment by name" feature.
 * The filename parameter is concatenated directly into a file path
 * with no sanitization, allowing ../ traversal to read arbitrary
 * files within the app's data directory (and beyond, depending on
 * permissions).
 *
 * Exploit: enter a filename like
 *   ../shared_prefs/user_prefs.xml
 * to read files outside the intended "attachments" directory.
 */
public class FileProviderActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_provider);

        // Set up a sample "attachments" directory with a normal file
        File attachmentsDir = new File(getFilesDir(), "attachments");
        attachmentsDir.mkdirs();
        try {
            File sample = new File(attachmentsDir, "invoice.txt");
            FileOutputStream fos = new FileOutputStream(sample);
            fos.write("Invoice #1234\nAmount: $42.00\nStatus: Paid".getBytes());
            fos.close();

            // Also seed the secret file this lab targets
            File secret = new File(getFilesDir(), "secret_notes.txt");
            FileOutputStream fos2 = new FileOutputStream(secret);
            fos2.write(("CONFIDENTIAL\nInternal notes file\nFLAG: "
                + FlagManager.FLAG_FP_01).getBytes());
            fos2.close();
        } catch (Exception ignored) {}

        EditText etFilename = findViewById(R.id.et_filename);
        Button btnDownload = findViewById(R.id.btn_download);
        TextView tvContent = findViewById(R.id.tv_file_content);

        etFilename.setText("invoice.txt");

        btnDownload.setOnClickListener(v -> {
            String filename = etFilename.getText().toString().trim();

            // VULN-FP-01: No sanitization - filename concatenated directly
            File target = new File(attachmentsDir, filename);

            try {
                FileInputStream fis = new FileInputStream(target);
                byte[] data = new byte[(int) target.length()];
                fis.read(data);
                fis.close();
                String content = new String(data);

                tvContent.setText(content);
                tvContent.setTextColor(0xFFFFFFFF);
                tvContent.setBackgroundColor(0xFF111111);

                if (content.contains("Gu3ssWeak{")) {
                    FlagManager.capture(FileProviderActivity.this, FlagManager.FLAG_FP_01);
                }
            } catch (Exception e) {
                tvContent.setText("Error reading file: " + e.getMessage());
                tvContent.setTextColor(0xFFFF3B30);
                tvContent.setBackgroundColor(0xFF200A0A);
            }
            tvContent.setVisibility(android.view.View.VISIBLE);
        });
    }
}
