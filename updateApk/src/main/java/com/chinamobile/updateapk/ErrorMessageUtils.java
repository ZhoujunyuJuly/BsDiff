package com.chinamobile.updateapk;

/**
 * 错误码管理类
 *
 * @author zhoujunyu
 * @Description: (用一句话描述)
 * @date 2023/6/27 16:38
 */
public class ErrorMessageUtils {
    public static final int SUCCESS = 200;
    public static final int FILE_NOT_EXIST = 100;
    public static final int NO_STORAGE_PERMISSION = 101;

    public static String getErrorMessage(int errorCode){
        switch (errorCode){
            case SUCCESS:
                return "成功";
            case FILE_NOT_EXIST:
                return "文件或文件夹不存在";
            case NO_STORAGE_PERMISSION:
                return "没有存储权限";
            default:
                return "其他错误";
        }
    }
}
