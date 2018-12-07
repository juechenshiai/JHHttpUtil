package com.jessehu.demo;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.content.CursorLoader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.jessehu.jhhttp.JH;
import com.jessehu.jhhttp.http.callback.ProgressCallback;
import com.jessehu.jhhttp.http.parameter.RequestParams;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class UploadActivity extends AppCompatActivity {

    @BindView(R2.id.pb_upload_progress)
    ProgressBar uploadProgressBar;
    @BindView(R2.id.tv_upload_result)
    TextView uploadResultTv;

    private static final String URL_UPLOAD = "http://192.168.88.2:8080/Upload";
    private static final int CODE_UPLOAD_NO_PROGRESS = 123;
    private static final int CODE_UPLOAD_PROGRESS = 456;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R2.layout.activity_upload);
        ButterKnife.bind(this);
    }

    @OnClick(R2.id.btn_upload)
    public void uploadClick(View view) {
        upload(CODE_UPLOAD_NO_PROGRESS);
    }


    @OnClick(R2.id.btn_upload_progress)
    public void uploadProgressClick(View view) {
        upload(CODE_UPLOAD_PROGRESS);
    }

    private void upload(int requestCode) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null && resultCode == Activity.RESULT_OK) {
            Uri uri = data.getData();
            String path = getPathByUri4kitkat(this, uri);
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
    @SuppressLint("NewApi")
    public static String getPathByUri4kitkat(final Context context, final Uri uri) {
        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            if (isExternalStorageDocument(uri)) {
                // ExternalStorageProvider
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            } else if (isDownloadsDocument(uri)) {
                // DownloadsProvider
                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"),
                        Long.valueOf(id));
                return getDataColumn(context, contentUri, null, null);
            } else if (isMediaDocument(uri)) {
                // MediaProvider
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }
                final String selection = "_id=?";
                final String[] selectionArgs = new String[] { split[1] };
                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            // MediaStore
            // (and
            // general)
            return getDataColumn(context, uri, null, null);
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            // File
            return uri.getPath();
        }
        return null;
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context
     *            The context.
     * @param uri
     *            The Uri to query.
     * @param selection
     *            (Optional) Filter used in the query.
     * @param selectionArgs
     *            (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = { column };
        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return null;
    }

    /**
     * @param uri
     *            The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri
     *            The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri
     *            The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    private void uploadWithNoProgress(String path) throws IOException {
        RequestParams requestParams = new RequestParams(URL_UPLOAD);
        requestParams.addFile("file", path);
        JH.http().upload(requestParams, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                showText("upload failed");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String resultStr = response.body() == null ? "body is null" : response.body().string();
                showText(resultStr);
            }
        });
    }

    private void uploadWithProgress(String path) throws IOException {
        RequestParams requestParams = new RequestParams(URL_UPLOAD);
        requestParams.addFile("file", path);
        JH.http().upload(requestParams, new ProgressCallback() {
            @Override
            public void onStarted() {
                showText("upload start");
            }

            @Override
            public void onProgress(long totalLength, long bytesWritten, float percent) {
                JH.thread().uiThread(new Runnable() {
                    @Override
                    public void run() {
                        uploadProgressBar.setProgress(Math.round(percent));
                        if (bytesWritten > (1024 * 1024 * 1024)) {
                            uploadResultTv.setText(bytesWritten / 1024f / 1024f / 1024f + "G");
                        } else if (bytesWritten > (1024 * 1024)) {
                            uploadResultTv.setText(bytesWritten / 1024f / 1024f + "M");
                        } else if (bytesWritten > 1024) {
                            uploadResultTv.setText(bytesWritten / 1024f + "k");
                        } else {
                            uploadResultTv.setText(bytesWritten + "b");
                        }
                    }
                });
            }

            @Override
            public void onFinished() {
                showText("upload finish");
            }

            @Override
            public void onFailure(Call call, IOException e) {
                showText("upload failed");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String resultStr = response.body() == null ? "body is null" : response.body().string();
                showText(resultStr);
            }
        });
    }

    private void showText(final String txt) {
        JH.thread().uiThread(() -> uploadResultTv.setText(txt));
    }


}
