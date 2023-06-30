package com.example.bsdiff.newPage.bean;

import android.text.TextUtils;

import androidx.annotation.NonNull;

/**
 * 请求更新的数据-139邮箱更新请求
 * @author zhoujunyu
 * @Description: (用一句话描述)
 * @date 2023/6/26 17:52
 */
public class UpdateBean {

    /**
     * 这些是升级模式 by liuguanghui 2017/8/17 15:09
     */
    public static final int UPDATE_MODE_NORMAL = 0;
    /**
     * 这些是升级模式 by liuguanghui 2017/8/17 15:09
     */
    public static final int UPDATE_MODE_APP_STORE = 1;
    /**
     * 这些是升级模式 by liuguanghui 2017/8/17 15:09
     */
    public static final int UPDATE_MODE_BROWSER = 2;


    private String upgrade;
    private String force;
    private String hash;
    private String uploadTime;
    private String url;
    private String version;
    private String content;
    /**
     * 升级模式：
     *
     * @see UpdateBean#UPDATE_MODE_NORMAL 0表示正常升级
     * @see UpdateBean#UPDATE_MODE_APP_STORE 1表示升级站升级
     * @see UpdateBean#UPDATE_MODE_BROWSER 2表示从浏览器升级 by liuguanghui 2017/8/17 15:07
     */
    private int updateMode;

    /**
     * 弹窗的周期，单位是小时
     * 初始化配置为48。同一个新版本下未升级时，距离上次弹窗的时间间隔，以小时为单位；0表示没有时间间隔，每次都弹；48表示距离上次弹窗时间超过48小时后弹出。
     */
    private int timeInterval;
    /**
     * 弹窗的总次数
     * 初始化配置为-1。同一个新版本下未升级时，总共可弹窗次数；-1表示不限弹窗次数；0表示不弹出；100表示弹窗100次后就不再弹出
     */
    private int totalNum;
    /**
     * 这个是为了兼容后台搞错了的字段名
     */
    private int toalNum;

    public boolean isForceUpdate() {
        return "Y".equalsIgnoreCase(getForce());
    }

    public UpdateBean() {
        //这里是按需求初始化的默认值
        this.timeInterval = 48;
        this.totalNum = -1;
        this.toalNum = -100;
    }

    public String getUpgrade() {
        return TextUtils.isEmpty(upgrade) ? "" : upgrade;
    }

    public String getForce() {
        return TextUtils.isEmpty(force) ? "" : force;
    }

    public String getHash() {
        return TextUtils.isEmpty(hash) ? "" : hash;
    }

    public String getUploadTime() {
        return TextUtils.isEmpty(uploadTime) ? "" : uploadTime;
    }

    public String getUrl() {
        return TextUtils.isEmpty(url) ? "" : url;
    }

    public String getVersion() {
        return TextUtils.isEmpty(version) ? "" : version;
    }

    public String getContent() {
        return TextUtils.isEmpty(content) ? "" : content;
    }

    public int getUpdateMode() {
        return updateMode;
    }

    public int getTimeInterval() {
        return timeInterval;
    }

    public int getTotalNum() {
        if (toalNum != -100) {
            return toalNum;
        }
        return totalNum;
    }

    public boolean isLimitShowCount(){
        return getTotalNum() > 0;
    }

    @NonNull
    @Override
    public String toString() {
        return "UpdateBean{" +
                "upgrade='" + upgrade + '\'' +
                ", force='" + force + '\'' +
                ", hash='" + hash + '\'' +
                ", uploadTime='" + uploadTime + '\'' +
                ", url='" + url + '\'' +
                ", version='" + version + '\'' +
                ", content='" + content + '\'' +
                '}';
    }
}
