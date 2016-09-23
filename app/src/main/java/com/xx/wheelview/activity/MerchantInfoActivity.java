package com.zj.wheelview.activity;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
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
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;
import com.zj.wheelview.Constant;
import com.zj.wheelview.NetApi;
import com.zj.wheelview.R;
import com.zj.wheelview.utils.CommonUtil;
import com.zj.wheelview.utils.ImageUtils;
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

public class MerchantInfoActivity extends BaseActivity {

    private static final int ACTION_TYPE_PHOTO = 0;
    private static final int REQUEST_CODE_GETIMAGE_BY_CAMERA = 2;
    private static final int ACTION_TYPE_ALBUM_SINGLE = 1;
    private static final int REQUEST_CODE_GETIMAGE_BY_SDCARD = 4;
    private static final int CROP_RESULT = 5;
    private static final int REQUEST_CAMERA = 6;
    private static final int REQUEST_ALUMN = 7;
    ImageLoader imgaeLoader;
    String merInfo;
    String shopimage;
    String resultShopImage;
    Uri uri;
    String theLarge;
    String theThumbnail;
    File imgFile;
    int clickId;

    @InjectView(R.id.tvStoreName)
    TextView tvStoreName;
    @InjectView(R.id.tvLocation)
    TextView tvLocation;
    @InjectView(R.id.tvLocationAddr)
    TextView tvLocationAddr;
    @InjectView(R.id.tvMerchantIndustry)
    TextView tvMerchantIndustry;
    @InjectView(R.id.tvRealName)
    TextView tvRealName;
    @InjectView(R.id.tvTellphone)
    TextView tvTellphone;
    @InjectView(R.id.tvIdentityCard)
    TextView tvIdentityCard;
    @InjectView(R.id.tvEmail)
    TextView tvEmail;
    @InjectView(R.id.tvBankAccountName)
    TextView tvBankAccountName;
    @InjectView(R.id.tvBankName)
    TextView tvBankName;
    @InjectView(R.id.tvBankNum)
    TextView tvBankNum;
    @InjectView(R.id.ivIDcard)
    ImageView ivIDcard;
    @InjectView(R.id.ivLicense)
    ImageView ivLicense;

    @InjectView(R.id.btnUploadStorePic)
    ImageView btnUploadStorePic;
    @InjectView(R.id.llContainOne)
    LinearLayout llContainOne;
    @InjectView(R.id.ivStorePic1)
    ImageView ivStorePic1;
    @InjectView(R.id.ivStorePic2)
    ImageView ivStorePic2;

    @Override
    protected boolean hasBackButton() {
        return true;
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_merchant_info;
    }

    @Override
    protected int getTitleResId() {
        return R.string.title_activity_merchant_info;
    }

    @Override
    protected void initViews() {
        imgaeLoader = ImageLoader.getInstance();
        merInfo = getIntent().getStringExtra("merInfo");
        shopimage = getIntent().getStringExtra("shopimage");
        resultShopImage = shopimage;
        try {
            JSONObject merInfoObj = new JSONObject(merInfo);
            tvStoreName.setText(CommonUtil.isEmpty(merInfoObj.getString("name"))?"":merInfoObj.getString("name"));
            tvLocation.setText((CommonUtil.isEmpty(merInfoObj.getString("province"))?"":merInfoObj.getString("province")) + (CommonUtil.isEmpty(merInfoObj.getString("city"))?"":merInfoObj.getString("city")));
            tvLocationAddr.setText(CommonUtil.isEmpty(merInfoObj.getString("addr"))?"":merInfoObj.getString("addr"));
            tvMerchantIndustry.setText(CommonUtil.isEmpty(merInfoObj.getString("industry_name"))?"":merInfoObj.getString("industry_name"));
            tvRealName.setText(CommonUtil.isEmpty(merInfoObj.getString("contacts"))?"":merInfoObj.getString("contacts"));
            String tellphone = merInfoObj.getString("tellphone");
            tvTellphone.setText(CommonUtil.isEmpty(tellphone) ? "" : tellphone.replaceAll("(\\d{3})\\d{6}(\\d{2})", "$1******$2"));
            String identity_card = merInfoObj.getString("identity_card");
            if (CommonUtil.isEmpty(identity_card)) {
                identity_card = "";
            }else {
                if (identity_card.length() == 18) {
                    identity_card = identity_card.replaceAll("(\\d{2})\\d{14}(\\w{2})", "$1**************$2");
                }else if (identity_card.length() == 15) {
                    identity_card = identity_card.replaceAll("(\\d{2})\\d{11}(\\w{2})", "$1***********$2");
                }
            }
            tvIdentityCard.setText(identity_card);
            tvEmail.setText(CommonUtil.isEmpty(merInfoObj.getString("email"))?"":merInfoObj.getString("email"));
            tvBankAccountName.setText(CommonUtil.isEmpty(merInfoObj.getString("bank_acc"))?"":merInfoObj.getString("bank_acc"));
            tvBankName.setText(CommonUtil.isEmpty(merInfoObj.getString("bank_type"))?"":merInfoObj.getString("bank_type"));
            String bankNum = merInfoObj.getString("bank_num");
            if (CommonUtil.isEmpty(bankNum)) {
                bankNum = "";
            }else {
                bankNum = bankNum.replace(bankNum.substring(0, bankNum.length() - 4), "**************");
            }
            tvBankNum.setText(bankNum);
            String[] newShopImage = getNewShopImage();
            if (newShopImage != null) {
                if (newShopImage.length > 1) {
                    ivStorePic1.setVisibility(View.VISIBLE);
                    ivStorePic2.setVisibility(View.VISIBLE);
                    ImageLoader.getInstance().displayImage(Constant.IMG_HEADER + newShopImage[0], new ImageViewAware(ivStorePic1));
                    ImageLoader.getInstance().displayImage(Constant.IMG_HEADER + newShopImage[1], new ImageViewAware(ivStorePic2));
                } else if (newShopImage.length == 1) {
                    ivStorePic1.setVisibility(View.VISIBLE);
                    ImageLoader.getInstance().displayImage(Constant.IMG_HEADER + newShopImage[0], new ImageViewAware(ivStorePic1));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String[] getNewShopImage() {
        List<String> arrList = new ArrayList<>();
        if (CommonUtil.isEmpty(shopimage)) {
            return new String[]{};
        }else {
            String[] split = shopimage.split(":GGGGGG:");
            if (split.length > 0) {
                for (int i = 0; i < split.length; i++) {
                    if (!CommonUtil.isEmpty(split[i])) {
                        arrList.add(split[i]);
                    }
                }
            }
            return arrList.toArray(new String[]{});
        }
    }

    @OnClick(R.id.btnUploadStorePic)
    public void showSelectDialog() {
        clickId = R.id.btnUploadStorePic;
        if(ivStorePic2.getVisibility() == View.VISIBLE) {
            CommonUtil.showToastMessage(this, "不能上传更多店铺图片了");
            return;
        }
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
        Intent intent = new Intent(MerchantInfoActivity.this, GalleryActivity.class);
        intent.putExtra("single", true);
        startActivityForResult(intent, REQUEST_CODE_GETIMAGE_BY_SDCARD);
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
        startActivityForResult(intent, REQUEST_CODE_GETIMAGE_BY_CAMERA);
    }

    @OnClick(R.id.ivStorePic1)
    public void ivStorePic1() {
        clickId = R.id.ivStorePic1;
        AlertDialog.Builder builder = new AlertDialog.Builder(MerchantInfoActivity.this);
        builder.setTitle("重新选择图片");
        builder.setItems(new String[]{"拍照", "相册"}, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                selectPicture(which);
            }
        });
        builder.setNegativeButton("取消", null);
        builder.show();
    }

    @OnClick(R.id.ivStorePic2)
    public void ivStorePic2() {
        clickId = R.id.ivStorePic2;
        AlertDialog.Builder builder = new AlertDialog.Builder(MerchantInfoActivity.this);
        builder.setTitle("重新选择图片");
        builder.setItems(new String[]{"拍照", "相册"}, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                selectPicture(which);
            }
        });
        builder.setNegativeButton("取消", null);
        builder.show();
    }

    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent imageReturnIntent) {

        if (resultCode != Activity.RESULT_OK)
            return;

        new Thread() {
            private String selectedImagePath;

            @Override
            public void run() {
                if (requestCode == REQUEST_CODE_GETIMAGE_BY_SDCARD) {//从相册选择图片（店铺图片）
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
                        //cropPath = out.getPath();
                        Uri destination = Uri.fromFile(out);
                        //裁剪
                        Uri source = Uri.fromFile(new File(theLarge));
                        theLarge = out.getPath();
                        if (clickId == R.id.btnUploadStorePic && ivStorePic1.getVisibility() != View.VISIBLE) {
                            Crop.of(source, destination).withAspect(4, 3).start(MerchantInfoActivity.this);
                        }else if (clickId == R.id.btnUploadStorePic && ivStorePic1.getVisibility() == View.VISIBLE) {
                            Crop.of(source, destination).withAspect(16, 9).start(MerchantInfoActivity.this);
                        }else if (clickId == R.id.ivStorePic1 ) {
                            Crop.of(source, destination).withAspect(4, 3).start(MerchantInfoActivity.this);
                        }else if (clickId == R.id.ivStorePic2) {
                            Crop.of(source, destination).withAspect(16, 9).start(MerchantInfoActivity.this);
                        }
                    }
                } else if (requestCode == REQUEST_CODE_GETIMAGE_BY_CAMERA) {//拍照（店铺图片）
                    if (!CommonUtil.isEmpty(theLarge)) {
                        //裁剪
                        Uri destination = Uri.fromFile(new File(theLarge));
                        if (clickId == R.id.btnUploadStorePic && ivStorePic1.getVisibility() != View.VISIBLE) {
                            Crop.of(uri, destination).withAspect(4, 3).start(MerchantInfoActivity.this);
                        }else if (clickId == R.id.btnUploadStorePic && ivStorePic1.getVisibility() == View.VISIBLE) {
                            Crop.of(uri, destination).withAspect(16, 9).start(MerchantInfoActivity.this);
                        }else if (clickId == R.id.ivStorePic1 ) {
                            Crop.of(uri, destination).withAspect(4, 3).start(MerchantInfoActivity.this);
                        }else if (clickId == R.id.ivStorePic2) {
                            Crop.of(uri, destination).withAspect(16, 9).start(MerchantInfoActivity.this);
                        }
                        //Crop.of(uri, destination).withAspect(4, 3).start(MerchantInfoActivity.this);
                    }
                }else if (requestCode == Crop.REQUEST_CROP) {
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

    /**
     * 回收ImageView原有bitmap
     * @param imageView
     */
    public void recycleOriginalBitmap(ImageView imageView) {
        BitmapDrawable bitmapDrawable = (BitmapDrawable) imageView.getDrawable();
        if (bitmapDrawable != null && !(bitmapDrawable.getBitmap()).isRecycled()) {
            bitmapDrawable.getBitmap().recycle();
        }
    }

    private final Handler handler = new Handler() {
        @Override
        public void handleMessage(final Message msg) {
            if (msg.what == CROP_RESULT) {
                LogUtil.log("theLarge----" + theLarge);
                Bitmap bitmap = ImageUtils.loadImgThumbnail(theLarge, 400, 300);
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
                                ImageUtils.createImageThumbnail(MerchantInfoActivity.this, theLarge, theThumbnail, bitmap, 90);
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
            }else if (msg.what == 1 && msg.obj != null) {
                if (clickId == R.id.btnUploadStorePic) {
                    if (ivStorePic1.getVisibility() != View.VISIBLE) {
                        ivStorePic1.setVisibility(View.VISIBLE);
                        ivStorePic1.setImageBitmap((Bitmap) msg.obj);
                    }else if (ivStorePic1.getVisibility() == View.VISIBLE) {
                        ivStorePic2.setVisibility(View.VISIBLE);
                        ivStorePic2.setImageBitmap((Bitmap) msg.obj);
                    }
                }else if (clickId == R.id.ivStorePic1){
                    recycleOriginalBitmap(ivStorePic1);
                    ivStorePic1.setImageBitmap((Bitmap) msg.obj);
                }else if (clickId == R.id.ivStorePic2){
                    recycleOriginalBitmap(ivStorePic2);
                    ivStorePic2.setImageBitmap((Bitmap) msg.obj);
                }
                Message message = new Message();
                message.what = 2;
                handler.sendMessage(message);
            }else if (msg.what == 2) {
                //上传店铺图片
                NetApi.uploadImage(imgFile, new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        new File(theThumbnail).delete();
                        new File(theLarge).delete();
                        try {
                            if (responseBody != null) {
                                String result = new String(responseBody);
                                LogUtil.log(result);
                                JSONObject obj = new JSONObject(result);
                                String resultCode = obj.getString("resultCode");
                                String msg = obj.getString("msg");
                                if("00".equals(resultCode)) {
                                    if (clickId == R.id.ivStorePic1){
                                        String[] split = CommonUtil.split(shopimage);
                                        if (split != null && !CommonUtil.isEmpty(split[0])) {
                                            split[0] = obj.getString("path");
                                        }
                                        shopimage = CommonUtil.join(split);
                                    }else if (clickId == R.id.ivStorePic2){
                                        String[] split = CommonUtil.split(shopimage);
                                        if (split != null && !CommonUtil.isEmpty(split[1])) {
                                            split[1] = obj.getString("path");
                                        }
                                        shopimage = CommonUtil.join(split);
                                    }else {
                                        if (CommonUtil.isEmpty(shopimage)) {
                                            shopimage = obj.getString("path");
                                        }else {
                                            shopimage += ":GGGGGG:" + obj.getString("path");
                                        }
                                    }
                                    Message message = new Message();
                                    message.what = 3;
                                    handler.sendMessage(message);
                                }else {
                                    CommonUtil.showToastMessage(MerchantInfoActivity.this, msg);
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                        new File(theThumbnail).delete();
                        new File(theLarge).delete();
                        if (responseBody != null) {
                            LogUtil.log(new String(responseBody));
                        }
                    }
                });
            }else if (msg.what == 3) {
                NetApi.updateShopImage(MerchantInfoActivity.this, shopimage, new AsyncHttpResponseHandler() {
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
                                    CommonUtil.showToastMessage(MerchantInfoActivity.this, "修改店铺成功");
                                    resultShopImage = shopimage;
                                }else {
                                    CommonUtil.showToastMessage(MerchantInfoActivity.this, msg);
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                        if (responseBody != null) {
                            LogUtil.log(new String(responseBody));
                        }
                    }
                });
            }
        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
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

    @Override
    public void onBackPressed() {
        setResult(RESULT_OK, new Intent().putExtra("shopimage", resultShopImage));
        super.onBackPressed();
    }

}
