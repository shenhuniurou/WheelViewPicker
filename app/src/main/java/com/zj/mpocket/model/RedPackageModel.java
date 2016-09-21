package com.zj.mpocket.model;

/**
 * Created by Administrator on 2016/5/8.
 */
public class RedPackageModel {

    private String trade_account;//交易金额
    private String trade_type;//交易类型
    private String date;//交易日期
    private String channel;//交易平台

    public String getTrade_account() {
        return trade_account;
    }

    public void setTrade_account(String trade_account) {
        this.trade_account = trade_account;
    }

    public String getTrade_type() {
        return trade_type;
    }

    public void setTrade_type(String trade_type) {
        this.trade_type = trade_type;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }
}
