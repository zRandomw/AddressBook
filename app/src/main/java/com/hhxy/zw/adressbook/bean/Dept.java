package com.hhxy.zw.adressbook.bean;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import org.litepal.LitePal;
import org.litepal.crud.LitePalSupport;

import java.util.ArrayList;
import java.util.List;

public class Dept extends LitePalSupport {
    @SerializedName(value = "id")
    private int deptId;
    private String name;
    private String pid;
    private List<ContactsBean> contactsBeanList=new ArrayList<>();

    public int getDid() {
        return deptId;
    }

    public void setDid(int did) {
        this.deptId = did;
    }


    public List<ContactsBean> getUser() {
        return LitePal.where("deptid = ?", String.valueOf(deptId)).find(ContactsBean.class);
    }

    public void setUser(List<ContactsBean> user) {
        this.contactsBeanList = user;
    }




    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @NonNull
    @Override
    public String toString() {
        return name;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }
    public boolean save() {
        List<ContactsBean> where = LitePal.where("dept_id = ?", String.valueOf(deptId)).find(ContactsBean.class);
        if (where.size()>0){
            this.updateAll("uid = ?",String.valueOf(deptId));
            return true;
        }
        return super.save();
    }
}
