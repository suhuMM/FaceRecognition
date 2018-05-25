package com.android.face.utils;


import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;

/**
 * @author suhu
 * @data 2018/5/9 0009.
 * @description 权限检查工具类
 */

public class PermissionUtils {
    public static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.WRITE_EXTERNAL_STORAGE

    };

    /**
     * 权限检查
     * @param activity
     */
    public static boolean checkPermission(Activity activity){
        int camera = ActivityCompat.checkSelfPermission(activity,PERMISSIONS_STORAGE[0]);
        if (camera!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(activity,PERMISSIONS_STORAGE,REQUEST_EXTERNAL_STORAGE);
            return false;
        }else {
            return true;
        }

    }










}
