 package com.example.bsdiff;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.bsdiff.newPage.MCloudDiffActivity;
import com.example.bsdiff.oldPage.VIew.BsDiffActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

 public class LanuchActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lanuch);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.btn_new,R.id.btn_old})
     public void handleClick(View view){
        switch (view.getId()){
            case R.id.btn_new:
                startActivity(new Intent(this, MCloudDiffActivity.class));
                break;
            case R.id.btn_old:
                startActivity(new Intent(this, BsDiffActivity.class));
                break;
        }
    }
}