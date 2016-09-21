package com.zj.mpocket.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.zj.mpocket.R;
import com.zj.mpocket.utils.DialogControl;
import com.zj.mpocket.utils.DialogHelper;
import com.zj.mpocket.view.WaitDialog;

import butterknife.ButterKnife;

public abstract class BaseActivity extends AppCompatActivity implements DialogControl {

    boolean _isVisible;
    private WaitDialog _waitDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_base);
        FrameLayout contentView = (FrameLayout) findViewById(R.id.llContent);
        contentView.addView(LayoutInflater.from(this).inflate(getLayoutResId(), null));
        ButterKnife.inject(this, contentView);
        initToolbar();
        _isVisible = true;
        initViews();
    }

    public Toolbar initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        TextView textView = (TextView) findViewById(R.id.toolbar_title);
        textView.setText(getTitleResId());
        setSupportActionBar(toolbar);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            if(hasBackButton()) {
                actionBar.setDisplayHomeAsUpEnabled(true);
                actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_back);
            }else {
                actionBar.setDisplayHomeAsUpEnabled(false);
            }
            actionBar.setDisplayShowTitleEnabled(false);
        }
        return toolbar;
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    protected abstract boolean hasBackButton();

    protected abstract int getLayoutResId();

    protected abstract int getTitleResId();

    protected abstract void initViews();

    @Override
    public WaitDialog showWaitDialog() {
        return showWaitDialog("");
    }

    @Override
    public WaitDialog showWaitDialog(int resid) {
        return showWaitDialog(getString(resid));
    }

    @Override
    public WaitDialog showWaitDialog(String message) {
        if (_isVisible) {
            if (_waitDialog == null) {
                _waitDialog = DialogHelper.getWaitDialog(this, message);
            }
            if (_waitDialog != null) {
                _waitDialog.setMessage(message);
                _waitDialog.show();
            }
            return _waitDialog;
        }
        return null;
    }

    @Override
    public void setWaitDialogMessage(String message) {
        if (_waitDialog != null && _waitDialog.isShowing()) {
            _waitDialog.setMessage(message);
        }
    }

    @Override
    public void hideWaitDialog() {
        if (_isVisible && _waitDialog != null) {
            try {
                _waitDialog.dismiss();
                _waitDialog = null;
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
