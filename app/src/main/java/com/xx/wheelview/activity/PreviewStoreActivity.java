package com.zj.wheelview.activity;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;
import com.zj.wheelview.Constant;
import com.zj.wheelview.R;
import com.zj.wheelview.model.ContentModel;
import com.zj.wheelview.utils.CommonUtil;

import java.util.List;

import butterknife.InjectView;

public class PreviewStoreActivity extends BaseActivity {

    ImageLoader imageLoader;
    ImageSize imageSize;
    DisplayImageOptions options;

    @InjectView(R.id.llContain)
    LinearLayout llContain;

    @Override
    protected boolean hasBackButton() {
        return true;
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_preview_store;
    }

    @Override
    protected int getTitleResId() {
        return R.string.preview_store;
    }

    @Override
    protected void initViews() {
        options = new DisplayImageOptions.Builder()
                .cacheOnDisk(false)
                .cacheInMemory(false)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();
        imageSize = new ImageSize(800, 450);
        List<ContentModel> contentsList = (List<ContentModel>) getIntent().getSerializableExtra("contentsList");
        imageLoader = ImageLoader.getInstance();
        for (int i = 0; i < contentsList.size(); i++) {
            ContentModel contentModel = contentsList.get(i);
            LinearLayout ll = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.layout_store, null);
            if (!CommonUtil.isEmpty(contentModel.getTitle())) {
                LinearLayout linearLayout = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.layout_title_textview, null);
                TextView textView = (TextView) linearLayout.findViewById(R.id.textView);
                textView.setText(contentModel.getTitle());
                ll.addView(linearLayout);
            }
            if (!CommonUtil.isEmpty(contentModel.getTitle_url())) {
                LinearLayout linearLayout = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.layout_store_image, null);
                ImageView imageView = (ImageView) linearLayout.findViewById(R.id.imageView);
                imageLoader.displayImage(Constant.IMG_HEADER + contentModel.getTitle_url(), new ImageViewAware(imageView), options, imageSize, null, null);
                ll.addView(linearLayout);
            }
            if (!CommonUtil.isEmpty(contentModel.getTitle_desc1())) {
                LinearLayout linearLayout = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.layout_title_des, null);
                TextView textView = (TextView) linearLayout.findViewById(R.id.textView);
                textView.setText(contentModel.getTitle_desc1());
                ll.addView(linearLayout);
            }
            if (!CommonUtil.isEmpty(contentModel.getTitle_desc_url1())) {
                String titleDescUrl1 = contentModel.getTitle_desc_url1();
                LinearLayout linearLayout = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.layout_store_image, null);
                ImageView imageView = (ImageView) linearLayout.findViewById(R.id.imageView);
                imageLoader.displayImage(Constant.IMG_HEADER + titleDescUrl1, new ImageViewAware(imageView), options, imageSize, null, null);
                ll.addView(linearLayout);
            }
            if (!CommonUtil.isEmpty(contentModel.getTitle_desc2())) {
                LinearLayout linearLayout = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.layout_title_des, null);
                TextView textView = (TextView) linearLayout.findViewById(R.id.textView);
                textView.setText(contentModel.getTitle_desc2());
                ll.addView(linearLayout);
            }
            if (!CommonUtil.isEmpty(contentModel.getTitle_desc_url2())) {
                String titleDescUrl2 = contentModel.getTitle_desc_url2();
                LinearLayout linearLayout = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.layout_store_image, null);
                ImageView imageView = (ImageView) linearLayout.findViewById(R.id.imageView);
                imageLoader.displayImage(Constant.IMG_HEADER + titleDescUrl2, new ImageViewAware(imageView), options, imageSize, null, null);
                ll.addView(linearLayout);
            }
            llContain.addView(ll);
        }
    }

}
