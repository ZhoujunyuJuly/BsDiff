package com.example.bsdiff.VIew;

import android.content.Context;
import android.os.Environment;
import android.os.Handler;

import com.example.bsdiff.DiffAndPatchUtil;
import com.example.bsdiff.Util.Constants;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.text.DecimalFormat;

public class BsDiffPresenter {

    private Context mContext;
    private BsDiffView mView;
    private boolean sdCardExist;
    private String targetFolder;
    private File mOldApk;
    private File mDestApk;
    private File mPatchFile;
    private File mMergeApk;

    public BsDiffPresenter(Context context,BsDiffView view) {
        mContext = context;
        mView = view;
        checkData();
    }

    private void checkData(){
        File sdDir = null;
        sdCardExist = Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
        if( sdCardExist ){
            //获得SD卡根目录路径
            sdDir = Environment.getExternalStorageDirectory();
            String sdCardPath = sdDir.getAbsolutePath();
            //获取指定路径
            targetFolder = sdCardPath + File.separator + Constants.PACKAGE_NAME;
        }
    }

    public void startDiff(){
        if(sdCardExist){
            mOldApk = new File(targetFolder,mView.getOldPath());
            mDestApk = new File(targetFolder, mView.getNewPath());
            mPatchFile = new File(targetFolder,mView.getPatchPath());

            boolean isExist = mOldApk.exists() || mDestApk.exists();
            if( isExist ){
                mView.setDiffText("开始生成差分包，请等待..." + "\n当前使用so库的路径为: " + System.getProperty("java.library.path"));
                System.out.println("开始生成差分包，请等待...");
                System.out.println("当前使用so库的路径为: " + System.getProperty("java.library.path"));

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        long start = System.currentTimeMillis();
                        int genDiff = DiffAndPatchUtil.diff(mOldApk.getAbsolutePath(), mDestApk.getAbsolutePath(),
                                mPatchFile.getAbsolutePath());
                        long end = System.currentTimeMillis();

                        System.out.println("生成差分包成功：" + mPatchFile.getAbsolutePath() + "，耗时："
                                + (end - start) / 1000 + "秒, result=" + genDiff);
                        mView.setOutPutPatch(mPatchFile.getAbsolutePath(),getFileSize(mPatchFile.getAbsolutePath()));
                        mView.setDiffText("生成差分包成功：" + mPatchFile.getAbsolutePath() + "，耗时："
                                + (end - start) / 1000 + "秒");
                    }
                },2000);

            }else {
                mView.setDiffText( targetFolder + " 路径下找不到目标文件，请重新确认。");
            }
        }else {
            mView.setDiffText( "sdCard不存在！");
        }
    }

    public void startMerge(){
        if(sdCardExist){
            mMergeApk = new File(targetFolder, Constants.MERGE_APK_SUFFIX + mView.getOldPath());
            boolean isExist = mOldApk.exists() || mPatchFile.exists();

            if( isExist ){
                mView.setMergeText( String.format("存在 %s 差分文件，开始合并...",mView.getPatchPath()));

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        long start = System.currentTimeMillis();
                        int genMerge = DiffAndPatchUtil.patch(mOldApk.getAbsolutePath(),
                                mMergeApk.getAbsolutePath(),
                                mPatchFile.getAbsolutePath());
                        long end = System.currentTimeMillis();
                        if (mMergeApk.exists()) {
                            //安装新包
                            //ApkExtract.install(this, destApk.getAbsolutePath());
                            System.out.println("合成新包成功：" + mMergeApk.getAbsolutePath() + "，耗时："
                                    + (end - start) / 1000 + "秒, result=" + genMerge);
                            mView.setMergeText("合成新包成功：" + mMergeApk.getAbsolutePath() + "，耗时："
                                    + (end - start) / 1000 + "秒");
                        }else {
                            mView.setMergeText(String.format("合成失败，新包文件 %s 不存在",mMergeApk.getAbsolutePath()));
                        }
                    }
                },2000);

            }else {
                mView.setMergeText(String.format("%s 路径下找不到合成所需的 %s 或者 %s 文件",targetFolder,mView.getOldPath(),mView.getPatchPath()));
            }
        }else {
            mView.setMergeText( "sdCard不存在！");
        }
    }

    public void startMd5(){
        String originMd5 = getMd5ByFile(mDestApk);
        String mergeMd5 = getMd5ByFile(mMergeApk);
        mView.setMd5(originMd5,mergeMd5);
    }

    private static String getMd5ByFile(File file) {
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

    public String getFileSize(String filePath){
        FileChannel fc= null;
        long fileSize = 0;
        try {
            File f= new File(filePath);
            if (f.exists() && f.isFile()){
                FileInputStream fis= new FileInputStream(f);
                fc= fis.getChannel();
                fileSize = fc.size();
            }
        } catch (Exception e) {
            System.out.println("计算文件大小出错");
        } finally {
            if (null!=fc){
                try{
                    fc.close();
                }catch(IOException e){
                    System.out.println("关闭文件出错");
                }
            }
        }
        return convertFileSize(fileSize);
    }

    /**
     * 根据long大小计算出对应带单位大小
     *
     * @param size 大小
     * @return 带单位的大小
     */
    private String convertFileSize(long size) {
        int M = 1024 * 1024;
        int K = 1024;
        DecimalFormat df = new DecimalFormat("0.0");
        if (size / M > 0) {
            if (size % M == 0) {
                return size / M + "MB";
            } else {
                return df.format((float) size / M) + "MB";
            }
        }
        if (size / K > 0) {
            if (size % K == 0) {
                return size / K + "KB";
            } else {
                return df.format((float) size /K) + "KB";
            }
        }
        return size + "B";
    }
}
