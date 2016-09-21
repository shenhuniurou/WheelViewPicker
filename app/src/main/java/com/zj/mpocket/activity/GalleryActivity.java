package com.zj.mpocket.activity;

import android.content.Intent;
import android.database.Cursor;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;
import com.zj.mpocket.R;
import com.zj.mpocket.adapter.GalleryAdapter;
import com.zj.mpocket.model.PhotoInfo;
import com.zj.mpocket.utils.CommonUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.InjectView;

public class GalleryActivity extends BaseActivity implements AdapterView.OnItemClickListener {

    GridView gallery;
    @InjectView(R.id.llOperateArea)
    LinearLayout llOperateArea;
    GalleryAdapter mAdapter;
    boolean single;
    List<PhotoInfo> photoInfoList;

    @Override
    protected boolean hasBackButton() {
        return true;
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_gallery;
    }

    @Override
    protected int getTitleResId() {
        return R.string.gallery_select;
    }

    @Override
    protected void initViews() {
        single = getIntent().getBooleanExtra("single", true);
        if (single) {
            llOperateArea.setVisibility(View.GONE);
        }
        photoInfoList = new ArrayList<>();
        gallery = (GridView) findViewById(R.id.gallery);
        mAdapter = new GalleryAdapter(GalleryActivity.this, photoInfoList, single);
        gallery.setAdapter(mAdapter);
        gallery.setOnItemClickListener(this);
        gallery.setOnScrollListener(new PauseOnScrollListener(ImageLoader.getInstance(), true, true));
        getPhotos();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (single) {
            //如果是单选，将选中的图片路径返回到上一个activity即可
            PhotoInfo photoInfo = mAdapter.getItem(position);
            String imgPath = photoInfo.getThumbImgPath();
            Intent intent = new Intent(GalleryActivity.this, RegisterActivity.class);
            intent.putExtra("imgPath", imgPath);
            setResult(RESULT_OK, intent);
            finish();
        }
    }

    /**
     * 获取相册图片
     *
     * @return
     */
    private void getPhotos() {

        final String[] projectionPhotos = {
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.BUCKET_ID,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
                MediaStore.Images.Media.DATA,
                MediaStore.Images.Media.DATE_TAKEN,
                MediaStore.Images.Media.ORIENTATION,
                MediaStore.Images.Thumbnails.DATA
        };
        Cursor cursor = null;

        try {
            cursor = MediaStore.Images.Media.query(getApplicationContext().getContentResolver(), MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projectionPhotos, "", null, MediaStore.Images.Media.DATE_TAKEN + " DESC");
            ArrayList<PhotoInfo> galleryList = new ArrayList<PhotoInfo>();
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    int dataColumn = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
                    int thumbImageColumn = cursor.getColumnIndex(MediaStore.Images.Thumbnails.DATA);
                    String path = cursor.getString(dataColumn);
                    String thumbPath = cursor.getString(thumbImageColumn);

                    File file = new File(path);
                    File thumbFile = new File(thumbPath);
                    if ( file.exists() && file.length() > 0 && thumbFile.exists() && thumbFile.length() > 0) {
                        PhotoInfo photoInfo = new PhotoInfo();
                        photoInfo.setImgPath(path);
                        photoInfo.setThumbImgPath(thumbPath);
                        photoInfo.setSelected(false);
                        galleryList.add(photoInfo);
                    }
                }
            }
            // 最新的图片显示在最前
            //Collections.reverse(galleryList);
            photoInfoList.addAll(galleryList);
            //如果相册中没有图片
            if (photoInfoList != null && photoInfoList.size() == 0) {
                CommonUtil.showToastMessage(GalleryActivity.this, "相册中没有图片哦！");
            }
            mAdapter.notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        photoInfoList.clear();
        photoInfoList = null;
        ImageLoader.getInstance().clearMemoryCache();
        System.gc();
    }

}
