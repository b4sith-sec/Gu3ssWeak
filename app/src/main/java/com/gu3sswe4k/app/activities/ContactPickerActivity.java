package com.gu3sswe4k.app.activities;

import android.app.Activity;
import android.content.ContentUris;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.gu3sswe4k.app.R;
import com.gu3sswe4k.app.FlagManager;

/**
 * CP-01: Simulates the Android 17 system contact picker granting
 * read access to a single contact, then demonstrates how an
 * unsanitized `selection` string lets a caller read every row
 * in the table — mirroring CVE-2026-28576.
 */
public class ContactPickerActivity extends Activity {

    private Uri grantedUri = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_picker);

        Button btnPick = findViewById(R.id.btn_pick_contact);
        Button btnRun = findViewById(R.id.btn_run_query);
        TextView tvGrant = findViewById(R.id.tv_grant_info);
        TextView tvResult = findViewById(R.id.tv_query_result);
        EditText etWhere = findViewById(R.id.et_where_clause);

        // Simulates the system picker returning a single-row grant,
        // exactly like ACTION_PICK does for the real Contacts picker.
        btnPick.setOnClickListener(v -> {
            grantedUri = Uri.parse("content://com.gu3sswe4k.app.contacts/contacts/1");
            tvGrant.setText("Granted: " + grantedUri + "\n(You are only supposed to see contact _id=1: Alice Victim)");
        });

        btnRun.setOnClickListener(v -> {
            if (grantedUri == null) {
                tvResult.setText("Pick a contact first.");
                return;
            }

            String userWhere = etWhere.getText().toString();

            Cursor c = getContentResolver().query(
                    grantedUri,
                    new String[]{"_id", "name", "phone", "email", "notes"},
                    userWhere.isEmpty() ? null : userWhere,
                    null, null);

            if (c == null) {
                tvResult.setText("Query failed / provider not found.");
                return;
            }

            StringBuilder sb = new StringBuilder();
            boolean sawOtherRow = false;
            while (c.moveToNext()) {
                long id = c.getLong(0);
                String name = c.getString(1);
                String notes = c.getString(4);
                sb.append("_id=").append(id)
                  .append(" name=").append(name)
                  .append(" notes=").append(notes)
                  .append("\n");
                if (id != 1) sawOtherRow = true;
            }
            c.close();

            if (sb.length() == 0) {
                sb.append("(no rows returned — oracle=false)");
            }
            tvResult.setText(sb.toString());

            // CP-01 flag: caller with a single-row grant (_id=1) observed
            // data belonging to a different row via the selection string.
            if (sawOtherRow) {
                FlagManager.capture(this, FlagManager.FLAG_CP_01);
            }
        });
    }
}
