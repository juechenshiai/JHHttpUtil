package com.jessehu.demo;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.jessehu.jhhttp.JH;
import com.jessehu.jhhttp.http.parameter.RequestParams;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class GetPostActivity extends AppCompatActivity {

    @BindView(R2.id.tv_http_result)
    TextView resultTv;

    private static final String URL_GET = "http://192.168.88.2:8080/HttpRequest";
    private static final String URL_POST = "http://192.168.88.2:8080/JsonRequest";
    private String resultStr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R2.layout.activity_get_post);
        ButterKnife.bind(this);
    }

    private RequestParams getRequestParams(String url) {
        RequestParams requestParams = new RequestParams(url);
        requestParams.addHeaders("name", "user");
        requestParams.addHeaders("pwd", "123456");
        requestParams.addBodyParams("name", "user");
        requestParams.addBodyParams("pwd", "123456");
        return requestParams;
    }

    @OnClick(R2.id.btn_get)
    public void getClick(View view) {
        RequestParams requestParams = getRequestParams(URL_GET);
        JH.http().get(requestParams, mCallback);
    }

    @OnClick(R2.id.btn_get_sync)
    public void getSyncClick(View view) {
        RequestParams requestParams = getRequestParams(URL_GET);
        JH.thread().singleThread(() -> {
            try {
                Response response = JH.http().getSync(requestParams);
                showResult(response);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

    }

    private void showResult(Response response) throws IOException {
        if (response.isSuccessful()) {
            resultStr = response.body() == null ? "body is null" : response.body().string();
            JH.thread().uiThread(() -> resultTv.setText(resultStr));
        } else {
            JH.thread().uiThread(() -> resultTv.setText("request failed"));
        }
    }

    @OnClick(R2.id.btn_post)
    public void postClick(View view) {
        RequestParams requestParams = getRequestParams(URL_POST);
        JH.http().post(requestParams, mCallback);
    }

    @OnClick(R2.id.btn_post_sync)
    public void postSyncClick(View view) {
        RequestParams requestParams = getRequestParams(URL_POST);
        JH.thread().singleThread(() -> {
            try {
                Response response = JH.http().postSync(requestParams);
                showResult(response);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private Callback mCallback = new Callback() {
        @Override
        public void onFailure(Call call, IOException e) {
            JH.thread().uiThread(() -> resultTv.setText("request failed"));
        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {
            try {
                resultStr = response.body() == null ? "body is null" : response.body().string();
                JH.thread().uiThread(() -> resultTv.setText(resultStr));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    };
}
