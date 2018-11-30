package com.jessehu.demo;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.jessehu.jhhttp.JH;
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
    @BindView(R2.id.tv_result)
    TextView resultTv;
    private Unbinder unbinder;
    public static final String URL_GET = "http://wanandroid.com/wxarticle/chapters/json ";
    public static final String URL_POST = "http://www.wanandroid.com/user/login";
    public static final String USERNAME = "yaoyue";
    public static final String PASSWORD = "jessehu";
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
        JH.thread().singleThread(new Runnable() {
            @Override
            public void run() {
                try {
                    requestWithResponse(JH.http().get(URL_GET));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

//        getWithCallback();
    }

    private void getWithCallback() {
        JH.http().get(URL_GET, new Callback() {
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

        RequestParams requestParams = new RequestParams(URL_POST);
        requestParams.addBodyParams("username", USERNAME);
        requestParams.addBodyParams("password", PASSWORD);
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
}
