from pathlib import Path

b = Path(r"C:\Users\ijk11\force-racing-app-src\build_apk.py")
s = b.read_text(encoding="utf-8")
s = s.replace('"--min-sdk-version", "24"', '"--min-sdk-version", "21"')
s = s.replace('"--min-api", "24"', '"--min-api", "21"')
b.write_text(s, encoding="utf-8")
print("min-sdk:", s.count('"--min-sdk-version", "21"'), "min-api:", s.count('"--min-api", "21"'))
