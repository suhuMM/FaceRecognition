package com.android.face.bean;

import android.graphics.Bitmap;

/**
 * @author suhu
 * @data 2018/5/15 0015.
 * @description
 */

public class PersonInformation {

    private Bitmap bitmap;
    private String name;
    private String time;
    private boolean isPass;

    public PersonInformation(Bitmap bitmap, String name, String time,boolean isPass) {
        this.bitmap = bitmap;
        this.name = name;
        this.time = time;
        this.isPass = isPass;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public boolean getPass() {
        return isPass;
    }

    public void setPass(boolean pass) {
        isPass = pass;
    }
}
