package com.android.face.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.android.face.R;
import com.android.face.utils.BitmapUtils;
import com.android.face.utils.ConfigUtils;
import com.android.face.utils.FileUtils;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddInformationActivity extends AppCompatActivity {
    @BindView(R.id.head)
   ImageView head;

    @BindView(R.id.edit_text)
   EditText editText;

    @BindView(R.id.camera)
    Button camera;

    @BindView(R.id.add)
   Button add;

    private static String SAVE_AVATORNAME;

    /**
     * 打开照相机
     */
    private final int IMAGE_RESULT_CODE = 2;

    /**
     * 裁剪
     */
    private static final int RESULT_REQUEST_CODE = 3;

    private Uri uri;
    private Bitmap photo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_information);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.camera,R.id.add})
    public void onViewClicked(View view){
        switch (view.getId()){
            case R.id.camera:
                addFaceMethod();
                break;
            case R.id.add:
                break;
        }
    }


    public void addFaceMethod() {
        long time = System.currentTimeMillis();
        SAVE_AVATORNAME = ConfigUtils.APP_FILE+"/recognition" + time + "perfxlab#";
        File file = new File( SAVE_AVATORNAME + ".png");
        FileUtils.startActionCapture(this, file, IMAGE_RESULT_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }
        switch (requestCode) {
            case IMAGE_RESULT_CODE:
                File file = new File( SAVE_AVATORNAME + ".png");
                String headPath = file.getAbsolutePath();
                Bitmap headBmp = BitmapUtils.getFitSampleBitmap(headPath,1000,1000);
                file=FileUtils.savePhotoToSDCard(headBmp, headPath);
                uri = FileUtils.startPictureCut(this, file, RESULT_REQUEST_CODE);
                break;

            case RESULT_REQUEST_CODE:
                if (data != null) {
                    getImageToView();
                }
                break;

        }
    }
    private void getImageToView() {
        photo = FileUtils.getBitmapByUri(getBaseContext(), uri);
        head.setImageBitmap(photo);
    }
}
