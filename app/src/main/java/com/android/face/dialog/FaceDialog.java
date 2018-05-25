package com.android.face.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.android.face.R;
import com.android.face.activity.MainActivity;
import com.android.face.utils.ProgressBar;
import com.bumptech.glide.Glide;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * @author suhu
 * @data 2018/5/8 0008.
 * @description FaceDialog
 */

public class FaceDialog extends Dialog{

    private static DisplayMetrics displayMetrics;
    private boolean isShow = false;
    private FaceDialog dialog;
    private Context context;
    private String url;
    private String name;
    private boolean isPass;
    private Bitmap bitmap;


    private CircleImageView face;
    private TextView textView;
    private TextView pass;
    private ProgressBar progressBar;
    private CountDownTimer timer;




    public FaceDialog(@NonNull Context context, int themeResId, String url, String name,boolean isPass) {
        super(context, themeResId);
        this.context = context;
        this.url = url;
        this.name = name;
        this.isPass = isPass;
    }


    public FaceDialog(@NonNull Context context, int themeResId, Bitmap bitmap, String name,boolean isPass) {
        super(context, themeResId);
        this.context = context;
        this.bitmap = bitmap;
        this.name = name;
        this.isPass = isPass;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {


        setContentView(R.layout.dialog_face);
        Window window = getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        //window.setGravity(Gravity.CENTER);

        Rect rect = new Rect();
        View view = window.getDecorView();
        view.getWindowVisibleDisplayFrame(rect);
        params.width = getScreenWidth();
        params.dimAmount = 0.5f;
        //getWindow().setWindowAnimations(R.style.popupWindowAnimation);
        //getWindow().setWindowAnimations(R.style.popupWindowScale);
        getWindow().setWindowAnimations(R.style.modal);

        params.x = 1080/2;
        params.y = -(360+ MainActivity.allHeight)/2;

        window.setAttributes(params);

        initView();
        setData();


    }













    private void countTimer(){
        timer =  new CountDownTimer(3000,500) {
            @Override
            public void onTick(long millisUntilFinished) {
                if (!isShow){
                    if (isPass){
                        progressBar.succesLoad();
                    }else {
                        progressBar.errorLoad();
                    }
                }
                isShow = true;

            }

            @Override
            public void onFinish() {
                if (dialog!=null&&dialog.isShowing()){
                     dialog.cancel();
                    dialog.dismiss();
                    //dialog = null;
                }
            }
        };
        timer.start();


    }

    private void setData() {
        textView.setText(name);
        if (bitmap!=null){
            face.setImageBitmap(bitmap);
        }else {
            Glide.with(context).load(url).into(face);
        }

        if (isPass){
            pass.setTextColor(Color.parseColor("#00FF00"));
            pass.setText("验证通过");
            progressBar.setColor(Color.parseColor("#00FF00"));
        }else {
            pass.setText("验证失败");
            pass.setTextColor(Color.parseColor("#FF0000"));
            progressBar.setColor(Color.parseColor("#FF0000"));

        }
    }

    private void initView() {
        face = findViewById(R.id.face);
        textView = findViewById(R.id.name);
        pass = findViewById(R.id.pass);
        progressBar = findViewById(R.id.progress);
        dialog = this;

    }


    public int getScreenWidth(){
        if (displayMetrics==null){
            displayMetrics = context.getResources().getDisplayMetrics();
        }
        return displayMetrics.widthPixels;
    }


    @Override
    public void dismiss() {
        if (timer!=null){
            timer.cancel();
            timer=null;
        }
        super.dismiss();
    }

    @Override
    public void show() {
        countTimer();
        super.show();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }
}
