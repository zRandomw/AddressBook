package com.hhxy.zw.adressbook.utils;

import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;

import com.hhxy.zw.adressbook.LoginActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;

public class MyInterceptor implements Interceptor {
    private static final String TAG = "MyInterceptor";
    private static final Charset UTF8 = Charset.forName("UTF-8");
        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request();
            Response originalResponse = chain.proceed(request);
            ResponseBody responseBody = originalResponse.body();
            BufferedSource source = responseBody.source();
            source.request(Long.MAX_VALUE); // Buffer the entire body.
            Buffer buffer = source.buffer();
            Charset charset = UTF8;
            MediaType contentType = responseBody.contentType();
            if (contentType != null) {
                charset = contentType.charset(UTF8);
            }

            //bodyString: {"error":"Token已经失效,或账号在别处登录！","status":-100}
            String bodyString = buffer.clone().readString(charset);
            Log.e(TAG, "intercept: "+bodyString );
            try {
                JSONObject jsonObject = new JSONObject(bodyString);
                int code = jsonObject.getInt("code");
                if (code==599) {//401
                    //TODO Token失效，刷新Token
                    Log.e(TAG, "intercept: 去登录" );
                    Intent intent = new Intent("com.hhxy.zw.adressbook.ACTION_START");
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    MyApplication.getContext().startActivity(intent);
                }
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }



            return originalResponse;
        }
    }


