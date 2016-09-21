package com.zj.mpocket.model;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/4/28.
 */
public class OrderModel implements Serializable {

    private String ids;//订单号
    private String merchant_id;//商户id
    private String name;//商户名称
    private String goodsids;//商品ids
    private String goods_name;//商品名称
    private String order_amt;//订单金额
    private String real_order_amt;//实付金额
    private String favorable_amt;//优惠金额
    private String order_status;//订单状态
    private String order_date;//日期
    private String pay_code;//支付码


    public String getMerchant_id() {
        return merchant_id;
    }

    public void setMerchant_id(String merchant_id) {
        this.merchant_id = merchant_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGoodsids() {
        return goodsids;
    }

    public void setGoodsids(String goodsids) {
        this.goodsids = goodsids;
    }

    public String getGoods_name() {
        return goods_name;
    }

    public void setGoods_name(String goods_name) {
        this.goods_name = goods_name;
    }

    public String getOrder_amt() {
        return order_amt;
    }

    public void setOrder_amt(String order_amt) {
        this.order_amt = order_amt;
    }

    public String getReal_order_amt() {
        return real_order_amt;
    }

    public void setReal_order_amt(String real_order_amt) {
        this.real_order_amt = real_order_amt;
    }

    public String getFavorable_amt() {
        return favorable_amt;
    }

    public void setFavorable_amt(String favorable_amt) {
        this.favorable_amt = favorable_amt;
    }

    public String getIds() {
        return ids;
    }

    public void setIds(String ids) {
        this.ids = ids;
    }

    public String getOrder_status() {
        return order_status;
    }

    public void setOrder_status(String order_status) {
        this.order_status = order_status;
    }

    public String getOrder_date() {
        return order_date;
    }

    public void setOrder_date(String order_date) {
        this.order_date = order_date;
    }

    public String getPay_code() {
        return pay_code;
    }

    public void setPay_code(String pay_code) {
        this.pay_code = pay_code;
    }
}
