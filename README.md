# Force Racing — Android App

Native Android app for **Force Racing**, the Formula Student team of Kakatiya Institute of Technology & Science, Warangal.

Written in Java as a single `Activity` with a hand-built native UI layer (splash, offline panel, back handling), with no Gradle wrapper and no third-party dependencies — the APK is assembled directly with the Android SDK toolchain.

## Features

- Branded splash screen with the team logo and colors
- Native offline / error screen with a retry action instead of the browser error page
- Native back navigation (in-app history first, then exit)
- Own launcher icon and adaptive icons (`mipmap-anydpi-v26`)
- Immersive, hardware-accelerated dark UI (`#0B0B0D` with Force Racing red)
- Content is loaded from the team's live site inside the app shell, with no browser chrome, address bar, zoom or external URL trap-doors
- Custom user agent (`ForceRacingApp/2.0`) so the site can serve app-specific content

## Requirements

- JDK 17
- Android SDK build-tools (`aapt2`, `d8`, `zipalign`, `apksigner`)
- `android.jar` for the target platform (API 35)

The toolchain paths are configured at the top of `build_apk.py`.

## Build

```bash
python build_apk.py
```

The script compiles resources with `aapt2`, generates `R.java`, compiles the Java sources, dexes with `d8`, aligns with `zipalign`, and signs the result with `apksigner`. Output: `ForceRacing-fixed.apk` in the project root.

## App details

| | |
|---|---|
| Package | `com.forceracing.pitwall` |
| Min SDK | 24 (Android 7.0) |
| Target SDK | 35 |
| Version | 2.0 |

## Repository notes

The built APK is **not committed** — it is produced locally by `build_apk.py` and is excluded via `.gitignore`, along with the build directory and all signing material (`.jks` / `.keystore`). Never commit your keystore or its passwords.

Application icons under `res/mipmap-*` and `res/drawable` are tracked; only generated preview images at the repo root are ignored.
