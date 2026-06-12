# Architecture Overview

This document explains how each vulnerable component in Gu3ssWeak works internally.

## App Structure

com.vulndroid.app/
- activities/MainActivity - Hub, links to all labs
- activities/WebViewActivity - WV-01 to WV-05
- activities/DeeplinkActivity - DL-01 to DL-04, DL-CHAIN
- activities/LoginActivity - SQL-01
- activities/AdminPanelActivity - AP-01 to AP-03
- activities/FlagBoardActivity - CTF scoreboard
- activities/FlagSubmitActivity - Flag submission UI
- receivers/TokenReceiver - BR-01 to BR-03
- services/DataSyncService - SV-01, SV-02a, SV-02b
- FlagManager - CTF flag state

## Why These Patterns Are Exploitable

### Exported components without permission checks
Components marked exported true can be invoked by any app on the device, or via adb shell am start/startservice/broadcast. Without a permission check, the component trusts all callers equally.

### WebView plus JavascriptInterface
addJavascriptInterface exposes Java methods to any JavaScript in the WebView, including attacker-controlled javascript: URIs. Combined with unsanitized loadUrl, this becomes a direct attack bridge.

### SQL Injection via String Concatenation
LoginActivity builds queries with direct string concatenation. Attackers can inject OR 1=1 to bypass authentication entirely.

### Deeplink Parameter Forwarding
DeeplinkActivity resolves class names via Class.forName from URI parameters, letting a remote link choose which internal component to launch.
