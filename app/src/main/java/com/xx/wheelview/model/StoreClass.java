package com.zj.wheelview.model;

import java.util.List;

/**
 * Created by Administrator on 2016/4/22.
 * 商店分类
 */
public class StoreClass {

    private String value;//一级分类ids
    private String logo;//一级分类图片
    private String text;//一级分类名称
    private List<ChildrenClass> children;//二级分类结果集

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public List<ChildrenClass> getChildren() {
        return children;
    }

    public void setChildren(List<ChildrenClass> children) {
        this.children = children;
    }

    @Override
    public String toString() {
        return "StoreClass{" +
                "value='" + value + '\'' +
                ", logo='" + logo + '\'' +
                ", text='" + text + '\'' +
                ", children=" + children +
                '}';
    }
}
