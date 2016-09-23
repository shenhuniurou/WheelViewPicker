package com.zj.wheelview.activity;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.zj.wheelview.NetApi;
import com.zj.wheelview.R;
import com.zj.wheelview.adapter.SettleAdapter;
import com.zj.wheelview.utils.CommonUtil;
import com.zj.wheelview.view.DividerItemDecoration;
import com.zj.wheelview.view.LoadMoreRecyclerView;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.InjectView;
import butterknife.OnClick;
import cz.msebera.android.httpclient.Header;

public class MerchantComeInActivity extends BaseActivity implements LoadMoreRecyclerView.LoadMoreListener, SettleAdapter.OnRecyclerViewListener {

    @InjectView(R.id.rvComein)
    LoadMoreRecyclerView rvComein;
    @InjectView(R.id.tvYesterdaySettleAmt)
    TextView tvYesterdaySettleAmt;
    @InjectView(R.id.tvRedPackage)
    TextView tvRedPackage;
    SettleAdapter mAdapter;
    List<SettleModel> settleList = new ArrayList<>();
    int pageSize = 20;
    int pagenum = 1;
    boolean hasMore = false;

    @Override
    protected boolean hasBackButton() {
        return true;
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_merchant_come_in;
    }

    @Override
    protected int getTitleResId() {
        return R.string.title_activity_merchant_come_in;
    }

    @Override
    protected void initViews() {
        java.text.DecimalFormat df = new java.text.DecimalFormat("0.00");
        String yesterday_settle_amt = getIntent().getStringExtra("yesterday_settle_amt");
        tvYesterdaySettleAmt.setText(df.format(Double.parseDouble(yesterday_settle_amt)));
        //查询商户清算列表
        getMerchantSettleList(pagenum, pageSize);
        //获取红包账户信息
        getListRedpacketsForMerchant();
        rvComein.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(MerchantComeInActivity.this);
        rvComein.setLayoutManager(layoutManager);
        rvComein.setLoadMoreListener(this);
        rvComein.setAutoLoadMoreEnable(hasMore);
        mAdapter = new SettleAdapter(settleList);
        mAdapter.setOnRecyclerViewListener(this);
        rvComein.setAdapter(mAdapter);
        rvComein.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));
        rvComein.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    public void onItemClick(int position) {
        SettleModel settleModel = settleList.get(position);
        if (settleModel != null) {
            Intent intent = new Intent(MerchantComeInActivity.this, MerchantOrderListActivity.class);
            intent.putExtra("settle_date", settleModel.getSettle_date());
            startActivity(intent);
        }
    }

    @Override
    public void onLoadMore() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                pagenum++;
                getMerchantSettleList(pagenum, pageSize);
            }
        }, 1000);
    }

    /**
     * 查询商户清算列表
     */
    public void getMerchantSettleList(final int pagenum, int pageSize) {
        showWaitDialog("加载中...");
        NetApi.getMerchantSettleList(MerchantComeInActivity.this, pagenum, pageSize, new AsyncHttpResponseHandler() {
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
                            String settleListStr = obj.getString("settleList");
                            List<SettleModel> list = JSON.parseArray(settleListStr, SettleModel.class);
                            settleList.addAll(list);
                            mAdapter.refreshData(settleList);
                            if (pagenum < Integer.parseInt(obj.getString("totalPage"))) {
                                hasMore = true;
                            }else {
                                hasMore = false;
                            }
                            rvComein.notifyMoreFinish(hasMore);
                        }else {
                            CommonUtil.showToastMessage(MerchantComeInActivity.this, msg);
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

    /**
     * 获取红包账户信息
     */
    public void getListRedpacketsForMerchant() {
        NetApi.getListRedpacketsForMerchant(MerchantComeInActivity.this, pagenum, pageSize, new AsyncHttpResponseHandler() {
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
                            java.text.DecimalFormat df = new java.text.DecimalFormat("0.00");
                            tvRedPackage.setText(df.format(Double.parseDouble(obj.getString("account"))));
                        }else {
                            CommonUtil.showToastMessage(MerchantComeInActivity.this, msg);
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

    @OnClick(R.id.llRedPackage)
    public void startRedPackage() {
        startActivity(new Intent(MerchantComeInActivity.this, RedPackageActivity.class));
    }

    @OnClick(R.id.llYesterdayAmt)
    public void startYesterdayAmt() {
        //获取昨天的日期
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -1);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String settle_date = sdf.format(calendar.getTime());
        Intent intent = new Intent(MerchantComeInActivity.this, MerchantOrderListActivity.class);
        intent.putExtra("settle_date", settle_date);
        startActivity(intent);
    }

}
