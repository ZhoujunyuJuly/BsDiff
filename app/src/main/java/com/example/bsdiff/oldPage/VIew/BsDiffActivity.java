package com.example.bsdiff.oldPage.VIew;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.bsdiff.R;

public class BsDiffActivity extends AppCompatActivity implements BsDiffView.OnButtonListener {


    private BsDiffPresenter mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RelativeLayout rootLayout = findViewById(R.id.main_relativelayout);
        BsDiffView bsDiffView = new BsDiffView(this);
        bsDiffView.setButtonListener(this);
        rootLayout.addView(bsDiffView);

        mPresenter = new BsDiffPresenter(this,bsDiffView);
    }


    @Override
    public void diff() {
        if (ContextCompat.checkSelfPermission(BsDiffActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(BsDiffActivity.this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);
        } else {
            mPresenter.startDiff();
        }
    }

    @Override
    public void merge() {
        mPresenter.startMerge();
    }

    @Override
    public void md5() {
        mPresenter.startMd5();
    }
}