package com.example.bsdiff.newPage;


import com.example.bsdiff.newPage.bean.UpdateBean;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

/**
 * 请求版本更新服务
 *
 * @author zhoujunyu
 * @Description: (用一句话描述)
 * @date 2023/6/26 17:51
 */
public interface ApiService {
    String UPDATE_POST = "update/android/android_version_2?ver=1.0&phone_type=AC20X";

    @FormUrlEncoded
    @POST(UPDATE_POST)
    Call<UpdateBean> updateVersion(@Field("current_ver") String currentVersion);

    @Streaming
    @GET
    Call<ResponseBody> downloadFile(@Url String fileUrl);
}
