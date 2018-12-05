package com.jessehu.demo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.jessehu.jhhttp.JH;
import com.jessehu.jhhttp.http.ProgressCallback;
import com.jessehu.jhhttp.http.RequestParams;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class MainActivity extends AppCompatActivity {

    @BindView(R2.id.btn_get)
    Button getBtn;
    @BindView(R2.id.btn_post)
    Button postBtn;
    @BindView(R2.id.btn_upload)
    Button uploadBtn;
    @BindView(R2.id.btn_download)
    Button downloadBtn;
    @BindView(R2.id.tv_result)
    TextView resultTv;
    private Unbinder unbinder;
    private static final String TAG = MainActivity.class.getSimpleName();
    public static final String URL_GET = "http://wanandroid.com/wxarticle/chapters/json";
    public static final String URL_GET2 = "http://192.168.1.142:8080/HttpRequest";
    public static final String URL_POST = "http://www.wanandroid.com/user/login";
    public static final String URL_POST2 = "http://192.168.1.142:8080/JsonRequest";
    public static final String URL_UPLOAD = "http://192.168.1.142:8080/Upload";
    public static final String URL_DOWNLOAD = "http://192.168.1.142:8080/v.apk";
    public static final String URL_DOWNLOAD2 = "http://openbox.mobilem.360.cn/index/d/sid/3990138";
    private static final String USERNAME = "123";
    private static final String PASSWORD = "456";
    private static final int CODE_UPLOAD_NO_PROGRESS = 123;
    private static final int CODE_UPLOAD_PROGRESS = 456;
    private Context mContext;
    private String resultStr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R2.layout.activity_main);
        unbinder = ButterKnife.bind(this);
        mContext = this;
        resultTv.setMovementMethod(ScrollingMovementMethod.getInstance());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    @OnClick({R2.id.btn_get})
    public void onGetClick(View v) {
//        JH.thread().singleThread(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    requestWithResponse(JH.http().get(URL_GET2));
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        });

        getWithCallback();
    }

    private void getWithCallback() {
        RequestParams requestParams = new RequestParams(URL_GET2);
        requestParams.addBodyParams("username", USERNAME);
        requestParams.addBodyParams("password", PASSWORD);
        requestParams.addHeaders("username", USERNAME);
        requestParams.addHeaders("password", PASSWORD);
        JH.http().get(requestParams, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                JH.thread().uiThread(new Runnable() {
                    @Override
                    public void run() {
                        resultTv.setText("request failed");
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    resultStr = response.body() == null ? "body is null" : response.body().string();
                    JH.thread().uiThread(new Runnable() {
                        @Override
                        public void run() {
                            resultTv.setText(resultStr);
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @OnClick(R2.id.btn_post)
    public void onPostClick(View view) {
//        postWithCallback();

//        JH.thread().singleThread(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    requestWithResponse(JH.http().post(URL_POST));
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        });

        RequestParams requestParams = new RequestParams(URL_POST2);
        requestParams.addBodyParams("username", USERNAME);
        requestParams.addBodyParams("password", PASSWORD);
        requestParams.setAsJsonContent(true);
        JH.http().post(requestParams, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                JH.thread().uiThread(new Runnable() {
                    @Override
                    public void run() {
                        resultTv.setText("request failed");
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    resultStr = response.body() == null ? "body is null" : response.body().string();
                    JH.thread().uiThread(new Runnable() {
                        @Override
                        public void run() {
                            resultTv.setText(resultStr);
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @OnClick(R2.id.btn_upload)
    public void onUploadClick(View view) {
        uploadWithNoProgress();
    }

    @OnClick(R2.id.btn_download)
    public void onDownloadClick(View view) {
        RequestParams requestParams = new RequestParams(URL_DOWNLOAD);
//        requestParams.setDownloadStartPoint("download", 1111);
        requestParams.setDownloadStartPoint("download", 1111, "download222");
        JH.http().download(requestParams, Environment.getExternalStorageDirectory().getAbsolutePath(), new ProgressCallback() {
            @Override
            public void onStarted() {

            }

            @Override
            public void onProgress(long totalLength, long bytesWritten, float percent) {
                JH.thread().uiThread(new Runnable() {
                    @Override
                    public void run() {
                        resultTv.setText(percent + "");
                    }
                });
            }

            @Override
            public void onFinished() {

            }

            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

            }
        });
    }

    private void requestWithResponse(Response post) {
        try {
            Response response = post;
            if (response.isSuccessful()) {
                ResponseBody responseBody = response.body();
                if (responseBody != null) {
                    resultStr = responseBody.string();
                } else {
                    resultStr = "body is null";

                }
            } else {
                resultStr = "request failed";
            }
            JH.thread().uiThread(new Runnable() {
                @Override
                public void run() {
                    resultTv.setText(resultStr);
                }
            });
        } catch (final IOException e) {
            JH.thread().uiThread(new Runnable() {
                @Override
                public void run() {
                    resultTv.setText(e.getMessage());
                }
            });
        }
    }

    private void postWithCallback() {
        JH.http().post(URL_POST, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                JH.thread().uiThread(new Runnable() {
                    @Override
                    public void run() {
                        resultTv.setText("request failed");
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    resultStr = response.body() == null ? "body is null" : response.body().string();
                    JH.thread().uiThread(new Runnable() {
                        @Override
                        public void run() {
                            resultTv.setText(resultStr);
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void uploadWithNoProgress() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, CODE_UPLOAD_PROGRESS);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null && resultCode == Activity.RESULT_OK) {
            Uri uri = data.getData();
            String path = getRealPathFromURI(this, uri);
            if (CODE_UPLOAD_NO_PROGRESS == requestCode) {
                try {
                    uploadWithNoProgress(path);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            } else if (CODE_UPLOAD_PROGRESS == requestCode) {
                try {
                    uploadWithProgress(path);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    private void uploadWithProgress(String path) throws IOException {
        RequestParams requestParams = new RequestParams(URL_UPLOAD);
        requestParams.addFile("file", path);
        requestParams.addBodyParams("token", "123");
        JH.http().upload(requestParams, new ProgressCallback() {
            @Override
            public void onStarted() {

            }

            @Override
            public void onProgress(long totalLength, long bytesWritten, float percent) {
                JH.thread().uiThread(new Runnable() {
                    @Override
                    public void run() {
                        resultTv.setText(percent + "");
                    }
                });
            }

            @Override
            public void onFinished() {

            }

            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "onFailure: ", e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                resultStr = response.body() == null ? "body is null" : response.body().string();
                JH.thread().uiThread(new Runnable() {
                    @Override
                    public void run() {
                        resultTv.setText(resultStr);
                    }
                });
            }
        });
    }

    private void uploadWithNoProgress(String path) throws IOException {
        //                JH.http().upload(URL_UPLOAD, "file", path, new Callback() {
//                    @Override
//                    public void onFailure(Call call, IOException e) {
//                        Log.e(TAG, "onFailure: ", e);
//                    }
//
//                    @Override
//                    public void onResponse(Call call, Response response) throws IOException {
//                        resultStr = response.body() == null ? "body is null" : response.body().string();
//                        JH.thread().uiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                resultTv.setText(resultStr);
//                            }
//                        });
//                    }
//                });
        RequestParams requestParams = new RequestParams(URL_UPLOAD);
        requestParams.addFile("file", path);
        requestParams.addBodyParams("token", "123");
        JH.http().upload(requestParams, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "onFailure: ", e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                resultStr = response.body() == null ? "body is null" : response.body().string();
                JH.thread().uiThread(new Runnable() {
                    @Override
                    public void run() {
                        resultTv.setText(resultStr);
                    }
                });
            }
        });
    }

    private String getRealPathFromURI(Context context, Uri contentURI) {
        String result;
        Cursor cursor = context.getContentResolver().query(contentURI,
                new String[]{MediaStore.Images.ImageColumns.DATA},
                null, null, null);
        if (cursor == null) {
            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(index);
            cursor.close();
        }
        return result;
    }
}
