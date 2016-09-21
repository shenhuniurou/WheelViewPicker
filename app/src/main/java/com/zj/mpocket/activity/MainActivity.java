package com.zj.mpocket.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;

import com.zj.mpocket.R;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    FinishBoardCast finishBoardCast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);

        //注册广播
        IntentFilter filter = new IntentFilter("finish");
        finishBoardCast = new FinishBoardCast();
        registerReceiver(finishBoardCast, filter);
    }

    @OnClick(R.id.loginBtn)
    public void startLoginActivity() {
        startActivity(new Intent(MainActivity.this, LoginActivity.class));
    }

    @OnClick(R.id.registerBtn)
    public void startRegisterActivity() {
        startActivity(new Intent(MainActivity.this, RegisterActivity.class));
    }

    @OnClick(R.id.tvProtorol)
    public void startLicenseActivity() {
        startActivity(new Intent(MainActivity.this, LicenseActivity.class));
    }

    public class FinishBoardCast extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("finish")) {
                finish();
            }
        }
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(finishBoardCast);
        super.onDestroy();
    }
}
