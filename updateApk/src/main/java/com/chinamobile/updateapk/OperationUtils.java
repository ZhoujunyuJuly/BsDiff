package com.chinamobile.updateapk;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;

/**
 * 差分算法操作类
 *
 * @author zhoujunyu
 * @Description: (用一句话描述)
 * @date 2023/6/25 14:53
 */
public class OperationUtils {
    public static final String TAG = "OperationUtils";

    static {
        System.loadLibrary("McDiff");
    }

    private static native int clDiff(String oldApk, String newApk, String patch);

    private static native int clPatch(String oldApk, String newApk, String patch);

    /**
     * 开始差分
     *
     * @param oldApkPath
     * @param newApkPath
     * @param middlePath
     * @param listener
     */
    public static void startDiff(String oldApkPath,String newApkPath,String middlePath,MCDiffOperationListener listener) {
        File oldApk = new File(oldApkPath);
        File newApk = new File(newApkPath);
        File middle = new File(middlePath);

        if (oldApk.isFile() && newApk.isFile() &&
                middle.getParentFile() != null && middle.getParentFile().exists()) {
            listener.onStart();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    long start = System.currentTimeMillis();
                    clDiff(oldApk.getAbsolutePath(), newApk.getAbsolutePath(),
                            middle.getAbsolutePath());
                    long end = System.currentTimeMillis();
                    int genTime = (int)((end - start) / 1000);
                    listener.onFinish(genTime);
                }
            }).start();
        } else {
            listener.onFailure(ErrorMessageUtils.FILE_NOT_EXIST,
                    ErrorMessageUtils.getErrorMessage(ErrorMessageUtils.FILE_NOT_EXIST));
        }
    }

    /**
     * 开始合并
     *
     * @param oldApkPath
     * @param newApkPath
     * @param middlePath
     * @param listener
     */
    public static void startPatch(String oldApkPath,String newApkPath,String middlePath,MCDiffOperationListener listener) {
        File oldApk = new File(oldApkPath);
        File newApk = new File(newApkPath);
        File middle = new File(middlePath);

        if (oldApk.isFile() && middle.isFile() &&
                newApk.getParentFile() != null && newApk.getParentFile().exists()) {
            listener.onStart();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    long start = System.currentTimeMillis();
                    clPatch(oldApk.getAbsolutePath(), newApk.getAbsolutePath(),
                            middle.getAbsolutePath());
                    long end = System.currentTimeMillis();
                    int genTime = (int)((end - start) / 1000);
                    listener.onFinish(genTime);
                }
            }).start();
        } else {
            listener.onFailure(ErrorMessageUtils.FILE_NOT_EXIST,ErrorMessageUtils.getErrorMessage(ErrorMessageUtils.FILE_NOT_EXIST));
        }
    }

    /**
     * 计算MD5值
     *
     * @param file
     * @return
     */
    public static String getMd5ByFile(File file) {
        String value = null;
        FileInputStream in = null;
        try {
            in = new FileInputStream(file);

            MessageDigest digester = MessageDigest.getInstance("MD5");
            byte[] bytes = new byte[8192];
            int byteCount;
            while ((byteCount = in.read(bytes)) > 0) {
                digester.update(bytes, 0, byteCount);
            }
            value = bytes2Hex(digester.digest());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != in) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return value;
    }

    private static String bytes2Hex(byte[] src) {
        char[] res = new char[src.length * 2];
        final char hexDigits[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
        for (int i = 0, j = 0; i < src.length; i++) {
            res[j++] = hexDigits[src[i] >>> 4 & 0x0f];
            res[j++] = hexDigits[src[i] & 0x0f];
        }

        return new String(res);
    }
}
