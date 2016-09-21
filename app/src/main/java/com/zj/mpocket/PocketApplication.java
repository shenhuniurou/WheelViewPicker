package com.zj.mpocket;

import android.app.Application;
import android.content.Context;
import android.content.pm.PackageManager;

import com.baidu.mapapi.SDKInitializer;
import com.loopj.android.http.AsyncHttpClient;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiskCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.utils.StorageUtils;
import com.zj.mpocket.bdlocation.LocationService;
import com.zj.mpocket.utils.LogUtil;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;

import java.io.File;

/**
 * Created by Administrator on 2016/4/16.
 */
public class PocketApplication extends Application {

    public LocationService locationService;

    public static RefWatcher getRefWatcher(Context context) {
        PocketApplication application = (PocketApplication) context.getApplicationContext();
        return application.refWatcher;
    }

    private RefWatcher refWatcher;

    @Override
    public void onCreate() {
        super.onCreate();
        refWatcher = LeakCanary.install(this);
        init();
        String pkName = this.getPackageName();
        PackageManager pm = this.getPackageManager();
        String strVName = null;
        String curVName = null;
        /*try {
            strVName = pm.getPackageInfo(pkName, 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        curVName = engine.getCurrentVersion(this);*/

    }

    private void init() {
        // 初始化网络请求
        AsyncHttpClient client = new AsyncHttpClient();
        ApiHttpClient.setHttpClient(client);
        //初始化定位sdk
        locationService = new LocationService(getApplicationContext());
        SDKInitializer.initialize(getApplicationContext());
        initImageLoader(getApplicationContext());
        // Log控制器
        LogUtil.DEBUG = BuildConfig.DEBUG;
    }

    /** 初始化ImageLoader */
    public static void initImageLoader(Context context) {

        // 获取到缓存的目录地址
        File cacheDir = StorageUtils.getOwnCacheDirectory(context, "koudai/cache");
        // 创建配置ImageLoader(所有的选项都是可选的,只使用那些你真的想定制)，这个可以设定在APPLACATION里面，设置为全局的配置参数
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
                .threadPoolSize(3)
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .denyCacheImageMultipleSizesInMemory()
                .diskCacheFileNameGenerator(new Md5FileNameGenerator())// 将保存的时候的URI名称用MD5加密
                .diskCacheSize(50 * 1024 * 1024) // 50 MiB
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .diskCache(new UnlimitedDiskCache(cacheDir))// 自定义缓存路径
                .writeDebugLogs() // Remove for release app
                .build();
        // Initialize ImageLoader with configuration.
        ImageLoader.getInstance().init(config);// 全局初始化此配置

    }

}
