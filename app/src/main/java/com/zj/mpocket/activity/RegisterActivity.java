package com.zj.mpocket.activity;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.zj.mpocket.NetApi;
import com.zj.mpocket.PocketApplication;
import com.zj.mpocket.R;
import com.zj.mpocket.bdlocation.LocationService;
import com.zj.mpocket.model.BankModel;
import com.zj.mpocket.model.ChildrenClass;
import com.zj.mpocket.model.StoreClass;
import com.zj.mpocket.utils.CommonUtil;
import com.zj.mpocket.utils.ImageUtils;
import com.zj.mpocket.utils.LogUtil;
import com.zj.mpocket.view.wheel.WheelMain;
import com.soundcloud.android.crop.Crop;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.InjectView;
import butterknife.OnClick;
import cz.msebera.android.httpclient.Header;

public class RegisterActivity extends BaseActivity implements WheelMain.WheelMainListener {

    private static final int REQUEST_ACCESS_FINE_LOCATION = 1;
    private static final int REQUEST_CAMERA = 2;
    private static final int REQUEST_ALUMN = 3;
    @InjectView(R.id.tvLocationCity)
    TextView tvLocationCity;
    @InjectView(R.id.tvLocationStreet)
    EditText tvLocationStreet;
    @InjectView(R.id.tvProClass)
    TextView tvProClass;
    @InjectView(R.id.bankSpinner)
    Spinner bankSpinner;
    @InjectView(R.id.tvSubmit)
    TextView tvSubmit;
    @InjectView(R.id.etStoreName)
    EditText etStoreName;
    @InjectView(R.id.etRealName)
    EditText etRealName;
    @InjectView(R.id.etTellphone)
    EditText etTellphone;
    @InjectView(R.id.etIdCard)
    EditText etIdCard;
    @InjectView(R.id.etEmail)
    EditText etEmail;
    @InjectView(R.id.etAccountName)
    EditText etAccountName;
    @InjectView(R.id.etBankNum)
    EditText etBankNum;
    @InjectView(R.id.ivBankImg)
    ImageView ivBankImg;
    @InjectView(R.id.ivLicense)
    ImageView ivLicense;
    @InjectView(R.id.llContainOne)
    LinearLayout llContainOne;
    @InjectView(R.id.etUndiscount)
    EditText etUndiscount;

    private LocationService locationService;
    double latitude;
    double lontitude;
    String province;
    String city;
    String district;
    String addr;
    String merchant_industry;
    String bankType;
    WheelMain wheelMain;
    List<StoreClass> proClassList;
    List<BankModel> bankList;
    PopupWindow popwindow;
    ArrayAdapter<String > bankAdapter;

    String name;
    String contacts;
    String tellphone;
    String identity_card;
    String email;
    String bank_acc;
    String bank_num;
    String undiscount;
    String id_card_image = null;//身份证图片
    String merchant_license = null;//营业执照图片
    String imageurls = "" ;//店铺图片路径
    boolean hasIdCard = false;
    boolean hasLicense = false;
    boolean hasStorePics = false;
    File bankImgFile;
    File licenseFile;
    File file43;
    File file169;
    List<File> fileList;
    Uri uri;
    ArrayList<View> selectedViews;

    /** 打开相机 */
    public static final int ACTION_TYPE_PHOTO = 0;
    /** 打开相册single */
    public static final int ACTION_TYPE_ALBUM_SINGLE = 1;
    /** 请求相册 */
    public static final int REQUEST_CODE_GETIMAGE_BY_SDCARD = 2;
    /** 请求相机 */
    public static final int REQUEST_CODE_GETIMAGE_BY_CAMERA = 3;

    public static final int REQUEST_CODE_GETIMAGE_BY_CAMERA_STORE = 4;

    public static final int REQUEST_CODE_GETIMAGE_BY_SDCARD_STORE = 5;

    public static final int CROP_RESULT = 8;

    private String theLarge, theThumbnail;
    private File imgFile;
    int clickId = -1;
    int count = 0;

    @Override
    protected boolean hasBackButton() {
        return true;
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_register;
    }

    @Override
    protected int getTitleResId() {
        return R.string.title_activity_register;
    }

    @Override
    protected void initViews() {
        selectedViews = new ArrayList<>();
        fileList = new ArrayList<>();
        //获取商店分类数据
        getProClassList();
        //23及以上版本要动态获取权限
        if (Build.VERSION.SDK_INT >= 23) {
            int checkCallPhonePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
            if(checkCallPhonePermission != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_ACCESS_FINE_LOCATION);
                return;
            }else {
                startLocation();
            }
        }else {
            startLocation();
        }
    }

    public void startLocation() {
        locationService = ((PocketApplication) getApplication()).locationService;
        //注册监听
        locationService.registerListener(mListener);
        locationService.setLocationOption(locationService.getDefaultLocationClientOption());
        locationService.start();// 开始定位
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_ACCESS_FINE_LOCATION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission Granted
                    startLocation();
                } else {
                    // Permission Denied
                    CommonUtil.showToastMessage(this, "定位权限被拒绝");
                }
                break;
            case REQUEST_CAMERA:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    byCamera();
                } else {
                    CommonUtil.showToastMessage(this, "拍照权限被拒绝");
                }
                break;
            case REQUEST_ALUMN:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    byAlumn();
                } else {
                    CommonUtil.showToastMessage(this, "访问相册权限被拒绝");
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    /**
     * 获取商店分类
     */
    public void getProClassList() {
        NetApi.getProclassList(new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {
                try {
                    if (responseBody != null) {
                        String result = new String(responseBody);
                        LogUtil.log(result);
                        JSONObject obj = new JSONObject(result);
                        String resultCode = obj.getString("resultCode");
                        String msg = obj.getString("msg");
                        String classList = obj.getString("classList");
                        if("00".equals(resultCode)) {
                            proClassList = JSON.parseArray(classList, StoreClass.class);
                            StoreClass storeClass = proClassList.get(0);
                            ChildrenClass childrenClass = storeClass.getChildren().get(0);
                            //默认取第一个一级分类的第一个二级分类
                            tvProClass.setText(childrenClass.getText());
                            merchant_industry = childrenClass.getValue();
                            //初始化商店分类的popupwindow
                            initPopupWindow();
                        }else {
                            CommonUtil.showToastMessage(RegisterActivity.this, "获取商店分类失败：" + msg);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error) {
                if (responseBody != null) {
                    LogUtil.log(new String(responseBody));
                }
            }
        });
    }

    /**
     * 获取银行列表
     */
    private void getBankList() {
        NetApi.getBankList(RegisterActivity.this, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {
                try {
                    if (responseBody != null) {
                        String result = new String(responseBody);
                        LogUtil.log(result);
                        JSONObject obj = new JSONObject(result);
                        String resultCode = obj.getString("resultCode");
                        String msg = obj.getString("msg");
                        if("00".equals(resultCode)) {
                            bankList = JSON.parseArray(obj.getString("bankList"), BankModel.class);
                            String[] arr = new String[bankList.size()];
                            int selection = -1;
                            boolean flag = false;
                            for (int i = 0; i < bankList.size(); i++) {
                                BankModel bankModel = bankList.get(i);
                                if (!flag && bankModel.getCity().equals(city)) {
                                    selection = i;
                                    flag = true;
                                    bankType = bankModel.getName();
                                }
                                arr[i] = bankModel.getName();
                            }
                            bankAdapter = new ArrayAdapter<String>(RegisterActivity.this, android.R.layout.simple_spinner_item, arr);
                            //设置下拉列表的风格
                            bankAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            //将adapter 添加到spinner中
                            bankSpinner.setAdapter(bankAdapter);
                            bankSpinner.setSelection(selection, true);
                            bankSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                    bankType = bankList.get(position).getName();
                                    LogUtil.log("backType----" + bankType);
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> parent) {

                                }
                            });
                        }else {
                            CommonUtil.showToastMessage(RegisterActivity.this, "获取银行列表失败：" + msg);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error) {
                if (responseBody != null) {
                    LogUtil.log(new String(responseBody));
                }
            }
        });
    }

    @Override
    public void getIndustry(String text, String value) {
        tvProClass.setText(text);
        merchant_industry = value;
    }

    public void initPopupWindow() {
        LayoutInflater inflater = LayoutInflater.from(RegisterActivity.this);
        View classpickerview = inflater.inflate(R.layout.layout_class_picker, null);
        wheelMain = new WheelMain(classpickerview);
        wheelMain.setListener(RegisterActivity.this);
        WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
        DisplayMetrics metrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(metrics);
        wheelMain.screenheight = metrics.widthPixels;
        wheelMain.initPickerView(proClassList);
        popwindow = new PopupWindow(classpickerview, WindowManager.LayoutParams.MATCH_PARENT, metrics.heightPixels / 4 + 100);
        popwindow.setFocusable(true);
        //实例化一个ColorDrawable颜色为半透明
        ColorDrawable dw = new ColorDrawable(0xb0000000);
        popwindow.setBackgroundDrawable(dw);
        // 设置popWindow的显示和消失动画
        popwindow.setAnimationStyle(R.style.popwindow_anim_style);
    }

    @OnClick(R.id.tvProClass)
    public void showPopupWindow() {
        if (proClassList != null) {
            if (popwindow.isShowing()) {
                popwindow.dismiss();
            }else {
                popwindow.showAtLocation(RegisterActivity.this.findViewById(R.id.main), Gravity.BOTTOM |Gravity.CENTER_HORIZONTAL, 0, 0);
            }
        }
    }

    /***
     * Stop location service
     */
    @Override
    protected void onDestroy() {
        //注销掉监听
        locationService.unregisterListener(mListener);
        //停止定位服务
        locationService.stop();
        super.onDestroy();
    }

    /**
     * 定位结果回调，重写onReceiveLocation方法
     */
    private BDLocationListener mListener = new BDLocationListener() {

        @Override
        public void onReceiveLocation(BDLocation location) {
            if (null != location && location.getLocType() != BDLocation.TypeServerError) {
                //定位成功，停止定位
                locationService.stop();
                latitude = location.getLatitude();
                lontitude = location.getLongitude();
                province = location.getProvince();
                city = location.getCity();
                district = location.getDistrict();
                String street = location.getStreet();
                String streetNumber = location.getStreetNumber();
                if (CommonUtil.isEmpty(province)) province = "";
                if (CommonUtil.isEmpty(city)) city = "";
                if (CommonUtil.isEmpty(district)) district = "";
                if (CommonUtil.isEmpty(street)) street = "";
                if (CommonUtil.isEmpty(streetNumber)) streetNumber = "";
                tvLocationCity.setText(province + city);
                tvLocationStreet.setText(district + street + streetNumber);
                //获取合作银行列表
                getBankList();
            }
        }

    };

    @OnClick(R.id.btnUploadBankImg)
    public void btnUploadBankImg() {
        showChooseTypeDialog(R.id.btnUploadBankImg);
    }

    @OnClick(R.id.btnUploadLicense)
    public void uploadLicense() {
        showChooseTypeDialog(R.id.btnUploadLicense);
    }

    @OnClick(R.id.btnUploadStorePic)
    public void uploadStorePic() {
        if (fileList != null && fileList.size() > 1) {
            CommonUtil.showToastMessage(RegisterActivity.this, "不能上传更多店铺图片了！");
        }else {
            showChooseTypeDialog(R.id.btnUploadStorePic);
        }

    }

    //弹出拍照和相册的选择框
    public void showChooseTypeDialog(final int id) {
        clickId = id;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setItems(new String[]{"拍照", "相册"}, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                selectPicture(which);
            }
        });
        builder.setNegativeButton("取消", null);
        builder.show();
    }

    private void selectPicture(int which) {
        switch (which) {
            case ACTION_TYPE_PHOTO://拍照
                //23及以上版本要动态获取权限
                if (Build.VERSION.SDK_INT >= 23) {
                    int checkCallPhonePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
                    if(checkCallPhonePermission != PackageManager.PERMISSION_GRANTED){
                        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA);
                        return;
                    }else {
                        byCamera();
                    }
                }else {
                    byCamera();
                }
                break;
            case ACTION_TYPE_ALBUM_SINGLE://进入相册
                if (Build.VERSION.SDK_INT >= 23) {
                    int checkCallPhonePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
                    if(checkCallPhonePermission != PackageManager.PERMISSION_GRANTED){
                        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_ALUMN);
                        return;
                    }else {
                        byAlumn();
                    }
                }else {
                    byAlumn();
                }

                break;
        }
    }

    public void byAlumn() {
        Intent intent = new Intent(RegisterActivity.this, GalleryActivity.class);
        intent.putExtra("single", true);
        if (clickId == R.id.btnUploadBankImg || clickId == R.id.btnUploadLicense) {
            startActivityForResult(intent, REQUEST_CODE_GETIMAGE_BY_SDCARD);
        }else if (clickId == R.id.btnUploadStorePic){
            startActivityForResult(intent, REQUEST_CODE_GETIMAGE_BY_SDCARD_STORE);
        }
    }

    public void byCamera() {
        // 判断是否挂载了SD卡
        String savePath = "";
        String storageState = Environment.getExternalStorageState();
        if (storageState.equals(Environment.MEDIA_MOUNTED)) {
            savePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/koudai/camera/";
            File savedir = new File(savePath);
            if (!savedir.exists()) {
                savedir.mkdirs();
            }
        }

        // 没有挂载SD卡，无法保存文件
        if (savePath == null || "".equals(savePath)) {
            System.out.println("无法保存照片，请检查SD卡是否挂载");
            return;
        }

        String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        //照片命名
        String fileName = timeStamp + ".jpg";
        File out = new File(savePath, fileName);
        uri = Uri.fromFile(out);
        //该照片的绝对路径
        theLarge = savePath + fileName;
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        if (clickId == R.id.btnUploadBankImg || clickId == R.id.btnUploadLicense) {
            startActivityForResult(intent, REQUEST_CODE_GETIMAGE_BY_CAMERA);
        }else if (clickId == R.id.btnUploadStorePic){
            startActivityForResult(intent, REQUEST_CODE_GETIMAGE_BY_CAMERA_STORE);
        }
    }

    @OnClick(R.id.tvSubmit)
    public void submitCheck() {
        count = 0;
        imageurls = "";
        name = etStoreName.getText().toString().trim();
        if (TextUtils.isEmpty(name)) {
            CommonUtil.showToastMessage(RegisterActivity.this, "店铺名称不能为空");
            return;
        }
        addr = tvLocationStreet.getText().toString().trim();
        if (TextUtils.isEmpty(addr)) {
            CommonUtil.showToastMessage(RegisterActivity.this, "街道地址不能为空");
            return;
        }
        if (TextUtils.isEmpty(merchant_industry)) {
            CommonUtil.showToastMessage(RegisterActivity.this, "请选择商店分类");
            return;
        }
        contacts = etRealName.getText().toString().trim();
        if (TextUtils.isEmpty(contacts)) {
            CommonUtil.showToastMessage(RegisterActivity.this, "真实姓名不能为空");
            return;
        }
        tellphone = etTellphone.getText().toString().trim();
        if (TextUtils.isEmpty(tellphone)) {
            CommonUtil.showToastMessage(RegisterActivity.this, "手机号码不能为空");
            return;
        }
        //验证手机号码格式
        if (!CommonUtil.checkMobileNumber(tellphone)) {
            CommonUtil.showToastMessage(RegisterActivity.this, "手机号码格式不正确");
            return;
        }
        identity_card = etIdCard.getText().toString().trim();
        if (TextUtils.isEmpty(identity_card)) {
            CommonUtil.showToastMessage(RegisterActivity.this, "身份证号不能为空");
            return;
        }
        //验证身份证格式
        if (!CommonUtil.checkIdCard(identity_card)) {
            CommonUtil.showToastMessage(RegisterActivity.this, "身份证号格式不正确");
            return;
        }
        email = etEmail.getText().toString().trim();
        if (TextUtils.isEmpty(email)) {
            email = "";
        }else {
            //验证身份证格式
            if (!CommonUtil.checkEmail(email)) {
                CommonUtil.showToastMessage(RegisterActivity.this, "邮箱格式不正确");
                return;
            }
        }
        bank_acc = etAccountName.getText().toString().trim();
        if (TextUtils.isEmpty(bank_acc)) {
            CommonUtil.showToastMessage(RegisterActivity.this, "收款户名不能为空");
            return;
        }
        bank_num = etBankNum.getText().toString().trim();
        if (TextUtils.isEmpty(bank_num)) {
            CommonUtil.showToastMessage(RegisterActivity.this, "收款账号不能为空");
            return;
        }
        if (!hasIdCard) {
            CommonUtil.showToastMessage(RegisterActivity.this, "请上传银行卡照片");
            return;
        }
        if (!hasLicense) {
            CommonUtil.showToastMessage(RegisterActivity.this, "请上传营业执照");
            return;
        }
        if (!hasStorePics) {
            CommonUtil.showToastMessage(RegisterActivity.this, "店铺图片必须要上传2张");
            return;
        }
        undiscount = etUndiscount.getText().toString().trim();
        if (TextUtils.isEmpty(undiscount)) {
            CommonUtil.showToastMessage(RegisterActivity.this, "折扣不能为空");
            return;
        }
        if (Float.parseFloat(undiscount) < 0 || Float.parseFloat(undiscount) > 10) {
            CommonUtil.showToastMessage(RegisterActivity.this, "折扣必须在0-10之间");
            return;
        }
        showWaitDialog("上传银行卡图片...");
        Message msg = new Message();
        msg.what = 111;
        handler.sendMessage(msg);
    }

    private final Handler handler = new Handler() {
        @Override
        public void handleMessage(final Message msg) {
            if (msg.what == CROP_RESULT) {
                Bitmap bitmap = ImageUtils.loadImgThumbnail(theLarge, 500, 350);
                if (bitmap != null) {
                    // 存放照片的文件夹
                    String savePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/koudai/camera/";
                    String largeFileName = theLarge.substring(theLarge.lastIndexOf(File.separator) + 1);
                    String largeFilePath = savePath + largeFileName;
                    // 判断是否已存在缩略图
                    if (largeFileName.startsWith("thumb_") && new File(largeFilePath).exists()) {
                        theThumbnail = largeFilePath;
                        imgFile = new File(theThumbnail);
                    } else {
                        String thumbFileName = "thumb_" + largeFileName;
                        theThumbnail = savePath + thumbFileName;
                        if (new File(theThumbnail).exists()) {
                            imgFile = new File(theThumbnail);
                        } else {
                            try {
                                // 压缩上传的图片
                                ImageUtils.createImageThumbnail(RegisterActivity.this, theLarge, theThumbnail, bitmap, 90);
                                imgFile = new File(theThumbnail);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    new File(theLarge).delete();
                    Message message = new Message();
                    message.what = 1;
                    message.obj = bitmap;
                    handler.sendMessage(message);
                }
            }else if (msg.what == 1 && msg.obj != null) {
                if (clickId == R.id.btnUploadBankImg) {
                    //先回收之前的bitmap
                    BitmapDrawable bitmapDrawable = (BitmapDrawable)ivBankImg.getDrawable();
                    if (!(bitmapDrawable.getBitmap().isRecycled())) {
                        bitmapDrawable.getBitmap().recycle();
                    }
                    ivBankImg.setImageBitmap((Bitmap) msg.obj);
                    bankImgFile = imgFile;
                    imgFile = null;
                    hasIdCard = true;
                }else if ( clickId == R.id.btnUploadLicense) {
                    BitmapDrawable bitmapDrawable = (BitmapDrawable)ivLicense.getDrawable();
                    if (!(bitmapDrawable.getBitmap().isRecycled())) {
                        bitmapDrawable.getBitmap().recycle();
                    }
                    ivLicense.setImageBitmap((Bitmap) msg.obj);
                    licenseFile = imgFile;
                    imgFile = null;
                    hasLicense = true;
                }else if ( clickId == R.id.btnUploadStorePic) {
                    View view = LayoutInflater.from(RegisterActivity.this).inflate(R.layout.layout_image_item, null);
                    ImageView ivBitamp = (ImageView) view.findViewById(R.id.ivBitmap);
                    ImageView ivDelete = (ImageView) view.findViewById(R.id.ivDelete);
                    LinearLayout.LayoutParams params =  new LinearLayout.LayoutParams(CommonUtil.dip2px(RegisterActivity.this, 100), CommonUtil.dip2px(RegisterActivity.this, 100));
                    params.setMargins(CommonUtil.dip2px(RegisterActivity.this, 2), CommonUtil.dip2px(RegisterActivity.this, 2), CommonUtil.dip2px(RegisterActivity.this, 8), CommonUtil.dip2px(RegisterActivity.this, 2));
                    view.setLayoutParams(params);
                    view.setTag(theLarge);
                    ivBitamp.setImageBitmap((Bitmap) msg.obj);
                    selectedViews.add(view);
                    if (file43 == null) {
                        file43 = imgFile;
                        fileList.add(file43);
                        ivDelete.setTag(file43);
                    }else {
                        file169 = imgFile;
                        fileList.add(file169);
                        ivDelete.setTag(file169);
                    }
                    llContainOne.addView(view);
                    if (fileList.size() == 2) {
                        hasStorePics = true;
                    }
                    ivDelete.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            hasStorePics = false;
                            ImageView imageView = (ImageView)((View)v.getParent()).findViewById(R.id.ivBitmap);
                            imageView.setDrawingCacheEnabled(true);
                            Bitmap bitmap = imageView.getDrawingCache();
                            imageView.setDrawingCacheEnabled(false);
                            if (bitmap != null) {
                                bitmap.recycle();
                                bitmap = null;
                            }
                            File tagFile = (File)v.getTag();
                            if (tagFile.length() == file43.length()) {
                                file43.delete();
                                file43 = null;
                            }else if (tagFile.length() == file169.length()) {
                                file169.delete();
                                file169 = null;
                            }
                            fileList.remove(tagFile);
                            llContainOne.removeView((View)v.getParent());
                        }
                    });
                }
            }else if (msg.what == 111) {
                NetApi.uploadImage(bankImgFile, new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        try {
                            if (responseBody != null) {
                                String result = new String(responseBody);
                                LogUtil.log(result);
                                JSONObject obj = new JSONObject(result);
                                String resultCode = obj.getString("resultCode");
                                String msg = obj.getString("msg");
                                if("00".equals(resultCode)) {
                                    id_card_image = obj.getString("path");
                                    Message message = new Message();
                                    message.what = 222;
                                    handler.sendMessage(message);
                                }else {
                                    hideWaitDialog();
                                    CommonUtil.showToastMessage(RegisterActivity.this, "银行卡上传失败");
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                        hideWaitDialog();
                        if (responseBody != null) {
                            LogUtil.log(new String(responseBody));
                        }
                    }
                });
            }else if (msg.what == 222) {
                setWaitDialogMessage("上传营业执照图片...");
                NetApi.uploadImage(licenseFile, new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        try {
                            if (responseBody != null) {
                                String result = new String(responseBody);
                                LogUtil.log(result);
                                JSONObject obj = new JSONObject(result);
                                String resultCode = obj.getString("resultCode");
                                String msg = obj.getString("msg");
                                if("00".equals(resultCode)) {
                                    merchant_license = obj.getString("path");
                                    Message message = new Message();
                                    message.what = 333;
                                    handler.sendMessage(message);
                                }else {
                                    hideWaitDialog();
                                    CommonUtil.showToastMessage(RegisterActivity.this, "营业执照上传失败");
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                        hideWaitDialog();
                        if (responseBody != null) {
                            LogUtil.log(new String(responseBody));
                        }
                    }
                });
            }else if (msg.what == 333) {
                setWaitDialogMessage("上传店铺图片...");
                //上传店铺图片
                for (int i = 0; i < fileList.size(); i++) {
                    final File storePicFile = fileList.get(i);
                    NetApi.uploadImage(storePicFile, new AsyncHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                            try {
                                if (responseBody != null) {
                                    String result = new String(responseBody);
                                    LogUtil.log(result);
                                    JSONObject obj = new JSONObject(result);
                                    String resultCode = obj.getString("resultCode");
                                    String msg = obj.getString("msg");
                                    if("00".equals(resultCode)) {
                                        count++;
                                        if (count == fileList.size()) {//第二张
                                            if (storePicFile.length() == file43.length()) {
                                                imageurls = obj.getString("path") + ":GGGGGG:" + imageurls;
                                            }else if (storePicFile.length() == file169.length()) {
                                                imageurls += obj.getString("path");
                                            }
                                            Message message = new Message();
                                            message.what = 444;
                                            handler.sendMessage(message);
                                        }else {//第一张
                                            if (storePicFile.length() == file43.length()) {
                                                imageurls = obj.getString("path") + ":GGGGGG:";
                                            }else if (storePicFile.length() == file169.length()) {
                                                imageurls = obj.getString("path");
                                            }
                                        }
                                    }else {
                                        hideWaitDialog();
                                        CommonUtil.showToastMessage(RegisterActivity.this, "店铺图片上传失败");
                                    }
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                            hideWaitDialog();
                            if (responseBody != null) {
                                LogUtil.log(new String(responseBody));
                            }
                        }
                    });
                }
            }else if (msg.what == 444) {
                setWaitDialogMessage("提交审核...");
                NetApi.register(name, addr, province, city, district, merchant_industry, Float.parseFloat(undiscount), tellphone, contacts, (float) lontitude, (float) latitude,
                        identity_card, bankType, bank_acc, bank_num, email, id_card_image, merchant_license, imageurls,
                        new AsyncHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                                hideWaitDialog();
                                try {
                                    if (responseBody != null) {
                                        String result = new String(responseBody);
                                        LogUtil.log(result);
                                        JSONObject obj = new JSONObject(result);
                                        String resultCode = obj.getString("resultCode");
                                        String msg = obj.getString("msg");
                                        if("00".equals(resultCode)) {
                                            bankImgFile.delete();
                                            licenseFile.delete();
                                            for (int i = 0; i < fileList.size(); i++) {
                                                fileList.get(i).delete();
                                            }
                                            //注册成功，跳转到申请成功页面
                                            startActivity(new Intent(RegisterActivity.this, CheckRegisterActivity.class));
                                            finish();
                                        }else {
                                            CommonUtil.showToastMessage(RegisterActivity.this, msg);
                                        }
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                                hideWaitDialog();
                                if (responseBody != null) {
                                    LogUtil.log(new String(responseBody));
                                }
                            }
                        }
                );
            }
        }
    };

    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent imageReturnIntent) {

        if (resultCode != Activity.RESULT_OK)
            return;

        new Thread() {
            private String selectedImagePath;

            @Override
            public void run() {
                if (requestCode == REQUEST_CODE_GETIMAGE_BY_SDCARD) {//从相册选择图片（银行卡或者营业执照）
                    if (imageReturnIntent == null)
                        return;
                    selectedImagePath = imageReturnIntent.getStringExtra("imgPath");
                    if (selectedImagePath != null) {
                        theLarge = selectedImagePath;
                    }
                    if (!CommonUtil.isEmpty(theLarge)) {
                        Bitmap bitmap = ImageUtils.loadImgThumbnail(theLarge, 500, 350);
                        if (bitmap != null) {
                            // 存放照片的文件夹
                            String savePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/koudai/camera/";
                            String largeFileName = theLarge.substring(theLarge.lastIndexOf(File.separator) + 1);
                            String largeFilePath = savePath + largeFileName;
                            // 判断是否已存在缩略图
                            if (largeFileName.startsWith("thumb_") && new File(largeFilePath).exists()) {
                                theThumbnail = largeFilePath;
                                imgFile = new File(theThumbnail);
                            } else {
                                String thumbFileName = "thumb_" + largeFileName;
                                theThumbnail = savePath + thumbFileName;
                                if (new File(theThumbnail).exists()) {
                                    imgFile = new File(theThumbnail);
                                } else {
                                    try {
                                        // 压缩上传的图片
                                        ImageUtils.createImageThumbnail(RegisterActivity.this, theLarge, theThumbnail, bitmap, 90);
                                        imgFile = new File(theThumbnail);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                            Message message = new Message();
                            message.what = 1;
                            message.obj = bitmap;
                            handler.sendMessage(message);
                        }
                    }
                } else if (requestCode == REQUEST_CODE_GETIMAGE_BY_SDCARD_STORE) {//从相册选择图片（店铺图片）
                    if (imageReturnIntent == null)
                        return;
                    selectedImagePath = imageReturnIntent.getStringExtra("imgPath");
                    if (selectedImagePath != null) {
                        theLarge = selectedImagePath;
                    }
                    if (!CommonUtil.isEmpty(theLarge)) {
                        String savePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/koudai/camera/";
                        File savedir = new File(savePath);
                        if (!savedir.exists()) {
                            savedir.mkdirs();
                        }
                        String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
                        String fileName = timeStamp + ".jpg";
                        File out = new File(savePath, fileName);
                        Uri destination = Uri.fromFile(out);
                        //裁剪
                        Uri source = Uri.fromFile(new File(theLarge));
                        theLarge = out.getPath();
                        if (file43 == null) {
                            Crop.of(source, destination).withAspect(4, 3).start(RegisterActivity.this);
                        }else if (file43 != null && file43.exists()) {
                            Crop.of(source, destination).withAspect(16, 9).start(RegisterActivity.this);
                        }
                    }
                } else if (requestCode == REQUEST_CODE_GETIMAGE_BY_CAMERA) {//拍照（银行卡或者营业执照）
                    if (!CommonUtil.isEmpty(theLarge)) {
                        Bitmap bitmap = ImageUtils.loadImgThumbnail(theLarge, 500, 350);
                        if (bitmap != null) {
                            // 存放照片的文件夹
                            String savePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/koudai/camera/";
                            String largeFileName = theLarge.substring(theLarge.lastIndexOf(File.separator) + 1);
                            String largeFilePath = savePath + largeFileName;
                            // 判断是否已存在缩略图
                            if (largeFileName.startsWith("thumb_") && new File(largeFilePath).exists()) {
                                theThumbnail = largeFilePath;
                                imgFile = new File(theThumbnail);
                            } else {
                                String thumbFileName = "thumb_" + largeFileName;
                                theThumbnail = savePath + thumbFileName;
                                if (new File(theThumbnail).exists()) {
                                    imgFile = new File(theThumbnail);
                                } else {
                                    try {
                                        // 压缩上传的图片
                                        ImageUtils.createImageThumbnail(RegisterActivity.this, theLarge, theThumbnail, bitmap, 90);
                                        imgFile = new File(theThumbnail);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                            Message message = new Message();
                            message.what = 1;
                            message.obj = bitmap;
                            handler.sendMessage(message);
                        }
                    }
                }else if (requestCode == REQUEST_CODE_GETIMAGE_BY_CAMERA_STORE) {//拍照（店铺图片）
                    if ( !CommonUtil.isEmpty(theLarge)) {
                        //裁剪
                        Uri destination = Uri.fromFile(new File(theLarge));
                        if (file43 == null) {
                            Crop.of(uri, destination).withAspect(4, 3).start(RegisterActivity.this);
                        }else if (file43 != null && file43.exists()) {
                            Crop.of(uri, destination).withAspect(16, 9).start(RegisterActivity.this);
                        }
                    }
                } else if (requestCode == Crop.REQUEST_CROP) {
                    if (resultCode == RESULT_OK) {
                        //裁剪成功后到handler去处理
                        Message msg = new Message();
                        msg.what = CROP_RESULT;
                        handler.sendMessage(msg);
                    }
                }
            };
        }.start();
    }

}
