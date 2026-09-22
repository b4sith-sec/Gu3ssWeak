package com.gu3sswe4k.app.models;

import java.util.ArrayList;
import java.util.List;

import com.gu3sswe4k.app.activities.*;
import static com.gu3sswe4k.app.models.LabItem.Severity.*;

public class LabRepository {

    public static List<LabItem> getAll() {
        List<LabItem> labs = new ArrayList<>();

        // WebView
        labs.add(new LabItem("WV-01", "JS enabled, no origin check", "WebView", CRITICAL, WebViewActivity.class));
        labs.add(new LabItem("WV-02", "JavascriptInterface bridge exposed", "WebView", CRITICAL, WebViewActivity.class));
        labs.add(new LabItem("WV-03", "Local file read via file://", "WebView", CRITICAL, WebViewActivity.class));
        labs.add(new LabItem("WV-04", "Arbitrary URL from Intent", "WebView", CRITICAL, WebViewActivity.class));
        labs.add(new LabItem("WV-05", "Deeplink loads file:// / javascript:", "WebView", CRITICAL, WebViewActivity.class));

        // Deeplink
        labs.add(new LabItem("DL-01/02", "Deeplink hijack", "Deeplink", HIGH, DeeplinkActivity.class));
        labs.add(new LabItem("DL-03", "Token overwrite via deeplink", "Deeplink", HIGH, DeeplinkActivity.class));
        labs.add(new LabItem("DL-CHAIN", "Deeplink to WebView RCE chain", "Deeplink", HIGH, DeeplinkActivity.class));

        // Auth / SQLi
        labs.add(new LabItem("SQL-01", "Login SQL injection bypass", "Auth / SQL Injection", CRITICAL, LoginActivity.class));

        // Admin Panel
        labs.add(new LabItem("AP-01/02/03", "Admin panel direct access, hardcoded secrets", "Admin Panel", HIGH, AdminPanelActivity.class));
        labs.add(new LabItem("AP-04", "Client-controlled intent auth bypass", "Admin Panel", HIGH, AdminPanelActivity.class));

        // ContentProvider
        labs.add(new LabItem("CP-01", "Contact grant SQL injection (CVE-2026-28576 pattern)", "ContentProvider", CRITICAL, ContactPickerActivity.class));

        // Broadcast Receiver
        labs.add(new LabItem("BR-01/02", "Token injection via broadcast", "Broadcast Receiver", MEDIUM, null));
        labs.add(new LabItem("BR-03", "Sensitive data in logcat", "Broadcast Receiver", MEDIUM, LogcatLeakActivity.class));

        // Service
        labs.add(new LabItem("SV-02A", "Data wipe via exported service", "Service", HIGH, null));
        labs.add(new LabItem("SV-02B", "Data exfil SSRF via service", "Service", HIGH, null));

        // Network
        labs.add(new LabItem("NET-01", "Cleartext traffic, no cert pinning", "Network Interception", MEDIUM, NetworkLabActivity.class));

        // OTP
        labs.add(new LabItem("OTP-01", "OTP brute force, no rate limit", "Banking / OTP", HIGH, OtpBruteForceActivity.class));

        // LFI
        labs.add(new LabItem("LFI-01", "Local file inclusion via path traversal", "LFI", CRITICAL, FileProviderActivity.class));

        // Storage
        labs.add(new LabItem("STORE-01", "Plaintext SharedPreferences", "Storage", LOW, InsecureStorageActivity.class));

        // XSS
        labs.add(new LabItem("XSS-01", "Reflected XSS, filter bypass", "XSS", HIGH, XssLabActivity.class));
        labs.add(new LabItem("XSS-02", "Reflected XSS to bridge UID leak", "XSS", HIGH, XssLabActivity.class));

        return labs;
    }
}
