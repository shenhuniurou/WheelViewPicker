package com.zj.mpocket.model;

import com.zj.mpocket.utils.CommonUtil;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/5/4.
 */
public class ContentModel implements Serializable {

    private static final long serialVersionUID = 1L;

    private String title;//题目
    private String title_url;//题目图片
    private String title_desc1;//题目描述1
    private String title_desc2;//题目描述2
    private String title_desc3;//题目描述3
    private String title_desc4;//题目描述4
    private String title_desc_url1;//描述图片1
    private String title_desc_url2;//描述图片2
    private String title_desc_url3;//描述图片3
    private String title_desc_url4;//描述图片4

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle_url() {
        return title_url;
    }

    public void setTitle_url(String title_url) {
        this.title_url = title_url;
    }

    public String getTitle_desc1() {
        return title_desc1;
    }

    public void setTitle_desc1(String title_desc1) {
        this.title_desc1 = title_desc1;
    }

    public String getTitle_desc2() {
        return title_desc2;
    }

    public void setTitle_desc2(String title_desc2) {
        this.title_desc2 = title_desc2;
    }

    public String getTitle_desc3() {
        return title_desc3;
    }

    public void setTitle_desc3(String title_desc3) {
        this.title_desc3 = title_desc3;
    }

    public String getTitle_desc4() {
        return title_desc4;
    }

    public void setTitle_desc4(String title_desc4) {
        this.title_desc4 = title_desc4;
    }

    public String getTitle_desc_url1() {
        return title_desc_url1;
    }

    public void setTitle_desc_url1(String title_desc_url1) {
        this.title_desc_url1 = title_desc_url1;
    }

    public String getTitle_desc_url2() {
        return title_desc_url2;
    }

    public void setTitle_desc_url2(String title_desc_url2) {
        this.title_desc_url2 = title_desc_url2;
    }

    public String getTitle_desc_url3() {
        return title_desc_url3;
    }

    public void setTitle_desc_url3(String title_desc_url3) {
        this.title_desc_url3 = title_desc_url3;
    }

    public String getTitle_desc_url4() {
        return title_desc_url4;
    }

    public void setTitle_desc_url4(String title_desc_url4) {
        this.title_desc_url4 = title_desc_url4;
    }

    public boolean empty() {
        if (CommonUtil.isEmpty(this.title) &&
                CommonUtil.isEmpty(this.title_url) &&
                CommonUtil.isEmpty(this.title_desc1) &&
                CommonUtil.isEmpty(this.title_desc2) &&
                CommonUtil.isEmpty(this.title_desc3) &&
                CommonUtil.isEmpty(this.title_desc4) &&
                CommonUtil.isEmpty(this.title_desc_url1) &&
                CommonUtil.isEmpty(this.title_desc_url2) &&
                CommonUtil.isEmpty(this.title_desc_url3) &&
                CommonUtil.isEmpty(this.title_desc_url4)) {
            return true;
        }
        return false;
    }

}
