package com.example.bsdiff.VIew;

import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.RelativeLayout;

import com.example.bsdiff.R;

public class MainActivity extends AppCompatActivity implements BsDiffView.OnButtonListener {


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
        if (ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);
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