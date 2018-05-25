package com.android.face.utils;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.util.Log;
import android.view.Surface;

import java.io.ByteArrayOutputStream;
import java.util.List;

public class CameraUtils {
    private static final String TAG = "CameraUtil";

    public static int getActivityDisplayOrientation(Activity activity) {
        int rotation = activity.getWindowManager().getDefaultDisplay()
                .getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }
        return degrees;
    }

    public static boolean isFlip(int mCameraId) {
        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(mCameraId, info);
        return info.facing != Camera.CameraInfo.CAMERA_FACING_BACK;
    }

    public static int getImageOrient(Camera.CameraInfo info, int degrees, int cameraId) {
        int result;
        if (cameraId == Camera.CameraInfo.CAMERA_FACING_BACK) {
            result = (360 - info.orientation + degrees + 360) % 360;
        } else {
            result = (360 - info.orientation - degrees + 360) % 360;
        }
        return result;
    }
    public static Bitmap getPreviewBitmap(byte[] bytes, int format, Camera.Size size) {
        return getPreviewBitmap(bytes, format, size.width, size.height);
    }

    public static Bitmap getPreviewBitmap(byte[] bytes, int format, int width, int height) {

        YuvImage yuv = new YuvImage(bytes, format, width, height, null);

        ByteArrayOutputStream jpgStream = new ByteArrayOutputStream();
        yuv.compressToJpeg(new Rect(0, 0, width, height), 50, jpgStream);

        byte[] jpgByte = jpgStream.toByteArray();
        return BitmapFactory.decodeByteArray(jpgByte, 0, jpgByte.length);
    }

    public static int getCameraRoate(int cameraId) {
        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(cameraId, info);
        return info.orientation;
    }

    public static int getNumberOfCameras() {
        return Camera.getNumberOfCameras();
    }
    public static Camera openDefaultCamera() {
        return Camera.open(0);
    }
    public static Camera openCamera(final int id) {
        return Camera.open(id);
    }
    public static boolean hasCamera(final int facing) {
        return getCameraId(facing) != -1;
    }
    private static int getCameraId(final int facing) {
        int numberOfCameras = Camera.getNumberOfCameras();
        Camera.CameraInfo info = new Camera.CameraInfo();
        for (int id = 0; id < numberOfCameras; id++) {
            Camera.getCameraInfo(id, info);
            if (info.facing == facing) {
                return id;
            }
        }
        return -1;
    }

    public static Camera.Size findBestPreviewSizeValue(Point screenResolution, Camera mCamera) {
        List<Camera.Size> sizeList = mCamera.getParameters().getSupportedPreviewSizes();
        return findBestPreviewSizeValue(sizeList,screenResolution);
    }
    private static Camera.Size findBestPreviewSizeValue(List<Camera.Size> sizeList, Point screenResolution) {
        int size = 0;
        int index =0;
        for(int i = 0; i < sizeList.size(); i ++){
            // 如果有符合的分辨率，则直接返回
            if(sizeList.get(i).width == screenResolution.x && sizeList.get(i).height == screenResolution.y){
                Log.d(TAG, "get default preview size!!!");
                return sizeList.get(i);
            }

            int newX = sizeList.get(i).width;
            int newY = sizeList.get(i).height;
            int newSize = Math.abs(newX * newX) + Math.abs(newY * newY);
            float ratio = (float)newY / (float)newX;
            Log.d(TAG, newX + ":" + newY + ":" + ratio);
            if (newSize >= size && ratio != 0.75) {  // 确保图片是16：9的
                index =i;
                size = newSize;
            } else if (newSize < size) {
                continue;
            }
        }
        return sizeList.get(index);
    }


    public static boolean hasFrontCamera() {
        return hasCamera(Camera.CameraInfo.CAMERA_FACING_FRONT);
    }

    public static boolean hasBackCamera() {
        return hasCamera(Camera.CameraInfo.CAMERA_FACING_BACK);

    }



    public static void stopCamera(Camera mCamera) {
        if (null != mCamera) {
            mCamera.setPreviewCallback(null);
            mCamera.stopPreview();
            mCamera.release();
        }
    }

}
