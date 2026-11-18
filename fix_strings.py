from pathlib import Path

p = Path(r"C:\Users\ijk11\force-racing-app-src\src\com\forceracing\pitwall\MainActivity.java")
src = p.read_text(encoding="utf-8")

# real newline inside a string literal -> escaped newline
src = src.replace('"ONE TEAM.\nEVERY DETAIL."', '"ONE TEAM.\\nEVERY DETAIL."')
# double-escaped sequences -> single escape (java newline)
for a, b in [
    ('"WE DON\'T JUST\\\\nSTUDY IT."', '"WE DON\'T JUST\\nSTUDY IT."'),
    ('"BE PART OF\\\\nTHE FORCE."', '"BE PART OF\\nTHE FORCE."'),
    ('"FUEL THE\\\\nNEXT CAR."', '"FUEL THE\\nNEXT CAR."'),
    ('"GET IN\\\\nTOUCH."', '"GET IN\\nTOUCH."'),
    ('"Kakatiya Institute of Technology & Science\\\\nWarangal, Telangana, India"',
     '"Kakatiya Institute of Technology & Science\\nWarangal, Telangana, India"'),
]:
    src = src.replace(a, b)

p.write_text(src, encoding="utf-8")
print("patched")
for i, line in enumerate(src.splitlines(), 1):
    if "ONE TEAM" in line or "JUST" in line or "BE PART OF" in line or "FUEL THE" in line or "GET IN" in line or "Kakatiya Institute" in line:
        print(i, repr(line.strip()))
