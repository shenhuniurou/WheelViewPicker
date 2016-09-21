package com.zj.mpocket.activity;

import com.zj.mpocket.R;

public class LoanSuccessfulActivity extends BaseActivity {

    @Override
    protected boolean hasBackButton() {
        return true;
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_loan_successful;
    }

    @Override
    protected int getTitleResId() {
        return R.string.merchant_loan;
    }

    @Override
    protected void initViews() {

    }

}
