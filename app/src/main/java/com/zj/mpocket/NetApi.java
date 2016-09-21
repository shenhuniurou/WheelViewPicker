package com.zj.mpocket;

import android.content.Context;

import com.alibaba.fastjson.JSON;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.zj.mpocket.model.ContentModel;
import com.zj.mpocket.utils.CommonUtil;
import com.zj.mpocket.utils.LogUtil;
import com.zj.mpocket.utils.Md5Util;
import com.zj.mpocket.utils.PreferenceUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.TreeMap;

public class NetApi {

    /**
     * 店铺图片修改接口
     * @param context
     * @param shopimage
     * @param handler
     */
    public static void updateShopImage(Context context, String shopimage, AsyncHttpResponseHandler handler) {
        String requestName = "jf/api/merchant/updateShopImage";
        RequestParams params = new RequestParams();
        String accessToken = PreferenceUtil.getPrefString(context, Constant.USER_INFO, Context.MODE_PRIVATE, Constant.TOKEN, null);
        String loginId = PreferenceUtil.getPrefString(context, Constant.USER_INFO, Context.MODE_PRIVATE, Constant.ids, null);
        String merchantId = PreferenceUtil.getPrefString(context, Constant.USER_INFO, Context.MODE_PRIVATE, Constant.MERCHANT_Id, null);
        params.put("accessToken", accessToken);
        params.put("clientId", Constant.clientId);
        params.put("loginId", loginId);
        params.put("merchantId", merchantId);
        params.put("shopimage", shopimage);
        String sign = Md5Util.md5(accessToken + Constant.clientId + loginId + merchantId + shopimage + Constant.key);
        params.put("sign", sign);
        LogUtil.log("params---" + params.toString());
        ApiHttpClient.post(requestName, params, handler);
    }

    /**
     * 修改店铺图文详情
     * @param context
     * @param contentsList
     */
    public static void saveStoreInfo(Context context, List<ContentModel> contentsList, AsyncHttpResponseHandler handler) {
        String requestName = "jf/api/contents/save";
        RequestParams params = new RequestParams();
        String accessToken = PreferenceUtil.getPrefString(context, Constant.USER_INFO, Context.MODE_PRIVATE, Constant.TOKEN, null);
        String loginId = PreferenceUtil.getPrefString(context, Constant.USER_INFO, Context.MODE_PRIVATE, Constant.ids, null);
        String merchantId = PreferenceUtil.getPrefString(context, Constant.USER_INFO, Context.MODE_PRIVATE, Constant.MERCHANT_Id, null);
        params.put("accessToken", accessToken);
        params.put("clientId", Constant.clientId);
        params.put("loginId", loginId);
        params.put("merchantId", merchantId);
        params.put("contentsList", JSON.toJSONString(contentsList));
        String sign = Md5Util.md5(accessToken + Constant.clientId + JSON.toJSONString(contentsList) + loginId + merchantId + Constant.key);
        params.put("sign", sign);
        LogUtil.log("params---" + params.toString());
        ApiHttpClient.post(requestName, params, handler);
    }

    /**
     * 商户红包账户信息和红包账单
     * @param context
     * @param pagenum
     * @param pageSize
     * @param handler
     */
    public static void getlistRedpacketsForMerchant(Context context, int pagenum, int pageSize, AsyncHttpResponseHandler handler) {
        String requestName = "jf/api/fundrecords/listRedpacketsForMerchant";
        RequestParams params = new RequestParams();
        String accessToken = PreferenceUtil.getPrefString(context, Constant.USER_INFO, Context.MODE_PRIVATE, Constant.TOKEN, null);
        String loginId = PreferenceUtil.getPrefString(context, Constant.USER_INFO, Context.MODE_PRIVATE, Constant.ids, null);
        String merchantId = PreferenceUtil.getPrefString(context, Constant.USER_INFO, Context.MODE_PRIVATE, Constant.MERCHANT_Id, null);
        params.put("accessToken", accessToken);
        params.put("clientId", Constant.clientId);
        params.put("loginId", loginId);
        params.put("merchantId", merchantId);
        params.put("pagenum", pagenum);
        params.put("pagesize", pageSize);
        LogUtil.log("params---" + params.toString());
        ApiHttpClient.get(requestName, params, handler);
    }

    /**
     * 查询店铺内容详情
     * @param context
     * @param handler
     */
    public static void getContentsList(Context context, AsyncHttpResponseHandler handler) {
        String requestName = "jf/api/contents/list";
        RequestParams params = new RequestParams();
        String accessToken = PreferenceUtil.getPrefString(context, Constant.USER_INFO, Context.MODE_PRIVATE, Constant.TOKEN, null);
        String loginId = PreferenceUtil.getPrefString(context, Constant.USER_INFO, Context.MODE_PRIVATE, Constant.ids, null);
        String merchantId = PreferenceUtil.getPrefString(context, Constant.USER_INFO, Context.MODE_PRIVATE, Constant.MERCHANT_Id, null);
        params.put("accessToken", accessToken);
        params.put("clientId", Constant.clientId);
        params.put("loginId", loginId);
        params.put("merchantId", merchantId);
        LogUtil.log("params---" + params.toString());
        ApiHttpClient.post(requestName, params, handler);
    }

    /**
     * 上传图片
     * @param imgFile 图片文件
     * @param handler
     */
    public static void uploadImage(File imgFile, AsyncHttpResponseHandler handler) {
        String requestName = "jf/api/upload/merchant";
        RequestParams params = new RequestParams();
        try {
            params.put("imgFile", imgFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        ApiHttpClient.post(requestName, params, handler);
    }

    /**
     * 商户注册
     * @param storeName
     * @param addr
     * @param province
     * @param city
     * @param district
     * @param merchant_industry
     * @param undiscount
     * @param tellphone
     * @param contacts
     * @param baidu_x
     * @param baidu_y
     * @param identity_card
     * @param bank_type
     * @param bank_acc
     * @param bank_num
     * @param email
     * @param id_card_image
     * @param merchant_license
     * @param imageurls
     * @param handler
     */
    public static void register(String storeName, String addr, String province, String city, String district, String merchant_industry,
                                float undiscount, String tellphone, String contacts, float baidu_x, float baidu_y, String identity_card,
                                String bank_type, String bank_acc, String bank_num, String email, String id_card_image,
                                String merchant_license, String imageurls, AsyncHttpResponseHandler handler) {
        String requestName = "jf/api/merchant/register";
        RequestParams params = new RequestParams();
        params.put("clientId", Constant.clientId);
        params.put("merinfo.name", storeName);
        params.put("merinfo.addr", addr);
        params.put("merinfo.province", province);
        params.put("merinfo.city", city);
        params.put("merinfo.area", district);
        params.put("merinfo.merchant_industry", merchant_industry);
        params.put("merinfo.undiscount", undiscount);
        params.put("merinfo.tellphone", tellphone);
        params.put("merinfo.contacts", contacts);
        params.put("merinfo.baidu_x", baidu_x);
        params.put("merinfo.baidu_y", baidu_y);
        params.put("merinfo.identity_card", identity_card);
        params.put("merinfo.bank_type", bank_type);
        params.put("merinfo.bank_acc", bank_acc);
        params.put("merinfo.bank_num", bank_num);
        params.put("merinfo.email", email);
        params.put("merinfo.id_card_image", id_card_image);
        params.put("merinfo.merchant_license", merchant_license);
        params.put("imageurls", imageurls);

        TreeMap<String, String> paramsMap = new TreeMap<>();
        paramsMap.put("clientId", Constant.clientId);
        paramsMap.put("merinfo.name", storeName);
        paramsMap.put("merinfo.addr", addr);
        paramsMap.put("merinfo.province", province);
        paramsMap.put("merinfo.city", city);
        paramsMap.put("merinfo.area", district);
        paramsMap.put("merinfo.merchant_industry", merchant_industry);
        paramsMap.put("merinfo.undiscount", String.valueOf(undiscount));
        paramsMap.put("merinfo.tellphone", tellphone);
        paramsMap.put("merinfo.contacts", contacts);
        paramsMap.put("merinfo.baidu_x", String.valueOf(baidu_x));
        paramsMap.put("merinfo.baidu_y", String.valueOf(baidu_y));
        paramsMap.put("merinfo.identity_card", identity_card);
        paramsMap.put("merinfo.bank_type", bank_type);
        paramsMap.put("merinfo.bank_acc", bank_acc);
        paramsMap.put("merinfo.bank_num", bank_num);
        paramsMap.put("merinfo.email", email);
        paramsMap.put("merinfo.id_card_image", id_card_image);
        paramsMap.put("merinfo.merchant_license", merchant_license);
        paramsMap.put("imageurls", imageurls);
        String sign = CommonUtil.getSign(paramsMap);
        params.put("sign", sign);
        LogUtil.log("params---" + params.toString());
        ApiHttpClient.post(requestName, params, handler);
    }

    /**
     * 退款
     * @param context
     * @param ids
     * @param handler
     */
    public static void retunPay(Context context, String ids, AsyncHttpResponseHandler handler) {
        String requestName = "jf/api/merchant/retunPay";
        RequestParams params = new RequestParams();
        String accessToken = PreferenceUtil.getPrefString(context, Constant.USER_INFO, Context.MODE_PRIVATE, Constant.TOKEN, null);
        String loginId = PreferenceUtil.getPrefString(context, Constant.USER_INFO, Context.MODE_PRIVATE, Constant.ids, null);
        String merchantId = PreferenceUtil.getPrefString(context, Constant.USER_INFO, Context.MODE_PRIVATE, Constant.MERCHANT_Id, null);
        params.put("accessToken", accessToken);
        params.put("clientId", Constant.clientId);
        params.put("loginId", loginId);
        params.put("merchantId", merchantId);
        params.put("orderId", ids);
        String sign = Md5Util.md5(accessToken + Constant.clientId + loginId + merchantId + ids + Constant.key);
        params.put("sign", sign);
        LogUtil.log("params---" + params.toString());
        ApiHttpClient.post(requestName, params, handler);
    }

    /**
     * 商户订单列表
     * @param context
     * @param begin_order_date
     * @param end_order_date
     * @param pagenum
     * @param pageSize
     * @param handler
     */
    public static void getOrderList(Context context, String begin_order_date, String end_order_date, int pagenum, int pageSize, AsyncHttpResponseHandler handler) {
        String requestName = "jf/api/order/list";
        RequestParams params = new RequestParams();
        String accessToken = PreferenceUtil.getPrefString(context, Constant.USER_INFO, Context.MODE_PRIVATE, Constant.TOKEN, null);
        String loginId = PreferenceUtil.getPrefString(context, Constant.USER_INFO, Context.MODE_PRIVATE, Constant.ids, null);
        String merchantId = PreferenceUtil.getPrefString(context, Constant.USER_INFO, Context.MODE_PRIVATE, Constant.MERCHANT_Id, null);
        params.put("accessToken", accessToken);
        params.put("clientId", Constant.clientId);
        params.put("loginId", loginId);
        params.put("merchantId", merchantId);
        params.put("begin_order_date", begin_order_date + "000000");
        params.put("end_order_date", end_order_date + "999999");
        params.put("pagenum", pagenum);
        params.put("pagesize", pageSize);
        LogUtil.log("params---" + params.toString());
        ApiHttpClient.get(requestName, params, handler);
    }

    /**
     * 查询商户清算列表
     * @param context
     * @param pagenum
     * @param pageSize
     * @param handler
     */
    public static void getMerchantSettleList(Context context, int pagenum, int pageSize, AsyncHttpResponseHandler handler) {
        String requestName = "jf/api/merchant/getMerchantSettleList";
        RequestParams params = new RequestParams();
        String accessToken = PreferenceUtil.getPrefString(context, Constant.USER_INFO, Context.MODE_PRIVATE, Constant.TOKEN, null);
        String loginId = PreferenceUtil.getPrefString(context, Constant.USER_INFO, Context.MODE_PRIVATE, Constant.ids, null);
        String merchantId = PreferenceUtil.getPrefString(context, Constant.USER_INFO, Context.MODE_PRIVATE, Constant.MERCHANT_Id, null);
        params.put("accessToken", accessToken);
        params.put("clientId", Constant.clientId);
        params.put("loginId", loginId);
        params.put("merchantId", merchantId);
        params.put("pagenum", pagenum);
        params.put("pagesize", pageSize);
        LogUtil.log("params---" + params.toString());
        ApiHttpClient.get(requestName, params, handler);
    }

    /**
     * 商户红包账户信息和红包账单
     * @param context
     * @param pagenum
     * @param pageSize
     * @param handler
     */
    public static void getListRedpacketsForMerchant(Context context, int pagenum, int pageSize, AsyncHttpResponseHandler handler) {
        String requestName = "jf/api/fundrecords/listRedpacketsForMerchant";
        RequestParams params = new RequestParams();
        String accessToken = PreferenceUtil.getPrefString(context, Constant.USER_INFO, Context.MODE_PRIVATE, Constant.TOKEN, null);
        String loginId = PreferenceUtil.getPrefString(context, Constant.USER_INFO, Context.MODE_PRIVATE, Constant.ids, null);
        String merchantId = PreferenceUtil.getPrefString(context, Constant.USER_INFO, Context.MODE_PRIVATE, Constant.MERCHANT_Id, null);
        params.put("accessToken", accessToken);
        params.put("clientId", Constant.clientId);
        params.put("loginId", loginId);
        params.put("merchantId", merchantId);
        params.put("pagenum", pagenum);
        params.put("pagesize", pageSize);
        LogUtil.log("params---" + params.toString());
        ApiHttpClient.get(requestName, params, handler);
    }

    /**
     * 获取银行列表
     * @param context
     * @param handler
     */
    public static void getBankList(Context context, AsyncHttpResponseHandler handler) {
        String requestName = "jf/api/bank/list";
        RequestParams params = new RequestParams();
        params.put("clientId", Constant.clientId);
        LogUtil.log("params---" + params.toString());
        ApiHttpClient.get(requestName, params, handler);
    }

    /**
     * 商户贷款
     * @param context
     * @param loan_amount
     * @param handler
     */
    public static void doApplyMerchantLoan(Context context, String loan_amount, AsyncHttpResponseHandler handler) {
        String requestName = "jf/api/merchantloan/doApplyMerchantLoan";
        RequestParams params = new RequestParams();
        String accessToken = PreferenceUtil.getPrefString(context, Constant.USER_INFO, Context.MODE_PRIVATE, Constant.TOKEN, null);
        String loginId = PreferenceUtil.getPrefString(context, Constant.USER_INFO, Context.MODE_PRIVATE, Constant.ids, null);
        String merchantId = PreferenceUtil.getPrefString(context, Constant.USER_INFO, Context.MODE_PRIVATE, Constant.MERCHANT_Id, null);
        params.put("accessToken", accessToken);
        params.put("clientId", Constant.clientId);
        params.put("loginId", loginId);
        params.put("merchantId", merchantId);
        params.put("msg.merchant_loan_amount", loan_amount);
        String sign = Md5Util.md5(accessToken + Constant.clientId + loginId + merchantId + loan_amount + Constant.key);
        params.put("sign", sign);
        LogUtil.log("params---" + params.toString());
        ApiHttpClient.post(requestName, params, handler);
    }

    /**
     * 查询商户清算金额
     * @param context
     * @param begin_settle_date
     * @param end_settle_date
     * @param handler
     */
    public static void getMerchantSettleAmt(Context context, String begin_settle_date, String end_settle_date, AsyncHttpResponseHandler handler) {
        String requestName = "jf/api/merchant/getMerchantSettleAmt";
        RequestParams params = new RequestParams();
        String accessToken = PreferenceUtil.getPrefString(context, Constant.USER_INFO, Context.MODE_PRIVATE, Constant.TOKEN, null);
        String loginId = PreferenceUtil.getPrefString(context, Constant.USER_INFO, Context.MODE_PRIVATE, Constant.ids, null);
        String merchantId = PreferenceUtil.getPrefString(context, Constant.USER_INFO, Context.MODE_PRIVATE, Constant.MERCHANT_Id, null);
        params.put("accessToken", accessToken);
        params.put("clientId", Constant.clientId);
        params.put("loginId", loginId);
        params.put("merchantId", merchantId);
        params.put("begin_settle_date", begin_settle_date);
        params.put("end_settle_date", end_settle_date);
        LogUtil.log("params---" + params.toString());
        ApiHttpClient.get(requestName, params, handler);
    }

    /**
     * 设置商户特惠比例
     * @param context
     * @param undiscount
     * @param handler
     */
    public static void setUnDiscount(Context context, float undiscount, AsyncHttpResponseHandler handler) {
        String requestName = "jf/api/merchant/setUnDiscount";
        RequestParams params = new RequestParams();
        String accessToken = PreferenceUtil.getPrefString(context, Constant.USER_INFO, Context.MODE_PRIVATE, Constant.TOKEN, null);
        String loginId = PreferenceUtil.getPrefString(context, Constant.USER_INFO, Context.MODE_PRIVATE, Constant.ids, null);
        String merchantId = PreferenceUtil.getPrefString(context, Constant.USER_INFO, Context.MODE_PRIVATE, Constant.MERCHANT_Id, null);
        params.put("accessToken", accessToken);
        params.put("clientId", Constant.clientId);
        params.put("loginId", loginId);
        params.put("merchantId", merchantId);
        params.put("undiscount", undiscount);
        String sign = Md5Util.md5(accessToken + Constant.clientId + loginId + merchantId + String.valueOf(undiscount) + Constant.key);
        params.put("sign", sign);
        LogUtil.log("params---" + params.toString());
        ApiHttpClient.post(requestName, params, handler);
    }

    /**
     * 支付码核实
     * @param context
     * @param payCode
     * @param handler
     */
    public static void checkPayCode(Context context, String payCode, AsyncHttpResponseHandler handler) {
        String requestName = "jf/api/order/checkpaycode";
        RequestParams params = new RequestParams();
        String accessToken = PreferenceUtil.getPrefString(context, Constant.USER_INFO, Context.MODE_PRIVATE, Constant.TOKEN, null);
        String loginId = PreferenceUtil.getPrefString(context, Constant.USER_INFO, Context.MODE_PRIVATE, Constant.ids, null);
        String merchantId = PreferenceUtil.getPrefString(context, Constant.USER_INFO, Context.MODE_PRIVATE, Constant.MERCHANT_Id, null);
        params.put("accessToken", accessToken);
        params.put("clientId", Constant.clientId);
        params.put("loginId", loginId);
        params.put("merchantId", merchantId);
        params.put("pay_code", payCode);
        String sign = Md5Util.md5(accessToken + Constant.clientId + loginId + merchantId +  payCode + Constant.key);
        params.put("sign", sign);
        LogUtil.log("params---" + params.toString());
        ApiHttpClient.post(requestName, params, handler);
    }

    /**
     * 商家管理界面信息
     * @param context
     * @param handler
     */
    public static void getMerchantManagerInfo(Context context, AsyncHttpResponseHandler handler) {
        String requestName = "jf/api/merchant/getMerchantManagerInfo";
        RequestParams params = new RequestParams();
        String accessToken = PreferenceUtil.getPrefString(context, Constant.USER_INFO, Context.MODE_PRIVATE, Constant.TOKEN, null);
        String loginId = PreferenceUtil.getPrefString(context, Constant.USER_INFO, Context.MODE_PRIVATE, Constant.ids, null);
        String merchantId = PreferenceUtil.getPrefString(context, Constant.USER_INFO, Context.MODE_PRIVATE, Constant.MERCHANT_Id, null);
        params.put("accessToken", accessToken);
        params.put("clientId", Constant.clientId);
        params.put("loginId", loginId);
        params.put("merchantId", merchantId);
        LogUtil.log("params---" + params.toString());
        ApiHttpClient.get(requestName, params, handler);
    }

    /**
     * 修改密码
     * @param oldPwd
     * @param newPwd
     * @param handler
     */
    public static void updatePwd(Context context, String oldPwd, String newPwd, AsyncHttpResponseHandler handler) {
        String requestName = "jf/api/mcerchaccount/updatePwd";
        RequestParams params = new RequestParams();
        String accessToken = PreferenceUtil.getPrefString(context, Constant.USER_INFO, Context.MODE_PRIVATE, Constant.TOKEN, null);
        String loginId = PreferenceUtil.getPrefString(context, Constant.USER_INFO, Context.MODE_PRIVATE, Constant.ids, null);
        String merchantId = PreferenceUtil.getPrefString(context, Constant.USER_INFO, Context.MODE_PRIVATE, Constant.MERCHANT_Id, null);
        params.put("accessToken", accessToken);
        params.put("clientId", Constant.clientId);
        params.put("loginId", loginId);
        params.put("merchantId", merchantId);
        params.put("newPwd", Md5Util.md5(newPwd).toUpperCase());
        params.put("oldPwd", Md5Util.md5(oldPwd).toUpperCase());
        String sign = Md5Util.md5(accessToken + Constant.clientId + loginId +
                merchantId +  Md5Util.md5(newPwd).toUpperCase() + Md5Util.md5(oldPwd).toUpperCase() + Constant.key);
        params.put("sign", sign);
        LogUtil.log("params---" + params.toString());
        ApiHttpClient.post(requestName, params, handler);
    }

    /**
     * 商家登录
     * @param loginAccount
     * @param password
     * @param handler
     */
    public static void login(String loginAccount, String password, AsyncHttpResponseHandler handler) {
        String requestName = "jf/api/mcerchaccount/login";
        RequestParams params = new RequestParams();
        params.put("clientId", Constant.clientId);
        params.put("loginAccount", loginAccount);
        params.put("password", Md5Util.md5(password).toUpperCase());
        String sign = Md5Util.md5(Constant.clientId + loginAccount + Md5Util.md5(password).toUpperCase() + Constant.key);
        params.put("sign", sign);
        LogUtil.log("params---" + params.toString());
        ApiHttpClient.post(requestName, params, handler);
    }

    /**
     * 获取商铺分类列表
     * @param handler
     */
    public static void getProclassList(AsyncHttpResponseHandler handler) {
        String requestName = "jf/api/proclass/list";
        RequestParams params = new RequestParams();
        params.put("clientId", Constant.clientId);
        ApiHttpClient.get(requestName, params, handler);
    }

    /**
     * 获取版本信息
     * @param handler
     */
    public static void getVersionInfo(AsyncHttpResponseHandler handler) {
        String requestName = "jf/api/app/version";
        RequestParams params = new RequestParams();
        params.put("clientId", Constant.clientId);
        ApiHttpClient.get(requestName, params, handler);
    }

}
