package com.hhxy.zw.adressbook;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.hhxy.zw.adressbook.utils.GsonUntil;
import com.hhxy.zw.adressbook.utils.HttpUtil;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LOGIN";
    private EditText account,pass;
    private Button login,zhuce;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        init();
    }

    private void init() {
        account=findViewById(R.id.account);
        pass=findViewById(R.id.pass);
        login=findViewById(R.id.login);
        login.setOnClickListener(v->{
            Login(account.getText().toString().trim(),pass.getText().toString().trim());
        });
    }

    private void Login(String phone,String pass) {
        HttpUtil.senOkHttpLogin(HttpUtil.url+"/api/sys/login", phone, pass, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e(TAG, "onFailure: "+e.toString() );
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                String string = response.body() != null ? response.body().string() : null;
                Log.e(TAG, "onResponse: "+string );
                if (GsonUntil.handleLogin(string, LoginActivity.this)) {
                    runOnUiThread(()->{
                        Toast.makeText(LoginActivity.this,"登录成功",Toast.LENGTH_LONG).show();
                        startActivity(new Intent(LoginActivity.this,MainActivity.class));
                    });

                }
            }
        });

    }
}