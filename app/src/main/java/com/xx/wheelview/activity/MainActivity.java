package com.xx.wheelview.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;

import com.zj.wheelview.R;

import butterknife.ButterKnife;

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
