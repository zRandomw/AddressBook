package com.hhxy.zw.adressbook.utils;

import static android.content.Context.MODE_PRIVATE;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hhxy.zw.adressbook.Launcher;
import com.hhxy.zw.adressbook.bean.ContactsBean;
import com.hhxy.zw.adressbook.bean.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class GsonUntil{

    private static final String TAG = "GsonUntil";
    public static boolean handleLogin(String data,ToActivity t){
        if(!TextUtils.isEmpty(data)){
            try {
                JSONObject jsonObject = new JSONObject(data);
                String msg = jsonObject.getString("msg");
                Log.e(TAG, "handleLogin: "+ msg);
                int code = jsonObject.getInt("code");
                if (code==200){
                    String token = jsonObject.getString("token");
                    return t.SharedPreferencesEdit(token);
                }else{
                    return !t.setShowToast(msg);
                }
            } catch (JSONException e) {
                 e.printStackTrace();
            }
        }return false;
    }
    public static ArrayList<ContactsBean> handleUserList(String data) throws JSONException {
        return new Gson().fromJson(data,new TypeToken<List<ContactsBean>>(){}.getType());
    }
}
