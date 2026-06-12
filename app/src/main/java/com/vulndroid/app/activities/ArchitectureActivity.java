package com.vulndroid.app.activities;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.vulndroid.app.R;

public class ArchitectureActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_architecture);

        findViewById(R.id.btn_continue).setOnClickListener(v ->
            startActivity(new Intent(this, MainActivity.class)));
    }
}
