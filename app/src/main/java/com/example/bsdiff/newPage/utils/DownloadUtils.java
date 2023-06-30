package com.example.bsdiff.newPage.utils;

import android.text.TextUtils;
import android.util.Log;


import com.example.bsdiff.newPage.ApiService;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * 下载管理
 *
 * @author zhoujunyu
 * @Description: (用一句话描述)
 * @date 2023/6/27 10:41
 */
public class DownloadUtils {
    private ApiService mDownloadService;
    private static DownloadUtils mInstance;
    private String TAG = "DownloadUtils";

    private DownloadUtils() {
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ConstantUtils.CHECK_UPDATE_URL) // 替换为您的 API 基本 URL
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient.build())
                .build();
        mDownloadService = retrofit.create(ApiService.class);
    }

    public static DownloadUtils getInstance(){
        if( mInstance == null){
            mInstance = new DownloadUtils();
        }
        return mInstance;
    }

    /**
     * 下载apk
     *
     * @param url
     * @param listener
     */
    public void downloadFile(String url,DownloadListener listener){
        if( TextUtils.isEmpty(ConstantUtils.NEW_APK) ){
            Log.e(TAG, "downloadFile: 下载路径为空");
            listener.onFailure("下载路径为空");
            return;
        }

        File downloadFile = new File(ConstantUtils.NEW_APK);
        if( !downloadFile.exists() ){
            if( mDownloadService == null){
                Log.e(TAG, "下载接口为空" );
                listener.onFailure("下载接口为空");
                return;
            }
            mDownloadService.downloadFile(url).enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            saveFile(response,downloadFile,listener);
                        }
                    }).start();
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    listener.onFailure(t.toString());
                }
            });
        }else {
            listener.onFinish();
        }

    }

    /**
     * 保存
     *
     * @param response
     * @param file
     * @param listener
     */
    private void saveFile(Response<ResponseBody> response, File file, DownloadListener listener){
        listener.onStart();
        long currentLength = 0;
        long totalLength = response.body().contentLength();

        OutputStream os = null;
        InputStream is = response.body().byteStream();

        try {
            os = new FileOutputStream(file);
            int len;
            byte[] buffer = new byte[1024];

            while ((len = is.read(buffer)) != -1){
                os.write(buffer,0,len);
                currentLength += len;
                int progress = (int)(100 * currentLength / totalLength);
                double fileSize = currentLength / (1024.0 * 1024.0);
                listener.onProgress(progress,fileSize);
                if( progress == 100 ){
                    listener.onFinish();
                }
            }

        }catch (FileNotFoundException e){
            e.printStackTrace();
            listener.onFailure(e.toString());
        }catch (IOException e){
            e.printStackTrace();
            listener.onFailure(e.toString());
        }finally {
            if( os != null){
                try {
                    os.close();
                }catch (IOException e){
                    e.printStackTrace();
                }
            }
            if( is != null){
                try {
                    is.close();
                }catch (IOException e){
                    e.printStackTrace();
                }
            }
        }
    }


    public interface DownloadListener{
        void onStart();
        void onProgress(int percent,double fileSize);
        void onFinish();
        void onFailure(String errorMessage);
    }
}
