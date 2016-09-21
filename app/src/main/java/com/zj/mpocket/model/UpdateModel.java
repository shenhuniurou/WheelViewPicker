package com.zj.mpocket.model;

import java.io.Serializable;

/**
 * 更新实体类
 *
 * @author wxx
 */
@SuppressWarnings("serial")
public class UpdateModel implements Serializable {

    private String version_name;//版本名
    private String version_code;//版本号
    private String download_url;//下载地址
    private String version_desc;//更新日志

    public String getVersion_name() {
        return version_name;
    }

    public void setVersion_name(String version_name) {
        this.version_name = version_name;
    }

    public String getVersion_code() {
        return version_code;
    }

    public void setVersion_code(String version_code) {
        this.version_code = version_code;
    }

    public String getDownload_url() {
        return download_url;
    }

    public void setDownload_url(String download_url) {
        this.download_url = download_url;
    }

    public String getVersion_desc() {
        return version_desc;
    }

    public void setVersion_desc(String version_desc) {
        this.version_desc = version_desc;
    }

}
