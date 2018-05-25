package com.android.face.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;

import com.android.face.bean.PhotoBean;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class FileUtils {

    private static String APP_DIR_NAME = "my_images";

    private static String mAppRootDir;
    private static String mRootDir;

    public static void init(Context context) {
        //mRootDir = context.getFilesDir().getAbsolutePath();
        mRootDir = Environment.getExternalStorageDirectory().getAbsolutePath();
        mAppRootDir = mRootDir;
    }

    public static String getAppRootDir() {
        return mAppRootDir;
    }

    public static String getRootPath() {
        return mRootDir;
    }

    /**
     * 打开相机
     * 兼容7.0
     *
     * @param activity    Activity
     * @param file        File
     * @param requestCode result requestCode
     */
    public static void startActionCapture(@NonNull Activity activity, File file, int requestCode) {

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, getUriForFile(activity, file));
        activity.startActivityForResult(intent, requestCode);
    }

    public static final String FILE_PATH = Environment.getExternalStorageDirectory().toString()+"/PerfXlabTest/";
    public static Uri getTempUri() {
        String fileName = System.currentTimeMillis() + ".jpg";
        File out = new File(FILE_PATH);
        if (!out.exists()) {
            out.mkdirs();
        }
        out = new File(FILE_PATH, fileName);
        return Uri.fromFile(out);
    }
    // CameraUtil获取Bitmap的方法
    public static Bitmap getBitmapByUri(Context context, Uri uri) {
        Bitmap bitmap = null;
        try {
            bitmap = BitmapFactory.decodeStream(context.getContentResolver()
                    .openInputStream(uri));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
        return bitmap;
    }

    public static Uri startPictureCut(@NonNull Activity activity, File imageFile/*,File outputFile*/, int requestCode) {
        Intent intent = new Intent("com.android.camera.action.CROP");

        Uri imageUri = getUriForFile(activity, imageFile);
        //  Uri outputUri = getUriForFile(activity, outputFile);
        intent.setDataAndType(imageUri, "image/*");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        // 设置裁剪
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", 300);
        intent.putExtra("outputY", 300);
        intent.putExtra("return-data", false);
        //  intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFile);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        Uri tempUri = getTempUri();
        intent.putExtra(MediaStore.EXTRA_OUTPUT, tempUri);
        activity.startActivityForResult(intent, requestCode);
        return tempUri;
    }

    public static Uri getUriForFile(Context context, File file) {
        if (context == null || file == null) {
            throw new NullPointerException();
        }
        Uri uri;
        if (Build.VERSION.SDK_INT >= 24) {
            uri = FileProvider.getUriForFile(context.getApplicationContext(), context.getApplicationInfo().processName, file);
        } else {
            uri = Uri.fromFile(file);
        }
        return uri;
    }

    public static File[] scanFile(String dir) {
        File dirName = new File(dir);
        File scanFile[];
        if( !dirName.exists() || !dirName.isDirectory()) {
            return null;
        }

        return dirName.listFiles();
    }

    public static boolean isImageFile(File f) {
        final String[] okFileExtensions =  new String[] {"jpg", "png", "gif","jpeg","bmp"};
        for (String extension : okFileExtensions) {
            if (f.getName().toLowerCase().endsWith(extension)) {
                return true;
            }
        }
        return false;
    }

    public static PhotoBean ByteToObject(byte[] bytes) {
        PhotoBean obj = null;
        try {

            ByteArrayInputStream bi = new ByteArrayInputStream(bytes);
            ObjectInputStream oi = new ObjectInputStream(bi);

            obj = (PhotoBean) oi.readObject();
            bi.close();
            oi.close();
        } catch (Exception e) {
            System.out.println("translation" + e.getMessage());
            e.printStackTrace();
        }
        return obj;
    }


    public static byte[] ObjectToByte(PhotoBean obj) {
        byte[] bytes = null;
        try {
            // object to bytearray
            ByteArrayOutputStream bo = new ByteArrayOutputStream();
            ObjectOutputStream oo = new ObjectOutputStream(bo);
            oo.writeObject(obj);

            bytes = bo.toByteArray();

            bo.close();
            oo.close();
        } catch (Exception e) {
            System.out.println("translation" + e.getMessage());
            e.printStackTrace();
        }
        return bytes;
    }

    public static void deleteFile(String path) {
        File file = new File(path);

        if(file.exists() && file.isFile()) {

            file.delete();
        }
    }

    public static File savePhotoToSDCard(Bitmap photoBitmap, String abspath) {
        if (checkSDCardAvailable()) {
            File dir = new File(abspath);
            if(dir.exists())
                dir.delete();
//            if (!dir.exists()) {
//                dir.mkdirs();
//            }

            File photoFile = new File(abspath);

            FileOutputStream fileOutputStream = null;
            try {
                fileOutputStream = new FileOutputStream(photoFile);
                if (photoBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream)) {
                    fileOutputStream.flush();
                    fileOutputStream.close();
                }
                return photoFile;
            } catch (FileNotFoundException e) {
                photoFile.delete();
                e.printStackTrace();
            } catch (IOException e) {
                photoFile.delete();
                e.printStackTrace();
            } finally {
                try {
                    if (fileOutputStream != null) {
                        fileOutputStream.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
        return null;
    }

    /**
     * Check the SD card
     *
     * @return 是否获能获取到SD卡
     */
    public static boolean checkSDCardAvailable() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

}