package com.hhxy.zw.adressbook;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.hhxy.zw.adressbook.bean.ContactsBean;

public class HomeActivity extends AppCompatActivity {

    private TextView nickname;
    private TextView NO;
    private TextView hPhone;
    private TextView deptName;
    Intent intent;
    private TextView job;
    private Button call;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        intent = getIntent();
        initView();
    }

    private static final String TAG = "HomeActivity";
    private void initView() {
        nickname = (TextView) findViewById(R.id.nickname);
        NO = (TextView) findViewById(R.id.NO);
        hPhone = (TextView) findViewById(R.id.h_phone);
        deptName = (TextView) findViewById(R.id.deptName);
        ContactsBean user = (ContactsBean) intent.getSerializableExtra("user");
        job = (TextView) findViewById(R.id.job);
        call = (Button) findViewById(R.id.call);
        Log.e(TAG, "initView: "+user.getName() );
        assert user != null;
        if (user.getName()!=null) {
            nickname.setText(user.getName());
        }
        if (user.getphone()!=null) {
            hPhone.append(user.getphone());
        }
        if (user.getPoliceId()!=null) {
            NO.setText(user.getPoliceId());
        }
        if (user.getDeptName()!=null) {
            deptName.append(user.getDeptName());
        }
        if (user.getJob()!=null) {
            job.append(user.getJob());
        }
        call.setOnClickListener(v->{
            Intent intent1 = new Intent(Intent.ACTION_DIAL);
            intent1.setData(Uri.parse("tel:"+user.getphone()));
            startActivity(intent1);
        });
    }

}