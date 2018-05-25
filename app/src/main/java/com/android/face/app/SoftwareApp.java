package com.android.face.app;

import android.app.Application;

import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author suhu
 * @data 2018/5/9 0009.
 * @description 程序入口
 */

public class SoftwareApp extends Application{
    private static ThreadPoolExecutor poolExecutor;
    private static SoftwareApp instance;
    public static SoftwareApp getInstance() {
        return instance;
    }
    @Override
    public void onCreate() {
        super.onCreate();
        poolExecutor = new ThreadPoolExecutor(4,6,1, TimeUnit.SECONDS,new LinkedBlockingDeque<Runnable>(128));


    }

    /**
     * 获得线程池
     * @return ThreadPoolExecutor
     */
    public static ThreadPoolExecutor getThreadPool(){
        return poolExecutor;
    }
}
