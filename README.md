# Flour Silo Monitor

Android app for monitoring two cone-bottom flour silos: **Silo 11** and **Silo 12**.

## Included screens
- Home dashboard with cone-bottom silo illustrations
- Silo detail pages
- Level trends: 24 hours / 7 days / 30 days
- Storage & usage
- Alarms and warnings
- Historical readings
- Silos overview
- Settings
- Demo/manual refresh data

The current version uses sample data so the UI can be tested immediately.
The next step can be connecting real level/temperature transmitters through PLC, Modbus TCP, OPC UA, MQTT, or another plant interface.

## Build without Android Studio
1. Upload the project to GitHub.
2. Open **Actions**.
3. Select **Build Android APK**.
4. Click **Run workflow**.
5. Download the `FlourSiloMonitor-debug-apk` artifact.

## Important
The displayed values are demonstration values only. They are not connected to real silo instrumentation.
