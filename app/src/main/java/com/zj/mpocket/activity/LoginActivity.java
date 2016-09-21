package com.zj.mpocket.activity;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.zj.mpocket.Constant;
import com.zj.mpocket.NetApi;
import com.zj.mpocket.R;
import com.zj.mpocket.utils.CommonUtil;
import com.zj.mpocket.utils.LogUtil;
import com.zj.mpocket.utils.PreferenceUtil;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.InjectView;
import butterknife.OnClick;

public class LoginActivity extends BaseActivity {

    @InjectView(R.id.tvLogin)
    TextView tvLogin;

    @InjectView(R.id.etLoginAccount)
    EditText etLoginAccount;

    @InjectView(R.id.etLoginPwd)
    EditText etLoginPwd;

    @Override
    protected boolean hasBackButton() {
        return true;
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_login;
    }

    @Override
    protected int getTitleResId() {
        return R.string.title_activity_login;
    }

    @Override
    protected void initViews() {
        
    }

    @OnClick(R.id.tvLogin)
    public void doLogin() {
        CommonUtil.hideSoftKeybord(this);
        //处理登录
        String loginAccount = etLoginAccount.getText().toString().trim();
        final String password = etLoginPwd.getText().toString().trim();
        if (TextUtils.isEmpty(loginAccount)) {
            CommonUtil.showToastMessage(LoginActivity.this, getString(R.string.hint_login_account));
            return;
        }
        if (TextUtils.isEmpty(password)) {
            CommonUtil.showToastMessage(LoginActivity.this, getString(R.string.hint_login_pwd));
            return;
        }

        showWaitDialog(R.string.logining);
        NetApi.login(loginAccount, password, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {
                hideWaitDialog();
                try {
                    if (responseBody != null) {
                        String result = new String(responseBody);
                        LogUtil.log("result----" + result);
                        JSONObject obj = new JSONObject(result);
                        String resultCode = obj.getString("resultCode");
                        String msg = obj.getString("msg");
                        if ("00".equals(resultCode)) {
                            //登录成功，保存用户信息
                            String accessToken = obj.getString("accessToken");//令牌
                            JSONObject loginInfo = new JSONObject(obj.getString("loginInfo"));
                            String ids = loginInfo.getString("ids");//主键
                            String merchantId = loginInfo.getString("merchant_id");//商户id
                            String account = loginInfo.getString("login_account");//账号
                            PreferenceUtil.setPrefString(LoginActivity.this, Constant.USER_INFO, Context.MODE_PRIVATE, Constant.TOKEN, accessToken);
                            PreferenceUtil.setPrefString(LoginActivity.this, Constant.USER_INFO, Context.MODE_PRIVATE, Constant.ids, ids);
                            PreferenceUtil.setPrefString(LoginActivity.this, Constant.USER_INFO, Context.MODE_PRIVATE, Constant.MERCHANT_Id, merchantId);
                            PreferenceUtil.setPrefString(LoginActivity.this, Constant.USER_INFO, Context.MODE_PRIVATE, Constant.MERCHANT_ACCOUNT, account);
                            PreferenceUtil.setPrefString(LoginActivity.this, Constant.USER_INFO, Context.MODE_PRIVATE, Constant.MERCHANT_PWD, password);
                            //跳转到商家管理页面
                            Intent intent = new Intent(LoginActivity.this, MerchantManageActivity.class);
                            //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finish();
                            //finish掉MainActivity
                            sendBroadcast(new Intent("finish"));
                        }else {
                            //登录失败
                            CommonUtil.showToastMessage(LoginActivity.this, msg);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error) {
                hideWaitDialog();
                if (responseBody != null) {
                    LogUtil.log("responseBody----" + new String(responseBody));
                }
            }

        });

    }
}
