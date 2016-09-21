package com.zj.mpocket.activity;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.zj.mpocket.Constant;
import com.zj.mpocket.R;
import com.zj.mpocket.adapter.ViewPagerAdapter;
import com.zj.mpocket.utils.PreferenceUtil;

import java.util.ArrayList;
import java.util.List;

public class GuideActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener {

    private ViewPager vp;
    private ViewPagerAdapter vpAdapter;
    private List<View> views;

    // 底部小点图片
    private ImageView[] points;

    // 记录当前选中位置
    private int currentIndex = 0;

    private GestureDetector gestureDetector; // 用户滑动

    private int flaggingWidth;// 互动翻页滚动的长度是当前屏幕宽度

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_guide);
        initViews();
    }

    public void initViews() {
        // 初始化页面
        LayoutInflater inflater = LayoutInflater.from(this);
        views = new ArrayList<>();
        // 初始化引导图片
        views.add(inflater.inflate(R.layout.guide_one, null));
        views.add(inflater.inflate(R.layout.guide_two, null));
        views.add(inflater.inflate(R.layout.guide_three, null));

        gestureDetector = new GestureDetector(new GuideViewTouch());
        // 获取分辨率
        DisplayMetrics displaymetrics = new DisplayMetrics();
        ((WindowManager)getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(displaymetrics);
        flaggingWidth = displaymetrics.widthPixels / 4;

        // 初始化Adapter
        vpAdapter = new ViewPagerAdapter(this, views);

        vp = (ViewPager) findViewById(R.id.viewpager);
        vp.setAdapter(vpAdapter);
        // 绑定回调
        vp.setOnPageChangeListener(this);
        // 初始化底部小圆圈
        initPoints();
    }

    private void initPoints() {
        LinearLayout ll = (LinearLayout) findViewById(R.id.llpoint);
        points = new ImageView[views.size()];
        // 循环取得小点图片
        for (int i = 0; i < views.size(); i++) {
            points[i] = (ImageView) ll.getChildAt(i);
            points[i].setEnabled(false);// 都设为白色
            points[i].invalidate();
        }
        points[0].setEnabled(true);
    }

    private void setCurrentDot(int position) {
        if (position < 0 || position > views.size() - 1 || currentIndex == position) {
            return;
        }
        points[position].setEnabled(true);
        points[currentIndex].setEnabled(false);
        currentIndex = position;
    }

    @Override
    public void onPageScrollStateChanged(int arg0) {
    }

    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {
    }

    @Override
    public void onPageSelected(int arg0) {
        // 设置底部小点选中状态
        setCurrentDot(arg0);
        currentIndex = arg0;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (gestureDetector.onTouchEvent(event)) {
            event.setAction(MotionEvent.ACTION_CANCEL);
        }
        return super.dispatchTouchEvent(event);
    }

    private class GuideViewTouch extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            if (currentIndex == points.length - 1) {
                if (Math.abs(e1.getX() - e2.getX()) > Math.abs(e1.getY() - e2.getY()) && (e1.getX() - e2.getX() <= (-flaggingWidth) || e1.getX() - e2.getX() >= flaggingWidth)) {
                    if (e1.getX() - e2.getX() >= flaggingWidth) {
                        //设置已经引导过了，下次启动不用再次引导
                        PreferenceUtil.setPrefBoolean(GuideActivity.this, Constant.APP_CONFIG, Context.MODE_PRIVATE, Constant.NEW_INSTALL, false);
                        Intent intent = new Intent(GuideActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                        return true;
                    }
                }
            }
            return false;
        }
    }

}
