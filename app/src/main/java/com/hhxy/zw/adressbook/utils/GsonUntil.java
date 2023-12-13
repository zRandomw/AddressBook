package com.hhxy.zw.adressbook.utils;

import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hhxy.zw.adressbook.bean.ContactsBean;
import com.hhxy.zw.adressbook.bean.Dept;

import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.crud.LitePalSupport;

import java.util.ArrayList;
import java.util.Arrays;
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
        ArrayList<ContactsBean> o = new Gson().fromJson(data, new TypeToken<List<ContactsBean>>() {
        }.getType());
        Util.getContactDataAndSave(o);
        return o;
    }
    public static ArrayList<ContactsBean> handleDeptUserList(String data) throws JSONException {
        ArrayList<ContactsBean> o = new Gson().fromJson(data, new TypeToken<List<ContactsBean>>() {
        }.getType());
        return o;
    }
    public static ArrayList<Dept> handleDeptList(String data) throws JSONException {
       ArrayList<Dept> depts= new Gson().fromJson(data, new TypeToken<List<Dept>>() {
        }.getType());
       depts.forEach(Dept::save);
        Log.e(TAG, "handleDeptList: "+ Arrays.toString(depts.toArray()));
        return depts;
    }
}
