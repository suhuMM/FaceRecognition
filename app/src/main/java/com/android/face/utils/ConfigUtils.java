package com.android.face.utils;

import android.os.Environment;

/**
 * @author suhu
 * @data 2018/5/24 0024.
 * @description
 */

public class ConfigUtils {
    /**
     * sd卡的目录
     */
    public static final String SDCARD = Environment.getExternalStorageDirectory().toString();

    /**
     * app目录
     */
    public static final String APP_FILE = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath();

}
