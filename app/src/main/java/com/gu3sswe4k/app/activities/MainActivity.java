package com.gu3sswe4k.app.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import com.gu3sswe4k.app.R;
import com.gu3sswe4k.app.adapters.LabAdapter;
import com.gu3sswe4k.app.models.LabItem;
import com.gu3sswe4k.app.models.LabRepository;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private List<LabItem> allLabs;
    private LabAdapter adapter;
    private String currentQuery = "";
    private String currentCategory = "All";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Seed a fake session, used by several other labs (WebView bridge, storage, etc.)
        getSharedPreferences("user_prefs", MODE_PRIVATE).edit()
            .putString("auth_token", "eyJhbGciOiJIUzI1NiJ9.VICTIM_TOKEN")
            .putString("username", "victim_user").apply();

        allLabs = LabRepository.getAll();

        RecyclerView rv = findViewById(R.id.rv_labs);
        rv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new LabAdapter(this::onLabClicked);
        rv.setAdapter(adapter);

        setupCategoryChips();
        setupSearch();

        TextView tvCount = findViewById(R.id.tv_lab_count);
        tvCount.setText(allLabs.size() + " vulnerabilities · OWASP Mobile Top 10");

        applyFilters();
    }

    private void onLabClicked(LabItem lab) {
        if (lab.targetActivity != null) {
            startActivity(new Intent(this, lab.targetActivity));
        } else if (lab.action != null) {
            lab.action.execute(this);
        } else {
            new AlertDialog.Builder(this)
                .setTitle(lab.id + " · " + lab.title)
                .setMessage("This lab is exploited via ADB. See the README's ADB cheatsheet for the exact command.")
                .setPositiveButton("OK", null)
                .show();
        }
    }

    private void setupCategoryChips() {
        ChipGroup chipGroup = findViewById(R.id.chip_group_categories);

        Set<String> categories = new LinkedHashSet<>();
        for (LabItem lab : allLabs) categories.add(lab.category);

        for (String category : categories) {
            Chip chip = new Chip(this);
            chip.setText(category);
            chip.setCheckable(true);
            chip.setId(android.view.View.generateViewId());
            chipGroup.addView(chip);
        }

        chipGroup.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds.isEmpty()) {
                currentCategory = "All";
            } else {
                Chip checked = group.findViewById(checkedIds.get(0));
                currentCategory = checked != null ? checked.getText().toString() : "All";
            }
            applyFilters();
        });
    }

    private void setupSearch() {
        EditText etSearch = findViewById(R.id.et_search);
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                currentQuery = s.toString().toLowerCase();
                applyFilters();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void applyFilters() {
        List<LabItem> filtered = new ArrayList<>();
        for (LabItem lab : allLabs) {
            boolean matchesCategory = currentCategory.equals("All") || lab.category.equals(currentCategory);
            boolean matchesQuery = currentQuery.isEmpty()
                    || lab.id.toLowerCase().contains(currentQuery)
                    || lab.title.toLowerCase().contains(currentQuery)
                    || lab.category.toLowerCase().contains(currentQuery);
            if (matchesCategory && matchesQuery) filtered.add(lab);
        }
        adapter.submitList(filtered);
    }
}
