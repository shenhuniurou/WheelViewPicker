package com.zj.mpocket.model;

/**
 * Created by Administrator on 2016/4/27.
 */
public class SettleModel {

    private String settle_amt;//当天营收
    private String settle_date;//日期

    public String getSettle_date() {
        return settle_date;
    }

    public void setSettle_date(String settle_date) {
        this.settle_date = settle_date;
    }

    public String getSettle_amt() {
        return settle_amt;
    }

    public void setSettle_amt(String settle_amt) {
        this.settle_amt = settle_amt;
    }
}
