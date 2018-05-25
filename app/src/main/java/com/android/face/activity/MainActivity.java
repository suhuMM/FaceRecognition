package com.android.face.activity;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicYuvToRGB;
import android.renderscript.Type;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.android.face.R;
import com.android.face.adapter.HistoryAdapter;
import com.android.face.adapter.SpaceItemDecoration;
import com.android.face.app.SoftwareApp;
import com.android.face.bean.PersonInformation;
import com.android.face.dialog.FaceDialog;
import com.android.face.utils.PermissionUtils;
import com.android.face.utils.Utils;
import com.android.face.utils.WeakRefHandler;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * @author suhu
 * @time 2018/5/9 0009 下午 1:30
 * @description 主Activity
 */

public class MainActivity extends AppCompatActivity implements SurfaceHolder.Callback, TextToSpeech.OnInitListener, Camera.PreviewCallback {
    private static final String TAG = "MainActivity";
    public static int allHeight;


    private byte bytes[] = new byte[((1920 * 1152) * ImageFormat.getBitsPerPixel(ImageFormat.NV21)) / 8];
    private SurfaceView mSurfaceView = null;
    private SurfaceHolder mSurfaceHolder = null;
    private TextToSpeech tts;
    private boolean isRead = false;
    private Boolean isClose = false;
    private Camera mCamera = null;

    private int  screenWidth;
    private int screenHeight;

    private boolean bIfPreview;
    private int mPreviewWidth, mPreviewHeight;
    private RecyclerView recyclerView;
    private HistoryAdapter adapter;
    private List<PersonInformation> list;



    private RenderScript rs;
    private ScriptIntrinsicYuvToRGB yuvToRGB;
    private Type.Builder yuvType,rgbaType;
    private Allocation in,out;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //取消状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        Toast.makeText(this, Build.CPU_ABI,Toast.LENGTH_LONG).show();

        setContentView(R.layout.activity_main);
        initView();
        if (PermissionUtils.checkPermission(this)){
            initSurfaceView();
        }

        ttsRead();
        httpTest();

        rs = RenderScript.create(this);
        yuvToRGB = ScriptIntrinsicYuvToRGB.create(rs, Element.U8_4(rs));

        allHeight = Utils.getDaoHangHeight(this)+Utils.getStatusBarHeight(this);
    }

    private void initView() {

        recyclerView = findViewById(R.id.recycler_view);
        list = new ArrayList<>();
        adapter = new HistoryAdapter(this, list);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.addItemDecoration(new SpaceItemDecoration(40));
        recyclerView.setAdapter(adapter);
        findViewById(R.id.text).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,AddInformationActivity.class));
            }
        });
    }


    private void initSurfaceView() {
        mSurfaceView = findViewById(R.id.surfaceView);
        mSurfaceHolder = mSurfaceView.getHolder();
        mSurfaceHolder.addCallback(this);
        // 预览大小設置

        //屏幕尺寸
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        Log.i(TAG,"w="+metrics.widthPixels+"        h="+metrics.heightPixels);

        WindowManager windowManager = getWindowManager();
        Log.i(TAG,"w="+windowManager.getDefaultDisplay().getWidth()+"        h="+windowManager.getDefaultDisplay().getHeight());

        //initSurfaceViewSize();
//        ViewGroup.LayoutParams params = mSurfaceView.getLayoutParams();
//            params.width = 720;
//            params.height = 1280;
//        mSurfaceView.setLayoutParams(params);

        mSurfaceHolder.setFixedSize(1920, 1080);
        // 設置顯示器類型，setType必须设置
        mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

    }

    private void ttsRead() {
        isRead = true;
        tts = new TextToSpeech(this, this);
    }


    private void read(String str) {

        if (isRead && !tts.isSpeaking()) {
            tts.speak(str, TextToSpeech.QUEUE_FLUSH, null);
        }
    }


    private void initCamera() {

        if (bIfPreview) {
            mCamera.stopPreview();//stopCamera();
        }
        if (null != mCamera) {
            try {
                /* Camera Service settings*/
                Camera.Parameters parameters = mCamera.getParameters();
                // parameters.setFlashMode("off"); // 无闪光灯
                //parameters.setPreviewFrameRate(10);//设置每秒3帧
                //Sets the image format for picture 设定相片格式为JPEG，默认为NV21
                parameters.setPictureFormat(ImageFormat.JPEG);
                //Sets the image format for preview picture，默认为NV21
                parameters.setPreviewFormat(ImageFormat.NV21);
                /*【ImageFormat】JPEG/NV16(YCrCb format，used for Video)/NV21(YCrCb format，used for Image)/RGB_565/YUY2/YU12*/

                // 【调试】获取caera支持的PictrueSize，看看能否设置？？
                List<Camera.Size> pictureSizes = mCamera.getParameters().getSupportedPictureSizes();
                List<Camera.Size> previewSizes = mCamera.getParameters().getSupportedPreviewSizes();
                List<Integer> previewFormats = mCamera.getParameters().getSupportedPreviewFormats();
                List<Integer> previewFrameRates = mCamera.getParameters().getSupportedPreviewFrameRates();
                Log.i(TAG + "initCamera", "cyy support parameters is ");
                Camera.Size psize = null;
                for (int i = 0; i < pictureSizes.size(); i++) {
                    psize = pictureSizes.get(i);
                    Log.i(TAG + "initCamera", "PictrueSize,width: " + psize.width + " height" + psize.height);
                }

                for (int i = 0; i < previewSizes.size(); i++) {
                    psize = previewSizes.get(i);
                    Log.i(TAG + "initCamera", "PreviewSize,width: " + psize.width + " height" + psize.height);
                }
                Integer pf = null;

                for (int i = 0; i < previewFormats.size(); i++) {
                    pf = previewFormats.get(i);
                    Log.i(TAG + "initCamera", "previewformates:" + pf);
                }


                Log.i(TAG,"w="+mSurfaceView.getMeasuredWidth()+"        h="+mSurfaceView.getMeasuredHeight() );




                //parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
                parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);

                // 横竖屏镜头自动调整
                boolean isPortrait = false;
                if (this.getResources().getConfiguration().orientation != Configuration.ORIENTATION_LANDSCAPE) {
                    parameters.set("orientation", "portrait"); //
                    parameters.set("rotation", 180); // 镜头角度转90度（默认摄像头是横拍）
                    parameters.setRotation(180);
                    //mCamera.setDisplayOrientation(90); // 魅族
                    mCamera.setDisplayOrientation(270); // 在2.2以上可以使用


                } else {
                    // 如果是横屏
                    parameters.set("orientation", "landscape"); //
                    parameters.setRotation(180);
                    mCamera.setDisplayOrientation(180); // 在2.2以上可以使用
                    isPortrait =true;
                }

//                Camera.Size size = Utils.getCloselyPreSize(mPreviewWidth,mPreviewHeight,previewSizes,isPortrait);
//                parameters.setPreviewSize(size.width, size.height); // 指定preview的大小
                // 设置拍照和预览图片大小
                parameters.setPictureSize(1920, 1080); //指定拍照图片的大小
                parameters.setPreviewSize(1920, 1080); // 指定preview的大小
                //这两个属性 如果这两个属性设置的和真实手机的不一样时，就会报错


                /* 视频流编码处理 */
                //添加对视频流处理函数


                // 设定配置参数并开启预览
                //mCamera.setParameters(parameters); // 将Camera.Parameters设定予Camera
                //回调
                mCamera.addCallbackBuffer(bytes);
                mCamera.setPreviewCallbackWithBuffer(this);

                mCamera.startPreview(); // 打开预览画面
                mCamera.cancelAutoFocus();

                //设置相机对焦
                mCamera.autoFocus(new Camera.AutoFocusCallback() {
                    @Override
                    public void onAutoFocus(boolean b, Camera camera) {
                    }
                });


                bIfPreview = true;

                // 【调试】设置后的图片大小和预览大小以及帧率
                Camera.Size csize = mCamera.getParameters().getPreviewSize();
                mPreviewHeight = csize.height; //
                mPreviewWidth = csize.width;
                Log.i(TAG + "initCamera", "after setting, previewSize:width: " + csize.width + " height: " + csize.height);
                csize = mCamera.getParameters().getPictureSize();
                Log.i(TAG + "initCamera", "after setting, pictruesize:width: " + csize.width + " height: " + csize.height);
                Log.i(TAG + "initCamera", "after setting, previewformate is " + mCamera.getParameters().getPreviewFormat());
                Log.i(TAG + "initCamera", "after setting, previewframetate is " + mCamera.getParameters().getPreviewFrameRate());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    protected void onDestroy() {

        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        isClose = true;
        super.onDestroy();

    }


    /*****************************************TTS回调*****************************************************************/

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            Log.e(TAG, "success");
            int result = tts.setLanguage(Locale.CHINA);
            if (result == TextToSpeech.LANG_MISSING_DATA
                    || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                tts.setLanguage(Locale.CHINA);
                tts.isLanguageAvailable(Locale.CHINA);
                tts.setPitch(1.0f);
                //tts.setSpeechRate(1.2f);
            }


        } else {
            Log.e(TAG, "失败");
        }
    }


    /*****************************************surfaceView回调*****************************************************************/

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

        // 开启摄像头（2.3版本后支持多摄像头,需传入参数）
        mCamera = Camera.open(0);
        //①原生yuv420sp视频存储方式
        //mCamera.setPreviewCallback(this);


        try {
            mCamera.setPreviewDisplay(mSurfaceHolder);

        } catch (Exception ex) {
            if (null != mCamera) {
                mCamera.release();
                mCamera = null;
            }
        }


    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        mPreviewHeight = height;
        mPreviewWidth = width;
        initCamera();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if (null != mCamera) {
            //这个必须在前，不然退出出错
            mCamera.setPreviewCallback(null);
            mCamera.stopPreview();
            bIfPreview = false;
            mCamera.release();
            mCamera = null;
        }
    }

    /*************************************************相机数据流回调***************************************************************/

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        read("小明同学欢迎光临");
        show(data, camera);


    }

    private void show(byte[] data, Camera camera) {
        long a = System.currentTimeMillis();
        Log.i(TAG,"时间"+a);
        Bitmap bitmap = getBitmap(data, camera);
        //Bitmap bitmap = fun(data);
        //getData(bitmap);
        long b = System.currentTimeMillis();
        Log.i(TAG,"时间"+b);
        Log.i(TAG,"时间差"+(b-a));
        //Bitmap bitmap = fun(data);

        fun1(data,camera);


        PersonInformation information = new PersonInformation(bitmap, "苏虎", Utils.getTime(),true  );
        adapter.insert(information);
        new FaceDialog(MainActivity.this, R.style.dialog, bitmap, "苏虎",true).show();
    }


    private long a;
    private void fun1(final byte [] data,final Camera camera) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
               a = System.currentTimeMillis();
                Log.i(TAG,"子线程时间"+a);

 //               long b = System.currentTimeMillis();
//                Log.i(TAG,"子线程时间"+b);
//                Log.i(TAG,"子线程时间差"+(b-a));
                Message message = mHandler.obtainMessage();
                message.what = 1;
                message.obj = getBitmap(data,camera);
                mHandler.sendMessage(message);
            }
        };
        SoftwareApp.getThreadPool().execute(runnable);

    }


    /****************************************************************dialog***********************************************************************************/


    private void faceDialog(String url, String name) {
        if (!isClose) {
            new FaceDialog(MainActivity.this, R.style.dialog, url, name,true).show();

        }
    }


    /*************************************************防止内存泄漏handler***************************************************************/


    private Handler.Callback callback = new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {

            switch (msg.what) {
                case 0:
                    //faceDialog("http://pic5.58cdn.com.cn/zhuanzh/n_v1bkuyfvltjuifpjbky4aq.jpg","张三");
                    if (mCamera != null) {
                        mCamera.addCallbackBuffer(bytes);
                    }
                    break;

                case 1:
                    long b = System.currentTimeMillis();
                    Log.i(TAG,"子线程时间"+b);
                    Log.i(TAG,"子线程时间差"+(b-a));
                    break;
                default:
            }
            return true;
        }
    };
    private Handler mHandler = new WeakRefHandler(callback);


    /**
     * test
     */
    public void httpTest() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                while (isClose==false){
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    mHandler.sendEmptyMessage(0);
                }
            }
        };
        SoftwareApp.getThreadPool().execute(runnable);


    }

    /**
     * 将byte数组转换成流
     *
     * @param data
     * @return ByteArrayInputStream
     */
    private byte[] rawImage;

    private ByteArrayInputStream stream(byte[] data) {
        ByteArrayInputStream inputStream = new ByteArrayInputStream(data);
        return inputStream;
    }


    private Bitmap getBitmap(byte[] data, Camera camera) {
        Camera.Size previewSize = camera.getParameters().getPreviewSize();//
        //获取尺寸,格式转换的时候要用到
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        newOpts.inJustDecodeBounds = true;
        YuvImage yuvimage = new YuvImage(
                data,
                ImageFormat.NV21,
                previewSize.width,
                previewSize.height,
                null);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        yuvimage.compressToJpeg(new Rect(0, 0, previewSize.width, previewSize.height), 100, bos);// 80--JPG图片的质量[0-100],100最高
        rawImage = bos.toByteArray();
        //将rawImage转换成bitmap
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        Bitmap bitmap = BitmapFactory.decodeByteArray(rawImage, 0, rawImage.length, options);
        Matrix matrix = new Matrix();
        matrix.setRotate(270);
        //matrix.setRotate(90);//魅族
        Bitmap btm = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        return btm;
    }




    private byte[] getData(Bitmap bitmap){
        if (bitmap==null) return null;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
        return bos.toByteArray();
    }










    private void initSurfaceViewSize(){
        WindowManager windowManager = getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        //获取屏幕的宽和高
        display.getMetrics(metrics);
        screenWidth = metrics.widthPixels;
        screenHeight = metrics.heightPixels;
        setSurfaceViewSize(Utils.getSurfaceViewSize(screenWidth,screenHeight));
    }

    /**
     * 根据分辨率设置预览SurfaceView的大小以防止变形
     *
     * @param surfaceSize
     */
    private void setSurfaceViewSize(String surfaceSize) {
        ViewGroup.LayoutParams params = mSurfaceView.getLayoutParams();
        if (surfaceSize.equals("16:9")) {
            params.height = ViewGroup.LayoutParams.MATCH_PARENT;
        } else if (surfaceSize.equals("4:3")) {
            params.height = 4 * screenWidth / 3;
        }
        mSurfaceView.setLayoutParams(params);
    }


    private Bitmap fun(byte[] data){
        if (yuvType == null)
        {
            yuvType = new Type.Builder(rs, Element.U8(rs)).setX(data.length);
            in = Allocation.createTyped(rs, yuvType.create(), Allocation.USAGE_SCRIPT);

            rgbaType = new Type.Builder(rs, Element.RGBA_8888(rs)).setX(1920).setY(1080);
            out = Allocation.createTyped(rs, rgbaType.create(), Allocation.USAGE_SCRIPT);
        }

        in.copyFrom(data);

        yuvToRGB.setInput(in);
        yuvToRGB.forEach(out);

        Bitmap bitmap = Bitmap.createBitmap(1920, 1080, Bitmap.Config.ARGB_8888);
        out.copyTo(bitmap);
        //return bitmap;

        Matrix matrix = new Matrix();
        matrix.setRotate(270);
        Bitmap btm = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        return btm;

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode==PermissionUtils.REQUEST_EXTERNAL_STORAGE){
            initSurfaceView();
        }
    }
}
