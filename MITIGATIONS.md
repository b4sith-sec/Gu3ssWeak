# Mitigation and Remediation Guide

## WebView Labs

VULN-WV-01: Only enable JavaScript for trusted content. Use shouldOverrideUrlLoading allowlist.

VULN-WV-02: Avoid addJavascriptInterface. Restrict to trusted origins, minimize exposed methods.

VULN-WV-03: Set setAllowFileAccess false, setAllowFileAccessFromFileURLs false, setAllowUniversalAccessFromFileURLs false.

VULN-WV-04: Validate intent-supplied URLs against an allowlist before loadUrl.

## Deeplink Labs

VULN-DL-01 and DL-02: Never use Class.forName with attacker-controlled strings. Use a hardcoded allowlist of destinations.

VULN-DL-03: Deeplinks must never modify auth state without a signed, validated token.

VULN-DL-CHAIN: Fix WV-02 and DL-02 independently. Defense in depth.

## SQL Injection

VULN-SQL-01: Use parameterized queries with rawQuery and a parameters array instead of string concatenation. Store password hashes using bcrypt or Argon2, never plaintext.

## Admin Panel Labs

VULN-AP-01: Set exported false unless external launch required. Use signature permissions.

VULN-AP-02: Never hardcode secrets. Retrieve at runtime after authentication.

VULN-AP-03: Verify active server-side session before rendering privileged UI.

## Broadcast Receiver Labs

VULN-BR-01 and BR-02: Set exported false. Require signature permission for cross-app broadcasts.

VULN-BR-03: Never log tokens or secrets. Strip debug logs from release builds.

## Service Labs

VULN-SV-01, SV-02a, SV-02b: Set exported false. Validate caller signature. Allowlist permitted actions. Never accept attacker-supplied exfil URLs.

## General Hardening Checklist

- Set debuggable false in release builds
- Avoid usesCleartextTraffic true
- Enable certificate pinning
- Run apktool and jadx against your own release APK to verify no secrets leak
- Use lint and MobSF in CI
