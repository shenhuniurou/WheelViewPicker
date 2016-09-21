package com.zj.mpocket.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;
import com.zj.mpocket.Constant;
import com.zj.mpocket.NetApi;
import com.zj.mpocket.R;
import com.zj.mpocket.model.ContentModel;
import com.zj.mpocket.utils.CommonUtil;
import com.zj.mpocket.utils.ImageUtils;
import com.zj.mpocket.utils.LogUtil;
import com.soundcloud.android.crop.Crop;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.InjectView;
import butterknife.OnClick;
import cz.msebera.android.httpclient.Header;

public class EditStoreActivity extends BaseActivity {

    List<ContentModel> contentsList;
    ImageLoader imageLoader;
    ImageSize imageSize;
    DisplayImageOptions options;

    @InjectView(R.id.llOne)
    LinearLayout llOne;
    @InjectView(R.id.etTitle1)
    EditText etTitle1;
    @InjectView(R.id.ivTitleUrl1)
    ImageView ivTitleUrl1;
    @InjectView(R.id.etTitleDesc11)
    EditText etTitleDesc11;
    @InjectView(R.id.ivTitleDescUrl11)
    ImageView ivTitleDescUrl11;
    @InjectView(R.id.ivTitleDescUrl12)
    ImageView ivTitleDescUrl12;
    @InjectView(R.id.etTitleDesc12)
    EditText etTitleDesc12;

    @InjectView(R.id.llTwo)
    LinearLayout llTwo;
    @InjectView(R.id.etTitle2)
    EditText etTitle2;
    @InjectView(R.id.ivTitleUrl2)
    ImageView ivTitleUrl2;
    @InjectView(R.id.etTitleDesc2)
    EditText etTitleDesc2;
    @InjectView(R.id.ivTitleDescUrl2)
    ImageView ivTitleDescUrl2;

    @InjectView(R.id.llThree)
    LinearLayout llThree;
    @InjectView(R.id.etTitle3)
    EditText etTitle3;
    @InjectView(R.id.ivTitleUrl3)
    ImageView ivTitleUrl3;
    @InjectView(R.id.etTitleDesc3)
    EditText etTitleDesc3;

    @InjectView(R.id.tvSaveStoreInfo)
    TextView tvSaveStoreInfo;
    @InjectView(R.id.tvPreviewStoreInfo)
    TextView tvPreviewStoreInfo;

    String theLarge;
    String theThumbnail;
    File imgFile;
    Uri uri;

    @Override
    protected boolean hasBackButton() {
        return true;
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_edit_store;
    }

    @Override
    protected int getTitleResId() {
        return R.string.edit_store;
    }

    @Override
    protected void initViews() {
        options = new DisplayImageOptions.Builder()
                .cacheOnDisk(false)
                .cacheInMemory(false)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();
        imageSize = new ImageSize(800, 450);
        imageLoader = ImageLoader.getInstance();
        //查询店铺图文详情
        getContentsList();
    }

    public void getContentsList() {
        showWaitDialog();
        NetApi.getContentsList(EditStoreActivity.this, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                hideWaitDialog();
                try {
                    if (responseBody != null) {
                        String result = new String(responseBody);
                        LogUtil.log("result----" + result);
                        JSONObject obj = new JSONObject(result);
                        String resultCode = obj.getString("resultCode");
                        String msg = obj.getString("msg");
                        if ("00".equals(resultCode)) {
                            String contentsListStr = obj.getString("contentsList");
                            contentsList = JSON.parseArray(contentsListStr, ContentModel.class);
                            fillContentsList();
                        } else {
                            CommonUtil.showToastMessage(EditStoreActivity.this, msg);
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
                    LogUtil.log("responseBody----" + new String(responseBody));
                }
            }
        });
    }

    public void fillContentsList() {
        if (contentsList.size() != 3) {
            contentsList.add(new ContentModel());
            fillContentsList();
        }else {
            Message msg = new Message();
            msg.what = 123;
            handler.sendMessage(msg);
        }
    }

    public void fillUI() {
        if (contentsList != null && contentsList.size() > 0) {
            for (int i = 0; i < contentsList.size(); i++) {
                ContentModel contentModel = contentsList.get(i);
                if (i == 0) {
                    etTitle1.setText(CommonUtil.isEmpty(contentModel.getTitle()) ? "" : contentModel.getTitle());
                    if (!CommonUtil.isEmpty(contentModel.getTitle_url())) {
                        imageLoader.displayImage(Constant.IMG_HEADER + contentModel.getTitle_url(), new ImageViewAware(ivTitleUrl1), options, imageSize, null, null);
                    }
                    etTitleDesc11.setText(CommonUtil.isEmpty(contentModel.getTitle_desc1()) ? "" : contentModel.getTitle_desc1());
                    if (!CommonUtil.isEmpty(contentModel.getTitle_desc_url1())) {
                        imageLoader.displayImage(Constant.IMG_HEADER + contentModel.getTitle_desc_url1(), new ImageViewAware(ivTitleDescUrl11), options, imageSize, null, null);
                    }
                    etTitleDesc12.setText(CommonUtil.isEmpty(contentModel.getTitle_desc2()) ? "" : contentModel.getTitle_desc2());
                    if (!CommonUtil.isEmpty(contentModel.getTitle_desc_url2())) {
                        imageLoader.displayImage(Constant.IMG_HEADER + contentModel.getTitle_desc_url2(), new ImageViewAware(ivTitleDescUrl12), options, imageSize, null, null);
                    }
                }else if (i == 1) {
                    etTitle2.setText(CommonUtil.isEmpty(contentModel.getTitle()) ? "" : contentModel.getTitle());
                    if (!CommonUtil.isEmpty(contentModel.getTitle_url())) {
                        imageLoader.displayImage(Constant.IMG_HEADER + contentModel.getTitle_url(), new ImageViewAware(ivTitleUrl2), options, imageSize, null, null);
                    }
                    etTitleDesc2.setText(CommonUtil.isEmpty(contentModel.getTitle_desc1()) ? "" : contentModel.getTitle_desc1());
                    if (!CommonUtil.isEmpty(contentModel.getTitle_desc_url1())) {
                        imageLoader.displayImage(Constant.IMG_HEADER + contentModel.getTitle_desc_url1(), new ImageViewAware(ivTitleDescUrl2), options, imageSize, null, null);
                    }
                }else if (i == 2) {
                    etTitle3.setText(CommonUtil.isEmpty(contentModel.getTitle()) ? "" : contentModel.getTitle());
                    if (!CommonUtil.isEmpty(contentModel.getTitle_url())) {
                        imageLoader.displayImage(Constant.IMG_HEADER + contentModel.getTitle_url(), new ImageViewAware(ivTitleUrl3), options, imageSize, null, null);
                    }
                    etTitleDesc3.setText(CommonUtil.isEmpty(contentModel.getTitle_desc1()) ? "" : contentModel.getTitle_desc1());
                }
            }
        }
    }

    @OnClick(R.id.tvSaveStoreInfo)
    public void save() {
        //更新contentsList
        contentsList.get(0).setTitle(etTitle1.getText().toString().trim());
        contentsList.get(1).setTitle(etTitle2.getText().toString().trim());
        contentsList.get(2).setTitle(etTitle3.getText().toString().trim());

        contentsList.get(0).setTitle_desc1(etTitleDesc11.getText().toString().trim());
        contentsList.get(0).setTitle_desc2(etTitleDesc12.getText().toString().trim());
        contentsList.get(1).setTitle_desc1(etTitleDesc2.getText().toString().trim());
        contentsList.get(2).setTitle_desc1(etTitleDesc3.getText().toString().trim());
        if (contentsList.get(0).empty() && contentsList.get(1).empty() && contentsList.get(2).empty()) {
            CommonUtil.showToastMessage(this, "请输入标题1");
            return;
        }
        if (!contentsList.get(0).empty()) {
            if (CommonUtil.isEmpty(contentsList.get(0).getTitle())) {
                CommonUtil.showToastMessage(this, "请输入标题1");
                return;
            }
        }
        if (!contentsList.get(1).empty()) {
            if (CommonUtil.isEmpty(contentsList.get(1).getTitle())) {
                CommonUtil.showToastMessage(this, "请输入标题2");
                return;
            }
        }
        if (!contentsList.get(2).empty()) {
            if (CommonUtil.isEmpty(contentsList.get(2).getTitle())) {
                CommonUtil.showToastMessage(this, "请输入标题3");
                return;
            }
        }
        showWaitDialog();
        NetApi.saveStoreInfo(EditStoreActivity.this, contentsList, new AsyncHttpResponseHandler() {
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
                            CommonUtil.showToastMessage(EditStoreActivity.this, "保存成功");
                            setResult(RESULT_OK, new Intent().putExtra("contents", obj.getString("contents")));
                            finish();
                        }else {
                            CommonUtil.showToastMessage(EditStoreActivity.this, msg);
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

    @OnClick(R.id.tvPreviewStoreInfo)
    public void preview() {
        //更新contentsList
        contentsList.get(0).setTitle(etTitle1.getText().toString().trim());
        contentsList.get(1).setTitle(etTitle2.getText().toString().trim());
        contentsList.get(2).setTitle(etTitle3.getText().toString().trim());

        contentsList.get(0).setTitle_desc1(etTitleDesc11.getText().toString().trim());
        contentsList.get(0).setTitle_desc2(etTitleDesc12.getText().toString().trim());
        contentsList.get(1).setTitle_desc1(etTitleDesc2.getText().toString().trim());
        contentsList.get(2).setTitle_desc1(etTitleDesc3.getText().toString().trim());

        List<ContentModel> tmpList = new ArrayList<>();
        tmpList.addAll(contentsList);

        if (!tmpList.get(2).empty()) {
            if (CommonUtil.isEmpty(tmpList.get(2).getTitle())) {
                tmpList.remove(2);
            }
        }else {
            tmpList.remove(2);
        }
        if (!tmpList.get(1).empty()) {
            if (CommonUtil.isEmpty(tmpList.get(1).getTitle())) {
                tmpList.remove(1);
            }
        }else {
            tmpList.remove(1);
        }
        if (!tmpList.get(0).empty()) {
            if (CommonUtil.isEmpty(tmpList.get(0).getTitle())) {
                tmpList.remove(0);
            }
        }else {
            tmpList.remove(0);
        }

        Intent intent = new Intent(EditStoreActivity.this, PreviewStoreActivity.class);
        intent.putExtra("contentsList", (Serializable) tmpList);
        startActivity(intent);
    }

    @OnClick(R.id.ivTitleUrl1)
    public void ivTitleUrl1() {
        clickId = R.id.ivTitleUrl1;
        showAlertDialog(contentsList.get(0).getTitle_url());
    }

    public void showAlertDialog(String url) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        if (CommonUtil.isEmpty(url)) {
            builder.setItems(new String[]{"拍照", "相册"}, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    selectAction(which, clickId);
                }
            });
        }else {
            builder.setItems(new String[]{"拍照", "相册", "删除图片"}, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    selectAction(which, clickId);
                }
            });
        }
        builder.setNegativeButton("取消", null);
        builder.show();
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
        startActivityForResult(intent, 2);
    }

    public void selectAction(int which, int id) {
        switch (which) {
            case 0:
                byCamera();
                break;
            case 1:
                Intent intent = new Intent(EditStoreActivity.this, GalleryActivity.class);
                intent.putExtra("single", true);
                startActivityForResult(intent, 1);
                break;
            case 2:
                if (id == R.id.ivTitleUrl1) {
                    recycleOriginalBitmap(ivTitleUrl1);
                    ivTitleUrl1.setImageResource(R.drawable.icon_upload_img);
                    contentsList.get(0).setTitle_url(null);
                }else if (id == R.id.ivTitleUrl2) {
                    recycleOriginalBitmap(ivTitleUrl2);
                    ivTitleUrl2.setImageResource(R.drawable.icon_upload_img);
                    contentsList.get(1).setTitle_url(null);
                }else if (id == R.id.ivTitleUrl3) {
                    recycleOriginalBitmap(ivTitleUrl3);
                    ivTitleUrl3.setImageResource(R.drawable.icon_upload_img);
                    contentsList.get(2).setTitle_url(null);
                }else if (id == R.id.ivTitleDescUrl11) {
                    recycleOriginalBitmap(ivTitleDescUrl11);
                    ivTitleDescUrl11.setImageResource(R.drawable.icon_upload_img);
                    contentsList.get(0).setTitle_desc_url1(null);
                }else if (id == R.id.ivTitleDescUrl12) {
                    recycleOriginalBitmap(ivTitleDescUrl12);
                    ivTitleDescUrl12.setImageResource(R.drawable.icon_upload_img);
                    contentsList.get(0).setTitle_desc_url2(null);
                }else if (id == R.id.ivTitleDescUrl2) {
                    recycleOriginalBitmap(ivTitleDescUrl2);
                    ivTitleDescUrl2.setImageResource(R.drawable.icon_upload_img);
                    contentsList.get(1).setTitle_desc_url1(null);
                }
                break;
        }
    }

    @OnClick(R.id.ivTitleDescUrl11)
    public void ivTitleDescUrl11() {
        clickId = R.id.ivTitleDescUrl11;
        String title_desc_url1 = contentsList.get(0).getTitle_desc_url1();
        showAlertDialog(title_desc_url1);
    }

    @OnClick(R.id.ivTitleDescUrl12)
    public void ivTitleDescUrl12() {
        clickId = R.id.ivTitleDescUrl12;
        String title_desc_url2 = contentsList.get(0).getTitle_desc_url2();
        showAlertDialog(title_desc_url2);
    }

    @OnClick(R.id.ivTitleUrl2)
    public void ivTitleUrl2() {
        clickId = R.id.ivTitleUrl2;
        showAlertDialog(contentsList.get(1).getTitle_url());
    }

    @OnClick(R.id.ivTitleDescUrl2)
    public void ivTitleDescUrl2() {
        clickId = R.id.ivTitleDescUrl2;
        String title_desc_url1 = contentsList.get(1).getTitle_desc_url1();
        showAlertDialog(title_desc_url1);
    }

    @OnClick(R.id.ivTitleUrl3)
    public void ivTitleUrl3() {
        clickId = R.id.ivTitleUrl3;
        showAlertDialog(contentsList.get(2).getTitle_url());
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        if (resultCode != Activity.RESULT_OK) return;
        new Thread() {

            private String selectedImagePath;

            @Override
            public void run() {
                if (requestCode == 1) {//从相册选择图片（店铺图片）
                    if (data == null)
                        return;
                    selectedImagePath = data.getStringExtra("imgPath");
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
                        Uri des = Uri.fromFile(out);
                        //裁剪
                        Uri source = Uri.fromFile(new File(theLarge));
                        theLarge = out.getPath();
                        Crop.of(source, des).withAspect(16, 9).start(EditStoreActivity.this);
                    }
                }else if (requestCode == 2) {//拍照
                    if ( !CommonUtil.isEmpty(theLarge)) {
                        //裁剪
                        Uri destination = Uri.fromFile(new File(theLarge));
                        Crop.of(uri, destination).withAspect(16, 9).start(EditStoreActivity.this);
                    }
                }else if (requestCode == Crop.REQUEST_CROP) {
                    if (resultCode == RESULT_OK) {
                        //裁剪成功后到handler去处理
                        Message msg = new Message();
                        msg.what = 1;
                        handler.sendMessage(msg);
                    }
                }
            }
        }.start();
    }

    int clickId = -1;

    private final Handler handler = new Handler() {
        @Override
        public void handleMessage(final Message msg) {
            if (msg.what == 1) {
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
                                ImageUtils.createImageThumbnail(EditStoreActivity.this, theLarge, theThumbnail, bitmap, 100);
                                imgFile = new File(theThumbnail);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    Message message = new Message();
                    message.what = 2;
                    message.obj = bitmap;
                    handler.sendMessage(message);
                }
            }else if (msg.what == 2 && msg.obj != null) {
                Message message = new Message();
                if (clickId == R.id.ivTitleUrl1) {
                    ivTitleUrl1.setImageBitmap((Bitmap) msg.obj);
                    message.what = 111;
                    handler.sendMessage(message);
                }else if ( clickId == R.id.ivTitleDescUrl11) {
                    ivTitleDescUrl11.setImageBitmap((Bitmap) msg.obj);
                    message.what = 222;
                    handler.sendMessage(message);
                }else if ( clickId == R.id.ivTitleDescUrl12) {
                    ivTitleDescUrl12.setImageBitmap((Bitmap) msg.obj);
                    message.what = 333;
                    handler.sendMessage(message);
                }else if ( clickId == R.id.ivTitleUrl2) {
                    ivTitleUrl2.setImageBitmap((Bitmap) msg.obj);
                    message.what = 444;
                    handler.sendMessage(message);
                }else if ( clickId == R.id.ivTitleDescUrl2) {
                    ivTitleDescUrl2.setImageBitmap((Bitmap) msg.obj);
                    message.what = 555;
                    handler.sendMessage(message);
                }else if ( clickId == R.id.ivTitleUrl3) {
                    ivTitleUrl3.setImageBitmap((Bitmap) msg.obj);
                    message.what = 666;
                    handler.sendMessage(message);
                }
            }else if (msg.what == 111) {
                tvSaveStoreInfo.setEnabled(false);
                tvPreviewStoreInfo.setEnabled(false);
                NetApi.uploadImage(imgFile, new AsyncHttpResponseHandler() {
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
                                    String path = obj.getString("path");
                                    contentsList.get(0).setTitle_url(path);
                                }else {
                                    CommonUtil.showToastMessage(EditStoreActivity.this, "图片上传失败");
                                }
                            }
                            imgFile.delete();
                            new File(theLarge).delete();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        tvSaveStoreInfo.setEnabled(true);
                        tvPreviewStoreInfo.setEnabled(true);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                        if (responseBody != null) {
                            LogUtil.log(new String(responseBody));
                        }
                        tvSaveStoreInfo.setEnabled(true);
                        tvPreviewStoreInfo.setEnabled(true);
                    }
                });
            }else if (msg.what == 222){
                tvSaveStoreInfo.setEnabled(false);
                tvPreviewStoreInfo.setEnabled(false);
                NetApi.uploadImage(imgFile, new AsyncHttpResponseHandler() {
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
                                    String path = obj.getString("path");
                                    contentsList.get(0).setTitle_desc_url1(path);
                                }else {
                                    CommonUtil.showToastMessage(EditStoreActivity.this, "图片上传失败");
                                }
                            }
                            imgFile.delete();
                            new File(theLarge).delete();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        tvSaveStoreInfo.setEnabled(true);
                        tvPreviewStoreInfo.setEnabled(true);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                        if (responseBody != null) {
                            LogUtil.log(new String(responseBody));
                        }
                        tvSaveStoreInfo.setEnabled(true);
                        tvPreviewStoreInfo.setEnabled(true);
                    }
                });
            }else if (msg.what == 333) {
                tvSaveStoreInfo.setEnabled(false);
                tvPreviewStoreInfo.setEnabled(false);
                NetApi.uploadImage(imgFile, new AsyncHttpResponseHandler() {
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
                                    String path = obj.getString("path");
                                    contentsList.get(0).setTitle_desc_url2(path);
                                }else {
                                    CommonUtil.showToastMessage(EditStoreActivity.this, "图片上传失败");
                                }
                            }
                            imgFile.delete();
                            new File(theLarge).delete();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        tvSaveStoreInfo.setEnabled(true);
                        tvPreviewStoreInfo.setEnabled(true);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                        if (responseBody != null) {
                            LogUtil.log(new String(responseBody));
                        }
                        tvSaveStoreInfo.setEnabled(true);
                        tvPreviewStoreInfo.setEnabled(true);
                    }
                });
            }else if (msg.what == 444) {
                tvSaveStoreInfo.setEnabled(false);
                tvPreviewStoreInfo.setEnabled(false);
                NetApi.uploadImage(imgFile, new AsyncHttpResponseHandler() {
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
                                    String path = obj.getString("path");
                                    contentsList.get(1).setTitle_url(path);
                                }else {
                                    CommonUtil.showToastMessage(EditStoreActivity.this, "图片上传失败");
                                }
                            }
                            imgFile.delete();
                            new File(theLarge).delete();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        tvSaveStoreInfo.setEnabled(true);
                        tvPreviewStoreInfo.setEnabled(true);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                        if (responseBody != null) {
                            LogUtil.log(new String(responseBody));
                        }
                        tvSaveStoreInfo.setEnabled(true);
                        tvPreviewStoreInfo.setEnabled(true);
                    }
                });
            }else if (msg.what == 555){
                tvSaveStoreInfo.setEnabled(false);
                tvPreviewStoreInfo.setEnabled(false);
                NetApi.uploadImage(imgFile, new AsyncHttpResponseHandler() {
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
                                    String path = obj.getString("path");
                                    contentsList.get(1).setTitle_desc_url1( path);
                                }else {
                                    CommonUtil.showToastMessage(EditStoreActivity.this, "图片上传失败");
                                }
                            }
                            imgFile.delete();
                            new File(theLarge).delete();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        tvSaveStoreInfo.setEnabled(true);
                        tvPreviewStoreInfo.setEnabled(true);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                        if (responseBody != null) {
                            LogUtil.log(new String(responseBody));
                        }
                        tvSaveStoreInfo.setEnabled(true);
                        tvPreviewStoreInfo.setEnabled(true);
                    }
                });
            }else if (msg.what == 666){
                tvSaveStoreInfo.setEnabled(false);
                tvPreviewStoreInfo.setEnabled(false);
                NetApi.uploadImage(imgFile, new AsyncHttpResponseHandler() {
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
                                    String path = obj.getString("path");
                                    contentsList.get(2).setTitle_url(path);
                                }else {
                                    CommonUtil.showToastMessage(EditStoreActivity.this, "图片上传失败");
                                }
                            }
                            imgFile.delete();
                            new File(theLarge).delete();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        tvSaveStoreInfo.setEnabled(true);
                        tvPreviewStoreInfo.setEnabled(true);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                        if (responseBody != null) {
                            LogUtil.log(new String(responseBody));
                        }
                        tvSaveStoreInfo.setEnabled(true);
                        tvPreviewStoreInfo.setEnabled(true);
                    }
                });
            }else if (msg.what == 123) {
                fillUI();
            }
        }
    };

}
