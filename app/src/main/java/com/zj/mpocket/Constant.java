package com.zj.mpocket;

import android.os.Environment;

import java.io.File;

/**
 * Created by Administrator on 2016/4/16.
 */
public class Constant {

    //约定的appid
    public static String clientId = "20001";

    //约定的key
    public static String key = "E10ADC3949BA59ABBE56E057F20F883E";

    //图片前缀
    //public static String IMG_HEADER = "http://112.74.126.9";

    public static String IMG_HEADER = "http://www.koudailingqian.com";

    //配置文件
    public static final String APP_CONFIG = "app_config";

    //是否是第一次安装，用于显示引导图
    public static final String NEW_INSTALL = "new_install";

    //商家信息配置文件
    public static final String USER_INFO = "user_info";

    //当前版本号
    public static final String CURRENT_VERSION = "current_version";

    //登录令牌
    public static final String TOKEN = "token";

    //主键ids
    public static final String ids = "ids";

    //商户id
    public static final String MERCHANT_Id = "merchant_id";

    //商户账号
    public static final String MERCHANT_ACCOUNT = "merchant_account";

    //商户密码
    public static final String MERCHANT_PWD = "merchant_pwd";

    // 默认存放文件下载的路径
    public final static String DEFAULT_SAVE_FILE_PATH = Environment
            .getExternalStorageDirectory()
            + File.separator
            + "koudai"
            + File.separator + "download" + File.separator;

}
