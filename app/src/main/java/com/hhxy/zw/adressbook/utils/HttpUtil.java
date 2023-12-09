package com.hhxy.zw.adressbook.utils;



import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class HttpUtil {
    public final static String url="http://10.0.2.2:9000";
    public static final MediaType JSON = MediaType.parse("application/json");
    public static void senOkHttpLogin(String url, String userAccount, String passWord,Callback callback) {
        OkHttpClient client = new OkHttpClient();
        JSONObject jsonObject=new JSONObject();
        try {
            jsonObject.put("phone",userAccount);
            jsonObject.put("password",passWord);
        }catch (Exception e){
            e.printStackTrace();
        }
//        Log.e(TAG, "senOkHttpLogin: "+String.valueOf(jsonObject) );
        RequestBody requestBody = RequestBody.create(String.valueOf(jsonObject),JSON);
        Request request = new Request.Builder().url(url).post(requestBody).build();
        client.newCall(request).enqueue(callback);
    }

    private static final String TAG = "HttpUtil";

    public static void senOkHttpRegitster(String url, String userAccount, String passWord, String userphone, String usersex, Callback callback) {
        OkHttpClient client = new OkHttpClient();
        JSONObject jsonObject=new JSONObject();
        try {
            jsonObject.put("userName",userAccount);
            jsonObject.put("password",passWord);
            jsonObject.put("phonenumber",userphone);
            jsonObject.put("sex",usersex);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody requestBody =RequestBody.create(jsonObject.toString(),JSON);
        Request request = new Request.Builder().url(url).post(requestBody).build();
       client.newCall(request).enqueue(callback);
     }

    public static void sendGetDataForUser(String token,Long poke,Callback callback) {
        String url= HttpUtil.url+"/api/people/afterList/"+poke;
        OkHttpClient client = new OkHttpClient();
        Request request=new Request.Builder().header("Authorization",token).url(url).build();
        client.newCall(request).enqueue(callback);
    }

//    public static void sendOkhttpPersonal(String token,Callback callback){
//        String url= SetHttpIpPort.url+"api/common/user/getInfo";
//        OkHttpClient client = new OkHttpClient();
//        Request request=new Request.Builder().header("Authorization",token).url(url).build();
//        client.newCall(request).enqueue(callback);
//    }
//    public static void sendOkhttpUpdatePersonal(String token,String avatar,String nickName,String phonenumber,String sex,Callback callback){
//        String url=SetHttpIpPort.url+"api/common/user";
//        OkHttpClient client = new OkHttpClient();
//        JSONObject jsonObject=new JSONObject();
//        try {
//            jsonObject.put("avatar",avatar);
//            jsonObject.put("nickName",nickName);
//            jsonObject.put("phonenumber",phonenumber);
//            jsonObject.put("sex",sex);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        RequestBody requestBody=RequestBody.create(JSON, String.valueOf(jsonObject));
//        Request request=new Request.Builder().header("Authorization",token).url(url).put(requestBody).build();
//        client.newCall(request).enqueue(callback);
//    }
//
//    public static void sendOkhttpUpdatepass(String token,String newPassword,String oldPassword,Callback callback){
//        String url=SetHttpIpPort.url+"api/common/user/resetPwd";
//        OkHttpClient client = new OkHttpClient();
//        JSONObject jsonObject=new JSONObject();
//        try {
//            jsonObject.put("newPassword",newPassword);
//            jsonObject.put("oldPassword",oldPassword);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        RequestBody requestBody=RequestBody.create(JSON, String.valueOf(jsonObject));
//        Request request=new Request.Builder().header("Authorization",token).url(url).put(requestBody).build();
//        client.newCall(request).enqueue(callback);
//    }
//    public static void senOkHttpAddfeedback(String token, String title, String cotent, Callback callback) {
//        String url=SetHttpIpPort.url+"api/common/feedback";
//        OkHttpClient client = new OkHttpClient();
//        JSONObject jsonObject=new JSONObject();
//        try {
//            jsonObject.put("title",title);
//            jsonObject.put("cotent",cotent);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        RequestBody requestBody =RequestBody.create(jsonObject.toString(),JSON);
//        Request request = new Request.Builder().url(url).header("Authorization",token).post(requestBody).build();
//        client.newCall(request).enqueue(callback);
//    }
//    public static void sendOkhttpNewsCategory(Callback callback){
//        String url=SetHttpIpPort.url+"press/category/list";
//        OkHttpClient client = new OkHttpClient();
//        Request request=new Request.Builder().url(url).build();
//        client.newCall(request).enqueue(callback);
//    }
//    public static void sendOkhttpNewsList(Callback callback){
//        String url=SetHttpIpPort.url+"press/press/list";
//        OkHttpClient client = new OkHttpClient();
//        Request request=new Request.Builder().url(url).build();
//        client.newCall(request).enqueue(callback);
//    }
//    public static void sendOkhttpNews(int id,Callback callback){
//        String url=SetHttpIpPort.url+"press/press/list/"+id;
//        OkHttpClient client = new OkHttpClient();
//        Request request=new Request.Builder().url(url).build();
//        client.newCall(request).enqueue(callback);
//
//    }
//    public static void sendOkhttpBanner(Callback callback){
//        String url=SetHttpIpPort.url+"api/rotation/list?pageNum=1&pageSize=8&type=2";
//        OkHttpClient client = new OkHttpClient();
//        Request request=new Request.Builder().url(url).build();
//        client.newCall(request).enqueue(callback);
//    }
//    public static void sendOkhttpUpload(String token,File file,Callback callback){
//        String url=SetHttpIpPort.url+"common/upload";
//        OkHttpClient client = new OkHttpClient();
//        RequestBody requestBody=new MultipartBody.Builder().setType(MultipartBody.FORM)
//                .addFormDataPart("upfile",file.getName(),RequestBody.create(file,MediaType.parse("application/octet-stream"))).build();
//        Request request=new Request.Builder().url(url).header("Authorization",token)
//                .post(requestBody).build();
//        client.newCall(request).enqueue(callback);
//    }
}
