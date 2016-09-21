package com.zj.mpocket.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.zj.mpocket.Constant;
import com.zj.mpocket.NetApi;
import com.zj.mpocket.PocketApplication;
import com.zj.mpocket.R;
import com.zj.mpocket.adapter.TaskAdpater;
import com.zj.mpocket.model.TaskModel;
import com.zj.mpocket.utils.CommonUtil;
import com.zj.mpocket.utils.LogUtil;
import com.zj.mpocket.utils.PreferenceUtil;
import com.zj.mpocket.utils.UpdateUtil;
import com.squareup.leakcanary.RefWatcher;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.InjectView;
import butterknife.OnClick;
import cz.msebera.android.httpclient.Header;

/**
 * Created by Administrator on 2016/4/14.
 */
public class MerchantManageActivity extends BaseActivity implements AdapterView.OnItemClickListener {

    private static final int MODIFY_UNDISCOUNT = 1;
    private static final int MERCHANT_LOAN = 2;
    private static final int UPDATE_STORE = 3;
    private static final int UPDATE_INFO = 4;
    String today_settle_amt;//今日营收
    String yesterday_settle_amt;//昨日营收
    String loan_amt;//贷款额度
    int loan_status;//贷款状态(0:可以贷款，1：贷款申请中)
    String shopimage;//店铺图片
    //String QRCodeLogo;//二维码中间的logo图片
    String merInfo;//商家信息的json串
    String contents;//店铺详情富文本字符串
    String name;//店铺名称
    String undiscount;
    TaskAdpater mAdpater;
    List<TaskModel> taskList;
    UpdateUtil mUpdateUtil;

    @InjectView(R.id.rlLoading)
    RelativeLayout rlLoading;
    @InjectView(R.id.tvTodaySettle)
    TextView tvTodaySettle;
    @InjectView(R.id.tvLoanSettle)
    TextView tvLoanSettle;
    @InjectView(R.id.gridView)
    GridView gridView;

    @Override
    protected boolean hasBackButton() {
        return false;
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_merchant_manage;
    }

    @Override
    protected int getTitleResId() {
        return R.string.merchant_manage;
    }

    @Override
    protected void initViews() {
        //初始化表格数据
        taskList = new ArrayList<>();
        taskList.add(new TaskModel(R.drawable.icon_store_manage, getString(R.string.store_manage)));
        taskList.add(new TaskModel(R.drawable.icon_merchant_info, getString(R.string.merchant_info)));
        taskList.add(new TaskModel(R.drawable.icon_discount_rate, getString(R.string.discount_rate)));
        taskList.add(new TaskModel(R.drawable.icon_pay_code, getString(R.string.title_activity_code_check)));

        taskList.add(new TaskModel(R.drawable.icon_merchant_comein, getString(R.string.merchant_comein)));
        taskList.add(new TaskModel(R.drawable.icon_merchant_vip, getString(R.string.vip_manage)));
        taskList.add(new TaskModel(R.drawable.icon_special_activities, getString(R.string.special_activities)));
        taskList.add(new TaskModel(R.drawable.icon_share_gang, getString(R.string.share_gang)));

        taskList.add(new TaskModel(R.drawable.icon_merchant_loan, getString(R.string.merchant_loan)));
        taskList.add(new TaskModel(R.drawable.icon_merchant_insurance, getString(R.string.merchant_insurance)));
        taskList.add(new TaskModel(R.drawable.icon_merchant_manage_finances, getString(R.string.merchant_manage_finances)));
        taskList.add(new TaskModel(R.drawable.icon_transfer_agent, getString(R.string.transfer_agent)));

        taskList.add(new TaskModel(R.drawable.icon_store_qrcode, getString(R.string.store_QR_code)));
        taskList.add(new TaskModel(R.drawable.icon_modify_pwd, getString(R.string.modify_pwd)));
        taskList.add(new TaskModel(R.drawable.icon_merchant_other, getString(R.string.other)));
        taskList.add(new TaskModel());

        mAdpater = new TaskAdpater(this, taskList);
        gridView.setAdapter(mAdpater);
        gridView.setOnItemClickListener(this);
        mUpdateUtil = new UpdateUtil(MerchantManageActivity.this, false);
        getMerchantManagerInfo();
        checkUpdate();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = null;
        switch (position) {
            case 0://店铺管理
                if (CommonUtil.isEmpty(contents)) {
                    //编辑界面
                    intent = new Intent(MerchantManageActivity.this, EditStoreActivity.class);
                }else {
                    //信息界面
                    intent = new Intent(MerchantManageActivity.this, StoreInfoActivity.class);
                    intent.putExtra("contents", contents);
                }
                startActivityForResult(intent, UPDATE_STORE);
                break;
            case 1://商家信息
                intent = new Intent(MerchantManageActivity.this, MerchantInfoActivity.class);
                intent.putExtra("merInfo", merInfo);
                intent.putExtra("shopimage", shopimage);
                startActivityForResult(intent, UPDATE_INFO);
                break;
            case 2://折扣比例
                intent = new Intent(MerchantManageActivity.this, DiscountRateActivity.class);
                intent.putExtra("undiscount", undiscount);
                startActivityForResult(intent, MODIFY_UNDISCOUNT);
                break;
            case 3://支付码核实
                startActivity(new Intent(MerchantManageActivity.this, CodeCheckActivity.class));
                break;
            case 4://商户收入
                intent = new Intent(MerchantManageActivity.this, MerchantComeInActivity.class);
                intent.putExtra("yesterday_settle_amt", yesterday_settle_amt);
                startActivity(intent);
                break;
            case 5://会员管理
                CommonUtil.showToastMessage(this, "该功能尚未开放");
                break;
            case 6://专场活动
                CommonUtil.showToastMessage(this, "该功能尚未开放");
                break;
            case 7://分享帮
                CommonUtil.showToastMessage(this, "该功能尚未开放");
                break;
            case 8://商家贷款
                CommonUtil.showToastMessage(this, "该功能尚未开放");
                break;
            case 9://商户保险
                CommonUtil.showToastMessage(this, "该功能尚未开放");
                break;
            case 10://商户理财
                CommonUtil.showToastMessage(this, "该功能尚未开放");
                break;
            case 11://代理转账
                CommonUtil.showToastMessage(this, "该功能尚未开放");
                break;
            case 12://店铺二维码
                /*String[] split = shopimage.split(":GGGGGG:");
                for (int i = 0; i < split.length; i++) {
                    if (!CommonUtil.isEmpty(split[i])) {
                        QRCodeLogo = split[i];
                        break;
                    }
                }*/
                intent = new Intent(MerchantManageActivity.this, QRcodeActivity.class);
                //intent.putExtra("logo", QRCodeLogo);
                intent.putExtra("name", name);
                startActivity(intent);
                break;
            case 13://修改密码
                startActivity(new Intent(MerchantManageActivity.this, ModifyPwdActivity.class));
                break;
            case 14://其他
                CommonUtil.showToastMessage(this, "该功能尚未开放");
                break;
        }
    }

    /**
     * 获取商家管理界面信息
     */
    public void getMerchantManagerInfo () {
        NetApi.getMerchantManagerInfo(MerchantManageActivity.this, new AsyncHttpResponseHandler() {
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
                            today_settle_amt = obj.getString("today_settle_amt");
                            yesterday_settle_amt = obj.getString("yesterday_settle_amt");
                            loan_amt = obj.getString("loan_amt");
                            loan_status = Integer.valueOf(obj.getString("loan_status"));
                            tvTodaySettle.setText(df.format(Double.parseDouble(today_settle_amt)));
                            tvLoanSettle.setText(df.format(Double.parseDouble(loan_amt)));
                            merInfo = obj.getString("merInfo");
                            JSONObject merInfoObj = new JSONObject(merInfo);
                            contents = merInfoObj.getString("contents");
                            shopimage = merInfoObj.getString("shopimage");
                            undiscount = merInfoObj.getString("undiscount");
                            name = merInfoObj.getString("name");
                        }else if ("88".equals(resultCode)) {
                            //token无效，需要重新登录
                            startActivity(new Intent(MerchantManageActivity.this, MainActivity.class));
                            finish();
                        }else {
                            CommonUtil.showToastMessage(MerchantManageActivity.this, msg);
                        }
                    }
                }catch (JSONException e) {
                    e.printStackTrace();
                }
                rlLoading.setVisibility(View.GONE);
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

    /**
     *获取版本信息
     */
    private void checkUpdate() {
        //默认为自动检查版本更新
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {

            @Override
            public void run() {
                mUpdateUtil.checkUpdate();
            }
        }, 2000);
    }

    @Override
    protected void onDestroy() {
        //销毁时先解绑服务
        mUpdateUtil.unbindService(MerchantManageActivity.this);
        super.onDestroy();
        RefWatcher refWatcher = PocketApplication.getRefWatcher(this);
        refWatcher.watch(this);
    }

    @OnClick(R.id.llLoanSettle)
    public void startMerchantLoan2() {
        /*Intent intent = null;
        if (loan_status == 0) {
            intent = new Intent(MerchantManageActivity.this, MerchantLoanActivity.class);
            intent.putExtra("loan_amt", loan_amt);
        }else if (loan_status == 1){
            intent = new Intent(MerchantManageActivity.this, LoanSuccessfulActivity.class);
        }
        startActivityForResult(intent, MERCHANT_LOAN);*/
        CommonUtil.showToastMessage(this, "该功能尚未开放");
    }

    @OnClick(R.id.llTodayAmt)
    public void startTodayAmt() {
        //获取今天的日期
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String settle_date = sdf.format(calendar.getTime());
        Intent intent = new Intent(MerchantManageActivity.this, MerchantOrderListActivity.class);
        intent.putExtra("settle_date", settle_date);
        startActivity(intent);
    }

    @OnClick(R.id.tvLogout)
    public void logout() {
        //退出登录
        AlertDialog.Builder builder = new AlertDialog.Builder(MerchantManageActivity.this);
        builder.setTitle("退出登录");
        builder.setMessage("确定退出账号吗？");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //1、清空缓存
                PreferenceUtil.clearPreference(MerchantManageActivity.this, Constant.USER_INFO, Context.MODE_PRIVATE);
                //2、回到登录界面
                startActivity(new Intent(MerchantManageActivity.this, MainActivity.class));
                finish();
            }
        });
        builder.setNegativeButton("取消", null);
        builder.create().show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == MODIFY_UNDISCOUNT && resultCode == RESULT_OK) {
            undiscount = data.getStringExtra("undiscount");
        }
        if (requestCode == MERCHANT_LOAN && resultCode == RESULT_OK) {
            loan_status = data.getIntExtra("loan_status", -1);
        }
        if (requestCode == UPDATE_STORE && resultCode == RESULT_OK) {
            contents = data.getStringExtra("contents");
        }
        if (requestCode == UPDATE_INFO && resultCode == RESULT_OK) {
            shopimage = data.getStringExtra("shopimage");
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

}
