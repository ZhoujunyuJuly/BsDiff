package com.example.bsdiff.newPage;

import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;

import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.chinamobile.updateapk.MCDiffOperationListener;
import com.chinamobile.updateapk.OperationUtils;
import com.example.bsdiff.R;
import com.example.bsdiff.newPage.bean.UpdateBean;
import com.example.bsdiff.newPage.utils.ConstantUtils;
import com.example.bsdiff.newPage.utils.DownloadUtils;
import com.example.bsdiff.oldPage.Util.ToastUtil;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.example.bsdiff.newPage.utils.ConstantUtils.MIDDLE_PATCH;
import static com.example.bsdiff.newPage.utils.ConstantUtils.NEW_APK;
import static com.example.bsdiff.newPage.utils.ConstantUtils.OLD_APK;
import static com.example.bsdiff.newPage.utils.ConstantUtils.PATCH_NEW_APK;

/**
 * 增量更新测试页面
 * Tips:
 * 1.点击按钮可重置从当前按钮之后所有流程
 * 2.点击标题切换测试路径
 *
 * @author zhoujunyu
 * @Description: (用一句话描述)
 * @date 2023/6/27 17:51
 */
public class MCloudDiffActivity extends AppCompatActivity {

    @BindView(R.id.btn_check_update)
    Button mBtnCheckUpdate;
    @BindView(R.id.tv_url)
    TextView mTvUlr;
    @BindView(R.id.tv_md5)
    TextView mTvMd5;
    @BindView(R.id.tv_version)
    TextView mTvVersion;
    @BindView(R.id.tv_content)
    TextView mTvContent;
    @BindView(R.id.tv_size)
    TextView mTvSize;

    @BindView(R.id.btn_download)
    Button mBtnDownload;
    @BindView(R.id.tv_location)
    TextView mTvDlLoc;
    @BindView(R.id.tv_progress)
    TextView mTvDlProgress;

    @BindView(R.id.btn_diff)
    Button mBtnDiff;
    @BindView(R.id.tv_diff_info)
    TextView mTvDiffInfo;
    @BindView(R.id.tv_diff_progress)
    TextView mTvDiffProgress;

    @BindView(R.id.btn_patch)
    Button mBtnPatch;
    @BindView(R.id.tv_patch_info)
    TextView mTvPatchInfo;
    @BindView(R.id.tv_patch_progress)
    TextView mTvPatchProgress;

    @BindView(R.id.btn_md5)
    Button mBtnMd5;
    @BindView(R.id.tv_md5_old)
    TextView mTvMd5Old;
    @BindView(R.id.tv_md5_new)
    TextView mTvMd5New;

    @BindView(R.id.btn_install)
    Button mBtnInstall;
    @BindView(R.id.tv_install)
    TextView mTvInstall;

    private ApiService mUpdateApiService;
    public static final String TAG = "MCloudDiffActivity";
    public UpdateBean mUpdateBean;
    private boolean isTest = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mcloud_diff);
        init();
    }

    private void init() {
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ConstantUtils.CHECK_UPDATE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient.build())
                .build();
        mUpdateApiService = retrofit.create(ApiService.class);
        ConstantUtils.setConfiguration(false,MCloudDiffActivity.this);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.btn_check_update, R.id.btn_download, R.id.btn_diff, R.id.btn_patch, R.id.btn_md5, R.id.btn_install,R.id.update_toolbar})
    public void handleClick(View v) {
        switch (v.getId()) {
            case R.id.update_toolbar:
                changeConfiguration();
                break;
            case R.id.btn_check_update:
                checkUpdate();
                break;
            case R.id.btn_download:
                startDownload();
                break;
            case R.id.btn_diff:
                startDiff();
                break;
            case R.id.btn_patch:
                startPatch();
                break;
            case R.id.btn_md5:
                checkMd5();
                break;
            case R.id.btn_install:
                startInstall();
                break;
            default:
                break;
        }
    }

    /**
     * 1.检测更新
     */
    private void checkUpdate() {
        resetCheckUpdate();
        mUpdateApiService.updateVersion(ConstantUtils.CURRENT_VERSION).enqueue(new Callback<UpdateBean>() {
            @Override
            public void onResponse(Call<UpdateBean> call, Response<UpdateBean> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ToastUtil.showDefaultToast(getApplicationContext(), "检测版本更新请求成功");
                    mUpdateBean = null;
                    mUpdateBean = response.body();
                    mTvUlr.setText(getTextWithStyle(R.string.note_update_url_address, mUpdateBean.getUrl()));
                    mTvVersion.setText(getTextWithStyle(R.string.note_update_url_version, mUpdateBean.getVersion()));
                    mTvContent.setText(getTextWithStyle(R.string.note_update_url_content, mUpdateBean.getContent()));
                    mTvSize.setText(getTextWithStyle(R.string.note_update_url_size, "还不知道自己写个吧"));
                    mTvMd5.setText(getTextWithStyle(R.string.note_update_url_md5, mUpdateBean.getHash()));
                    mBtnDownload.setEnabled(true);
                } else {
                    ToastUtil.showDefaultToast(getApplicationContext(), "检测版本更新请求为空");
                    mBtnDownload.setEnabled(false);
                }
            }

            @Override
            public void onFailure(Call<UpdateBean> call, Throwable t) {
                Log.e(TAG, "onFailure: " + t.toString());
                ToastUtil.showDefaultToast(getApplicationContext(), "检测版本更新网络异常");
                mBtnDownload.setEnabled(false);
            }
        });
    }

    /**
     * 2.开始下载
     */
    private void startDownload() {
        resetDownload();
        if( mUpdateBean != null && !TextUtils.isEmpty(mUpdateBean.getUrl())){
            DownloadUtils.getInstance().downloadFile(mUpdateBean.getUrl(), new DownloadUtils.DownloadListener() {
                @Override
                public void onStart() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mTvDlLoc.setText(getTextWithStyle(R.string.note_update_download_path, NEW_APK));
                        }
                    });
                }

                @Override
                public void onProgress(int currentLength,double fileSize) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mTvDlProgress.setText(getString(R.string.note_update_download_progress,
                                    String.valueOf(currentLength),fileSize));
                        }
                    });
                }

                @Override
                public void onFinish() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ToastUtil.showDefaultToast(MCloudDiffActivity.this,"已下载完成");
                            File file = new File(NEW_APK);
                            if(file.exists()){
                                mTvDlLoc.setText(getTextWithStyle(R.string.note_update_download_path, NEW_APK));
                                mTvDlProgress.setText(getString(R.string.note_update_download_progress,
                                        100 +"",getFileSizeToMB(file.length())));
                                mBtnDiff.setEnabled(true);
                            }
                        }
                    });
                }

                @Override
                public void onFailure(String errorMessage) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ToastUtil.showDefaultToast(MCloudDiffActivity.this,"下载失败，" + errorMessage);
                        }
                    });
                }
            });
        }

    }

    /**
     * 3.开始差分
     */
    private void startDiff() {
        resetDiff();
        if(new File(MIDDLE_PATCH).isFile()){
            //如果差分包存在，就进行下一步
            mBtnPatch.setEnabled(true);
            mTvDiffInfo.setText(getTextWithStyle(R.string.note_update_diff_path,MIDDLE_PATCH));
            mTvDiffProgress.setText(getString(R.string.note_update_diff_exist, getFileSizeToMB(new File(MIDDLE_PATCH).length())));
        }else {
            OperationUtils.startDiff(OLD_APK, NEW_APK, MIDDLE_PATCH, new MCDiffOperationListener() {
                @Override
                public void onStart() {
                    mTvDiffInfo.post(new Runnable() {
                        @Override
                        public void run() {
                            mTvDiffInfo.setText(getTextWithStyle(R.string.note_update_diff_path,MIDDLE_PATCH));
                        }
                    });
                }

                @Override
                public void onProgress(int percent) {

                }

                @Override
                public void onFinish(int secondTime) {
                    mTvDiffProgress.post(new Runnable() {
                        @Override
                        public void run() {
                            mTvDiffProgress.setText(getString(R.string.note_update_diff_size,getTimeFormat(secondTime),
                                    getFileSizeToMB(new File(MIDDLE_PATCH).length())));
                            mBtnPatch.setEnabled(true);
                        }
                    });
                }

                @Override
                public void onFailure(int errorCode, String errorMsg) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ToastUtil.showDefaultToast(MCloudDiffActivity.this,"差分失败，" + errorMsg);
                        }
                    });
                }
            });
        }

    }

    /**
     * 4.开始合并
     */
    private void startPatch() {
        resetPatch();
        if( new File(PATCH_NEW_APK).isFile()){
            mBtnMd5.setEnabled(true);
            mTvPatchProgress.setText(getString(R.string.note_update_patch_exist, getFileSizeToMB(new File(PATCH_NEW_APK).length())));
            mTvPatchInfo.setText(getTextWithStyle(R.string.note_update_patch_path, PATCH_NEW_APK));
        }else {
            OperationUtils.startPatch(OLD_APK, PATCH_NEW_APK, MIDDLE_PATCH,
                    new MCDiffOperationListener() {
                        @Override
                        public void onStart() {
                            mTvPatchInfo.post(new Runnable() {
                                @Override
                                public void run() {
                                    mTvPatchInfo.setText(getTextWithStyle(R.string.note_update_patch_path, PATCH_NEW_APK));
                                }
                            });
                        }

                        @Override
                        public void onProgress(int percent) {

                        }

                        @Override
                        public void onFinish(int secondTime) {
                            mTvPatchProgress.post(new Runnable() {
                                @Override
                                public void run() {
                                    mTvPatchProgress.setText(getString(R.string.note_update_patch_size,getTimeFormat(secondTime),
                                            getFileSizeToMB(new File(PATCH_NEW_APK).length())));
                                    mBtnMd5.setEnabled(true);
                                }
                            });
                        }

                        @Override
                        public void onFailure(int errorCode, String errorMsg) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ToastUtil.showDefaultToast(MCloudDiffActivity.this,"合并失败，" + errorMsg);
                                }
                            });
                        }
                    });
        }
    }

    /**
     * 5.md5校验
     */
    private void checkMd5() {
        resetMd5();
        String oldMd5 = mUpdateBean.getHash();
        String newMd5 = OperationUtils.getMd5ByFile(new File(PATCH_NEW_APK));
        mTvMd5New.setText(getTextWithStyle(R.string.note_update_md5_new,newMd5));
        mTvMd5Old.setText(getTextWithStyle(R.string.note_update_md5_old,oldMd5));
        if( newMd5.equals(oldMd5)){
            mBtnInstall.setEnabled(true);
            mTvInstall.setText(R.string.note_update_install);
        }
    }

    /**
     * 6.安装
     */
    private void startInstall() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (!getPackageManager().canRequestPackageInstalls()) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, 100);
            } else {
                doInstallApk();
            }
        } else {
            doInstallApk();
        }
    }

    private void doInstallApk(){
        File file = new File(PATCH_NEW_APK);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        Uri uri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            uri = FileProvider.getUriForFile(getApplicationContext(), getApplicationContext().getPackageName() + ".fileprovider", file);
            // 给目标应用一个临时授权
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        } else {
            uri = Uri.fromFile(file);
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setDataAndType(uri,
                "application/vnd.android.package-archive");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION
                | Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        startActivity(intent);
    }

    private SpannableString getTextWithStyle(@StringRes int stringRes, String addContent) {
        String origin = getString(stringRes);
        String text = String.format(getString(stringRes), addContent);
        SpannableString spannableString = new SpannableString(text);

        int startIndex = origin.indexOf("%1$s");
        startIndex = Math.max(startIndex, 0);
        int endIndex = startIndex + String.valueOf(addContent).length();

        spannableString.setSpan(new StyleSpan(Typeface.BOLD), 0, startIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new StyleSpan(Typeface.BOLD), endIndex, text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        return spannableString;
    }

    private double getFileSizeToMB(long fileSize){
        return fileSize /(1024.0 * 1024.0);
    }

    private void changeConfiguration(){
        //从第一步开始重置
        resetCheckUpdate();
        ConstantUtils.setConfiguration((isTest = !isTest),this);
    }

    private String getTimeFormat(int second){
        if( second > 60 ){
            return second / 60 + "分" + second % 60 + "秒";
        }else {
            return second + "秒";
        }
    }

    private void resetCheckUpdate() {
        mTvUlr.setText("");
        mTvVersion.setText("");
        mTvContent.setText("");
        mTvSize.setText("");
        mTvMd5.setText("");
        mBtnDownload.setEnabled(false);
        resetDownload();
    }

    private void resetDownload(){
        mTvDlLoc.setText("");
        mTvDlProgress.setText("");
        mBtnDiff.setEnabled(false);
        resetDiff();
    }

    private void resetDiff(){
        mTvDiffInfo.setText("");
        mTvDiffProgress.setText("");
        mBtnPatch.setEnabled(false);
        resetPatch();
    }

    private void resetPatch(){
        mTvPatchInfo.setText("");
        mTvPatchProgress.setText("");
        mBtnMd5.setEnabled(false);
        resetMd5();
    }

    private void resetMd5(){
        mTvMd5Old.setText("");
        mTvMd5New.setText("");
        mBtnInstall.setEnabled(false);
        resetInstall();
    }

    private void resetInstall(){
        mTvInstall.setText("");
    }
}