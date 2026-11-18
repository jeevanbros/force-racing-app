package com.forceracing.pitwall;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowInsets;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

/**
 * Force Racing - native Android app.
 *
 * Every screen is built from native Android views. Nothing is loaded from a
 * website, so there is no browser framing, no page reloads and no WebView
 * crash surface.
 */
public class MainActivity extends Activity {

    private static final int BG = 0xFF0B0B0D;
    private static final int CARD = 0xFF14161B;
    private static final int LINE = 0xFF262A31;
    private static final int INK = 0xFFF2F3F5;
    private static final int MUTED = 0xFF8A8F98;
    private static final int RED = 0xFFFB0102;
    private static final int GOLD = 0xFFE7B24B;
    private static final int SILVER = 0xFFC7CDD6;

    private static final String IG = "https://www.instagram.com/force.racing?igsh=MXI6cGRnYjA0cGE=";
    private static final String LI = "https://www.linkedin.com/company/force-racing/";
    private static final String YT = "https://youtube.com/@forceracing8847?si=3Ki2roWmQvvdFWp-";
    private static final String FB = "https://www.facebook.com/forceracingkitsw/";
    private static final String MAPS = "https://www.google.com/maps/search/?api=1&query=Force+Racing+India+KITS+Warangal";

    private static final String[] TABS = {"HOME", "CARS", "TEAM", "SUPPORT", "CONTACT"};

    private FrameLayout contentArea;
    private TextView[] tabLabels = new TextView[TABS.length];
    private View[] tabDots = new View[TABS.length];
    private FrameLayout root;
    private int current = -1;

    @Override
    protected void onCreate(Bundle saved) {
        super.onCreate(saved);

        root = new FrameLayout(this);
        root.setBackgroundColor(BG);

        LinearLayout body = new LinearLayout(this);
        body.setOrientation(LinearLayout.VERTICAL);

        body.addView(buildHeader());

        contentArea = new FrameLayout(this);
        LinearLayout.LayoutParams contentParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f);
        body.addView(contentArea, contentParams);

        body.addView(divider());
        body.addView(buildNav());

        root.addView(body, new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        root.setOnApplyWindowInsetsListener(new View.OnApplyWindowInsetsListener() {
            @Override
            public WindowInsets onApplyWindowInsets(View view, WindowInsets insets) {
                view.setPadding(0, insets.getSystemWindowInsetTop(), 0, 0);
                return insets;
            }
        });

        setContentView(root);

        show(0);
        addSplash();
    }

    // ---------------------------------------------------------------- chrome

    private View buildHeader() {
        LinearLayout bar = new LinearLayout(this);
        bar.setOrientation(LinearLayout.HORIZONTAL);
        bar.setGravity(Gravity.CENTER_VERTICAL);
        bar.setPadding(dp(20), dp(14), dp(20), dp(14));

        ImageView logo = new ImageView(this);
        logo.setImageResource(R.drawable.logo);
        bar.addView(logo, new LinearLayout.LayoutParams(dp(32), dp(32)));

        LinearLayout names = new LinearLayout(this);
        names.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams namesParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        namesParams.leftMargin = dp(12);
        TextView title = t("FORCE RACING", 15, INK, true);
        title.setLetterSpacing(0.16f);
        names.addView(title);
        TextView sub = t("KITS WARANGAL", 9, MUTED, false);
        sub.setLetterSpacing(0.22f);
        names.addView(sub);
        bar.addView(names, namesParams);

        View spring = new View(this);
        bar.addView(spring, new LinearLayout.LayoutParams(0, 1, 1f));

        TextView est = t("EST 2009", 9, MUTED, false);
        est.setLetterSpacing(0.18f);
        bar.addView(est);

        LinearLayout wrap = new LinearLayout(this);
        wrap.setOrientation(LinearLayout.VERTICAL);
        wrap.addView(bar);
        wrap.addView(divider());
        return wrap;
    }

    private View buildNav() {
        LinearLayout nav = new LinearLayout(this);
        nav.setOrientation(LinearLayout.HORIZONTAL);
        nav.setBackgroundColor(0xFF0E1014);
        nav.setPadding(dp(6), dp(9), dp(6), dp(9));

        for (int i = 0; i < TABS.length; i++) {
            final int index = i;
            LinearLayout item = new LinearLayout(this);
            item.setOrientation(LinearLayout.VERTICAL);
            item.setGravity(Gravity.CENTER);
            item.setPadding(0, dp(2), 0, dp(2));
            item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    show(index);
                }
            });

            TextView label = t(TABS[i], 9, MUTED, true);
            label.setLetterSpacing(0.1f);
            label.setGravity(Gravity.CENTER);
            item.addView(label);

            View dot = new View(this);
            LinearLayout.LayoutParams dotParams = new LinearLayout.LayoutParams(dp(16), dp(2));
            dotParams.topMargin = dp(6);
            dot.setLayoutParams(dotParams);
            dot.setBackgroundColor(RED);
            dot.setAlpha(0f);
            item.addView(dot);

            nav.addView(item, new LinearLayout.LayoutParams(0,
                    ViewGroup.LayoutParams.WRAP_CONTENT, 1f));

            tabLabels[i] = label;
            tabDots[i] = dot;
        }

        LinearLayout wrap = new LinearLayout(this);
        wrap.setOrientation(LinearLayout.VERTICAL);
        wrap.addView(divider());
        wrap.addView(nav);
        return wrap;
    }

    private View divider() {
        View v = new View(this);
        v.setBackgroundColor(LINE);
        v.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, 1));
        return v;
    }

    private void show(int index) {
        if (index == current) {
            return;
        }
        current = index;
        for (int i = 0; i < TABS.length; i++) {
            boolean active = i == index;
            tabLabels[i].setTextColor(active ? INK : MUTED);
            tabDots[i].setAlpha(active ? 1f : 0f);
        }
        contentArea.removeAllViews();
        View screen;
        switch (index) {
            case 1:
                screen = screenCars();
                break;
            case 2:
                screen = screenTeam();
                break;
            case 3:
                screen = screenSupport();
                break;
            case 4:
                screen = screenContact();
                break;
            default:
                screen = screenHome();
        }
        contentArea.addView(screen, new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
    }

    private View scroller(LinearLayout inner) {
        ScrollView scroll = new ScrollView(this);
        scroll.setFillViewport(true);
        scroll.setClipToPadding(false);
        scroll.setVerticalScrollBarEnabled(false);
        scroll.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
        scroll.addView(inner, new ScrollView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        scroll.post(new Runnable() {
            @Override
            public void run() {
                scroll.scrollTo(0, 0);
            }
        });
        return scroll;
    }

    private LinearLayout page() {
        LinearLayout p = new LinearLayout(this);
        p.setOrientation(LinearLayout.VERTICAL);
        p.setPadding(dp(20), dp(18), dp(20), dp(28));
        return p;
    }

    // ---------------------------------------------------------------- screens

    private View screenHome() {
        LinearLayout p = page();

        p.addView(kicker("FORMULA STUDENT · WARANGAL, INDIA"));
        p.addView(space(10));

        p.addView(heroLine("DREAM"));
        p.addView(heroLine("BUILD"));
        p.addView(heroLine("RACE"));
        p.addView(space(16));

        p.addView(paragraph("Representing Kakatiya Institute of Technology & Science on national circuits. Designing, engineering and manufacturing championship formula cars since 2009."));
        p.addView(space(20));

        LinearLayout stats = new LinearLayout(this);
        stats.setOrientation(LinearLayout.HORIZONTAL);
        stats.addView(stat("2009", "FOUNDED"), weight());
        stats.addView(stat("FR 26", "CURRENT CAR"), weight());
        stats.addView(stat("06", "SUBSYSTEMS"), weight());
        p.addView(stats);

        p.addView(space(26));
        p.addView(eyebrow("WE DON'T JUST STUDY IT. WE BUILD IT."));
        p.addView(space(10));
        p.addView(paragraph("Established in 2009, Force Racing brings students from the classroom to the workshop. We design, manufacture and compete with formula-style race cars, turning theory into machines that actually run."));

        p.addView(space(22));
        LinearLayout actions = new LinearLayout(this);
        actions.setOrientation(LinearLayout.HORIZONTAL);
        actions.addView(button("SEE THE CARS", true, new Runnable() {
            @Override
            public void run() {
                show(1);
            }
        }), weight());
        actions.addView(spaceH(10));
        actions.addView(button("MEET THE TEAM", false, new Runnable() {
            @Override
            public void run() {
                show(2);
            }
        }), weight());
        p.addView(actions);

        p.addView(space(26));
        p.addView(eyebrow("IDEAS BECOME MACHINES."));
        p.addView(space(10));
        p.addView(paragraph("Our mission is to develop industry-ready engineers through rigorous technical execution, business practice and project management. Alumni have progressed into high-performance motorsport, engineering consultancy and other engineering sectors."));

        p.addView(space(24));
        LinearLayout footer = card(false);
        footer.addView(kicker("STUDENT BUILT. RACE DRIVEN."));
        footer.addView(space(6));
        footer.addView(t("Warangal, Telangana, India", 13, MUTED, false));
        p.addView(footer);

        return scroller(p);
    }

    private View screenCars() {
        LinearLayout p = page();

        p.addView(kicker("THE GARAGE"));
        p.addView(space(8));
        p.addView(heading("ONE TEAM.\nEVERY DETAIL."));
        p.addView(space(16));

        LinearLayout featured = card(true);
        TextView badge = t("CURRENT CAR", 9, RED, true);
        badge.setLetterSpacing(0.2f);
        featured.addView(badge);
        featured.addView(space(10));
        featured.addView(t("FR 26", 30, INK, true));
        featured.addView(space(8));
        featured.addView(t("Formula Student combustion prototype", 12, MUTED, false));
        featured.addView(space(14));
        featured.addView(paragraph("Designed from a blank sheet by the team, validated in simulation, then fabricated in the KITS Warangal workshop."));
        p.addView(featured);

        p.addView(space(26));
        p.addView(eyebrow("HOW A CAR GETS BUILT"));
        p.addView(space(12));

        p.addView(step("01", "DESIGN", "CAD models, packaging studies and load cases for every subsystem."));
        p.addView(step("02", "ANALYSE", "Structural, thermal and aerodynamic validation before a single cut."));
        p.addView(step("03", "MANUFACTURE", "Chassis jig, tube notching, welding, composites and wiring in-house."));
        p.addView(step("04", "TEST", "Assembly, driver-in-loop checks and track shakedowns before competition."));
        p.addView(step("05", "RACE", "National Formula Student events, scrutineering and dynamic disciplines."));

        p.addView(space(24));
        LinearLayout note = card(false);
        note.addView(paragraph("Every chassis carries the thinking, making and persistence of the students behind it."));
        p.addView(note);

        return scroller(p);
    }

    private View screenTeam() {
        LinearLayout p = page();

        p.addView(kicker("THE CREW"));
        p.addView(space(8));
        p.addView(heading("WE DON'T JUST\nSTUDY IT."));
        p.addView(space(16));
        p.addView(paragraph("We are the Formula Student team of Kakatiya Institute of Technology & Science, Warangal. Engineers, designers and managers working as one unit."));
        p.addView(space(22));

        p.addView(eyebrow("SUBSYSTEMS"));
        p.addView(space(12));
        p.addView(subsystem("AERODYNAMICS", "Aero package, CFD and bodywork composites."));
        p.addView(subsystem("CHASSIS", "Space frame design, FEA and fabrication."));
        p.addView(subsystem("POWERTRAIN", "Engine integration, intake, exhaust and drivetrain."));
        p.addView(subsystem("SUSPENSION & STEERING", "Geometry, damping, uprights and steering feel."));
        p.addView(subsystem("ELECTRONICS & DAQ", "Wiring harness, sensors, telemetry and logging."));
        p.addView(subsystem("BUSINESS & MEDIA", "Sponsorship, costing, branding and outreach."));

        p.addView(space(24));
        LinearLayout recruit = card(true);
        TextView tag = t("RECRUITMENT", 9, RED, true);
        tag.setLetterSpacing(0.2f);
        recruit.addView(tag);
        recruit.addView(space(10));
        recruit.addView(t("BE PART OF\nTHE FORCE.", 22, INK, true));
        recruit.addView(space(10));
        recruit.addView(paragraph("Recruitment opens every academic year for first and second year students across all subsystems. No prior experience required — only commitment."));
        recruit.addView(space(14));
        recruit.addView(button("TALK TO US", true, new Runnable() {
            @Override
            public void run() {
                show(4);
            }
        }));
        p.addView(recruit);

        return scroller(p);
    }

    private View screenSupport() {
        LinearLayout p = page();

        p.addView(kicker("SUPPORT"));
        p.addView(space(8));
        p.addView(heading("FUEL THE\nNEXT CAR."));
        p.addView(space(16));
        p.addView(paragraph("Force Racing runs on partnerships. Sponsors put their name on a car designed and built by students, and get access to a pipeline of industry-ready engineers."));
        p.addView(space(22));

        p.addView(tier("TITLE", "Naming on the car, team kit and all team communication.", GOLD));
        p.addView(tier("GOLD", "Logo on the car bodywork, team apparel and event stand.", GOLD));
        p.addView(tier("SILVER", "Logo on the car and on the team website.", SILVER));
        p.addView(tier("BRONZE", "Logo on the team website and event material.", SILVER));

        p.addView(space(22));
        p.addView(button("BECOME A SPONSOR", true, new Runnable() {
            @Override
            public void run() {
                show(4);
            }
        }));
        p.addView(space(20));
        LinearLayout note = card(false);
        note.addView(kicker("WHERE IT GOES"));
        note.addView(space(8));
        note.addView(paragraph("Materials, machining, tyres, safety equipment, travel and event entry fees — every rupee goes back into the car."));
        p.addView(note);

        return scroller(p);
    }

    private View screenContact() {
        LinearLayout p = page();

        p.addView(kicker("CONTACT"));
        p.addView(space(8));
        p.addView(heading("GET IN\nTOUCH."));
        p.addView(space(16));
        p.addView(paragraph("Sponsorship, recruitment or collaboration — reach the team directly."));
        p.addView(space(22));

        p.addView(link("INSTAGRAM", "@force.racing", IG));
        p.addView(link("LINKEDIN", "Force Racing", LI));
        p.addView(link("YOUTUBE", "@forceracing8847", YT));
        p.addView(link("FACEBOOK", "forceracingkitsw", FB));
        p.addView(link("LOCATION", "KITS Warangal", MAPS));

        p.addView(space(22));
        LinearLayout where = card(false);
        where.addView(kicker("THE WORKSHOP"));
        where.addView(space(8));
        where.addView(t("Force Racing", 15, INK, true));
        where.addView(space(4));
        where.addView(t("Kakatiya Institute of Technology & Science\nWarangal, Telangana, India", 13, MUTED, false));
        p.addView(where);

        p.addView(space(24));
        TextView version = t("FORCE RACING APP · VERSION 3.0", 9, 0xFF4A4F58, false);
        version.setLetterSpacing(0.16f);
        p.addView(version);

        return scroller(p);
    }

    // ---------------------------------------------------------------- pieces

    private View heroLine(String word) {
        SpannableStringBuilder sb = new SpannableStringBuilder(word + ".");
        sb.setSpan(new ForegroundColorSpan(RED), word.length(), word.length() + 1,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        TextView tv = new TextView(this);
        tv.setText(sb);
        tv.setTextColor(INK);
        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 40);
        tv.setTypeface(Typeface.DEFAULT_BOLD);
        tv.setIncludeFontPadding(false);
        tv.setLineSpacing(0f, 0.92f);
        return tv;
    }

    private TextView heading(String text) {
        TextView tv = t(text, 25, INK, true);
        tv.setIncludeFontPadding(false);
        tv.setLineSpacing(dp(2), 1f);
        return tv;
    }

    private TextView paragraph(String text) {
        TextView tv = t(text, 13.5f, 0xFFB9BEC7, false);
        tv.setLineSpacing(dp(5), 1f);
        return tv;
    }

    private TextView kicker(String text) {
        TextView tv = t(text, 9, MUTED, true);
        tv.setLetterSpacing(0.2f);
        return tv;
    }

    private TextView eyebrow(String text) {
        TextView tv = t(text, 13, INK, true);
        tv.setLetterSpacing(0.02f);
        return tv;
    }

    private View stat(String value, String label) {
        LinearLayout box = new LinearLayout(this);
        box.setOrientation(LinearLayout.VERTICAL);
        box.setBackground(drawable(14, CARD, LINE));
        box.setPadding(dp(12), dp(12), dp(12), dp(12));
        TextView v = t(value, 17, INK, true);
        v.setIncludeFontPadding(false);
        box.addView(v);
        box.addView(space(5));
        TextView l = t(label, 8, MUTED, true);
        l.setLetterSpacing(0.14f);
        box.addView(l);
        return box;
    }

    private View step(String number, String title, String body) {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setPadding(0, 0, 0, dp(16));

        TextView n = t(number, 11, RED, true);
        n.setLetterSpacing(0.1f);
        LinearLayout.LayoutParams nParams = new LinearLayout.LayoutParams(dp(38),
                ViewGroup.LayoutParams.WRAP_CONTENT);
        row.addView(n, nParams);

        LinearLayout copy = new LinearLayout(this);
        copy.setOrientation(LinearLayout.VERTICAL);
        TextView titleView = t(title, 12, INK, true);
        titleView.setLetterSpacing(0.14f);
        copy.addView(titleView);
        copy.addView(space(5));
        copy.addView(paragraph(body));
        row.addView(copy, new LinearLayout.LayoutParams(0,
                ViewGroup.LayoutParams.WRAP_CONTENT, 1f));

        return row;
    }

    private View subsystem(String title, String body) {
        LinearLayout box = card(false);
        TextView tv = t(title, 12, INK, true);
        tv.setLetterSpacing(0.1f);
        box.addView(tv);
        box.addView(space(6));
        box.addView(paragraph(body));
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.bottomMargin = dp(8);
        box.setLayoutParams(params);
        return box;
    }

    private View tier(String name, String body, int accent) {
        LinearLayout box = new LinearLayout(this);
        box.setOrientation(LinearLayout.VERTICAL);
        GradientDrawable bg = new GradientDrawable();
        bg.setColor(CARD);
        bg.setCornerRadius(dp(14));
        bg.setStroke(dp(1), accent);
        box.setBackground(bg);
        box.setPadding(dp(16), dp(14), dp(16), dp(14));

        TextView n = t(name, 12, accent, true);
        n.setLetterSpacing(0.22f);
        box.addView(n);
        box.addView(space(8));
        box.addView(paragraph(body));

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.bottomMargin = dp(10);
        box.setLayoutParams(params);
        return box;
    }

    private View link(final String label, final String value, final String url) {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER_VERTICAL);
        row.setBackground(drawable(14, CARD, LINE));
        row.setPadding(dp(16), dp(15), dp(16), dp(15));
        row.setClickable(true);
        row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                open(url);
            }
        });

        LinearLayout copy = new LinearLayout(this);
        copy.setOrientation(LinearLayout.VERTICAL);
        TextView l = t(label, 9, MUTED, true);
        l.setLetterSpacing(0.2f);
        copy.addView(l);
        copy.addView(space(4));
        copy.addView(t(value, 13, INK, true));
        row.addView(copy, new LinearLayout.LayoutParams(0,
                ViewGroup.LayoutParams.WRAP_CONTENT, 1f));

        TextView arrow = t("â†’", 16, RED, true);
        row.addView(arrow);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.bottomMargin = dp(8);
        row.setLayoutParams(params);
        return row;
    }

    private LinearLayout card(boolean accent) {
        LinearLayout box = new LinearLayout(this);
        box.setOrientation(LinearLayout.VERTICAL);
        GradientDrawable bg = new GradientDrawable();
        bg.setColor(CARD);
        bg.setCornerRadius(dp(16));
        bg.setStroke(dp(1), accent ? RED : LINE);
        box.setBackground(bg);
        box.setPadding(dp(18), dp(16), dp(18), dp(18));
        return box;
    }

    private View button(String text, boolean primary, final Runnable action) {
        TextView tv = new TextView(this);
        tv.setText(text);
        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 11);
        tv.setTypeface(Typeface.DEFAULT_BOLD);
        tv.setLetterSpacing(0.14f);
        tv.setGravity(Gravity.CENTER);
        tv.setPadding(dp(8), dp(15), dp(8), dp(15));
        if (primary) {
            tv.setTextColor(Color.WHITE);
            GradientDrawable bg = new GradientDrawable();
            bg.setColor(RED);
            bg.setCornerRadius(dp(10));
            tv.setBackground(bg);
        } else {
            tv.setTextColor(INK);
            tv.setBackground(drawable(10, 0x00000000, LINE));
        }
        tv.setClickable(true);
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                action.run();
            }
        });
        return tv;
    }

    private GradientDrawable drawable(int radiusDp, int fill, int stroke) {
        GradientDrawable bg = new GradientDrawable();
        bg.setColor(fill);
        bg.setCornerRadius(dp(radiusDp));
        bg.setStroke(dp(1), stroke);
        return bg;
    }

    private TextView t(String text, float sizeSp, int color, boolean bold) {
        TextView tv = new TextView(this);
        tv.setText(text);
        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, sizeSp);
        tv.setTextColor(color);
        if (bold) {
            tv.setTypeface(Typeface.DEFAULT_BOLD);
        }
        return tv;
    }

    private View space(int dp) {
        View v = new View(this);
        v.setLayoutParams(new LinearLayout.LayoutParams(1, dp(dp)));
        return v;
    }

    private View spaceH(int dp) {
        View v = new View(this);
        v.setLayoutParams(new LinearLayout.LayoutParams(dp(dp), 1));
        return v;
    }

    private LinearLayout.LayoutParams weight() {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
        params.rightMargin = dp(4);
        params.leftMargin = dp(4);
        return params;
    }

    private int dp(float value) {
        return Math.round(value * getResources().getDisplayMetrics().density);
    }

    private void open(String url) {
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
        } catch (ActivityNotFoundException ignored) {
            // no browser installed
        }
    }

    // ---------------------------------------------------------------- splash

    private void addSplash() {
        final LinearLayout splash = new LinearLayout(this);
        splash.setOrientation(LinearLayout.VERTICAL);
        splash.setGravity(Gravity.CENTER);
        splash.setBackgroundColor(BG);

        ImageView logo = new ImageView(this);
        logo.setImageResource(R.drawable.logo);
        splash.addView(logo, new LinearLayout.LayoutParams(dp(140), dp(140)));

        TextView name = t("FORCE RACING", 21, INK, true);
        name.setLetterSpacing(0.16f);
        name.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams nameParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        nameParams.topMargin = dp(24);
        splash.addView(name, nameParams);

        View rule = new View(this);
        rule.setBackgroundColor(RED);
        LinearLayout.LayoutParams ruleParams = new LinearLayout.LayoutParams(dp(44), dp(3));
        ruleParams.topMargin = dp(14);
        splash.addView(rule, ruleParams);

        TextView motto = t("DREAM.  BUILD.  RACE.", 10, MUTED, true);
        motto.setLetterSpacing(0.22f);
        LinearLayout.LayoutParams mottoParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mottoParams.topMargin = dp(20);
        splash.addView(motto, mottoParams);

        root.addView(splash, new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        splash.postDelayed(new Runnable() {
            @Override
            public void run() {
                splash.animate().alpha(0f).setDuration(260).withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        root.removeView(splash);
                    }
                }).start();
            }
        }, 900);
    }
}

