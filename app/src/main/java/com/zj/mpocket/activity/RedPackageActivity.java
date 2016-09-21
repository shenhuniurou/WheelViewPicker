package com.zj.mpocket.activity;

import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.alibaba.fastjson.JSON;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.zj.mpocket.NetApi;
import com.zj.mpocket.R;
import com.zj.mpocket.adapter.RedPackageAdapter;
import com.zj.mpocket.model.RedPackageModel;
import com.zj.mpocket.utils.CommonUtil;
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

public class RedPackageActivity extends BaseActivity implements LoadMoreRecyclerView.LoadMoreListener, SwipyRefreshLayout.OnRefreshListener {

    @InjectView(R.id.rvRedPackageList)
    LoadMoreRecyclerView rvRedPackageList;
    @InjectView(R.id.swiperefreshlayout)
    SwipyRefreshLayout swiperefreshlayout;

    List<RedPackageModel> redPackageList = new ArrayList<>();
    RedPackageAdapter mAdapter;
    int pageSize = 20;
    int pagenum = 1;
    int mState = 0;//0没刷新，1正在刷新
    boolean hasMore = false;

    @Override
    protected boolean hasBackButton() {
        return true;
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_red_package;
    }

    @Override
    protected int getTitleResId() {
        return R.string.red_packets_bill;
    }

    @Override
    protected void initViews() {
        //获取商户订单列表
        //getlistRedpacketsForMerchant( pagenum, pageSize);
        swiperefreshlayout.setColorSchemeResources(
                android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        swiperefreshlayout.setOnRefreshListener(this);
        swiperefreshlayout.setDirection(SwipyRefreshLayoutDirection.TOP);
        rvRedPackageList.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(RedPackageActivity.this);
        rvRedPackageList.setLayoutManager(layoutManager);
        rvRedPackageList.setLoadMoreListener(this);
        rvRedPackageList.setAutoLoadMoreEnable(hasMore);
        mAdapter = new RedPackageAdapter(redPackageList);
        rvRedPackageList.setAdapter(mAdapter);
        rvRedPackageList.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));
        onRefresh();
    }

    private void getlistRedpacketsForMerchant(final int pagenum, int pageSzie) {
        if (pagenum == 1) {
            showWaitDialog();
        }
        NetApi.getlistRedpacketsForMerchant(RedPackageActivity.this, pagenum, pageSzie,  new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                hideWaitDialog();
                try {
                    if (responseBody != null) {
                        String result = new String(responseBody);
                        LogUtil.log("result----" + result);
                        JSONObject obj = new JSONObject(result);
                        String resultCode = obj.getString("resultCode");
                        if ("00".equals(resultCode)) {
                            String data = obj.getString("datalist");
                            if (!CommonUtil.isEmpty(data)) {
                                List<RedPackageModel> list = JSON.parseArray(data, RedPackageModel.class);
                                redPackageList.addAll(list);
                                mAdapter.refreshData(redPackageList);
                                if (pagenum < Integer.parseInt(obj.getString("totalPage"))) {
                                    hasMore = true;
                                } else {
                                    hasMore = false;
                                }
                                if (mState == 1) {
                                    mAdapter.notifyDataSetChanged();
                                    rvRedPackageList.setAutoLoadMoreEnable(hasMore);
                                } else {
                                    rvRedPackageList.notifyMoreFinish(hasMore);
                                }
                            }
                        }else {
                            //CommonUtil.showToastMessage(MerchantOrderListActivity.this, msg);
                        }
                    }
                }catch (JSONException e) {
                    e.printStackTrace();
                }
                if (pagenum == 1) {
                    doneRefresh();
                }
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
    public void onRefresh() {
        if (mState != 1) {
            //清空原有数据集
            redPackageList.clear();
            setSwipeRefreshLoadingState();
            rvRedPackageList.setLoadingMore(false);
            pagenum = 1;
            mState = 1;
            getlistRedpacketsForMerchant(pagenum, pageSize);
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
                getlistRedpacketsForMerchant(pagenum, pageSize);
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

}
