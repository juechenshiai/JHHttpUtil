package com.jessehu.demo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LaunchActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R2.layout.activity_launch);
        ButterKnife.bind(this);
    }

    @OnClick(R2.id.btn_to_get_post)
    public void toGetPost(View view) {
        Intent intent = new Intent(this, GetPostActivity.class);
        startActivity(intent);
    }

    @OnClick(R2.id.btn_to_upload)
    public void toUpdate(View view) {
        Intent intent = new Intent(this, UploadActivity.class);
        startActivity(intent);
    }

    @OnClick(R2.id.btn_to_download)
    public void toDownload(View view) {
        Intent intent = new Intent(this, DownloadActivity.class);
        startActivity(intent);
    }
}
