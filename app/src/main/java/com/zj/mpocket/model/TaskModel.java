package com.zj.mpocket.model;

/**
 * Created by Administrator on 2016/5/20.
 */
public class TaskModel {

    private int imgId;//图标
    private String text;//文字

    public TaskModel() {}

    public TaskModel(int imgId, String text) {
        this.imgId = imgId;
        this.text = text;
    }

    public int getImgId() {
        return imgId;
    }

    public void setImgId(int imgId) {
        this.imgId = imgId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

}
