from pathlib import Path

p = Path(r"C:\Users\ijk11\force-racing-app-src\src\com\forceracing\pitwall\MainActivity.java")
src = p.read_text(encoding="utf-8")

# repair characters that were mangled by an earlier PowerShell rewrite
src = src.replace("Â·", "·").replace("â†'", "→").replace("â€”", "—")

# always open a screen at the top
old = """        scroll.setVerticalScrollBarEnabled(false);
        scroll.addView(inner, new ScrollView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        return scroll;"""
new = """        scroll.setVerticalScrollBarEnabled(false);
        scroll.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
        scroll.addView(inner, new ScrollView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        scroll.post(new Runnable() {
            @Override
            public void run() {
                scroll.scrollTo(0, 0);
            }
        });
        return scroll;"""
if old not in src:
    raise SystemExit("scroller block not found")
src = src.replace(old, new)

p.write_text(src, encoding="utf-8")
bad = [(i, ch) for i, ch in enumerate(src) if ord(ch) > 127]
print("non-ascii:", len(bad), sorted({ch for _, ch in bad}))
