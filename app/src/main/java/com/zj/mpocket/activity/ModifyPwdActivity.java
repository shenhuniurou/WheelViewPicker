package com.zj.mpocket.activity;

import android.content.Context;
import android.text.TextUtils;
import android.widget.EditText;

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

public class ModifyPwdActivity extends BaseActivity {

    @InjectView(R.id.etOldPwd)
    EditText etOldPwd;
    @InjectView(R.id.etNewPwd)
    EditText etNewPwd;
    @InjectView(R.id.etConfirmPwd)
    EditText etConfirmPwd;

    @Override
    protected boolean hasBackButton() {
        return true;
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_modify_pwd;
    }

    @Override
    protected int getTitleResId() {
        return R.string.title_activity_modify_pwd;
    }

    @Override
    protected void initViews() {

    }

    @OnClick(R.id.tvSave)
    public void savePwd() {
        CommonUtil.hideSoftKeybord(this);
        //修改密码
        String oldPwd = etOldPwd.getText().toString().trim();
        final String newPwd = etNewPwd.getText().toString().trim();
        String confirmPwd = etConfirmPwd.getText().toString().trim();
        if (TextUtils.isEmpty(oldPwd)) {
            CommonUtil.showToastMessage(ModifyPwdActivity.this, getString(R.string.input_old_pwd));
            return;
        }
        if (TextUtils.isEmpty(newPwd)) {
            CommonUtil.showToastMessage(ModifyPwdActivity.this, getString(R.string.input_new_pwd));
            return;
        }
        if (TextUtils.isEmpty(confirmPwd)) {
            CommonUtil.showToastMessage(ModifyPwdActivity.this, getString(R.string.input_new_pwd_again));
            return;
        }
        if (!TextUtils.equals(newPwd, confirmPwd)) {
            CommonUtil.showToastMessage(ModifyPwdActivity.this, getString(R.string.different_pwd));
            return;
        }
        showWaitDialog();
        NetApi.updatePwd(ModifyPwdActivity.this, oldPwd, newPwd, new AsyncHttpResponseHandler() {

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
                            //修改成功
                            CommonUtil.showToastMessage(ModifyPwdActivity.this, getString(R.string.modify_successful));
                            PreferenceUtil.setPrefString(ModifyPwdActivity.this, Constant.USER_INFO, Context.MODE_PRIVATE, Constant.MERCHANT_PWD, newPwd);
                            finish();
                        }else {
                            //修改失败
                            CommonUtil.showToastMessage(ModifyPwdActivity.this, msg);
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
