"""
VULN-BOLA-01: Broken Object Level Authorization mock API.

Two users exist. Each has a token. The /api/user/<id>/profile endpoint
checks that SOME valid token was sent (authentication) but never checks
that the token belongs to the SAME id being requested (authorization).

Valid tokens:
  user_token_5  -> belongs to user 5 (Alice, low-privilege)
  user_token_1  -> belongs to user 1 (Admin, high-privilege)

Exploit: authenticate as user 5, then request /api/user/1/profile
using user_token_5. The server returns the admin's data anyway.

Run:
  python3 scripts/bola_api.py
Then set your emulator's HTTP proxy to your machine's IP:8080
(or 10.0.2.2:8080 from the emulator, which maps to host localhost)
and intercept with Burp.
"""
from http.server import HTTPServer, BaseHTTPRequestHandler
import json
import re

USERS = {
    1: {"id": 1, "name": "Admin", "email": "admin@gu3sswe4k.local",
        "role": "admin", "balance": 999999.99,
        "notes": "FLAG{b0la_1d0r_pr0f1l3_l34k_BOLA01}"},
    5: {"id": 5, "name": "Alice", "email": "alice@gu3sswe4k.local",
        "role": "user", "balance": 42.50,
        "notes": "just a regular user"},
}

VALID_TOKENS = {"user_token_5": 5, "user_token_1": 1}


class Handler(BaseHTTPRequestHandler):
    def do_GET(self):
        print(f"\n[+] GET {self.path}")
        print(f"[+] HEADERS: {dict(self.headers)}")

        m = re.match(r"^/api/user/(\d+)/profile$", self.path)
        if not m:
            self.send_response(404)
            self.end_headers()
            self.wfile.write(b'{"error":"not found"}')
            return

        requested_id = int(m.group(1))
        auth = self.headers.get("Authorization", "")
        token = auth.replace("Bearer ", "").strip()

        # VULN-BOLA-01: only checks the token is VALID (authenticated),
        # never checks it belongs to the SAME user as requested_id.
        if token not in VALID_TOKENS:
            self.send_response(401)
            self.end_headers()
            self.wfile.write(b'{"error":"unauthorized"}')
            return

        user = USERS.get(requested_id)
        if not user:
            self.send_response(404)
            self.end_headers()
            self.wfile.write(b'{"error":"user not found"}')
            return

        self.send_response(200)
        self.send_header("Content-Type", "application/json")
        self.end_headers()
        self.wfile.write(json.dumps(user).encode())

    def log_message(self, format, *args):
        pass  # keep default request logging above, suppress duplicate


if __name__ == "__main__":
    print("BOLA mock API running on :8080")
    HTTPServer(("0.0.0.0", 8080), Handler).serve_forever()
