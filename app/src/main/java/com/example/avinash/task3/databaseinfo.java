package com.example.avinash.task3;

import android.net.Uri;

/**
 * Created by AVINASH on 7/1/2016.
 */
public class databaseinfo {
    public String name,num;byte[] img;

    public databaseinfo(String s, String s1, byte[] i) {
        this.name=s;this.num=s1;this.img=i;
    }

    public String getName(){
        return this.name;
    }
    public String getNum(){
        return this.num;
    }
    public byte[] getImg(){
        return this.img;
    }
}
