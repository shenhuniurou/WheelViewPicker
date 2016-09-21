package com.zj.mpocket.utils;

import android.graphics.Bitmap;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 二维码生成工具类
 */
public class QRCodeUtil {
    /**
     * 生成二维码Bitmap
     *
     * @param str       内容
     * @param widthPix  图片宽度
     * @param heightPix 图片高度
     * @param logoBm    二维码中心的Logo图标（可以为null）
     * @param filePath  用于存储二维码图片的文件路径
     * @return 生成二维码及保存文件是否成功
     */
    public static boolean createQRImage(String str, int widthPix, int heightPix, Bitmap logoBm, String filePath) {

        try {
            if (str == null || "".equals(str)) {
                return false;
            }
            int IMAGE_HALFWIDTH = 80;
            //Matrix matrix = new Matrix();
            //float sx = (float) 2 * IMAGE_HALFWIDTH / logoBm.getWidth();
            //float sy = (float) 2 * IMAGE_HALFWIDTH / logoBm.getHeight();
            //matrix.setScale(sx, sy);//设置缩放信息
            //将logo图片按martix设置的信息缩放
            //logoBm = Bitmap.createBitmap(logoBm, 0, 0, logoBm.getWidth(), logoBm.getHeight(), m, false);
            //配置参数
            Map<EncodeHintType, Object> hints = new HashMap<>();
            hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
            //容错级别
            hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
            //设置空白边距的宽度
            //hints.put(EncodeHintType.MARGIN, 2); //default is 4

            // 图像数据转换，使用了矩阵转换
            BitMatrix bitMatrix = new MultiFormatWriter().encode(str, BarcodeFormat.QR_CODE, widthPix, heightPix, hints);
            int[] pixels = new int[widthPix * heightPix];
            // 下面这里按照二维码的算法，逐个生成二维码的图片，
            // 两个for循环是图片横列扫描的结果
            int width = bitMatrix.getWidth();//矩阵高度
            int height = bitMatrix.getHeight();//矩阵宽度
            int halfW = width / 2;
            int halfH = height / 2;
            for (int y = 0; y < heightPix; y++) {
                for (int x = 0; x < widthPix; x++) {
                    //if (x > halfW - IMAGE_HALFWIDTH && x < halfW + IMAGE_HALFWIDTH &&y > halfH - IMAGE_HALFWIDTH &&y<halfH +IMAGE_HALFWIDTH){//该位置用于存放图片信息
                        //记录图片每个像素信息
                        //pixels[y * width + x] = logoBm.getPixel(x - halfW+  IMAGE_HALFWIDTH, y - halfH + IMAGE_HALFWIDTH);
                    //}else{
                        if (bitMatrix.get(x, y)) {
                            pixels[y * width + x] = 0xff000000;
                        } else { // 无信息设置像素点为白色，或者什么都不设置也行
                            pixels[y * width + x] = 0xffffffff;
                        }
                    //}
                }
            }

            // 生成二维码图片的格式，使用ARGB_8888
            Bitmap bitmap = Bitmap.createBitmap(widthPix, heightPix, Bitmap.Config.ARGB_8888);
            bitmap.setPixels(pixels, 0, widthPix, 0, 0, widthPix, heightPix);

            //必须使用compress方法将bitmap保存到文件中再进行读取。直接返回的bitmap是没有任何压缩的，内存消耗巨大！
            return bitmap != null && bitmap.compress(Bitmap.CompressFormat.JPEG, 90, new FileOutputStream(filePath));
        } catch (WriterException | IOException e) {
            e.printStackTrace();
        }

        return false;
    }

}