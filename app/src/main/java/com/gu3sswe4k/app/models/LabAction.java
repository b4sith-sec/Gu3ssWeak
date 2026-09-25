package com.gu3sswe4k.app.models;

import androidx.appcompat.app.AppCompatActivity;

/** Custom click behavior for a lab that isn't a plain startActivity(). */
public interface LabAction {
    void execute(AppCompatActivity activity);
}
