package com.zj.mpocket.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Environment;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zj.mpocket.Constant;
import com.zj.mpocket.R;
import com.zj.mpocket.utils.CommonUtil;
import com.zj.mpocket.utils.PreferenceUtil;
import com.zj.mpocket.utils.QRCodeUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import butterknife.InjectView;
import butterknife.OnClick;

public class QRcodeActivity extends BaseActivity {

    @InjectView(R.id.ivQrcode)
    ImageView ivQrcode;
    @InjectView(R.id.tvStoreName)
    TextView tvStoreName;
    @InjectView(R.id.tvSaveToLocal)
    TextView tvSaveToLocal;
    @InjectView(R.id.rlQrCode)
    RelativeLayout rlQrCode;

    @Override
    protected boolean hasBackButton() {
        return true;
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_qrcode;
    }

    @Override
    protected int getTitleResId() {
        return R.string.title_activity_QR_code;
    }

    @Override
    protected void initViews() {
        //final String logo = getIntent().getStringExtra("logo");
        final String name = getIntent().getStringExtra("name");
        String merchantId = PreferenceUtil.getPrefString(QRcodeActivity.this, Constant.USER_INFO, Context.MODE_PRIVATE, Constant.MERCHANT_Id, null);
        //保存二维码的路径
        final String filePath = getFileRoot(QRcodeActivity.this) + File.separator + merchantId + "_QR_Code.jpg";
        final String content = Constant.IMG_HEADER + "/jf/appinterface/scanmerch/" + merchantId;
        //生成二维码
        showWaitDialog();
        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean success = QRCodeUtil.createQRImage(content, CommonUtil.dip2px(QRcodeActivity.this, 200), CommonUtil.dip2px(QRcodeActivity.this, 200), null, filePath);
                if (success) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            hideWaitDialog();
                            ivQrcode.setImageBitmap(BitmapFactory.decodeFile(filePath));
                            new File(filePath).delete();
                            tvStoreName.setText(name);
                        }
                    });
                }
            }
        }).start();

    }

    @OnClick(R.id.tvSaveToLocal)
    public void saveQrcode() {
        try {
            Bitmap bitmap = Bitmap.createBitmap(rlQrCode.getWidth(), rlQrCode.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            rlQrCode.draw(canvas);
            String merchantId = PreferenceUtil.getPrefString(QRcodeActivity.this, Constant.USER_INFO, Context.MODE_PRIVATE, Constant.MERCHANT_Id, null);
            //保存二维码的路径
            String filePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/DCIM/Camera/" + merchantId + "_QR_Code.jpg";
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, new FileOutputStream(filePath));
            Uri uri = Uri.fromFile(new File(filePath));
            Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            mediaScanIntent.setData(uri);
            sendBroadcast(mediaScanIntent);
            CommonUtil.showToastMessage(this, "已将二维码保存到相册中");
        }catch (FileNotFoundException e) {
           e.printStackTrace();
        }
    }

    //存储二维码图片的目录
    private String getFileRoot(Context context) {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            return Environment.getExternalStorageDirectory().getAbsolutePath() + "/koudai/";
        }
        return context.getFilesDir().getAbsolutePath();
    }

}
