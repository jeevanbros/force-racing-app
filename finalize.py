"""One pass: verify the APK on device, export the brand icon as .ico for the PC app."""
import subprocess
from pathlib import Path

from PIL import Image

SRC = Path(r"C:\Users\ijk11\force-racing-app-src")
ICON = SRC / "res" / "mipmap-xxxhdpi" / "ic_launcher.png"

# 1. Windows icon from the same brand tile
art = Image.open(ICON).convert("RGBA")
for target in [SRC, Path(r"C:\Users\ijk11\Desktop\forceracing-app"), Path(r"C:\Users\ijk11\forceracing-website")]:
    if target.exists():
        art.save(target / "forceracing.ico", sizes=[(16, 16), (24, 24), (32, 32), (48, 48), (64, 64), (128, 128), (256, 256)])
        print("ico ->", target / "forceracing.ico")

# 2. point the PyInstaller spec at it
spec = Path(r"C:\Users\ijk11\Desktop\forceracing-app\ForceRacing-x64.spec")
if spec.exists():
    text = spec.read_text(encoding="utf-8", errors="ignore")
    if "forceracing.ico" not in text:
        import re
        text = re.sub(r"icon\s*=\s*'[^']*'", "icon='forceracing.ico'", text)
        text = re.sub(r'icon\s*=\s*"[^"]*"', "icon='forceracing.ico'", text)
        spec.write_text(text, encoding="utf-8")
        print("spec icon updated:", "icon='forceracing.ico'" in text)

# 3. install + launch the current APK and read the crash buffer
adb = r"C:\Users\ijk11\AppData\Local\Temp\opencode\platform-tools\adb.exe"
dev = "192.168.1.11:43915"
apk = str(SRC / "ForceRacing-fixed.apk")
for cmd in (
    [adb, "-s", dev, "install", "-r", apk],
    [adb, "-s", dev, "shell", "am", "force-stop", "com.forceracing.pitwall"],
    [adb, "-s", dev, "shell", "am", "start", "-n", "com.forceracing.pitwall/.MainActivity"],
):
    r = subprocess.run(cmd, capture_output=True, text=True)
    print(cmd[-1][:40], "->", (r.stdout or r.stderr).strip().splitlines()[-1:])
