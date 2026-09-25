package com.gu3sswe4k.app.models;

public class LabItem {

    public enum Severity {
        CRITICAL("Critical", "#D32F2F"),
        HIGH("High", "#F57C00"),
        MEDIUM("Medium", "#FBC02D"),
        LOW("Low", "#388E3C");

        public final String label;
        public final String colorHex;

        Severity(String label, String colorHex) {
            this.label = label;
            this.colorHex = colorHex;
        }
    }

    public final String id;          // e.g. "WV-01"
    public final String title;       // e.g. "JS enabled, no origin check"
    public final String category;    // e.g. "WebView"
    public final Severity severity;
    public final Class<?> targetActivity; // activity to launch, nullable
    public final LabAction action;         // custom click behavior, nullable

    public LabItem(String id, String title, String category, Severity severity, Class<?> targetActivity) {
        this(id, title, category, severity, targetActivity, null);
    }

    public LabItem(String id, String title, String category, Severity severity, Class<?> targetActivity, LabAction action) {
        this.id = id;
        this.title = title;
        this.category = category;
        this.severity = severity;
        this.targetActivity = targetActivity;
        this.action = action;
    }
}
