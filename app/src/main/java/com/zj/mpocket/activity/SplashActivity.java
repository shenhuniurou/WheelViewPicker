package com.zj.mpocket.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.zj.mpocket.Constant;
import com.zj.mpocket.Engine;
import com.zj.mpocket.NetApi;
import com.zj.mpocket.R;
import com.zj.mpocket.utils.LogUtil;
import com.zj.mpocket.utils.PreferenceUtil;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class SplashActivity extends AppCompatActivity {

    long lastCurrentTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        View rootView = View.inflate(this, R.layout.activity_splash, null);
        setContentView(rootView);
        //渐变启动
        AlphaAnimation aa = new AlphaAnimation(0.7f, 1.0f);
        aa.setDuration(2000);
        rootView.startAnimation(aa);
        aa.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationEnd(Animation arg0) {
                startTask();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}

            @Override
            public void onAnimationStart(Animation animation) {}
        });

    }

    protected void startTask() {

        //先判断是否出现过引导页
        boolean newInstall = PreferenceUtil.getPrefBoolean(SplashActivity.this, Constant.APP_CONFIG, Context.MODE_PRIVATE, Constant.NEW_INSTALL, true);

        if(newInstall) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    //跳转到引导页
                    startActivity(new Intent(SplashActivity.this, GuideActivity.class));
                    finish();
                }
            }, 2000);
        }else {
            if (Engine.getInstance().isLogined(SplashActivity.this)) {
                lastCurrentTime = System.currentTimeMillis();

                //请求一次登录接口
                String account = PreferenceUtil.getPrefString(SplashActivity.this, Constant.USER_INFO, Context.MODE_PRIVATE, Constant.MERCHANT_ACCOUNT, null);
                String password = PreferenceUtil.getPrefString(SplashActivity.this, Constant.USER_INFO, Context.MODE_PRIVATE, Constant.MERCHANT_PWD, null);

                NetApi.login(account, password, new AsyncHttpResponseHandler() {

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        try {
                            if (responseBody != null) {
                                String result = new String(responseBody);
                                LogUtil.log("result----" + result);
                                JSONObject obj = new JSONObject(result);
                                final String resultCode = obj.getString("resultCode");
                                String msg = obj.getString("msg");
                                long currentTime = System.currentTimeMillis();
                                long dTime = currentTime - lastCurrentTime;
                                if (dTime < 2000) {
                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            if ("00".equals(resultCode)) {
                                                // 登录成功就跳转到商家管理
                                                startActivity(new Intent(SplashActivity.this, MerchantManageActivity.class));
                                                //在请求结束后finish是为了避免异步请求还未完成时就finish导致的短暂出现桌面的情况
                                                finish();
                                            }else {
                                                //否则就回到首页
                                                startActivity(new Intent(SplashActivity.this, MainActivity.class));
                                                finish();
                                            }
                                        }
                                    },2000 - dTime);
                                }else {
                                    if ("00".equals(resultCode)) {
                                        // 登录成功就跳转到商家管理
                                        startActivity(new Intent(SplashActivity.this, MerchantManageActivity.class));
                                        //在请求结束后finish是为了避免异步请求还未完成时就finish导致的短暂出现桌面的情况
                                        finish();
                                    }else {
                                        //否则就回到首页
                                        startActivity(new Intent(SplashActivity.this, MainActivity.class));
                                        finish();
                                    }
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                        //否则就回到首页
                        startActivity(new Intent(SplashActivity.this, MainActivity.class));
                        finish();
                    }
                });

            }else{

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //否则跳转到登录页
                        startActivity(new Intent(SplashActivity.this, MainActivity.class));
                        finish();
                    }
                }, 2000);

            }
        }

    }

}
