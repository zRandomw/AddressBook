package com.hhxy.zw.adressbook.utils;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.text.TextUtils;

import com.github.promeg.pinyinhelper.Pinyin;
import com.hhxy.zw.adressbook.bean.ContactsBean;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


public class Util {
    private static String indexStr = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";


    public static ArrayList<ContactsBean> getContactData(ArrayList<ContactsBean> searchContactLists) {
        //向下移动光标
            //取得联系人名字
        searchContactLists.forEach(o->{
            getContactById(o);
        });

        Collections.sort(searchContactLists, new SortByPinyin());//数据排序

        return searchContactLists;
    }

    private static void getContactById(ContactsBean c) {
                if (c.getName()!=null) {
                    getPinyinList(c);
                } else {
                    c.setPinyinFirst("#");
                }
        }

    private static void getPinyinList(ContactsBean contactsBean) {
        StringBuffer bufferNamePiny = new StringBuffer();//NIHAO
        StringBuffer bufferNameMatch = new StringBuffer();//NH
        String name = contactsBean.getName();
        for (int i = 0; i < name.length(); i++) {
            StringBuffer bufferNamePer = new StringBuffer();
            String namePer = name.charAt(i) + "";//名字的每个字
            for (int j = 0; j < namePer.length(); j++) {
                char character = namePer.charAt(j);

                String pinCh = Pinyin.toPinyin(character).toUpperCase();
                bufferNamePer.append(pinCh);
                bufferNameMatch.append(pinCh.charAt(0));
                bufferNamePiny.append(pinCh);
            }
            contactsBean.getNamePinyinList().add(bufferNamePer.toString());//单个名字集合
        }
        contactsBean.setNamePinYin(bufferNamePiny.toString());
        contactsBean.setMatchPin(bufferNameMatch.toString());
        String firstPinyin = contactsBean.getNamePinYin().charAt(0) + "";
        if (indexStr.contains(firstPinyin)) {
            contactsBean.setPinyinFirst(firstPinyin);
        } else {
            contactsBean.setPinyinFirst("#");
        }
    }
    public static String transformPinYin(String character) {
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < character.length(); i++) {
            buffer.append(Pinyin.toPinyin(character.charAt(i)).toUpperCase());
        }
        return buffer.toString();
    }
    /**
     * 按照名字分类方便索引
     */
    static class SortByPinyin implements Comparator {
        public int compare(Object o1, Object o2) {
            ContactsBean s1 = (ContactsBean) o1;
            ContactsBean s2 = (ContactsBean) o2;
            if (s1.getPinyinFirst().equals("#")) {
                return 1;
            } else if (s2.getPinyinFirst().equals("#")) {
                return -1;
            }
            return s1.getPinyinFirst().compareTo(s2.getPinyinFirst());
        }
    }

}
