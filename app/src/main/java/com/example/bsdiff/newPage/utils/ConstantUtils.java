package com.example.bsdiff.newPage.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.util.Log;


import com.example.bsdiff.oldPage.Util.ToastUtil;

import java.io.File;

/**
 * 增量更新-常量管理类
 *
 * @author zhoujunyu
 * @Description: (用一句话描述)
 * @date 2023/6/26 17:56
 */
public class ConstantUtils {
    /**
     * 检测更新的地址
     */
    public static final String CHECK_UPDATE_URL = "https://pesupport.mail.10086.cn/";
    /**
     * 当前版本
     */
    public static final String CURRENT_VERSION = "10.0.5";

    public static  String DOWNLOAD_FOLDER = "ClDiff";
    public static  String STORAGE_PATH = Environment.getExternalStorageDirectory() + File.separator + DOWNLOAD_FOLDER;
    public static  String OLD_APK = STORAGE_PATH + "/old.apk";
    public static  String NEW_APK = STORAGE_PATH + "/new.apk";
    public static  String MIDDLE_PATCH = STORAGE_PATH + "/middle.patch";
    public static  String PATCH_NEW_APK = STORAGE_PATH + "/new_patch.apk";

    /**
     * 设置配置
     * 业务路径：为正常从139渠道下载全量包，本地差分，时间较长
     * 测试路径：用已存在的apk(约10-20MB)进行差分，用于测试
     *
     * @param isTest
     * @param context
     */
    public static void setConfiguration(boolean isTest, Context context){
        if( isTest ){
            DOWNLOAD_FOLDER = "ClDiff";
            STORAGE_PATH = Environment.getExternalStorageDirectory() + File.separator + DOWNLOAD_FOLDER;
            OLD_APK = STORAGE_PATH + "/old.apk";
            NEW_APK = STORAGE_PATH + "/new.apk";
            MIDDLE_PATCH = STORAGE_PATH + "/middle.patch";
            PATCH_NEW_APK = STORAGE_PATH + "/new_patch.apk";
            ToastUtil.showDefaultToast(context,"当前为测试路径");
        }else {
            try {
                PackageManager pm = context.getPackageManager();
                ApplicationInfo appInfo = pm.getApplicationInfo(context.getPackageName(),0);
                DOWNLOAD_FOLDER = "MCloudUpdate";
                OLD_APK = appInfo.sourceDir;
                NEW_APK = getFileDownloadPath(context);
                STORAGE_PATH = new File(NEW_APK).getParentFile().getAbsolutePath();
                MIDDLE_PATCH = STORAGE_PATH + "/middle_139.patch";
                PATCH_NEW_APK = STORAGE_PATH + "/new_patch_139.apk";
            }catch (Exception e){
                e.printStackTrace();
                Log.e("异常", "获取引用报名异常： " + e.toString());
            }
            ToastUtil.showDefaultToast(context,"当前为业务路径");
        }
    }

    /**
     * 获取下载路径
     * sdcard/Android/data/demo/files
     *
     * @param context
     * @return
     */
    public static String getFileDownloadPath(Context context) {
        boolean sdCardExist = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
        //判断外置内存卡有没有被装载
        if (sdCardExist) {
            //获取外置内存卡里面sdcard/Android/data/cn.cj.pe/files的目录
            File dir = context.getExternalFilesDir(null);
            //判断目录是否存在
            if (dir != null && dir.exists()) {
                //在目录下面创建文件夹
                File file = new File(dir, DOWNLOAD_FOLDER);
                if (file.exists() || file.mkdirs()) {
                    return getFileName(file);
                }
            }
        }
        //外置内存卡没有被装载就用内置内存卡目录data/data/cn.ci.pe
        File file = new File(context.getCacheDir(), DOWNLOAD_FOLDER);
        if (!file.exists()) {
            file.mkdirs();
        }
        return getFileName(file);
    }

    private static String getFileName(File file){
        return file.exists() ? file.getAbsolutePath() + "/PE_V10.0.6.apk" : "";
    }

}
