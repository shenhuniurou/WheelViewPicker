package com.zj.wheelview.activity;

import com.zj.wheelview.R;

import butterknife.OnClick;

public class CheckRegisterActivity extends BaseActivity {

    @Override
    protected boolean hasBackButton() {
        return true;
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_check_register;
    }

    @Override
    protected int getTitleResId() {
        return R.string.check_register;
    }

    @Override
    protected void initViews() {

    }

    @OnClick(R.id.tvOK)
    public void OK() {
        finish();
    }

}
