package com.zj.mpocket.activity;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.alibaba.fastjson.JSON;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.zj.mpocket.NetApi;
import com.zj.mpocket.R;
import com.zj.mpocket.adapter.OrderAdapter;
import com.zj.mpocket.model.OrderModel;
import com.zj.mpocket.utils.LogUtil;
import com.zj.mpocket.view.DividerItemDecoration;
import com.zj.mpocket.view.LoadMoreRecyclerView;
import com.zj.mpocket.view.swiperefreshlayout.SwipyRefreshLayout;
import com.zj.mpocket.view.swiperefreshlayout.SwipyRefreshLayoutDirection;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.InjectView;
import cz.msebera.android.httpclient.Header;

public class MerchantOrderListActivity extends BaseActivity implements LoadMoreRecyclerView.LoadMoreListener,
        OrderAdapter.OnRecyclerViewListener, SwipyRefreshLayout.OnRefreshListener {

    private static final int UPDATE_ORDER = 1;
    @InjectView(R.id.rvOrderList)
    LoadMoreRecyclerView rvOrderList;
    @InjectView(R.id.swiperefreshlayout)
    SwipyRefreshLayout swiperefreshlayout;

    List<OrderModel> orderList = new ArrayList<>();
    OrderAdapter mAdapter;
    String settle_date;
    int pageSize = 20;
    int pagenum = 1;
    int mState = 0;//0没刷新，1正在刷新
    int index = -1;
    boolean hasMore = false;

    @Override
    protected boolean hasBackButton() {
        return true;
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_merchant_order_list;
    }

    @Override
    protected int getTitleResId() {
        return R.string.title_activity_merchant_order_list;
    }

    @Override
    protected void initViews() {
        settle_date = getIntent().getStringExtra("settle_date");
        //获取商户订单列表
        getOrderList(pagenum, pageSize);
        swiperefreshlayout.setColorSchemeResources(
                android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        swiperefreshlayout.setOnRefreshListener(this);
        swiperefreshlayout.setDirection(SwipyRefreshLayoutDirection.TOP);
        rvOrderList.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(MerchantOrderListActivity.this);
        rvOrderList.setLayoutManager(layoutManager);
        rvOrderList.setLoadMoreListener(this);
        rvOrderList.setAutoLoadMoreEnable(hasMore);
        mAdapter = new OrderAdapter(orderList);
        mAdapter.setOnRecyclerViewListener(this);
        rvOrderList.setAdapter(mAdapter);
        rvOrderList.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));
    }

    private void getOrderList(final int pagenum, int pageSzie) {
        if (pagenum == 1) {
            showWaitDialog();
        }
        NetApi.getOrderList(MerchantOrderListActivity.this, settle_date, settle_date, pagenum, pageSzie,  new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                hideWaitDialog();
                try {
                    if (responseBody != null) {
                        String result = new String(responseBody);
                        LogUtil.log("result----" + result);
                        JSONObject obj = new JSONObject(result);
                        String resultCode = obj.getString("resultCode");
                        //String msg = obj.getString("msg");
                        if ("00".equals(resultCode)) {
                            String data = obj.getString("data");
                            List<OrderModel> list = JSON.parseArray(data, OrderModel.class);
                            orderList.addAll(list);
                            mAdapter.refreshData(orderList);
                            if (pagenum < Integer.parseInt(obj.getString("totalPage"))) {
                                hasMore = true;
                            }else {
                                hasMore = false;
                            }
                            if (mState == 1) {
                                mAdapter.notifyDataSetChanged();
                                rvOrderList.setAutoLoadMoreEnable(hasMore);
                            }else {
                                rvOrderList.notifyMoreFinish(hasMore);
                            }
                        }else {
                            //CommonUtil.showToastMessage(MerchantOrderListActivity.this, msg);
                        }
                    }
                }catch (JSONException e) {
                    e.printStackTrace();
                }
                doneRefresh();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                hideWaitDialog();
                doneRefresh();
                if (responseBody != null) {
                    String result = new String(responseBody);
                    LogUtil.log("result----" + result);
                }
            }
        });
    }

    @Override
    public void onItemClick(int position) {
        if (orderList != null && orderList.size() > 0) {
            index = position;
            OrderModel orderModel = orderList.get(position);
            //跳转至订单详情
            Intent intent = new Intent(MerchantOrderListActivity.this, OrderDetailActivity.class);
            intent.putExtra("orderModel", orderModel);
            startActivityForResult(intent, UPDATE_ORDER);
        }
    }

    @Override
    public void onRefresh() {
        if (mState != 1) {
            //清空原有数据集
            orderList.clear();
            setSwipeRefreshLoadingState();
            pagenum = 1;
            mState = 1;
            getOrderList(pagenum, pageSize);
        }
    }

    //刷新完之后
    public void doneRefresh() {
        setSwipeRefreshLoadedState();
        mState = 0;
    }

    @Override
    public void onLoadMore() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                pagenum++;
                getOrderList(pagenum, pageSize);
            }
        }, 1000);
    }

    /** 设置顶部正在加载的状态*/
    private void setSwipeRefreshLoadingState() {
        if (swiperefreshlayout != null) {
            swiperefreshlayout.setRefreshing(true);
            // 防止多次重复刷新
            swiperefreshlayout.setEnabled(false);
        }
    }

    /** 设置顶部加载完毕的状态*/
    private void setSwipeRefreshLoadedState() {
        if (swiperefreshlayout != null) {
            swiperefreshlayout.setRefreshing(false);
            swiperefreshlayout.setEnabled(true);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == UPDATE_ORDER && resultCode == RESULT_OK) {
            orderList.remove(index);
            orderList.add(index, (OrderModel) data.getSerializableExtra("orderModel"));
            mAdapter.notifyDataSetChanged();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
