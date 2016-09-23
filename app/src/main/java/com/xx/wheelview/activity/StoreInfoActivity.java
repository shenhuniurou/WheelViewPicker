package com.zj.wheelview.activity;

import android.content.Intent;
import android.webkit.WebView;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.xx.wheelview.activity.BaseActivity;
import com.zj.wheelview.NetApi;
import com.zj.wheelview.R;
import com.zj.wheelview.utils.CommonUtil;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.InjectView;
import butterknife.OnClick;
import cz.msebera.android.httpclient.Header;

public class StoreInfoActivity extends BaseActivity {

    @InjectView(R.id.webview)
    WebView webview;
    String contents;

    @Override
    protected boolean hasBackButton() {
        return true;
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_store_info;
    }

    @Override
    protected int getTitleResId() {
        return R.string.store_info;
    }

    @Override
    protected void initViews() {
        getContentsList();
    }

    @OnClick(R.id.tvEditStore)
    public void editStore() {
        startActivityForResult(new Intent(StoreInfoActivity.this, com.zj.wheelview.activity.EditStoreActivity.class), 10);
    }

    public void getContentsList() {
        showWaitDialog();
        NetApi.getContentsList(StoreInfoActivity.this, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                hideWaitDialog();
                try {
                    if (responseBody != null) {
                        String result = new String(responseBody);
                        LogUtil.log("result----" + result);
                        JSONObject obj = new JSONObject(result);
                        String resultCode = obj.getString("resultCode");
                        String msg = obj.getString("msg");
                        if ("00".equals(resultCode)) {
                            contents = obj.getString("contents");
                            webview.loadDataWithBaseURL(null, contents, "text/html", "utf-8", null);
                        } else {
                            CommonUtil.showToastMessage(StoreInfoActivity.this, msg);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                hideWaitDialog();
                if (responseBody != null) {
                    LogUtil.log("responseBody----" + new String(responseBody));
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 10 && resultCode == RESULT_OK) {
            getContentsList();
        }
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_OK, new Intent().putExtra("contents", contents));
        super.onBackPressed();
    }
}
