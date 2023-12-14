package com.hhxy.zw.adressbook;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.hhxy.zw.adressbook.adapter.PhoneContactAdapter;
import com.hhxy.zw.adressbook.bean.ContactsBean;
import com.hhxy.zw.adressbook.bean.Dept;
import com.hhxy.zw.adressbook.utils.GsonUntil;
import com.hhxy.zw.adressbook.utils.HttpUtil;
import com.hhxy.zw.adressbook.utils.MyApplication;
import com.hhxy.zw.adressbook.utils.RegexChk;
import com.hhxy.zw.adressbook.utils.Util;
import com.hhxy.zw.adressbook.view.QuickIndexBar;
import com.hhxy.zw.adressbook.view.StickyHeaderDecoration;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.LitePal;
import org.litepal.exceptions.DataSupportException;
import org.litepal.tablemanager.Connector;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;


public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener{
    private static final int ALL_USER=0;
    private static final int DEPT_NAME_USER=1;
    private int current=0;
    private EditText mEtSearch;
    private QuickIndexBar quickIndexBar;
    private RecyclerView rvContacts;
    ArrayList<ContactsBean> contactLists = new ArrayList<>();
    ArrayList<ContactsBean> searchContactLists = new ArrayList<>();//用于搜索的集合数据
    private PhoneContactAdapter contactAdapter;
    SharedPreferences s_data ;
    private LinearLayoutManager manager;
    private StickyHeaderDecoration decoration;
    private Spinner spinner;
    private SwipeRefreshLayout refresh;
    ArrayAdapter<Dept> sAdapter;
    List<Dept> deptNameList;
    String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        setContentView(R.layout.activity_main);
        Connector.getDatabase();
        s_data=getSharedPreferences("token",MODE_PRIVATE);
        initView();
        token= s_data.getString("token", null);
        getContacts();
        getDeptNameList();
    }

    private void getContacts() {

        List<ContactsBean> user = LitePal.findAll(ContactsBean.class);
        if (user.size()>0){
            searchContactLists.clear();
            contactLists.clear();
            user.sort(new Util.SortByPinyin());
            searchContactLists.addAll(user);
            contactLists.addAll(searchContactLists);
            contactAdapter.notifyDataSetChanged();
        }else {
            requestGetContacts();
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initView() {
//        下拉选择框
        spinner=(Spinner)findViewById(R.id.sp);
//        下拉刷新
        refresh=(SwipeRefreshLayout)findViewById(R.id.refresh);
        //搜索框
        mEtSearch = (EditText) findViewById(R.id.mEtSearch);
        //索引
        quickIndexBar = (QuickIndexBar) findViewById(R.id.qiBar);
        //数据列表
        rvContacts = (RecyclerView) findViewById(R.id.rvContacts);
        deptNameList=new ArrayList<>();
        sAdapter=new ArrayAdapter<Dept>(this,R.layout.item_for_custom_spinner,deptNameList);
        spinner.setAdapter(sAdapter);
        manager = new LinearLayoutManager(this);
        rvContacts.setLayoutManager(manager);
        rvContacts.setHasFixedSize(true);
        contactAdapter = new PhoneContactAdapter(this, contactLists);
        //设置悬浮索引
        decoration = new StickyHeaderDecoration(contactAdapter);
        rvContacts.setAdapter(contactAdapter);
        rvContacts.addItemDecoration(decoration);
        refresh.setOnRefreshListener(this);
        //索引监听
        quickIndexBar.setOnLetterChangeListener(new QuickIndexBar.OnLetterChangeListener() {
            @Override
            public void onLetterChange(String letter) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                // 隐藏软键盘
                imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
                for (int i = 0; i < contactLists.size(); i++) {

                    if (letter.equals(contactLists.get(i).getPinyinFirst() + "")) {

                        int position = contactAdapter.getPositionForSection(contactLists.get(i).getPinyinFirst().charAt(0));
                        if (position != -1) {
                            //滑动到指定位置
                            manager.scrollToPositionWithOffset(position, 0);
                        }
                        break;
                    }
                }
            }

            @Override
            public void onReset() {

            }
        });
        //触摸隐藏键盘
        rvContacts.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                InputMethodManager imm = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                // 隐藏软键盘
                imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
                return false;
            }
        });
        //搜索功能
        mEtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                //查询数据
                filtDatas(s);
            }
        });
        final int[] flag = {0};
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (flag[0] == 0){
                    flag[0] = flag[0] + 1;
                    return;
                }
                if (position==0){
                    getContacts();
                    current=ALL_USER;
                    return;
                }else current=DEPT_NAME_USER;
                searchContactLists.clear();
                contactLists.clear();
                Dept dept = ((Dept) parent.getItemAtPosition(position));
                List<ContactsBean> user = dept.getUser();
                user.sort(new Util.SortByPinyin());
                searchContactLists.addAll(dept.getUser());
                contactLists.addAll(searchContactLists);
                contactAdapter.notifyDataSetChanged();
//                HttpUtil.sendGetDataUserForDeptId(token, dept.getDid(),new Callback() {
//                    @Override
//                    public void onFailure(@NonNull Call call, @NonNull IOException e) {
//
//                    }
//                    @Override
//                    public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
//                            String data=response.body()!=null?response.body().string():null;
//                        JSONObject jsonObject = null;
//                        try {
//                            jsonObject = new JSONObject(data);
//                            String msg = jsonObject.getString("msg");
//                            int code = jsonObject.getInt("code");
//                            if (code==200) {
//                                JSONArray data1 = jsonObject.getJSONArray("data");
//                                ArrayList<ContactsBean> contacts = GsonUntil.handleDeptUserList((String.valueOf(data1)));
//                                contacts.forEach(Util::getContactById);
//                                contacts.sort(new Util.SortByPinyin());
//                                searchContactLists.addAll(contacts);
//                                contactLists.addAll(searchContactLists);
//                                runOnUiThread(()->{
//                                            contactAdapter.notifyDataSetChanged();
//                                }
//                                );
//                            }else runOnUiThread(()->{
//                                Toast.makeText(MainActivity.this,msg,Toast.LENGTH_LONG).show();
//                            });
//
//                        } catch (JSONException e) {
//                            throw new RuntimeException(e);
//                        }
//
//                    }
//                });
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

    }


private static final String TAG = "MainActivity";
    private void requestGetContacts(){

        long poke = s_data.getLong("poke", 0);
        HttpUtil.sendGetDataForUser(token, poke, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                runOnUiThread(()->{
                    Toast.makeText(MainActivity.this,"可能没有网络或ip设置错误",Toast.LENGTH_LONG).show();
                });
            }
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                String data=response.body()!=null?response.body().string():null;
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(data);
                    String msg = jsonObject.getString("msg");
                    if (jsonObject.getInt("code")==200) {
                        JSONArray data1 = jsonObject.getJSONArray("data");
                        Long poke = jsonObject.getLong("poke");
                        GsonUntil.handleUserList(String.valueOf(data1));
//                        Log.e(TAG, "onResponse: "+contactLists.get(1).getName() );
                        runOnUiThread(()->{
                                    SharedPreferences.Editor edit = s_data.edit();
                                    edit.putLong("poke", poke);
                                    edit.apply();
                                    getContacts();
                                    refresh.setRefreshing(false);
                        }
                        );
                    }else runOnUiThread(()->{
                        Toast.makeText(MainActivity.this,msg,Toast.LENGTH_LONG).show();
                    });

                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }


            }
        });

    }
    private void requestGetDeptNameList(){

            HttpUtil.sendGetDataForDeptNameList(token, new Callback() {
                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    String data=response.body()!=null?response.body().string():null;
                    JSONObject jsonObject = null;
                    try {
                        jsonObject = new JSONObject(data);
                        final String msg = jsonObject.getString("msg");
                        final int code = jsonObject.getInt("code");
                        if (code==200) {
                            JSONArray data1 = jsonObject.getJSONArray("data");
                            runOnUiThread(()->{
                                LitePal.deleteAll(Dept.class);
                                GsonUntil.handleDeptList(String.valueOf(data1));
                                deptNameList.clear();
                                getDeptNameList();
                            });
                        } else {
                            runOnUiThread(()->{
                                Toast.makeText(MainActivity.this,msg,Toast.LENGTH_LONG).show();

                            });
                        }
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }

                }

                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {

                }
            });
        }


    private void getDeptNameList(){
        List<Dept> all = LitePal.findAll(Dept.class);
        if (all.size()>0){
                deptNameList.clear();
                Dept dept = new Dept();
                dept.setName("全部");
                deptNameList.add(dept);
                deptNameList.addAll(all);
                sAdapter.notifyDataSetChanged();
        }else {
            requestGetDeptNameList();
        }
    }
    //搜索数据
    private void filtDatas(Editable s) {
        if (searchContactLists.size() == 0) {
            return;
        }
        String inputStr = s.toString();
        if (TextUtils.isEmpty(inputStr)) {
            resetSearchData();
            contactLists.clear();
            contactLists.addAll(searchContactLists);
            contactAdapter.notifyDataSetChanged();
        } else {
            contactLists.clear();
            //因为每次搜索的结果不同，所以匹配类型不同，但是数据源都是同一个数据源，所以每次搜索前，要重置数据
            resetSearchData();
            if (RegexChk.isNumeric(inputStr)) {//如果是数字
                findDataByphoneOrCN(inputStr);
            } else if (RegexChk.isContainChinese(inputStr)) {//如果含义中文，需要精确匹配
                findDataByphoneOrCN(inputStr);
            } else if (RegexChk.isEnglishAlphabet(inputStr)) {//是不是全是英文字母或者是拼音的话
                findDataByEN(inputStr);
            } else {//需要精确匹配
                findDataByphoneOrCN(inputStr);
            }
            contactAdapter.notifyDataSetChanged();
        }
    }

    private void resetSearchData() {
        for (int i = 0; i < searchContactLists.size(); i++) {
            searchContactLists.get(i).setMatchType(0);//重置为没有匹配类型
        }
    }

    private void findDataByphoneOrCN(String inputStr) {
        for (int i = 0; i < searchContactLists.size(); i++) {
            ContactsBean contactsBean = searchContactLists.get(i);
            if (!TextUtils.isEmpty(contactsBean.getName()) && contactsBean.getName().contains(inputStr)) {
                contactsBean.setMatchType(1);//名字匹配
                contactsBean.setHighlightedStart(contactsBean.getName().indexOf(inputStr));
                contactsBean.setHighlightedEnd(contactsBean.getHighlightedStart() + inputStr.length());
                contactLists.add(contactsBean);
                continue;
            }
                    String phone = contactsBean.getphone();
                    if (!TextUtils.isEmpty(phone) && phone.contains(inputStr)) {
//                        contactsBean.setShowphoneIndex(j);//显示号码的下标
                        contactsBean.setMatchType(2);//电话匹配
                        contactsBean.setHighlightedStart(phone.indexOf(inputStr));
                        contactsBean.setHighlightedEnd(contactsBean.getHighlightedStart() + inputStr.length());
                        contactLists.add(contactsBean);
                    }
            }

    }

    //通过拼音或者英文字母
    private void findDataByEN(String inputStr) {
        //把输入的内容变为大写
        String searPinyin = Util.transformPinYin(inputStr);
        //搜索字符串的长度
        int searLength = searPinyin.length();
        //搜索的第一个大写字母
        String searPinyinFirst = searPinyin.charAt(0) + "";
        for (int i = 0; i < searchContactLists.size(); i++) {
            ContactsBean contactsBean = searchContactLists.get(i);
            contactsBean.setMatchType(1);//字母匹配肯定是名字
            //如果输入的每一个字母都和名字的首字母一样，那就可以匹配比如：你好，NH，输入nh就ok
            if (contactsBean.getMatchPin().contains(searPinyin)) {
                contactsBean.setHighlightedStart(contactsBean.getMatchPin().indexOf(searPinyin));
                contactsBean.setHighlightedEnd(contactsBean.getHighlightedStart() + searLength);
                contactLists.add(contactsBean);
            } else {
                boolean isMatch = false;
                //先去匹配单个字，比如你好：NI,HAO.输入NI，肯定匹配第一个
                for (int j = 0; j < contactsBean.getNamePinyinList().size(); j++) {
                    String namePinyinPer = contactsBean.getNamePinyinList().get(j);
                    if (!TextUtils.isEmpty(namePinyinPer) && namePinyinPer.startsWith(searPinyin)) {
                        //符合的话就是当前字匹配成功
                        contactsBean.setHighlightedStart(j);
                        contactsBean.setHighlightedEnd(j + 1);
                        contactLists.add(contactsBean);
                        isMatch = true;
                        break;
                    }
                }
                if (isMatch) {
                    continue;
                }
                //根据拼音包含来实现，比如你好：NIHAO,输入NIHA或者NIHAO。
                if (!TextUtils.isEmpty(contactsBean.getNamePinYin()) && contactsBean.getNamePinYin().contains(searPinyin)) {
                    //这样的话就要从每个字的拼音开始匹配起
                    for (int j = 0; j < contactsBean.getNamePinyinList().size(); j++) {
                        StringBuilder sbMatch = new StringBuilder();
                        for (int k = j; k < contactsBean.getNamePinyinList().size(); k++) {
                            sbMatch.append(contactsBean.getNamePinyinList().get(k));
                        }
                        if (sbMatch.toString().startsWith(searPinyin)) {
                            //匹配成功
                            contactsBean.setHighlightedStart(j);
                            int length = 0;
                            //比如输入是NIH，或者NIHA,或者NIHAO,这些都可以匹配上，从而就可以通过NIHAO>=NIH,HIHA,NIHAO
                            for (int k = j; k < contactsBean.getNamePinyinList().size(); k++) {
                                length = length + contactsBean.getNamePinyinList().get(k).length();
                                if (length >= searLength) {
                                    contactsBean.setHighlightedEnd(k + 1);
                                    break;
                                }
                            }
                            isMatch = true;
                            contactLists.add(contactsBean);
                        }
                    }
                }

                if (isMatch) {
                    continue;
                }

                //最后一种情况比如：广发银行，输入GuangFY或者GuangFYH都可以匹配成功，这样的情况名字集合必须大于等于3
                if (contactsBean.getNamePinyinList().size() > 2) {
                    for (int j = 0; j < contactsBean.getNamePinyinList().size(); j++) {

                        StringBuilder sbMatch0 = new StringBuilder();
                        sbMatch0.append(contactsBean.getNamePinyinList().get(j));
                        //只匹配到倒数第二个
                        if (j < contactsBean.getNamePinyinList().size() - 2) {
                            for (int k = j + 1; k < contactsBean.getMatchPin().length(); k++) {
                                //依次添加后面每个字的首字母
                                sbMatch0.append(contactsBean.getMatchPin().charAt(k));
                                if (sbMatch0.toString().equals(searPinyin)) {
                                    contactsBean.setHighlightedStart(j);
                                    contactsBean.setHighlightedEnd(k + 1);
                                    contactLists.add(contactsBean);
                                    isMatch = true;
                                    break;
                                }
                            }
                        }

                        if (isMatch) {
                            //跳出循环已找到
                            break;
                        }

                        //sbMatch1是循环匹配对象比如GUANGFYH，GUANGFAYH，GUANGFAYINH,GUANGFAYINHANG，
                        //FAYH,YINH
                        StringBuilder sbMatch1 = new StringBuilder();
                        for (int k = 0; k <= j; k++) {//依次作为初始匹配的起点
                            sbMatch1.append(contactsBean.getNamePinyinList().get(k));
                        }
                        //只匹配到倒数第二个
                        if (j < contactsBean.getNamePinyinList().size() - 2) {
                            for (int k = j + 1; k < contactsBean.getMatchPin().length(); k++) {
                                //依次添加后面每个字的首字母
                                sbMatch1.append(contactsBean.getMatchPin().charAt(k));
                                if (sbMatch1.toString().equals(searPinyin)) {
                                    contactsBean.setHighlightedStart(j);
                                    contactsBean.setHighlightedEnd(k + 1);
                                    contactLists.add(contactsBean);
                                    isMatch = true;
                                    break;
                                }
                            }
                        }
                        if (isMatch) {
                            //跳出循环已找到
                            break;
                        }

                        if (j >= contactsBean.getNamePinyinList().size() - 2) {
                            //如果说是剩余最后两个拼音不需要匹配了
                            break;
                        }
                        StringBuilder sbMatch2 = new StringBuilder();
                        sbMatch2.append(contactsBean.getNamePinyinList().get(j));
                        for (int k = j + 1; k < contactsBean.getNamePinyinList().size(); k++) {
                            sbMatch2.append(contactsBean.getNamePinyinList().get(k));
                            StringBuilder sbMatch3 = new StringBuilder();
                            sbMatch3.append(sbMatch2.toString());
                            //只匹配到倒数第二个
                            if (j < contactsBean.getNamePinyinList().size() - 2) {
                                for (int m = k + 1; m < contactsBean.getMatchPin().length(); m++) {
                                    //依次添加后面每个字的首字母
                                    sbMatch3.append(contactsBean.getMatchPin().charAt(m));
                                    if (sbMatch3.toString().equals(searPinyin)) {
                                        contactsBean.setHighlightedStart(j);
                                        contactsBean.setHighlightedEnd(m + 1);
                                        contactLists.add(contactsBean);
                                        isMatch = true;
                                        break;
                                    }
                                }
                            }
                            if (isMatch) {
                                //跳出循环已找到
                                break;
                            }
                        }

                        if (isMatch) {
                            //跳出循环已找到
                            break;
                        }
                    }
                }

            }
        }
    }

    @Override
    public void onRefresh() {
        switch (current){
            case ALL_USER:
                new Thread(()->{
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    runOnUiThread(()->{
                        requestGetDeptNameList();
                        requestGetContacts();
                    });
                }).start();
                break;
            case DEPT_NAME_USER:
                 refresh.setRefreshing(false);
        }

    }
}
