package com.jessehu.jhhttp.http.progress;

import android.support.annotation.NonNull;

import com.jessehu.jhhttp.http.callback.ProgressCallback;

import java.io.IOException;
import java.text.NumberFormat;

import javax.annotation.Nullable;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.Buffer;
import okio.BufferedSink;
import okio.ForwardingSink;
import okio.Okio;
import okio.Sink;

/**
 * ProgressRequestBody
 *
 * @author JesseHu
 * @date 2018/12/3
 */
public class ProgressRequestBody extends RequestBody {

    private RequestBody mRequestBody;
    private ProgressCallback mCallback;
    private BufferedSink mSink;

    public ProgressRequestBody(RequestBody mRequestBody, ProgressCallback mCallback) {
        this.mRequestBody = mRequestBody;
        this.mCallback = mCallback;
    }

    @Nullable
    @Override
    public MediaType contentType() {
        return mRequestBody.contentType();
    }

    @Override
    public void writeTo(@NonNull BufferedSink sink) throws IOException {
        if (mSink == null) {
            mSink = Okio.buffer(sink(sink));
        }
        mRequestBody.writeTo(mSink);
        mSink.flush();
    }

    @Override
    public long contentLength() throws IOException {
        return mRequestBody.contentLength();
    }

    private Sink sink(final BufferedSink sink) {
        return new ForwardingSink(sink) {
            long bytesWritten = 0L;
            long contentLength = 0L;
            float percent = 0F;

            @Override
            public void write(@NonNull Buffer source, long byteCount) throws IOException {
                super.write(source, byteCount);
                if (contentLength == 0) {
                    if (contentLength() == -1) {
                        throw new RuntimeException("File is not exist");
                    } else {
                        contentLength = contentLength();
                    }
                }
                bytesWritten += byteCount;
                percent = bytesWritten * 100F / contentLength;
                NumberFormat nf = NumberFormat.getNumberInstance();
                nf.setMaximumFractionDigits(2);
                percent = Float.valueOf(nf.format(percent));
                mCallback.onProgress(contentLength, bytesWritten, percent);
                if (contentLength == bytesWritten) {
                    mCallback.onFinished();
                }
            }
        };
    }


}
