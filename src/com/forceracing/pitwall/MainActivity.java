package com.forceracing.pitwall;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
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
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Force Racing - branded native shell around the team site.
 *
 * Own launcher icon, own splash screen, own offline screen and back handling,
 * so the app reads as an app and not as a browser tab. Nothing is parsed from
 * a config file at startup, which is what used to crash the old wrapper.
 */
public class MainActivity extends Activity {

    private static final String HOME = "https://force-racing.force-racing-revival.workers.dev/";
    private static final int BG = 0xFF0B0B0D;
    private static final int RED = 0xFFFB0102;

    private WebView web;
    private View splash;
    private View errorPanel;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle saved) {
        super.onCreate(saved);

        FrameLayout root = new FrameLayout(this);
        root.setBackgroundColor(BG);

        web = new WebView(this);
        web.setBackgroundColor(BG);
        root.addView(web, new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        WebSettings s = web.getSettings();
        s.setJavaScriptEnabled(true);
        s.setDomStorageEnabled(true);
        s.setDatabaseEnabled(true);
        s.setLoadWithOverviewMode(true);
        s.setUseWideViewPort(true);
        s.setSupportZoom(false);
        s.setMediaPlaybackRequiresUserGesture(false);
        s.setCacheMode(WebSettings.LOAD_DEFAULT);
        s.setUserAgentString(s.getUserAgentString() + " ForceRacingApp/2.0");

        web.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                Uri uri = request.getUrl();
                String scheme = uri.getScheme() == null ? "" : uri.getScheme();
                if (scheme.startsWith("http")) {
                    String host = uri.getHost() == null ? "" : uri.getHost();
                    if (host.contains("force-racing") || host.contains("workers.dev")
                            || host.contains("forceracing")) {
                        return false; // stay inside the app
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
                    showError();
                }
            }
        });

        errorPanel = buildErrorPanel();
        errorPanel.setVisibility(View.GONE);
        root.addView(errorPanel, new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        splash = buildSplash();
        root.addView(splash, new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        setContentView(root);

        // Android 15+ is edge-to-edge: keep the page out from under the bars.
        root.setOnApplyWindowInsetsListener(new View.OnApplyWindowInsetsListener() {
            @Override
            public WindowInsets onApplyWindowInsets(View view, WindowInsets insets) {
                view.setPadding(0, insets.getSystemWindowInsetTop(), 0, 0);
                return insets;
            }
        });

        if (saved != null) {
            web.restoreState(saved);
            hideSplash();
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
        box.addView(logo, new LinearLayout.LayoutParams(dp(150), dp(150)));

        TextView name = new TextView(this);
        name.setText("FORCE RACING");
        name.setTextColor(Color.WHITE);
        name.setTypeface(Typeface.DEFAULT_BOLD);
        name.setLetterSpacing(0.12f);
        name.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
        name.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams nameParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        nameParams.topMargin = dp(26);
        box.addView(name, nameParams);

        View rule = new View(this);
        rule.setBackgroundColor(RED);
        LinearLayout.LayoutParams ruleParams = new LinearLayout.LayoutParams(dp(46), dp(3));
        ruleParams.topMargin = dp(14);
        box.addView(rule, ruleParams);

        TextView tag = new TextView(this);
        tag.setText("KAKATIYA INSTITUTE OF TECHNOLOGY & SCIENCE");
        tag.setTextColor(0xFF8A8F98);
        tag.setLetterSpacing(0.14f);
        tag.setTextSize(TypedValue.COMPLEX_UNIT_SP, 9);
        tag.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams tagParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        tagParams.topMargin = dp(12);
        box.addView(tag, tagParams);

        return box;
    }

    private View buildErrorPanel() {
        LinearLayout box = new LinearLayout(this);
        box.setOrientation(LinearLayout.VERTICAL);
        box.setGravity(Gravity.CENTER);
        box.setBackgroundColor(BG);
        box.setPadding(dp(32), dp(32), dp(32), dp(32));

        ImageView logo = new ImageView(this);
        logo.setImageResource(R.drawable.logo);
        logo.setAlpha(0.5f);
        box.addView(logo, new LinearLayout.LayoutParams(dp(84), dp(84)));

        TextView title = new TextView(this);
        title.setText("NO CONNECTION");
        title.setTextColor(Color.WHITE);
        title.setTypeface(Typeface.DEFAULT_BOLD);
        title.setLetterSpacing(0.1f);
        title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        title.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams tParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        tParams.topMargin = dp(22);
        box.addView(title, tParams);

        TextView msg = new TextView(this);
        msg.setText("Force Racing needs a network connection to load the latest data.");
        msg.setTextColor(0xFF8A8F98);
        msg.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
        msg.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams mParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mParams.topMargin = dp(10);
        box.addView(msg, mParams);

        Button retry = new Button(this);
        retry.setText("RETRY");
        retry.setTextColor(Color.WHITE);
        retry.setBackgroundColor(RED);
        retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                errorPanel.setVisibility(View.GONE);
                web.loadUrl(HOME);
            }
        });
        LinearLayout.LayoutParams rParams = new LinearLayout.LayoutParams(dp(150), dp(46));
        rParams.topMargin = dp(24);
        box.addView(retry, rParams);

        return box;
    }

    private void showError() {
        errorPanel.setVisibility(View.VISIBLE);
        errorPanel.setAlpha(0f);
        errorPanel.animate().alpha(1f).setDuration(180).start();
    }

    private void hideSplash() {
        if (splash == null || splash.getVisibility() != View.VISIBLE) {
            return;
        }
        long elapsed = android.os.SystemClock.uptimeMillis() - startedAt;
        long wait = Math.max(0, 1150 - elapsed);
        splash.postDelayed(new Runnable() {
            @Override
            public void run() {
                splash.animate().alpha(0f).setDuration(220).withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        splash.setVisibility(View.GONE);
                    }
                }).start();
            }
        }, wait);
    }

    private int dp(int value) {
        return Math.round(value * getResources().getDisplayMetrics().density);
    }

    private void openOutside(Uri uri) {
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, uri));
        } catch (ActivityNotFoundException ignored) {
            // Nothing can handle it; stay where we are.
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle out) {
        super.onSaveInstanceState(out);
        web.saveState(out);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && web != null) {
            if (splash != null && splash.getVisibility() == View.VISIBLE) {
                return true;
            }
            if (errorPanel != null && errorPanel.getVisibility() == View.VISIBLE) {
                return super.onKeyDown(keyCode, event);
            }
            if (web.canGoBack()) {
                web.goBack();
                return true;
            }
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
