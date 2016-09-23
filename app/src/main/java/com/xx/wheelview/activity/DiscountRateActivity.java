package com.zj.wheelview.activity;

import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.zj.wheelview.NetApi;
import com.zj.wheelview.R;
import com.zj.wheelview.utils.CommonUtil;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.InjectView;
import butterknife.OnClick;
import cz.msebera.android.httpclient.Header;

public class DiscountRateActivity extends BaseActivity {

    @InjectView(R.id.etDiscount)
    EditText etDiscount;

    @Override
    protected boolean hasBackButton() {
        return true;
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_discount_rate;
    }

    @Override
    protected int getTitleResId() {
        return R.string.title_activity_discount_rate;
    }

    @Override
    protected void initViews() {
        String undiscount = getIntent().getStringExtra("undiscount");
        etDiscount.setHint(undiscount);
        etDiscount.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().contains(".")) {
                    if (s.length() - 1 - s.toString().indexOf(".") > 2) {
                        s = s.toString().subSequence(0, s.toString().indexOf(".") + 3);
                        etDiscount.setText(s);
                        etDiscount.setSelection(s.length());
                    }
                }else {
                    if (s.length() > 2) {
                        s = s.toString().subSequence(0, 2);
                        etDiscount.setText(s);
                        etDiscount.setSelection(s.length());
                    }
                }
                if (s.toString().trim().substring(0).equals(".")) {
                    s = "0" + s;
                    etDiscount.setText(s);
                    etDiscount.setSelection(2);
                }

                if (s.toString().startsWith("0") && s.toString().trim().length() > 1) {
                    if (!s.toString().substring(1, 2).equals(".")) {
                        etDiscount.setText(s.subSequence(0, 1));
                        etDiscount.setSelection(1);
                        return;
                    }
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,int after) {}

            @Override
            public void afterTextChanged(Editable s) {}

        });
    }

    @OnClick(R.id.tvModifyRate)
    public void modifyRate() {
        CommonUtil.hideSoftKeybord(this);
        String undiscountStr = etDiscount.getText().toString().trim();
        if (CommonUtil.isEmpty(undiscountStr)) {
            CommonUtil.showToastMessage(DiscountRateActivity.this, "请输入折扣");
            return;
        }
        final float undiscount = Float.valueOf(undiscountStr);
        if (undiscount > 10) {
            CommonUtil.showToastMessage(DiscountRateActivity.this, getString(R.string.error_discount_format));
            return;
        }
        showWaitDialog();
        NetApi.setUnDiscount(DiscountRateActivity.this, undiscount, new AsyncHttpResponseHandler() {
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
                            CommonUtil.showToastMessage(DiscountRateActivity.this, "修改成功");
                            setResult(RESULT_OK, new Intent().putExtra("undiscount", String.valueOf(undiscount)));
                            finish();
                        }else {
                            CommonUtil.showToastMessage(DiscountRateActivity.this, msg);
                        }
                    }
                }catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                hideWaitDialog();
                if (responseBody != null) {
                    String result = new String(responseBody);
                    LogUtil.log("result----" + result);
                }
            }
        });
    }

}
