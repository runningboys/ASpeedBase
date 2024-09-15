package com.common.utils.dutil.upload;

import android.os.Handler;
import android.os.Message;

import androidx.annotation.NonNull;

import com.common.utils.dutil.callback.UploadCallback;
import com.common.utils.dutil.data.Consts;
import com.common.utils.dutil.net.OkHttpManager;

import java.io.IOException;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.RequestBody;
import okhttp3.Response;

public abstract class BaseUploadRequest {

    protected String              url;
    protected Map<String, String> params;
    protected Map<String, String> headers;
    private   Handler             handler;

    public Call upload(final UploadCallback callback) {
        if (callback == null) {
            throw new IllegalArgumentException("UploadCallback can not be null");
        }

        UploadProgressHandler progressHandler = new UploadProgressHandler(callback);
        handler = progressHandler.getHandler();
        handler.sendEmptyMessage(Consts.START);

        RequestBody requestBody = initRequestBody();
        requestBody = new ProgressRequestBody(requestBody, handler);

        return OkHttpManager.getInstance().initRequest(url, requestBody, headers, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Message message = Message.obtain();
                message.what = Consts.ERROR;
                message.obj = e.toString();
                handler.sendMessage(message);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    Message message = Message.obtain();
                    message.what = Consts.FINISH;
                    message.obj = response.body().string();
                    handler.sendMessage(message);
                }
            }
        });
    }

    protected abstract RequestBody initRequestBody();
}
