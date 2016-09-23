package com.zj.wheelview.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.zj.wheelview.NetApi;
import com.zj.wheelview.R;
import com.zj.wheelview.utils.CommonUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.InjectView;
import butterknife.OnClick;
import cz.msebera.android.httpclient.Header;

public class OrderDetailActivity extends BaseActivity {

    @InjectView(R.id.tvRedpacketAmt)
    TextView tvRedpacketAmt;
    @InjectView(R.id.tvKoudaiAmt)
    TextView tvKoudaiAmt;
    @InjectView(R.id.tvOrderAmt)
    TextView tvOrderAmt;
    @InjectView(R.id.tvRealOrderAmt)
    TextView tvRealOrderAmt;
    @InjectView(R.id.tvPayCode)
    TextView tvPayCode;
    @InjectView(R.id.tvOrderID)
    TextView tvOrderID;
    @InjectView(R.id.tvOrderDate)
    TextView tvOrderDate;
    @InjectView(R.id.tvOrderStatus)
    TextView tvOrderStatus;
    @InjectView(R.id.rlRetunPay)
    RelativeLayout rlRetunPay;

    OrderModel orderModel;

    @Override
    protected boolean hasBackButton() {
        return true;
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_order_detail;
    }

    @Override
    protected int getTitleResId() {
        return R.string.title_activity_order_detail;
    }

    @Override
    protected void initViews() {
        orderModel = (OrderModel) getIntent().getSerializableExtra("orderModel");
        Date orderDate = null;
        try {
            orderDate = new SimpleDateFormat("yyyyMMddHHmmss").parse(orderModel.getOrder_date());
            String dateStr = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(orderDate);
            tvOrderDate.setText("消费时间：" + dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String status = orderModel.getOrder_status();
        /*if (("1".equals(status) || "3".equals(status)) && CommonUtil.isSameDate(orderDate, new Date())) {
            rlRetunPay.setVisibility(View.VISIBLE);
        }*/
        switch (Integer.parseInt(status)) {
            case 0:status = "未支付";break;
            case 1:status = "已经支付";break;
            case 2:status = "已取消";break;
            case 3:status = "确认收货";break;
            case 5:status = "已退款";break;
            case 6:status = "待退款";break;
        }
        tvOrderStatus.setText(status);
        String favorableStr = "已为您的客户节省" + "<font color=\"#FB9629\">￥" + orderModel.getFavorable_amt() + "元</font>";
        tvRedpacketAmt.setText(Html.fromHtml(favorableStr));
        tvKoudaiAmt.setText(Html.fromHtml("口袋优惠：<font color=\"#FB9629\">" + orderModel.getFavorable_amt() + "元</font>"));
        tvOrderAmt.setText(Html.fromHtml("收款金额：<font color=\"#FB9629\">" + orderModel.getOrder_amt() + "元</font>"));
        String realPayStr = "客户支付：<font color=\"#FB9629\">" + orderModel.getReal_order_amt() + "元</font>";
        tvRealOrderAmt.setText(Html.fromHtml(realPayStr));
        tvPayCode.setText("支  付  码：" + (CommonUtil.isEmpty(orderModel.getPay_code())?"":orderModel.getPay_code()));
        tvOrderID.setText("消费编号：" + orderModel.getIds());

    }

    @OnClick(R.id.btnRetunPay)
    public void returnPay() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("确认退款？");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                doReturnPay();
            }
        });
        builder.setNegativeButton("取消", null);
        builder.show();
    }

    public void doReturnPay() {
        showWaitDialog();
        NetApi.retunPay(OrderDetailActivity.this, orderModel.getIds(),  new AsyncHttpResponseHandler() {
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
                            orderModel.setOrder_status("6");
                            CommonUtil.showToastMessage(OrderDetailActivity.this, "退款申请成功，等待审核...");
                            //更新UI为待退款
                            tvOrderStatus.setText("待退款");
                            rlRetunPay.setVisibility(View.GONE);
                        }else {
                            CommonUtil.showToastMessage(OrderDetailActivity.this, msg);
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
    public void onBackPressed() {
        setResult(RESULT_OK, new Intent().putExtra("orderModel", orderModel));
        super.onBackPressed();
    }
}
