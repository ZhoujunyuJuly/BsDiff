package com.chinamobile.updateapk;

/**
 * 差分/合并监听
 *
 * @author zhoujunyu
 * @Description: (用一句话描述)
 * @date 2023/6/29 09:25
 */
public interface MCDiffOperationListener {
    void onStart();
    void onProgress(int percent);
    void onFinish(int secondTime);
    void onFailure(int errorCode,String errorMsg);
}
