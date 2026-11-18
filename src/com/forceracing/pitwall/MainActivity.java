package com.forceracing.pitwall;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowInsets;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Force Racing - the team app.
 *
 * Opens with the Force Racing splash, then the app content, with the device
 * back button wired into the app. No browser chrome, no address bar.
 */
public class MainActivity extends Activity {

    private static final String HOME = "https://force-racing.force-racing-revival.workers.dev/";
    private static final int BG = 0xFF0B0B0D;
    private static final int RED = 0xFFFB0102;
    private static final int MUTED = 0xFF8A8F98;

    private WebView web;
    private FrameLayout root;
    private View splash;
    private View offline;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle saved) {
        super.onCreate(saved);

        root = new FrameLayout(this);
        root.setBackgroundColor(BG);

        web = new WebView(this);
        web.setBackgroundColor(BG);

        WebSettings s = web.getSettings();
        s.setJavaScriptEnabled(true);
        s.setDomStorageEnabled(true);
        s.setDatabaseEnabled(true);
        s.setLoadWithOverviewMode(true);
        s.setUseWideViewPort(true);
        s.setSupportZoom(false);
        s.setMediaPlaybackRequiresUserGesture(false);
        s.setCacheMode(WebSettings.LOAD_DEFAULT);

        web.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                Uri uri = request.getUrl();
                String scheme = uri.getScheme() == null ? "" : uri.getScheme();
                if (scheme.startsWith("http")) {
                    String host = uri.getHost() == null ? "" : uri.getHost();
                    if (host.contains("force-racing") || host.contains("workers.dev")
                            || host.contains("forceracing")) {
                        return false;
                    }
                    openOutside(uri);
                    return true;
                }
                openOutside(uri);
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                hideSplash();
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                if (request.isForMainFrame()) {
                    hideSplash();
                    offline.setVisibility(View.VISIBLE);
                }
            }
        });

        root.addView(web, new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        offline = buildOffline();
        offline.setVisibility(View.GONE);
        root.addView(offline, new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        splash = buildSplash();
        root.addView(splash, new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        root.setOnApplyWindowInsetsListener(new View.OnApplyWindowInsetsListener() {
            @Override
            public WindowInsets onApplyWindowInsets(View view, WindowInsets insets) {
                view.setPadding(0, insets.getSystemWindowInsetTop(), 0, 0);
                return insets;
            }
        });

        setContentView(root);

        if (saved != null) {
            web.restoreState(saved);
            splash.setVisibility(View.GONE);
        } else {
            web.loadUrl(HOME);
        }
    }

    private View buildSplash() {
        LinearLayout box = new LinearLayout(this);
        box.setOrientation(LinearLayout.VERTICAL);
        box.setGravity(Gravity.CENTER);
        box.setBackgroundColor(BG);

        ImageView logo = new ImageView(this);
        logo.setImageResource(R.drawable.logo);
        box.addView(logo, new LinearLayout.LayoutParams(dp(140), dp(140)));

        TextView name = text("FORCE RACING", 21, Color.WHITE, true);
        name.setLetterSpacing(0.16f);
        name.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams nameParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        nameParams.topMargin = dp(24);
        box.addView(name, nameParams);

        View rule = new View(this);
        rule.setBackgroundColor(RED);
        LinearLayout.LayoutParams ruleParams = new LinearLayout.LayoutParams(dp(44), dp(3));
        ruleParams.topMargin = dp(14);
        box.addView(rule, ruleParams);

        TextView motto = text("DREAM.  BUILD.  RACE.", 10, MUTED, true);
        motto.setLetterSpacing(0.22f);
        LinearLayout.LayoutParams mottoParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mottoParams.topMargin = dp(20);
        box.addView(motto, mottoParams);

        return box;
    }

    private View buildOffline() {
        LinearLayout box = new LinearLayout(this);
        box.setOrientation(LinearLayout.VERTICAL);
        box.setGravity(Gravity.CENTER);
        box.setBackgroundColor(BG);
        box.setPadding(dp(32), dp(32), dp(32), dp(32));

        ImageView logo = new ImageView(this);
        logo.setImageResource(R.drawable.logo);
        logo.setAlpha(0.45f);
        box.addView(logo, new LinearLayout.LayoutParams(dp(84), dp(84)));

        TextView title = text("NO CONNECTION", 15, Color.WHITE, true);
        title.setLetterSpacing(0.1f);
        title.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams tp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        tp.topMargin = dp(22);
        box.addView(title, tp);

        TextView retry = text("RETRY", 11, Color.WHITE, true);
        retry.setLetterSpacing(0.16f);
        retry.setGravity(Gravity.CENTER);
        GradientDrawable bg = new GradientDrawable();
        bg.setColor(RED);
        bg.setCornerRadius(dp(10));
        retry.setBackground(bg);
        retry.setPadding(dp(30), dp(14), dp(30), dp(14));
        retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                offline.setVisibility(View.GONE);
                web.loadUrl(HOME);
            }
        });
        LinearLayout.LayoutParams rp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        rp.topMargin = dp(26);
        box.addView(retry, rp);

        return box;
    }

    private void hideSplash() {
        if (splash == null || splash.getVisibility() != View.VISIBLE) {
            return;
        }
        splash.postDelayed(new Runnable() {
            @Override
            public void run() {
                splash.animate().alpha(0f).setDuration(260).withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        splash.setVisibility(View.GONE);
                    }
                }).start();
            }
        }, 700);
    }

    private TextView text(String value, float sizeSp, int color, boolean bold) {
        TextView tv = new TextView(this);
        tv.setText(value);
        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, sizeSp);
        tv.setTextColor(color);
        if (bold) {
            tv.setTypeface(Typeface.DEFAULT_BOLD);
        }
        return tv;
    }

    private int dp(float value) {
        return Math.round(value * getResources().getDisplayMetrics().density);
    }

    private void openOutside(Uri uri) {
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, uri));
        } catch (ActivityNotFoundException ignored) {
            // nothing can open it
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle out) {
        super.onSaveInstanceState(out);
        web.saveState(out);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && web != null && web.canGoBack()) {
            web.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        if (web != null) {
            web.destroy();
        }
        super.onDestroy();
    }
}
