package com.zj.wheelview.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.zj.wheelview.NetApi;
import com.zj.wheelview.R;
import com.zj.wheelview.utils.CommonUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import butterknife.InjectView;
import butterknife.OnClick;
import cz.msebera.android.httpclient.Header;

public class MerchantLoanActivity extends BaseActivity {

    int loan_status = -1;
    String loan_amt;
    @InjectView(R.id.llApplyPage)
    LinearLayout llApplyPage;
    @InjectView(R.id.tvLoanLimit)
    TextView tvLoanLimit;
    @InjectView(R.id.tvLoanSettle)
    TextView tvLoanSettle;
    @InjectView(R.id.etLoan)
    EditText etLoan;
    @InjectView(R.id.tvSubmitLoan)
    TextView tvSubmitLoan;

    @Override
    protected boolean hasBackButton() {
        return true;
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_merchant_loan;
    }

    @Override
    protected int getTitleResId() {
        return R.string.title_activity_merchant_loan;
    }

    @Override
    protected void initViews() {
        loan_amt = getIntent().getStringExtra("loan_amt");
        tvLoanLimit.setText(loan_amt);
        //获取商家最近三个月的收益
        getMerchantSettleAmt();
    }

    public void getMerchantSettleAmt() {
        //获取三个月前的那一天
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -90);
        String  begin_settle_date = new SimpleDateFormat("yyyyMMdd").format(calendar.getTime());
        LogUtil.log("begin_settle_date----" + begin_settle_date);
        //获取前一天
        calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -1);
        String end_settle_date = new SimpleDateFormat("yyyyMMdd").format(calendar.getTime());
        LogUtil.log("end_settle_date----" + end_settle_date);
        NetApi.getMerchantSettleAmt(MerchantLoanActivity.this, begin_settle_date, end_settle_date, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    if (responseBody != null) {
                        String result = new String(responseBody);
                        LogUtil.log("result----" + result);
                        JSONObject obj = new JSONObject(result);
                        String resultCode = obj.getString("resultCode");
                        String msg = obj.getString("msg");
                        if ("00".equals(resultCode)) {
                            String settle_amt = obj.getString("settle_amt");
                            java.text.DecimalFormat df = new java.text.DecimalFormat("0.00");
                            tvLoanSettle.setText(settle_amt.equals("null") ? "0.00" : df.format(Double.parseDouble(settle_amt)));
                        }else {
                            CommonUtil.showToastMessage(MerchantLoanActivity.this, msg);
                        }
                    }
                }catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                if (responseBody != null) {
                    String result = new String(responseBody);
                    LogUtil.log("result----" + result);
                }
            }
        });
    }

    @OnClick(R.id.tvSubmitLoan)
    public void submitLoan() {
        CommonUtil.hideSoftKeybord(this);
        //弹确认框
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("确认提交？");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                doApplyMerchantLoan();
            }
        });
        builder.setNegativeButton("取消", null);
        builder.show();
    }

    public void doApplyMerchantLoan() {
        String loan_amount = etLoan.getText().toString().trim();
        if (CommonUtil.isEmpty(loan_amount)) {
            CommonUtil.showToastMessage(MerchantLoanActivity.this, "请输入贷款金额");
            return;
        }
        if (Integer.parseInt(loan_amount) > Float.parseFloat(loan_amt)) {
            CommonUtil.showToastMessage(MerchantLoanActivity.this, "输入的金额超出了贷款额度");
            return;
        }else if (Integer.parseInt(loan_amount) == 0) {
            CommonUtil.showToastMessage(MerchantLoanActivity.this, "请输入大于0的金额");
            return;
        }
        showWaitDialog("正在提交...");
        NetApi.doApplyMerchantLoan(MerchantLoanActivity.this, loan_amount, new AsyncHttpResponseHandler() {
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
                            //CommonUtil.showToastMessage(MerchantLoanActivity.this, "已提交申请");
                            startActivityForResult(new Intent(MerchantLoanActivity.this, LoanSuccessfulActivity.class), 1);
                        }else {
                            CommonUtil.showToastMessage(MerchantLoanActivity.this, msg);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1 && resultCode == RESULT_OK) {
            setResult(RESULT_OK, new Intent().putExtra("loan_status", 1));
            finish();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
