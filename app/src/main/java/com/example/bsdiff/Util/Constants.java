package com.example.bsdiff.Util;

import android.view.View;

public class Constants {

    public static final String PACKAGE_NAME = "BsDiff";

    public static final String COMMON_ADDRESS = "/Users/zhoujunyu/Documents/139Pro/Other/DynamicUpdate/TestApkCollection/";

    public static final String OLD_APK_NAME = "139_old_44MB.apk";
    public static final String NEW_APK_NAME = "139_new_35MB.apk";
    public static final String PATCH_NAME = "middle.patch";
    public static final String MERGE_APK_SUFFIX = "merge_";

    // 旧版本
    public static final String COMPUTER_OLD_APK = COMMON_ADDRESS + OLD_APK_NAME;

    // 新版本
    public static final String COMPUTER_NEW_APK = COMMON_ADDRESS + NEW_APK_NAME;

    // patch包存储路径
    public static final String COMPUTER_PATCH_FILE =  COMMON_ADDRESS + PATCH_NAME;

    // 使用旧版本微博+patch包，合成的新包
    public static final String OLD_2_NEW_APK = COMMON_ADDRESS + "patchNew.apk";
}
