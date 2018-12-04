package com.jessehu.jhhttp.http;

import java.io.IOException;
import java.text.NumberFormat;

import javax.annotation.Nullable;

import okhttp3.MediaType;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;
import okio.ForwardingSource;
import okio.Okio;

/**
 * ProgressResponseBody
 *
 * @author JesseHu
 * @date 2018/12/3
 */
public class ProgressResponseBody extends ResponseBody {
    private ResponseBody mResponseBody;
    private ProgressCallback mCallback;

    public ProgressResponseBody(ResponseBody mResponseBody, ProgressCallback mCallback) {
        this.mResponseBody = mResponseBody;
        this.mCallback = mCallback;
    }

    @Nullable
    @Override
    public MediaType contentType() {
        return mResponseBody.contentType();
    }

    @Override
    public long contentLength() {
        return mResponseBody.contentLength();
    }

    @Override
    public BufferedSource source() {
        return Okio.buffer(new ForwardingSource(mResponseBody.source()) {
            private long bytesRead = 0L;
            private long totalLength = 0L;
            float percent = 0F;

            @Override
            public long read(Buffer sink, long byteCount) throws IOException {
                if (totalLength == 0L) {
                    if (contentLength() == -1) {
                        throw new RuntimeException("File is not exist");
                    } else {
                        totalLength = contentLength();
                    }
                }
                long bytesRead = super.read(sink, byteCount);
                this.bytesRead += bytesRead == -1 ? 0 : bytesRead;
                percent = this.bytesRead * 100F / totalLength;
                NumberFormat nf = NumberFormat.getNumberInstance();
                nf.setMaximumFractionDigits(2);
                percent = Float.valueOf(nf.format(percent));
                mCallback.onProgress(totalLength, this.bytesRead, percent);
                if (totalLength == this.bytesRead) {
                    mCallback.onFinished();
                }
                return bytesRead;
            }
        });

    }
}