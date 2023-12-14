package com.hhxy.zw.adressbook.utils;

import android.app.Application;
import android.content.SharedPreferences;

import org.litepal.LitePalApplication;

public class MyApplication extends LitePalApplication {

        @Override
        public void onCreate() {
            super.onCreate();
            SharedPreferences data = getSharedPreferences("data", MODE_PRIVATE);
            HttpUtil.url=data.getString("url","http://10.0.2.2:9000");
        }



}
