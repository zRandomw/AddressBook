package com.hhxy.zw.adressbook.bean;


import android.util.Log;

import androidx.annotation.NonNull;


import com.google.gson.annotations.SerializedName;

import org.litepal.FluentQuery;
import org.litepal.LitePal;
import org.litepal.annotation.Column;
import org.litepal.crud.LitePalSupport;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/**
 *
 */

public class ContactsBean extends LitePalSupport implements Comparable<ContactsBean> , Serializable {
    private static final String TAG = "ContactsBean";


    private static final long serialVersionUID = 1L;

    @SerializedName(value = "id")
    @Column(unique = true)
    private long uid;//通讯录ID
    private String name; //名字
    private String phone;//电话号码
    private String pinyinFirst;//拼音首字母用于悬浮栏
//    private int showphoneIndex = 0;//因为一个人可能会有多个号码
    private int highlightedStart = 0;//需要高亮的开始下标
    private int highlightedEnd = 0;//需要高亮的结束下标
    private String matchPin = "";//用来匹配的拼音每个字的首字母比如：你好，NH
    private String namePinYin = "";//全名字拼音,比如：你好,NIHAO
    private int matchType = 0;//匹配类型，名字1，电话号码2，其他0,根据输入的来判断

    public int getDelete() {
        return delete;
    }

    public void setDelete(int delete) {
        this.delete = delete;
    }

    private int delete=0;//  判断是否被删除
    private List<String> namePinyinList = new ArrayList<>();//名字拼音集合，比如你好，NI,HAO
//    private ArrayList<String> phoneList = new ArrayList<>();//电话号码集合，一个人可能会有多个号码
    private int matchIndex = 0;
    //匹配到号码后的下标
    private String policeId;
    private String deptId;
    private String deptName;
    private String sex;
    private String cardId;
    private String job;
    private int isLeader;
    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getCardId() {
        return cardId;
    }

    public void setCardId(String cardId) {
        this.cardId = cardId;
    }


    public String getDeptName() {
        return deptName;
    }

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }

    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }

    public String getDeptId() {
        return deptId;
    }

    public void setDeptId(String deptId) {
        this.deptId = deptId;
    }

    public String getPoliceId() {
        return policeId;
    }

    public void setPoliceId(String policeId) {
        this.policeId = policeId;
    }




    public ContactsBean() {
    }
    public int getMatchIndex() {
        return matchIndex;
    }

    public void setMatchIndex(int matchIndex) {
        this.matchIndex = matchIndex;
    }

    public String getPinyinFirst() {
        return pinyinFirst;
    }

    public void setPinyinFirst(String pinyinFirst) {
        this.pinyinFirst = pinyinFirst;
    }

    public Long getid() {
        return uid;
    }

    public void setid(Long id) {
        this.uid = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getphone() {
        return phone;
    }

    public void setphone(String phone) {
        this.phone = phone;
    }


    public int getHighlightedStart() {
        return highlightedStart;
    }

    public void setHighlightedStart(int highlightedStart) {
        this.highlightedStart = highlightedStart;
    }

    public int getHighlightedEnd() {
        return highlightedEnd;
    }

    public void setHighlightedEnd(int highlightedEnd) {
        this.highlightedEnd = highlightedEnd;
    }

    public String getMatchPin() {
        return matchPin;
    }

    public void setMatchPin(String matchPin) {
        this.matchPin = matchPin;
    }

    public String getNamePinYin() {
        return namePinYin;
    }

    public void setNamePinYin(String namePinYin) {
        this.namePinYin = namePinYin;
    }

    public int getMatchType() {
        return matchType;
    }

    public void setMatchType(int matchType) {
        this.matchType = matchType;
    }

    public List<String> getNamePinyinList() {
        return namePinyinList;
    }

    public void setNamePinyinList(ArrayList<String> namePinyinList) {
        this.namePinyinList = namePinyinList;
    }


    @Override
    public int compareTo(@NonNull ContactsBean o) {
        return 0;
    }

    public long getId() {
        return this.uid;
    }

    public void setId(long id) {
        this.uid = id;
    }

    public String getPhone() {
        return this.phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public int getIsLeader() {
        return this.isLeader;
    }

    public void setIsLeader(int isLeader) {
        this.isLeader = isLeader;
    }

    public void setNamePinyinList(List<String> namePinyinList) {
        this.namePinyinList = namePinyinList;
    }
    @Override
    public boolean save() {
        if (delete==1){
            LitePal.deleteAll(ContactsBean.class,"uid = ?",String.valueOf(uid));
            return true;
        }
        List<ContactsBean> where = LitePal.where("uid = ?", String.valueOf(uid)).find(ContactsBean.class);
        if (where.size()>0){
            Log.e(TAG, "save: "+uid );
            this.updateAll("uid = ?",String.valueOf(uid));
            return true;
        }
        return super.save();
    }
}
