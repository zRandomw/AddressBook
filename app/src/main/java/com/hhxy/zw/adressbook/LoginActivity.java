package com.hhxy.zw.adressbook;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.hhxy.zw.adressbook.utils.GsonUntil;
import com.hhxy.zw.adressbook.utils.HttpUtil;
import com.hhxy.zw.adressbook.utils.ToActivity;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity implements ToActivity {

    private static final String TAG = "LOGIN";
    private EditText account,pass;
    private Button login;
    private Button ip;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        init();
    }

    private void init() {
        ip=findViewById(R.id.set_ip);
        account=findViewById(R.id.account);
        pass=findViewById(R.id.pass);
        login=findViewById(R.id.login);
        login.setOnClickListener(v->{
            Login(account.getText().toString().trim(),pass.getText().toString().trim());
        });
        ip.setOnClickListener(v->{
            AlertDialog.Builder builder=new AlertDialog.Builder(this);
            builder.setTitle("网络设置");
            LayoutInflater layoutInflater = getLayoutInflater();
            View view=layoutInflater.inflate(R.layout.my_dialog,null);
            builder.setView(view);
            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    EditText ip1,ip2,ip3,ip4,port;
                    ip1=view.findViewById(R.id.ip1);
                    ip2=view.findViewById(R.id.ip2);
                    ip3=view.findViewById(R.id.ip3);
                    ip4=view.findViewById(R.id.ip4);
                    port=view.findViewById(R.id.port);
                    String ip1s = ip1.getText().toString().trim();
                    String ip2s = ip2.getText().toString().trim();
                    String ip3s = ip3.getText().toString().trim();
                    String ip4s = ip4.getText().toString().trim();
                    String prot = port.getText().toString().trim();
                    String url="http://"+ip1s+"."+ip2s+"."+ip3s+"."+ip4s+":"+prot;
                    HttpUtil.url=url;
                    try {
                        SharedPreferences tokendata = getSharedPreferences("data", MODE_PRIVATE);
                        SharedPreferences.Editor editor=tokendata.edit();
                        editor.putString("url",url);
                        editor.apply();
                        Log.d("TAG", "onClick: "+url);
                    }catch (Exception e){
                        Toast.makeText(LoginActivity.this,"请输入数字",Toast.LENGTH_SHORT).show();
                    }
                    Toast.makeText(LoginActivity.this,"已成功修改",Toast.LENGTH_SHORT).show();
                }
            });
            builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            builder.show();
        });
    }

    private void Login(String phone,String pass) {
        HttpUtil.senOkHttpLogin(HttpUtil.url+"/api/sys/login", phone, pass, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                runOnUiThread(()->{
                    Toast.makeText(LoginActivity.this,"ip或端口号未设置或设置错误",Toast.LENGTH_LONG).show();
                });

            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                String string = response.body() != null ? response.body().string() : null;
                if (GsonUntil.handleLogin(string,LoginActivity.this)) {
                    runOnUiThread(()->{
                        Toast.makeText(LoginActivity.this,"登录成功",Toast.LENGTH_LONG).show();
                        startActivity(new Intent(LoginActivity.this,MainActivity.class));
                    });

                }
            }
        });

    }


    @Override
    public boolean setShowToast(String msg) {
        if (!TextUtils.isEmpty(msg)) {
            runOnUiThread(() -> {
                Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
            });
            return true;
        }else return false;
    }

    @Override
    public boolean SharedPreferencesEdit(String token) {
        if (!TextUtils.isEmpty(token)){
            SharedPreferences.Editor editor=getSharedPreferences("token",MODE_PRIVATE).edit();
            editor.putString("token",token);
            editor.apply();
            return true;
        }else return false;
    }
}