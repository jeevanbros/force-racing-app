from pathlib import Path

p = Path(r"C:\Users\ijk11\force-racing-app-src\src\com\forceracing\pitwall\MainActivity.java")
src = p.read_text(encoding="utf-8")

# 1. repair characters mangled by an earlier PowerShell rewrite
src = src.replace("Â·", "·").replace("â†'", "→").replace("â€”", "—")

# 2. always open a screen at the top
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
if old in src:
    src = src.replace(old, new)

# 3. splash: DREAM. BUILD. RACE. under the brand mark
old_rule = """        ruleParams.topMargin = dp(14);
        splash.addView(rule, ruleParams);"""
new_rule = """        ruleParams.topMargin = dp(14);
        splash.addView(rule, ruleParams);

        TextView motto = t("DREAM.  BUILD.  RACE.", 10, MUTED, true);
        motto.setLetterSpacing(0.22f);
        LinearLayout.LayoutParams mottoParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mottoParams.topMargin = dp(20);
        splash.addView(motto, mottoParams);"""
if old_rule not in src:
    raise SystemExit("splash block not found")
src = src.replace(old_rule, new_rule)

p.write_text(src, encoding="utf-8")
print("ok", len(src))
