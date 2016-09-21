package com.zj.mpocket.model;

/**
 * Created by Administrator on 2016/4/22.
 * 商店二级分类
 */
public class ChildrenClass {

    private String text;//二级分类名称
    private String value;//二级分类ids

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "ChildrenClass{" +
                "text='" + text + '\'' +
                ", value='" + value + '\'' +
                '}';
    }
}
