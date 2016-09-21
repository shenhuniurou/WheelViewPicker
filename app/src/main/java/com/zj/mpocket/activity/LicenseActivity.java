package com.zj.mpocket.activity;

import android.webkit.WebSettings;
import android.webkit.WebView;

import com.zj.mpocket.R;

import butterknife.InjectView;

public class LicenseActivity extends BaseActivity {

    @InjectView(R.id.webview)
    WebView webView;

    @Override
    protected boolean hasBackButton() {
        return true;
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_license;
    }

    @Override
    protected int getTitleResId() {
        return R.string.title_activity_license;
    }

    @Override
    protected void initViews() {
        WebSettings set = webView.getSettings();
        // 设置可以支持缩放
        set.setSupportZoom(true);
        // 设置出现缩放工具
        set.setBuiltInZoomControls(false);
        set.setDefaultTextEncodingName("utf-8");
        webView.loadUrl("file:///android_asset/licensing.html");
    }

}
