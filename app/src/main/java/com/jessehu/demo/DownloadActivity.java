package com.jessehu.demo;

import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.jessehu.jhhttp.JH;
import com.jessehu.jhhttp.http.callback.ProgressCallback;
import com.jessehu.jhhttp.http.parameter.RequestParams;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class DownloadActivity extends AppCompatActivity {

    @BindView(R2.id.pb_download_progress)
    ProgressBar downloadProgressBar;
    @BindView(R2.id.tv_download_result)
    TextView downloadResultTv;

    //    private static final String URL_DOWNLOAD = "http://192.168.88.2:8080/v.apk";
    private static final String URL_DOWNLOAD = "http://192.168.1.142:8080/v.apk";
    private boolean coverFile = false;
    private boolean coverTemp = false;
    private boolean downloadAuto = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R2.layout.activity_download);
        ButterKnife.bind(this);
    }

    @OnClick(R2.id.btn_download)
    public void downloadClick(View view) {
        RequestParams requestParams = new RequestParams(URL_DOWNLOAD);
        requestParams.setDownloadStartPoint("download", 1111, "download222");
        JH.http().download(requestParams, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                showText("download failed");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                saveFile(response);
            }
        });
    }

    @OnClick(R2.id.btn_download_progress)
    public void downloadProgressClick() {
        RequestParams requestParams = new RequestParams(URL_DOWNLOAD);
        requestParams.setDownloadStartPoint("download", 1111, "download222");
        requestParams.setDownloadFile(Environment.getExternalStorageDirectory().getAbsolutePath());
        requestParams.setDownloadCover(coverFile);
        requestParams.setDownloadTempCover(coverTemp);
        requestParams.setDownloadAuto(downloadAuto);
        JH.http().download(requestParams, new ProgressCallback() {
            @Override
            public void onStarted() {
                showText("download start");
            }

            @Override
            public void onProgress(long totalLength, long bytesWritten, float percent) {
                JH.thread().uiThread(new Runnable() {
                    @Override
                    public void run() {
                        downloadProgressBar.setProgress(Math.round(percent));
                        if (bytesWritten > (1024 * 1024 * 1024)) {
                            downloadResultTv.setText(bytesWritten / 1024f / 1024f / 1024f + "G");
                        } else if (bytesWritten > (1024 * 1024)) {
                            downloadResultTv.setText(bytesWritten / 1024f / 1024f + "M");
                        } else if (bytesWritten > 1024) {
                            downloadResultTv.setText(bytesWritten / 1024f + "k");
                        } else {
                            downloadResultTv.setText(bytesWritten + "b");
                        }
                    }
                });
            }

            @Override
            public void onFinished() {
                showText("download finish");
            }

            @Override
            public void onFailure(Call call, IOException e) {
                showText("download failed");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!downloadAuto) {
                    saveFile(response);
                }
            }
        });
    }

    @OnCheckedChanged(R2.id.cb_cover_file)
    public void onCoverFileCheck(CompoundButton buttonView, boolean isChecked) {
        coverFile = isChecked;
    }

    @OnCheckedChanged(R2.id.cb_cover_temp)
    public void onCoverTempCheck(CompoundButton buttonView, boolean isChecked) {
        coverTemp = isChecked;
    }

    @OnCheckedChanged(R2.id.cb_download_auto)
    public void onDownloadAuto(CompoundButton buttonView, boolean isChecked) {
        downloadAuto = isChecked;
    }

    private void showText(String txt) {
        JH.thread().uiThread(() -> downloadResultTv.setText(txt));
    }

    private void saveFile(Response response) {
        String path = Environment.getExternalStorageDirectory().getAbsolutePath();
        File file = new File(path, "v666.apk");
        InputStream is = null;
        RandomAccessFile randomAccessFile = null;
        BufferedInputStream bis = null;
        byte[] buff = new byte[2048];
        int len;
        try {
            is = response.body().byteStream();
            bis = new BufferedInputStream(is);

            File parentFile = file.getParentFile();
            if (!parentFile.exists()) {
                parentFile.mkdirs();
            }
            // 随机访问文件，可以指定断点续传的起始位置
            randomAccessFile = new RandomAccessFile(file, "rwd");
            while ((len = bis.read(buff)) != -1) {
                randomAccessFile.write(buff, 0, len);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
                if (bis != null) {
                    bis.close();
                }
                if (randomAccessFile != null) {
                    randomAccessFile.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
