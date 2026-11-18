"""Build the Force Racing APK without Gradle (fixed toolchain paths)."""
from __future__ import annotations

import os
import shutil
import subprocess
import sys
import zipfile
from pathlib import Path

TMP = Path(r"C:\Users\ijk11\AppData\Local\Temp\opencode")
SRC = Path(r"C:\Users\ijk11\force-racing-app-src")
BUILD = SRC / "build"
FINAL = SRC / "ForceRacing-fixed.apk"

JDK = TMP / "jdk-17.0.21+9"
ANDROID_JAR = TMP / "android-35" / "android.jar"
BT = TMP / "build-tools" / "android-15"

JAVAC = JDK / "bin" / "javac.exe"
KEYTOOL = JDK / "bin" / "keytool.exe"
AAPT2 = BT / "aapt2.exe"
D8 = BT / "d8.bat"
ZIPALIGN = BT / "zipalign.exe"
APKSIGNER = BT / "apksigner.bat"

ENV = dict(os.environ)
ENV["JAVA_HOME"] = str(JDK)
ENV["PATH"] = str(JDK / "bin") + os.pathsep + ENV.get("PATH", "")


def run(cmd):
    result = subprocess.run([str(c) for c in cmd], capture_output=True, text=True, env=ENV)
    if result.returncode != 0:
        print(result.stdout[-2500:])
        print(result.stderr[-2500:])
        sys.exit(f"FAILED: {Path(str(cmd[0])).name}")
    return result.stdout


def main() -> None:
    for name, tool in [("javac", JAVAC), ("keytool", KEYTOOL), ("android.jar", ANDROID_JAR),
                       ("aapt2", AAPT2), ("d8", D8), ("zipalign", ZIPALIGN),
                       ("apksigner", APKSIGNER)]:
        if not Path(tool).exists():
            sys.exit(f"missing {name}: {tool}")

    if BUILD.exists():
        shutil.rmtree(BUILD)
    (BUILD / "classes").mkdir(parents=True)
    (BUILD / "dex").mkdir()
    (BUILD / "gen").mkdir()

    # resources + manifest first, so R.java can be generated
    run([AAPT2, "compile", "--dir", SRC / "res", "-o", BUILD / "res.zip"])

    unsigned = BUILD / "unsigned.apk"
    run([AAPT2, "link", "-o", unsigned, "-I", ANDROID_JAR,
         "--manifest", SRC / "AndroidManifest.xml",
         "--min-sdk-version", "24", "--target-sdk-version", "35",
         "--version-code", "2", "--version-name", "2.0",
         "--java", BUILD / "gen",
         BUILD / "res.zip"])

    java_files = [str(p) for p in (SRC / "src").rglob("*.java")]
    java_files += [str(p) for p in (BUILD / "gen").rglob("*.java")]
    run([JAVAC, "-source", "8", "-target", "8", "-nowarn",
         "-classpath", ANDROID_JAR, "-d", BUILD / "classes", *java_files])

    class_files = [str(p) for p in (BUILD / "classes").rglob("*.class")]
    run([D8, "--lib", ANDROID_JAR, "--min-api", "24", "--output", BUILD / "dex", *class_files])

    with_dex = BUILD / "with-dex.apk"
    with zipfile.ZipFile(unsigned) as src, zipfile.ZipFile(with_dex, "w", zipfile.ZIP_DEFLATED) as dst:
        for item in src.infolist():
            dst.writestr(item, src.read(item.filename))
        dst.write(BUILD / "dex" / "classes.dex", "classes.dex")

    aligned = BUILD / "aligned.apk"
    run([ZIPALIGN, "-f", "-p", "4", with_dex, aligned])

    keystore = SRC / "forceracing.jks"
    if not keystore.exists():
        run([KEYTOOL, "-genkeypair", "-keystore", keystore, "-alias", "forceracing",
             "-keyalg", "RSA", "-keysize", "2048", "-validity", "10000",
             "-storepass", "forceracing", "-keypass", "forceracing",
             "-dname", "CN=Force Racing, O=Force Racing"])

    if FINAL.exists():
        FINAL.unlink()
    run([APKSIGNER, "sign", "--ks", keystore, "--ks-pass", "pass:forceracing",
         "--key-pass", "pass:forceracing", "--out", FINAL, aligned])
    run([APKSIGNER, "verify", FINAL])

    print("BUILT", FINAL, f"{FINAL.stat().st_size / 1024 / 1024:.1f} MB")


if __name__ == "__main__":
    main()
