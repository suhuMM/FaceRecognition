package com.android.face.utils;

import android.content.Context;
import android.graphics.Point;
import android.hardware.Camera;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * @author suhu
 * @data 2018/5/15 0015.
 * @description
 */

public class Utils {

    /**
     * 获取当前时间
     * @return time
     */
    public static String getTime(){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM-dd HH:mm:ss");// HH:mm:ss
        Date date = new Date(System.currentTimeMillis());
        return simpleDateFormat.format(date);
    }


    /**
     * 获取相机预览尺寸
     *
     * @param surfaceWidth
     * @param surfaceHeight
     * @param preSizeList
     * @param mIsPortrait
     * @return Camera.Size
     */
    public static Camera.Size getCloselyPreSize(int surfaceWidth, int surfaceHeight, List<Camera.Size> preSizeList,boolean mIsPortrait) {
        int ReqTmpWidth;
        int ReqTmpHeight;
        // 当屏幕为垂直的时候需要把宽高值进行调换，保证宽大于高
        if (mIsPortrait) {
            ReqTmpWidth = surfaceHeight;
            ReqTmpHeight = surfaceWidth;
        } else {
            ReqTmpWidth = surfaceWidth;
            ReqTmpHeight = surfaceHeight;
        }
        for(Camera.Size size : preSizeList){
            if((size.width == ReqTmpWidth) && (size.height == ReqTmpHeight)){
                return size;
            }
        }
        // 得到与传入的宽高比最接近的size
        float reqRatio = ((float) ReqTmpWidth) / ReqTmpHeight;
        float curRatio, deltaRatio;
        float deltaRatioMin = Float.MAX_VALUE;
        Camera.Size retSize = null;
        for (Camera.Size size : preSizeList) {
            curRatio = ((float) size.width) / size.height;
            deltaRatio = Math.abs(reqRatio - curRatio);
            if (deltaRatio < deltaRatioMin) {
                deltaRatioMin = deltaRatio;
                retSize = size;
            }
        }
        return retSize;
    }


    public static String getSurfaceViewSize(int width, int height) {

        if (equalRate(width, height, 1.33f)) {
            return "4:3";
        } else {
            return "16:9";
        }
    }

    public static boolean equalRate(int width, int height, float rate) {
        float r = (float)width /(float) height;
        if (Math.abs(r - rate) <= 0.2) {
            return true;
        } else {
            return false;
        }
    }


    /**
     * 获取状态栏高度
     *
     * @param context
     * @return
     */
    public static int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen",
                "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    /**
     * 获取导航栏高度
     * @param context
     * @return
     */
    public static int getDaoHangHeight(Context context) {
        int result = 0;
        int resourceId=0;
        int rid = context.getResources().getIdentifier("config_showNavigationBar", "bool", "android");
        if (rid!=0){
            resourceId = context.getResources().getIdentifier("navigation_bar_height", "dimen", "android");
            return context.getResources().getDimensionPixelSize(resourceId);
        }else
            return 0;
    }


    public static void setCameraParams(Camera mCamera,int x,int y){

        Point screenResolution = new Point(x, y);
        Camera.Size previewSize = CameraUtils.findBestPreviewSizeValue(screenResolution, mCamera);
        Camera.Parameters mParams = mCamera.getParameters();
        List<String> supportedFlashModes = mParams.getSupportedFocusModes();
        if (supportedFlashModes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO)) {
            mParams.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
        } else if (supportedFlashModes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
            mParams.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
        } else if (supportedFlashModes.contains(Camera.Parameters.FOCUS_MODE_AUTO)) {
            mParams.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
        }
        int[] minFps = new int[1], maxFps = new int[1];
        determineCameraPreviewFpsRange(mParams, minFps, maxFps);
        mParams.setPreviewFpsRange(minFps[0], maxFps[0]);
        mParams.setPreviewSize(previewSize.width, previewSize.height);
        mCamera.setParameters(mParams);
    }

    private static void determineCameraPreviewFpsRange(Camera.Parameters parameters, int[] minFps, int[] maxFps) {
        final int MAX_FPS = 30 * 1000;
        List<int[]> frameRates = parameters.getSupportedPreviewFpsRange();
        minFps[0] = 0;
        for (int[] intArr : frameRates) {
            if (minFps[0] == 0) {
                minFps[0] = intArr[Camera.Parameters.PREVIEW_FPS_MIN_INDEX];
                maxFps[0] = intArr[Camera.Parameters.PREVIEW_FPS_MAX_INDEX];
                continue;
            }
            if (intArr[Camera.Parameters.PREVIEW_FPS_MAX_INDEX] <= MAX_FPS) {
                if (intArr[Camera.Parameters.PREVIEW_FPS_MIN_INDEX] >= minFps[0] &&
                        intArr[Camera.Parameters.PREVIEW_FPS_MAX_INDEX] >= maxFps[0]) {
                    minFps[0] = intArr[Camera.Parameters.PREVIEW_FPS_MIN_INDEX];
                    maxFps[0] = intArr[Camera.Parameters.PREVIEW_FPS_MAX_INDEX];
                }
            }
        }
    }


}
