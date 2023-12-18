package com.hhxy.zw.adressbook;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

public class Launcher extends AppCompatActivity {

    SharedPreferences pre;
    SharedPreferences.Editor editor;
    private boolean isFirst;
    private static final int HANDLER_SPLASH = 1;
    private static final String SHARE_IS_FIRST = "isFirst";

    private final Handler handler = new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case HANDLER_SPLASH:
                    //判断程序是否是第一次运行
                    Log.d("TAG", "handleMessage: "+isFirst);
                    if (isFirst()) {
                        startActivity(new Intent(Launcher.this,LoginActivity.class));
                    } else {
                        Log.d("TAG", "handleMessage: "+isFirst);
                        startActivity(new Intent(Launcher.this, MainActivity.class));
                    }
                    finish();
                    break;
                default:
                    break;
            }
        }

    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        setContentView(R.layout.activity_launcher);
        initView();
    }

    private void initView() {
        pre = getSharedPreferences("data", MODE_PRIVATE);
        editor = getSharedPreferences("data", MODE_PRIVATE).edit();
        isFirst = pre.getBoolean("isFirst", true);
        handler.sendEmptyMessageDelayed(HANDLER_SPLASH, 3000);

    }
    private boolean isFirst() {
        boolean isFirst = pre.getBoolean("isFirst", true);
        if (isFirst) {
            editor.putBoolean("isFirst", false);
            editor.apply();
            return true;
        } else {
            return false;
        }

    }

    @Override
    public void onResume() {
        super.onResume();
    }
    @Override
    public void onPause() {
        super.onPause();
    }

}
