package com.example.bsdiff.oldPage.VIew;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.example.bsdiff.R;

import static com.example.bsdiff.oldPage.Util.Constants.*;

public class BsDiffView extends RelativeLayout implements View.OnClickListener{
    /**
     * 旧包路径
     */
    private EditText oldApkPath;
    /**
     * 新包路径
     */
    private EditText newApkPath;
    /**
     * 差分包路径
     */
    private EditText patchPath;
    /**
     * 完成差分的差分包路径
     */
    private EditText patchOutputPatch;
    /**
     * 差分包大小
     */
    private EditText patchSize;
    /**
     * 原始MD5
     */
    private EditText originMd5;
    /**
     * 合并包MD5
     */
    private EditText newMd5;
    /**
     * 差分按钮
     */
    private Button diffBtn;
    /**
     * 合并按钮
     */
    private Button mergeBtn;
    /**
     * 一键重置按钮
     */
    private Button resetBtn;
    /**
     * MD5按钮
     */
    private Button md5Btn;
    /**
     * 按钮事件监听
     */
    private OnButtonListener mListener;
    private TextView diffTv;
    private TextView mergeTv;

    public BsDiffView(Context context) {
        this(context,null);
    }

    public BsDiffView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public BsDiffView(Context context, @Nullable  AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    public void initView(Context context){
       View rootView =  LayoutInflater.from(context).inflate(R.layout.bsdiff_view,this,true);
       oldApkPath = rootView.findViewById(R.id.old_path);
       newApkPath = rootView.findViewById(R.id.new_path);
       patchPath = rootView.findViewById(R.id.middle_path);

       diffBtn = rootView.findViewById(R.id.diff_btn);
       setDefaultPath();

       patchOutputPatch = rootView.findViewById(R.id.patch_finish_path);
       patchSize = rootView.findViewById(R.id.patch_finish_size);
       mergeBtn = rootView.findViewById(R.id.merge_btn);

       originMd5 = rootView.findViewById(R.id.md5_origin);
       newMd5 = rootView.findViewById(R.id.md5_new);
       md5Btn = rootView.findViewById(R.id.md5_btn);

       diffTv = rootView.findViewById(R.id.diff_tv);
       mergeTv = rootView.findViewById(R.id.merge_tv);
       resetBtn = rootView.findViewById(R.id.reset_btn);

       diffBtn.setOnClickListener(this);
       mergeBtn.setOnClickListener(this);
       md5Btn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if( mListener != null) {
            switch (v.getId()) {
                case R.id.diff_btn:
                    diffBtn.setText("正在差分");
                    mListener.diff();
                    break;
                case R.id.merge_btn:
                    mergeBtn.setText("正在合并");
                    mListener.merge();
                    break;
                case R.id.md5_btn:
                    mListener.md5();
                    break;
                case R.id.reset_btn:
                    reset();
                default:
                    break;
            }
        }
    }

    public String getOldPath(){
        return "".equals(oldApkPath.getText().toString()) ? oldApkPath.getHint().toString() : oldApkPath.getText().toString();
    }

    public String getNewPath(){
        return "".equals(newApkPath.getText().toString()) ? newApkPath.getHint().toString() : newApkPath.getText().toString();
    }

    public String getPatchPath(){
        return "".equals(patchPath.getText().toString()) ? patchPath.getHint().toString() : patchPath.getText().toString();
    }

    public void setDiffText(String text){
        diffTv.setText(text);
    }

    public void setMergeText(String text){
        mergeTv.setText(text);
    }

    public void setOutPutPatch(String path,String size){
        patchOutputPatch.setText(path);
        patchSize.setText(size);
    }

    public void setMd5(String origin,String newValue){
        originMd5.setText(origin);
        newMd5.setText(newValue);
        md5Btn.setText( origin.equals(newValue) ? R.string.check_success : R.string.check_fail);
    }

    public void setButtonListener(OnButtonListener listener){
        mListener = listener;
    }

    private void reset(){
        setDefaultPath();
        //todo 可以试一下直接把文字置空，会不会出现提示文字
        patchOutputPatch.setHint(R.string.output_path_hint);
        patchSize.setHint(R.string.output_size_hint);
        originMd5.setHint(R.string.origin_md5_hint);
        newMd5.setHint(R.string.new_md5_hint);
    }

    public void setDefaultPath(){
        oldApkPath.setHint(OLD_APK_NAME);
        newApkPath.setHint(NEW_APK_NAME);
        patchPath.setHint(PATCH_NAME);
    }

    public interface OnButtonListener{
        void diff();
        void merge();
        void md5();
    }

}
