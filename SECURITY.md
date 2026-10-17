# Security Policy

**SurveyApp** is an Android mobile survey platform utilizing Firebase realtime backend services.

---

## 🔒 Security Practices

1. **Android Keystore Protection:** Cryptographic keys and app signing keystores (`.jks`, `.keystore`) must never be committed to source control.
2. **Firebase Security Rules:** Firestore and Firebase Realtime Database paths are enforced using role-based read/write rules.
3. **Data Transport Security:** All mobile network requests use HTTPS/TLS connections.

---

## 🚨 Vulnerability Reporting

Please report security issues directly to:
- **Email:** `tanmaynileshshinde@gmail.com`
- **Subject:** `[SECURITY] SurveyApp Vulnerability Report`
