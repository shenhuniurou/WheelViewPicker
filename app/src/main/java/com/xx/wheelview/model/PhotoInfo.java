package com.zj.wheelview.model;

/**
 * Created by Administrator on 2016/4/21.
 */
public class PhotoInfo {

    public String imgPath;//原始图路径

    public String thumbImgPath;//缩略图路径

    public boolean isSelected = false;

    public String getImgPath() {
        return imgPath;
    }

    public void setImgPath(String imgPath) {
        this.imgPath = imgPath;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public String getThumbImgPath() {
        return thumbImgPath;
    }

    public void setThumbImgPath(String thumbImgPath) {
        this.thumbImgPath = thumbImgPath;
    }
}
